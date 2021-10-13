import org.xbib.content.config.ConfigLogger;
import org.xbib.settings.SettingsLoader;

module org.xbib.content.config {
    exports org.xbib.content.config;
    uses ConfigLogger;
    uses SettingsLoader;
    provides ConfigLogger with org.xbib.content.config.SystemConfigLogger;
    requires org.xbib.settings.api;
    requires transitive org.xbib.settings.datastructures;
}
