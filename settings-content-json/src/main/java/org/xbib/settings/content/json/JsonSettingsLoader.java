package org.xbib.settings.content.json;

import org.xbib.content.XContent;
import org.xbib.content.json.JsonXContent;
import org.xbib.settings.content.AbstractSettingsLoader;
import java.util.Set;

/**
 * Settings loader that loads (parses) the settings in a json format by flattening them
 * into a map.
 */
public class JsonSettingsLoader extends AbstractSettingsLoader {

    @Override
    public XContent content() {
        return JsonXContent.jsonContent();
    }

    @Override
    public Set<String> suffixes() {
        return Set.of("json");
    }

}
