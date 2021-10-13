package org.xbib.content.config;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigFinder {

    private final FileSystem fileSystem;

    private final EnumSet<FileVisitOption> opts;

    private final List<Path> result;

    private Comparator<Path> comparator;

    public ConfigFinder() {
        this(FileSystems.getDefault(), EnumSet.of(FileVisitOption.FOLLOW_LINKS));
    }

    public ConfigFinder(FileSystem fileSystem, EnumSet<FileVisitOption> opts) {
        this.fileSystem = fileSystem;
        this.opts = opts;
        this.result = new ArrayList<>();
    }

    public ConfigFinder find(String path, String pathPattern) throws IOException {
        return find(null, null, fileSystem.getPath(path), pathPattern);
    }

    public ConfigFinder find(String base, String basePattern, String path, String pathPattern) throws IOException {
        return find(base == null || base.isEmpty() ? null : fileSystem.getPath(base), basePattern,
                path == null || path.isEmpty() ? null : fileSystem.getPath(path), pathPattern);
    }

    public ConfigFinder find(Path base, String basePattern, Path path, String pathPattern) throws IOException {
        return find(base, basePattern, path, pathPattern, null);
    }

    /**
     * Find the most recent version of a file.
     *
     * @param base        the path of the base directory
     * @param basePattern a pattern to match directory entries in the base directory or null to match '*'
     * @param path        the path of the file if no recent path can be found in the base directory or null
     * @param pathPattern     the file name pattern to match
     * @param modifiedSince time stamp for file or null
     * @return this Finder
     * @throws IOException if find fails
     */
    public ConfigFinder find(Path base,
                             String basePattern,
                             Path path,
                             String pathPattern,
                             FileTime modifiedSince) throws IOException {
        if (base != null && path == null) {
            // find input in base
            final PathMatcher baseMatcher = base.getFileSystem()
                    .getPathMatcher("glob:" + (basePattern != null ? basePattern : "*"));
            final PathMatcher pathMatcher = base.getFileSystem()
                    .getPathMatcher("glob:" + (pathPattern != null ? pathPattern : "*"));
            List<Path> directories = new ArrayList<>();
            List<Path> list = Files.find(base, 1,
                    (p, a) -> {
                        if (Files.isDirectory(p) && baseMatcher.matches(p.getFileName())) {
                            directories.add(p);
                            return false;
                        }
                        return Files.isRegularFile(p) && pathMatcher.matches(p.getFileName());
                    }, FileVisitOption.FOLLOW_LINKS)
                    .collect(Collectors.toList());
            if (directories.isEmpty()) {
                return this;
            }
            list.sort(LAST_MODIFIED_COMPARATOR.reversed());
            result.addAll(list);
            path = list.iterator().next();
        }
        if (path == null) {
            return this;
        }
        final PathMatcher pathMatcher = path.getFileSystem()
                .getPathMatcher("glob:" + (pathPattern != null ? pathPattern : "*"));
        Files.walkFileTree(path, opts, Integer.MAX_VALUE, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path p, BasicFileAttributes a) {
                if ((Files.isRegularFile(p) && pathMatcher.matches(p.getFileName())) &&
                        (modifiedSince == null || a.lastModifiedTime().toMillis() > modifiedSince.toMillis())) {
                    result.add(p);
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return this;
    }

    public ConfigFinder sortBy(String mode) {
        if ("lastmodified".equals(mode)) {
            this.comparator = LAST_MODIFIED_COMPARATOR;
        } else if ("name".equals(mode)) {
            this.comparator = PATH_NAME_COMPARATOR;
        }
        return this;
    }

    public ConfigFinder order(String mode) {
        if ("desc".equals(mode)) {
            this.comparator = Collections.reverseOrder(comparator);
        }
        return this;
    }

    public Stream<Path> getPathFiles() {
        return getPathFiles(-1);
    }

    public Stream<Path> getPathFiles(long max) {
        if (comparator != null) {
            result.sort(comparator);
        }
        return result.stream().limit(max < 0 ? result.size() : max);
    }

    public Stream<Path> skipPathFiles(long skip) {
        if (comparator != null) {
            result.sort(comparator);
        }
        return result.stream().skip(skip < 0 ? 0 : skip);
    }

    public Stream<String> getPaths() {
        return getPaths(-1);
    }

    public Stream<String> getPaths(long max) {
        if (comparator != null) {
            result.sort(comparator);
        }
        return result.stream()
                .map(p -> p.toAbsolutePath().toString())
                .limit(max < 0 ? result.size() : max);
    }

   private static final Comparator<Path> LAST_MODIFIED_COMPARATOR = Comparator.comparing(p -> {
        try {
            return Files.getLastModifiedTime(p);
        } catch (IOException e) {
            return null;
        }
   });

    private static final Comparator<Path> PATH_NAME_COMPARATOR = Comparator.comparing(Path::toString);
}
