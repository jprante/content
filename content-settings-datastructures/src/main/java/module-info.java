import org.xbib.content.SettingsBuilder;
import org.xbib.content.SettingsLoader;
import org.xbib.content.settings.datastructures.DatastructureSettingsBuilder;
import org.xbib.content.settings.datastructures.PropertiesSettingsLoader;

module org.xbib.content.settings.datastructures {
    uses SettingsLoader;
    provides SettingsLoader with PropertiesSettingsLoader;
    uses SettingsBuilder;
    provides SettingsBuilder with DatastructureSettingsBuilder;
    exports org.xbib.content.settings.datastructures;
    requires transitive  org.xbib.content.api;
    requires org.xbib.datastructures.tiny;
    requires transitive org.xbib.datastructures.api;
}
