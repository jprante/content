import org.xbib.settings.SettingsLoader;
import org.xbib.settings.datastructures.yaml.YamlSettingsLoader;

module org.xbib.settings.datastructures.yaml {
    exports org.xbib.settings.datastructures.yaml;
    requires transitive org.xbib.settings.datastructures;
    requires org.xbib.datastructures.yaml.tiny;
    uses SettingsLoader;
    provides SettingsLoader with YamlSettingsLoader;
}
