module org.xbib.content.yaml {
  exports org.xbib.content.yaml;
  requires transitive org.xbib.content.core;
  requires transitive com.fasterxml.jackson.dataformat.yaml;
  provides org.xbib.content.XContent with
          org.xbib.content.yaml.YamlXContent;
  provides org.xbib.content.settings.SettingsLoader with
          org.xbib.content.yaml.YamlSettingsLoader;
}