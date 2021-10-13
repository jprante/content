package org.xbib.settings.datastructures.json;

import org.xbib.settings.datastructures.AbstractSettingsLoader;
import org.xbib.datastructures.api.DataStructure;
import org.xbib.datastructures.json.tiny.Json;
import java.util.Set;

public class JsonSettingsLoader extends AbstractSettingsLoader {

    @Override
    public DataStructure dataStructure() {
        return new Json();
    }

    @Override
    public Set<String> suffixes() {
        return Set.of("json");
    }

    @Override
    public boolean canLoad(String source) {
        return source.indexOf('{') != -1 && source.indexOf('}') != -1;
    }
}
