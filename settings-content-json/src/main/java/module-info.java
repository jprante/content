import org.xbib.settings.SettingsLoader;
import org.xbib.settings.content.json.JsonSettingsLoader;

module org.xbib.settings.content.json {
    exports org.xbib.settings.content.json;
    requires transitive org.xbib.settings.content;
    requires org.xbib.content.api;
    requires org.xbib.content.json;
    requires org.xbib.settings.api;
    uses SettingsLoader;
    provides SettingsLoader with JsonSettingsLoader;
}
