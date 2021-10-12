import org.xbib.content.settings.datastructures.PropertiesSettingsLoader;
import org.xbib.content.settings.datastructures.SettingsLoader;

module org.xbib.content.settings.datastructures {
    uses SettingsLoader;
    provides SettingsLoader with PropertiesSettingsLoader;
    exports org.xbib.content.settings.datastructures;
    requires org.xbib.datastructures.tiny;
    requires transitive org.xbib.datastructures.api;
    requires transitive java.sql;
}
