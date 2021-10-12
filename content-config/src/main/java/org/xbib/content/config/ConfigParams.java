package org.xbib.content.config;

import org.xbib.content.settings.datastructures.Settings;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ConfigParams implements Comparable<ConfigParams> {

    private static final Comparator<ConfigParams> COMPARATOR =
            Comparator.comparing(ConfigParams::toString);

    boolean withSystemEnvironment = false;

    boolean withSystemProperties = false;

    boolean includeAll = false;

    boolean withStdin = false;

    List<ClassLoader> classLoaders = null;

    final List<SuffixedReader> reader = new ArrayList<>();

    final List<Settings> settings = new ArrayList<>();

    List<String> args = null;

    String directoryName = null;

    final List<String> fileNamesWithoutSuffix = new ArrayList<>();

    final List<String> fileLocations = new ArrayList<>();

    public ConfigParams() {
    }

    public ConfigParams withSystemEnvironment() {
        this.withSystemEnvironment = true;
        return this;
    }

    public ConfigParams withSystemProperties() {
        this.withSystemProperties = true;
        return this;
    }

    public ConfigParams includeAll() {
        this.includeAll = true;
        return this;
    }

    public ConfigParams withStdin(boolean withStdin) {
        this.withStdin = withStdin;
        return this;
    }

    public ConfigParams withArgs(String[] args) {
        this.args = Arrays.asList(args);
        return this;
    }

    public ConfigParams withClassLoaders(ClassLoader... classLoaders) {
        this.classLoaders = Arrays.asList(classLoaders);
        return this;
    }

    public ConfigParams withReader(Reader reader, String suffix) {
        SuffixedReader suffixedReader = new SuffixedReader();
        suffixedReader.reader = reader;
        suffixedReader.suffix = suffix;
        this.reader.add(suffixedReader);
        return this;
    }

    public ConfigParams withSettings(Settings settings) {
        this.settings.add(settings);
        return this;
    }

    public ConfigParams withDirectoryName(String directoryName) {
        this.directoryName = directoryName;
        return this;
    }

    public ConfigParams withFileNamesWithoutSuffix(String... fileNamesWithoutSuffix) {
        this.fileNamesWithoutSuffix.addAll(Arrays.asList(fileNamesWithoutSuffix));
        return this;
    }

    public ConfigParams withLocation(String location) {
        this.fileLocations.add(location);
        return this;
    }

    public ConfigParams withPath(String basePath, String basePattern, String path, String pathPattern) throws IOException {
        ConfigFinder configFinder = new ConfigFinder();
        configFinder.find(basePath, basePattern, path, pathPattern).getPaths().forEach(this::withLocation);
        return this;
    }

    @Override
    public int compareTo(ConfigParams o) {
        return COMPARATOR.compare(this, o);
    }

    @Override
    public String toString() {
        return "" +
                withSystemEnvironment +
                withSystemProperties +
                withStdin +
                classLoaders +
                reader +
                args +
                directoryName +
                fileNamesWithoutSuffix +
                fileLocations;
    }

    public static class SuffixedReader {
        Reader reader;
        String suffix;
    }
}
