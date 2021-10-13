import org.xbib.settings.SettingsBuilder;
import org.xbib.settings.SettingsLoader;
import org.xbib.settings.datastructures.DatastructureSettingsBuilder;
import org.xbib.settings.datastructures.PropertiesSettingsLoader;

module org.xbib.settings.datastructures {
    uses SettingsLoader;
    provides SettingsLoader with PropertiesSettingsLoader;
    uses SettingsBuilder;
    provides SettingsBuilder with DatastructureSettingsBuilder;
    exports org.xbib.settings.datastructures;
    requires transitive org.xbib.settings.api;
    requires org.xbib.datastructures.tiny;
    requires transitive org.xbib.datastructures.api;
}
