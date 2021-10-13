import org.xbib.content.SettingsBuilder;

module org.xbib.content.api {
    exports org.xbib.content;
    requires transitive org.xbib.datastructures.api;
    requires java.sql;
    uses SettingsBuilder;
}
