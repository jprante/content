import org.xbib.settings.SettingsLoader;
import org.xbib.settings.datastructures.json.JsonSettingsLoader;

module org.xbib.settings.datastructures.json {
    exports org.xbib.settings.datastructures.json;
    requires transitive org.xbib.settings.datastructures;
    requires org.xbib.datastructures.json.tiny;
    uses SettingsLoader;
    provides SettingsLoader with JsonSettingsLoader;
}
