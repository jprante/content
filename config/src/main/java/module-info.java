import org.xbib.config.ConfigLogger;
import org.xbib.config.NullConfigLogger;
import org.xbib.config.SystemConfigLogger;
import org.xbib.settings.SettingsLoader;

module org.xbib.config {
    exports org.xbib.config;
    uses ConfigLogger;
    uses SettingsLoader;
    provides ConfigLogger with NullConfigLogger, SystemConfigLogger;
    requires org.xbib.settings.api;
    requires transitive org.xbib.settings.datastructures;
}
