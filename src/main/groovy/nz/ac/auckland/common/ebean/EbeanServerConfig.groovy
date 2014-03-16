package nz.ac.auckland.common.ebean

import com.avaje.ebean.config.DataSourceConfig
import com.avaje.ebean.config.GlobalProperties
import com.avaje.ebean.config.ServerConfig
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**

 * author: Richard Vowles - http://gplus.to/RichardVowles
 */
@CompileStatic
class EbeanServerConfig extends ServerConfig {
  private static final Logger log = LoggerFactory.getLogger(EbeanServerConfig)

  @Override
  boolean isDefaultServer() {
    return true;
  }

  class SystemPropertyConfigPropertyMap implements GlobalProperties.PropertySource {
	  protected Map<String, String> ourDefaults = [
		  "databaseDriver":"oracle.jdbc.driver.OracleDriver",
		  "minConnections":"1",
		  "maxConnections":"25",
		  "heartbeatsql":"select count(*) from dual",
		  "isolationlevel":"read_committed"
	  ]

    @Override
    String getServerName() {
      return "default";
    }

    private String getKey(String key, String defaultValue) {
      String val = System.getProperty("dataSource." + key)

      if (val) {
        log.trace("ebean system property key ${key} : ${val}")
        return val
      }

      val = ourDefaults[key]

      if (val) {
        log.trace("ebean our defaults key ${key} : ${val}")
        return val
      }

      return defaultValue
    }

    @Override
    String get(String key, String defaultValue) {
      return getKey(key, defaultValue)
    }

    @Override
    int getInt(String key, int defaultValue) {
      String val = getKey(key, null)

      if (!val) return defaultValue

      return Integer.parseInt(val)
    }

    @Override
    boolean getBoolean(String key, boolean defaultValue) {
      String val = getKey(key, Boolean.toString(defaultValue))

      return Boolean.parseBoolean(val)
    }

    @Override
    def <T extends Enum<T>> T getEnum(Class<T> enumType, String key, T defaultValue) {
      String level = get(key, defaultValue.name());
      return Enum.valueOf(enumType, level.toUpperCase());
    }
  }


  public EbeanServerConfig() {
    GlobalProperties.setSkipPrimaryServer(true) // otherwise ebean tries to create a server with no config before we get to set one up

    GlobalProperties.PropertySource properties = new SystemPropertyConfigPropertyMap()

    DataSourceConfig dsConfig = new DataSourceConfig()
    dsConfig.loadSettingsCustomPrefix("", properties)

    setDataSourceConfig(dsConfig)

    loadSettings(properties)
  }

  @Override
  protected void loadDataSourceSettings(GlobalProperties.PropertySource p) {
    // do not reload
  }
}
