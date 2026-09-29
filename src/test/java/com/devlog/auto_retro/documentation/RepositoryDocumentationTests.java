package com.devlog.auto_retro.documentation;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class RepositoryDocumentationTests {

    private static final Pattern LOCAL_MARKDOWN_LINK = Pattern.compile(
        "\\[[^]]+]\\((?!https?://|mailto:|#)([^)#]+)(?:#[^)]*)?\\)"
    );

    private final Path repositoryRoot = Path.of("").toAbsolutePath().normalize();

    @Test
    void requiredAgentDocumentationExists() {
        assertThat(repositoryRoot.resolve("AGENTS.md")).isRegularFile();
        assertThat(repositoryRoot.resolve("docs/architecture-definition.ko.md")).isRegularFile();
        assertThat(repositoryRoot.resolve("docs/ai-guidelines.ko.md")).isRegularFile();
        assertThat(repositoryRoot.resolve("docs/ai-engineering-roadmap.ko.md")).isRegularFile();
        assertThat(repositoryRoot.resolve("docs/exec-plans/README.md")).isRegularFile();
    }

    @Test
    void claudeCompatibilityFilesReferenceCanonicalAgentInstructions() throws IOException {
        List<String> instructionBridges = List.of(
            "CLAUDE.md",
            "frontend/CLAUDE.md",
            "src/main/java/com/devlog/auto_retro/CLAUDE.md",
            "src/main/resources/db/migration/CLAUDE.md",
            "docs/exec-plans/CLAUDE.md"
        );

        for (String bridge : instructionBridges) {
            Path bridgePath = repositoryRoot.resolve(bridge);
            assertThat(bridgePath).isRegularFile();
            assertThat(Files.readString(bridgePath)).contains("@AGENTS.md");
        }

        Path skillBridge = repositoryRoot.resolve(".claude/skills/agents-sync/SKILL.md");
        assertThat(skillBridge).isRegularFile();
        assertThat(Files.readString(skillBridge))
            .contains("@../../../.agents/skills/agents-sync/SKILL.md")
            .contains("@../../../.agents/skills/agents-sync/references/glossary.md")
            .contains("disable-model-invocation: true");
    }

    @Test
    void localMarkdownLinksResolve() throws IOException {
        List<String> brokenLinks = new ArrayList<>();

        try (Stream<Path> paths = Files.walk(repositoryRoot)) {
            paths.filter(this::isProjectMarkdown)
                .forEach(markdown -> collectBrokenLinks(markdown, brokenLinks));
        }

        assertThat(brokenLinks)
            .as("Local Markdown links must point to existing repository files")
            .isEmpty();
    }

    private boolean isProjectMarkdown(Path path) {
        if (!Files.isRegularFile(path) || !path.toString().endsWith(".md")) {
            return false;
        }

        String relative = repositoryRoot.relativize(path).toString().replace('\\', '/');
        return !relative.startsWith(".git/")
            && !relative.startsWith(".gradle/")
            && !relative.startsWith("build/")
            && !relative.startsWith("node_modules/")
            && !relative.contains("/node_modules/");
    }

    private void collectBrokenLinks(Path markdown, List<String> brokenLinks) {
        try {
            Matcher matcher = LOCAL_MARKDOWN_LINK.matcher(Files.readString(markdown));
            while (matcher.find()) {
                String rawTarget = matcher.group(1).trim();
                Path target = markdown.getParent().resolve(rawTarget).normalize();
                if (!Files.exists(target)) {
                    brokenLinks.add(repositoryRoot.relativize(markdown) + " -> " + rawTarget);
                }
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to inspect " + markdown, exception);
        }
    }
}
