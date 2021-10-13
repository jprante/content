import org.xbib.settings.SettingsBuilder;
import org.xbib.settings.SettingsLoader;
import org.xbib.settings.content.ContentSettingsBuilder;
import org.xbib.settings.content.PropertiesSettingsLoader;

module org.xbib.settings.content {
    uses SettingsLoader;
    provides SettingsLoader with PropertiesSettingsLoader;
    uses SettingsBuilder;
    provides SettingsBuilder with ContentSettingsBuilder;
    exports org.xbib.settings.content;
    requires org.xbib.settings.api;
    requires org.xbib.content.core;
    requires org.xbib.datastructures.api;
    requires transitive org.xbib.datastructures.tiny;
}
