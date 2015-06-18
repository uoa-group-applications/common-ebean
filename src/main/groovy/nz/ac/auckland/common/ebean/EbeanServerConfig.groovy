package nz.ac.auckland.common.ebean

import com.avaje.ebean.config.PropertiesWrapper
import com.avaje.ebean.config.ServerConfig
import groovy.transform.CompileStatic

/**
 * @author: Richard Vowles - http://gplus.to/RichardVowles
 */
@CompileStatic
class EbeanServerConfig extends ServerConfig {

	/**
	 * Default constructor
	 */
	public EbeanServerConfig() {
		loadSettings(new SystemPropertyConfigPropertyMap())
	}

	@Override
	public boolean isDefaultServer() {
		return true
	}

	@Override
	protected void loadDataSourceSettings(PropertiesWrapper p) {
		dataSourceConfig.loadSettings(p)
	}

	class SystemPropertyConfigPropertyMap extends PropertiesWrapper {

		public static final Map<String, String> UOA_DEFAULTS = [
				"databaseDriver": "oracle.jdbc.driver.OracleDriver",
				"minConnections": "1",
				"maxConnections": "25",
				"heartbeatsql"  : "select count(*) from dual",
				"isolationlevel": "read_committed"
		]

		public static final String DEFAULT_PREFIX = "dataSource"

		public static final String DEFAULT_SERVER = "db"

		public SystemPropertyConfigPropertyMap() {
			super(DEFAULT_PREFIX, DEFAULT_SERVER, new Properties())
			propertyMap.putEvalAll(UOA_DEFAULTS)
			properties.putAll(UOA_DEFAULTS)
		}

		@Override
		public String get(String key, String defaultValue) {

			if (key == null) {
				return defaultValue;
			}

			String value = getValueFromMap(propertyMap.asProperties(), key);

			if (value == null) {
				value = getValueFromMap(propertyMap.asProperties(), key.toLowerCase());
			}

			if (value == null) {
				value = getValueFromMap(System.properties, key);
			}

			if (value == null) {
				value = getValueFromMap(System.properties, key.toLowerCase());
			}

			return value == null ? defaultValue : value;
		}

		/**
		 * get a value from map by key
		 */
		public String getValueFromMap(Map map, String key) {
			String value = null;
			if (serverName != null && prefix != null) {
				value = map.get(prefix + "." + serverName + "." + key);
			}
			if (value == null && prefix != null) {
				value = map.get(prefix + "." + key);
			}
			if (value == null) {
				value = map.get(key);
			}
			return value;
		}

	}

}
