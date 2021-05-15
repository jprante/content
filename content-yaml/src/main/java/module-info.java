import org.xbib.content.SettingsLoader;
import org.xbib.content.XContent;

module org.xbib.content.yaml {
  exports org.xbib.content.yaml;
  requires transitive org.xbib.content.core;
  requires transitive org.xbib.content.settings;
  requires transitive com.fasterxml.jackson.dataformat.yaml;
  provides XContent with org.xbib.content.yaml.YamlXContent;
  provides SettingsLoader with org.xbib.content.yaml.YamlSettingsLoader;
}
