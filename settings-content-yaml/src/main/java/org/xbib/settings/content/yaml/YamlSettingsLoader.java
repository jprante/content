package org.xbib.settings.content.yaml;

import org.xbib.content.XContent;
import org.xbib.content.yaml.YamlXContent;
import org.xbib.settings.content.AbstractSettingsLoader;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * Settings loader that loads (parses) the settings in a yaml format by flattening them
 * into a map.
 */
public class YamlSettingsLoader extends AbstractSettingsLoader {

    private static final Set<String> YAML_SUFFIXES = Set.of("yml", "yaml");

    @Override
    public XContent content() {
        return YamlXContent.yamlContent();
    }

    @Override
    public Set<String> suffixes() {
        return YAML_SUFFIXES;
    }

    @Override
    public Map<String, String> load(String source) throws IOException {
        // replace tabs with whitespace (yaml does not accept tabs, but many users might use it still...)
        return super.load(source.replace("\t", "  "));
    }

    @Override
    public boolean canLoad(String source) {
        return source.indexOf(':') != -1;
    }
}
