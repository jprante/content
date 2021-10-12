module org.xbib.content.settings {
    uses org.xbib.content.SettingsLoader;
    exports org.xbib.content.settings;
    requires org.xbib.content.core;
    requires transitive org.xbib.datastructures.tiny;
}
