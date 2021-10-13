package org.xbib.settings.datastructures.yaml;

import org.xbib.settings.datastructures.AbstractSettingsLoader;
import org.xbib.datastructures.api.DataStructure;
import org.xbib.datastructures.yaml.tiny.Yaml;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class YamlSettingsLoader extends AbstractSettingsLoader {

    @Override
    public DataStructure dataStructure() {
        return new Yaml();
    }

    @Override
    public Set<String> suffixes() {
        return Set.of("yml", "yaml");
    }

    @Override
    public boolean canLoad(String source) {
        return source.indexOf(':') != -1;
    }

    @Override
    public Map<String, String> load(String source) throws IOException {
        // replace tabs with whitespace (yaml does not accept tabs, but many users might use it still...)
        return super.load(source.replace("\t", "  "));
    }
}
