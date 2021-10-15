import org.xbib.config.ConfigLogger;
import org.xbib.config.SystemConfigLogger;
import org.xbib.settings.SettingsLoader;

module org.xbib.config {
    exports org.xbib.config;
    uses ConfigLogger;
    uses SettingsLoader;
    provides ConfigLogger with SystemConfigLogger;
    requires org.xbib.settings.api;
    requires transitive org.xbib.settings.datastructures;
}
