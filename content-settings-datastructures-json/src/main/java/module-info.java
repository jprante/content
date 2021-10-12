import org.xbib.content.settings.datastructures.SettingsLoader;
import org.xbib.content.settings.datastructures.json.JsonSettingsLoader;

module org.xbib.content.settings.datastructures.json {
    exports org.xbib.content.settings.datastructures.json;
    requires transitive org.xbib.content.settings.datastructures;
    requires org.xbib.datastructures.json.tiny;
    provides SettingsLoader with JsonSettingsLoader;
}
