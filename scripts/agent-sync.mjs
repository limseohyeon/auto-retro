import { createHash } from 'node:crypto'
import { existsSync, readFileSync, readdirSync, writeFileSync } from 'node:fs'
import { dirname, join, relative, resolve, sep } from 'node:path'
import process from 'node:process'

const repositoryRoot = resolve(import.meta.dirname, '..')
const manifestPath = join(repositoryRoot, '.agents', 'agent-sync-manifest.json')
const ignoredDirectories = new Set(['.git', '.gradle', 'build', 'dist', 'node_modules'])
const emojiPattern = /[\u{1F300}-\u{1FAFF}\u{2600}-\u{27BF}]/u

const mode = process.argv[2]

if (!['check', 'record'].includes(mode)) {
  fail('Usage: node scripts/agent-sync.mjs <check|record>')
}

const pairs = discoverPairs(repositoryRoot)

if (pairs.length === 0) {
  fail('No AGENTS.ko.md files were found.')
}

const validationErrors = pairs.flatMap(validatePair)

if (validationErrors.length > 0) {
  fail(validationErrors.join('\n'))
}

if (mode === 'record') {
  const manifest = {
    version: 1,
    pairs: Object.fromEntries(
      pairs.map(({ sourcePath, targetPath }) => [
        toRepositoryPath(sourcePath),
        {
          target: toRepositoryPath(targetPath),
          sourceSha256: sha256(readFileSync(sourcePath)),
          targetSha256: sha256(readFileSync(targetPath)),
        },
      ]),
    ),
  }

  writeFileSync(manifestPath, `${JSON.stringify(manifest, null, 2)}\n`, 'utf8')
  console.log(`Recorded ${pairs.length} synchronized AGENTS file pairs.`)
  process.exit(0)
}

const manifest = readManifest()
const currentSources = new Set(pairs.map(({ sourcePath }) => toRepositoryPath(sourcePath)))
const recordedSources = new Set(Object.keys(manifest.pairs ?? {}))
const synchronizationErrors = []

for (const source of currentSources) {
  if (!recordedSources.has(source)) {
    synchronizationErrors.push(`${source}: pair is not recorded in the manifest.`)
  }
}

for (const source of recordedSources) {
  if (!currentSources.has(source)) {
    synchronizationErrors.push(`${source}: manifest entry has no Korean source.`)
  }
}

for (const { sourcePath, targetPath } of pairs) {
  const source = toRepositoryPath(sourcePath)
  const entry = manifest.pairs?.[source]

  if (!entry) {
    continue
  }

  const target = toRepositoryPath(targetPath)

  if (entry.target !== target) {
    synchronizationErrors.push(`${source}: recorded target must be ${target}.`)
  }

  if (entry.sourceSha256 !== sha256(readFileSync(sourcePath))) {
    synchronizationErrors.push(`${source}: Korean source changed; run $agents-sync.`)
  }

  if (entry.targetSha256 !== sha256(readFileSync(targetPath))) {
    synchronizationErrors.push(`${target}: generated English file changed; run $agents-sync.`)
  }
}

if (synchronizationErrors.length > 0) {
  fail(synchronizationErrors.join('\n'))
}

console.log(`Verified ${pairs.length} synchronized AGENTS file pairs.`)

function discoverPairs(directory) {
  const discovered = []

  for (const entry of readdirSync(directory, { withFileTypes: true })) {
    if (entry.isDirectory() && ignoredDirectories.has(entry.name)) {
      continue
    }

    const entryPath = join(directory, entry.name)

    if (entry.isDirectory()) {
      discovered.push(...discoverPairs(entryPath))
    } else if (entry.isFile() && entry.name === 'AGENTS.ko.md') {
      discovered.push({
        sourcePath: entryPath,
        targetPath: join(dirname(entryPath), 'AGENTS.md'),
      })
    }
  }

  return discovered.sort((left, right) => left.sourcePath.localeCompare(right.sourcePath))
}

function validatePair({ sourcePath, targetPath }) {
  const source = toRepositoryPath(sourcePath)
  const target = toRepositoryPath(targetPath)
  const errors = []

  if (!existsSync(targetPath)) {
    return [`${source}: missing paired ${target}.`]
  }

  const sourceContent = readFileSync(sourcePath, 'utf8')
  const targetContent = readFileSync(targetPath, 'utf8')

  for (const [path, content] of [
    [source, sourceContent],
    [target, targetContent],
  ]) {
    if (lineCount(content) >= 500) {
      errors.push(`${path}: must remain below 500 lines.`)
    }

    if (emojiPattern.test(content)) {
      errors.push(`${path}: emojis are not allowed.`)
    }
  }

  compareSequence(errors, source, 'heading levels', headings(sourceContent), headings(targetContent))
  compareSequence(errors, source, 'list structure', listStructure(sourceContent), listStructure(targetContent))
  compareSequence(errors, source, 'fenced code blocks', fencedCodeBlocks(sourceContent), fencedCodeBlocks(targetContent))
  compareSequence(errors, source, 'inline code', inlineCode(sourceContent), inlineCode(targetContent))
  compareSequence(errors, source, 'Markdown link targets', linkTargets(sourceContent), linkTargets(targetContent))

  return errors
}

function headings(content) {
  return content
    .split(/\r?\n/)
    .filter((line) => /^#{1,6}\s/.test(line))
    .map((line) => line.match(/^#+/)[0].length)
}

function listStructure(content) {
  return content
    .split(/\r?\n/)
    .map((line) => line.match(/^(\s*)(?:([-+*])|(\d+)\.)\s/))
    .filter(Boolean)
    .map((match) => `${match[1].length}:${match[2] ? 'unordered' : 'ordered'}`)
}

function fencedCodeBlocks(content) {
  return [...content.matchAll(/^(```[^\r\n]*\r?\n[\s\S]*?^```)[ \t]*$/gm)].map((match) =>
    match[1].replace(/\r\n/g, '\n'),
  )
}

function inlineCode(content) {
  const withoutFences = content.replace(/^```[^\r\n]*\r?\n[\s\S]*?^```[ \t]*$/gm, '')
  return [...withoutFences.matchAll(/`([^`\r\n]+)`/g)].map((match) => match[1])
}

function linkTargets(content) {
  return [...content.matchAll(/\[[^\]]+\]\(([^)]+)\)/g)].map((match) => match[1])
}

function compareSequence(errors, source, label, left, right) {
  if (JSON.stringify(left) !== JSON.stringify(right)) {
    errors.push(`${source}: Korean and English ${label} must match exactly.`)
  }
}

function readManifest() {
  if (!existsSync(manifestPath)) {
    fail('.agents/agent-sync-manifest.json is missing; run $agents-sync.')
  }

  try {
    const manifest = JSON.parse(readFileSync(manifestPath, 'utf8'))

    if (manifest.version !== 1 || typeof manifest.pairs !== 'object' || manifest.pairs === null) {
      fail('Agent sync manifest has an unsupported format.')
    }

    return manifest
  } catch (error) {
    fail(`Could not read the agent sync manifest: ${error.message}`)
  }
}

function sha256(content) {
  return createHash('sha256').update(content).digest('hex')
}

function lineCount(content) {
  return content.split(/\r?\n/).length - (content.endsWith('\n') ? 1 : 0)
}

function toRepositoryPath(path) {
  return relative(repositoryRoot, path).split(sep).join('/')
}

function fail(message) {
  console.error(message)
  process.exit(1)
}
