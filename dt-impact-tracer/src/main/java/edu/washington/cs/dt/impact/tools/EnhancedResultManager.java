package edu.washington.cs.dt.impact.tools;

import edu.washington.cs.dt.impact.figure.generator.EnhancedResults;
import edu.washington.cs.dt.impact.util.Constants;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EnhancedResultManager {
    private final Map<Constants.TECHNIQUE, EnhancedResultAverager> origAveragers = new HashMap<>();
    private final Map<Constants.TECHNIQUE, EnhancedResultAverager> autoAveragers = new HashMap<>();

    public EnhancedResultManager(final List<Path> paths) throws IOException {
        this();

        for (final Path path : paths) {
            add(path);
        }
    }

    public EnhancedResultManager() {
        for (final Constants.TECHNIQUE technique : Constants.TECHNIQUE.values()) {
            origAveragers.put(technique, new EnhancedResultAverager("orig", technique));
            autoAveragers.put(technique, new EnhancedResultAverager("auto", technique));
        }
    }

    private boolean has(final Path p, final String s) throws IOException {
        return Files.list(p).anyMatch(path -> path.getFileName().toString().contains(s));
    }

    public EnhancedResultManager add(final Path resultDir) throws IOException {
        Files.list(resultDir).forEach(p -> {
            try {
                if (Files.isDirectory(p)) {
                    if (has(p, "-ORIG-")) {
                        origAveragers.get(EnhancedResults.getTechnique(p)).add(p);
                    }

                    if (has(p, "-AUTO-")) {
                        autoAveragers.get(EnhancedResults.getTechnique(p)).add(p);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return this;
    }

    public Optional<EnhancedResultAverager> retrieve(final String origOrAuto, final Constants.TECHNIQUE technique) {
        switch (origOrAuto) {
            case "orig":
                return Optional.ofNullable(origAveragers.get(technique));

            case "auto":
                return Optional.ofNullable(autoAveragers.get(technique));

            default:
                throw new IllegalArgumentException("Unknown test type: " + origOrAuto);
        }
    }
}