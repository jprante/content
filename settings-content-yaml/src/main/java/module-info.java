import org.xbib.settings.SettingsLoader;
import org.xbib.settings.content.yaml.YamlSettingsLoader;

module org.xbib.settings.content.yaml {
    exports org.xbib.settings.content.yaml;
    requires transitive org.xbib.settings.content;
    requires transitive org.xbib.content.yaml;
    requires org.xbib.settings.api;
    uses SettingsLoader;
    provides SettingsLoader with YamlSettingsLoader;
}
