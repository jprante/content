import org.xbib.settings.SettingsBuilder;
import org.xbib.settings.SettingsLoader;

module org.xbib.settings.api {
    exports org.xbib.settings;
    uses SettingsBuilder;
    uses SettingsLoader;
    requires transitive org.xbib.datastructures.api;
    requires transitive java.sql;
}
