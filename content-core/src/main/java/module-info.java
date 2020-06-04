module org.xbib.content.core {
    exports org.xbib.content;
    exports org.xbib.content.io;
    exports org.xbib.content.json;
    exports org.xbib.content.settings;
    exports org.xbib.content.util.geo;
    exports org.xbib.content.util.unit;
    requires transitive com.fasterxml.jackson.core;
    provides org.xbib.content.XContent with
            org.xbib.content.json.JsonXContent;
    provides org.xbib.content.settings.SettingsLoader with
            org.xbib.content.settings.PropertiesSettingsLoader,
            org.xbib.content.json.JsonSettingsLoader;
}
