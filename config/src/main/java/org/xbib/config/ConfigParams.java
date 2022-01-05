package org.xbib.config;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import org.xbib.settings.Settings;
import org.xbib.settings.datastructures.DatastructureSettings;

public class ConfigParams implements Comparable<ConfigParams> {

    private static final Comparator<ConfigParams> COMPARATOR =
            Comparator.comparing(ConfigParams::toString);

    boolean withSystemEnvironment = false;

    boolean withSystemProperties = false;

    boolean failIfEmpty = false;

    boolean includeAll = false;

    boolean withStdin = false;

    boolean withSystemPropertiesOverride = false;

    List<ClassLoader> classLoaders = null;

    final List<SuffixedReader> reader = new ArrayList<>();

    final List<JdbcLookup> jdbcLookups = new ArrayList<>();

    final List<DatastructureSettings> settings = new ArrayList<>();

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

    public ConfigParams withSystemPropertiesOverride() {
        this.withSystemPropertiesOverride = true;
        return this;
    }

    public ConfigParams includeAll() {
        this.includeAll = true;
        return this;
    }

    public ConfigParams failIfEmpty() {
        this.failIfEmpty = true;
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
        this.settings.add(DatastructureSettings.builder().put(settings.getAsMap()).build());
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

    public ConfigParams withJdbc(Connection connection, String statement, String[] params) {
        JdbcLookup jdbcLookup = new JdbcLookup();
        jdbcLookup.connection = connection;
        jdbcLookup.statement = statement;
        jdbcLookup.params = params;
        jdbcLookups.add(jdbcLookup);
        return this;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConfigParams that = (ConfigParams) o;
        return withSystemEnvironment == that.withSystemEnvironment &&
                withSystemProperties == that.withSystemProperties &&
                failIfEmpty == that.failIfEmpty &&
                includeAll == that.includeAll &&
                withStdin == that.withStdin &&
                withSystemPropertiesOverride == that.withSystemPropertiesOverride &&
                Objects.equals(classLoaders, that.classLoaders) &&
                Objects.equals(reader, that.reader) &&
                Objects.equals(jdbcLookups, that.jdbcLookups) &&
                Objects.equals(settings, that.settings) &&
                Objects.equals(args, that.args) &&
                Objects.equals(directoryName, that.directoryName) &&
                Objects.equals(fileNamesWithoutSuffix, that.fileNamesWithoutSuffix) &&
                Objects.equals(fileLocations, that.fileLocations);
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

    public static class JdbcLookup {
        Connection connection;
        String statement;
        String[] params;
    }
}
