import org.xbib.content.config.ConfigLogger;
import org.xbib.content.SettingsLoader;

module org.xbib.content.config {
    exports org.xbib.content.config;
    uses ConfigLogger;
    uses SettingsLoader;
    provides ConfigLogger with org.xbib.content.config.SystemConfigLogger;
    requires org.xbib.content.api;
    requires transitive org.xbib.content.settings.datastructures;
}
