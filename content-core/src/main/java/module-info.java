import org.xbib.content.XContent;
import org.xbib.content.SettingsLoader;
import org.xbib.content.properties.PropertiesSettingsLoader;

module org.xbib.content.core {
    uses XContent;
    uses SettingsLoader;
    exports org.xbib.content.io;
    exports org.xbib.content.properties;
    exports org.xbib.content.util.geo;
    exports org.xbib.content.util.unit;
    exports org.xbib.content.core;
    requires transitive org.xbib.content;
    requires com.fasterxml.jackson.core;
    provides SettingsLoader with PropertiesSettingsLoader;
}
