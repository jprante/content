import org.xbib.content.settings.datastructures.SettingsLoader;
import org.xbib.content.settings.datastructures.yaml.YamlSettingsLoader;

module org.xbib.content.settings.datastructures.yaml {
    exports org.xbib.content.settings.datastructures.yaml;
    requires transitive org.xbib.content.settings.datastructures;
    requires org.xbib.datastructures.yaml.tiny;
    provides SettingsLoader with YamlSettingsLoader;
}
