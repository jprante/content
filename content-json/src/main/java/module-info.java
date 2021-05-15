import org.xbib.content.SettingsLoader;

module org.xbib.content.json {
    exports org.xbib.content.json;
    exports org.xbib.content.json.diff;
    exports org.xbib.content.json.jackson;
    exports org.xbib.content.json.mergepatch;
    exports org.xbib.content.json.patch;
    exports org.xbib.content.json.pointer;
    requires transitive org.xbib.content.core;
    requires transitive org.xbib.content.settings;
    requires transitive com.fasterxml.jackson.core;
    requires transitive com.fasterxml.jackson.databind;
    provides org.xbib.content.XContent with
            org.xbib.content.json.JsonXContent;
    provides SettingsLoader with
            org.xbib.content.json.JsonSettingsLoader;
}
