import org.xbib.content.SettingsLoader;
import org.xbib.content.properties.PropertiesSettingsLoader;

module org.xbib.content.core {
    uses org.xbib.content.XContent;
    uses SettingsLoader;
    exports org.xbib.content.io;
    exports org.xbib.content.properties;
    exports org.xbib.content.util.geo;
    exports org.xbib.content.util.unit;
    exports org.xbib.content.core;
    requires transitive org.xbib.content;
    requires transitive org.xbib.datastructures.tiny;
    requires transitive com.fasterxml.jackson.core;
    provides SettingsLoader with PropertiesSettingsLoader;
}
