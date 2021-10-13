import org.xbib.content.SettingsBuilder;
import org.xbib.content.SettingsLoader;
import org.xbib.content.settings.ContentSettingsBuilder;
import org.xbib.content.settings.PropertiesSettingsLoader;

module org.xbib.content.settings {
    uses SettingsLoader;
    provides SettingsLoader with PropertiesSettingsLoader;
    uses SettingsBuilder;
    provides SettingsBuilder with ContentSettingsBuilder;
    exports org.xbib.content.settings;
    requires org.xbib.content.core;
    requires org.xbib.datastructures.api;
    requires transitive org.xbib.datastructures.tiny;
}
