import org.xbib.content.SettingsLoader;
import org.xbib.content.settings.datastructures.json.JsonSettingsLoader;

module org.xbib.content.settings.datastructures.json {
    exports org.xbib.content.settings.datastructures.json;
    requires transitive org.xbib.content.settings.datastructures;
    requires org.xbib.datastructures.json.tiny;
    uses SettingsLoader;
    provides SettingsLoader with JsonSettingsLoader;
}
