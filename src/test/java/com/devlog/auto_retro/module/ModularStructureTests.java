package com.devlog.auto_retro.module;

import com.devlog.auto_retro.AutoRetroApplication;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

public class ModularStructureTests {
    private final ApplicationModules modules =
        ApplicationModules.of(AutoRetroApplication.class);

    @Test
    void verifiesModuleBoundaries() {
        modules.verify();
    }

    @Test
    void printsDetectedModules() {
        modules.forEach(System.out::println);
    }
}
