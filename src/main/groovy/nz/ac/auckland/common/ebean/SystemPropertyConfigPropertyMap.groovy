package nz.ac.auckland.common.ebean

import com.avaje.ebean.config.PropertiesWrapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author Kefeng Deng (deng@51any.com)
 */
public class SystemPropertyConfigPropertyMap extends PropertiesWrapper {

	public static final Logger log = LoggerFactory.getLogger(SystemPropertyConfigPropertyMap)

	public final Map<String, String> UOA_DEFAULTS = [
			"databaseDriver": "oracle.jdbc.driver.OracleDriver",
			"minConnections": "1",
			"maxConnections": "25",
			"heartbeatsql"  : "select count(*) from dual",
			"isolationlevel": "read_committed"
	]

	public static final String DEFAULT_PREFIX = "dataSource"

	public static final String DEFAULT_SERVER = "db"

	public static final String JARS = "jars"

	public static final String DEFAULT_PACKAGE = "/nz/ac/auckland/"

	public SystemPropertyConfigPropertyMap() {
		super(DEFAULT_PREFIX, DEFAULT_SERVER, new Properties())
		propertyMap.putEvalAll(UOA_DEFAULTS)
		properties.putAll(UOA_DEFAULTS)

	}

	@Override
	public SystemPropertyConfigPropertyMap withPrefix(String prefix) {
		return new SystemPropertyConfigPropertyMap()
	}

	@Override
	public String get(String key, String defaultValue) {

		if (key == null) {
			return defaultValue;
		}

		// System Property is priority
		String value = getValueFromMap(System.properties, key);

		if (value == null) {
			value = getValueFromMap(propertyMap.asProperties(), key);
		}

		if (value == null) {
			value = getValueFromMap(propertyMap.asProperties(), key.toLowerCase());
		}

		if (JARS.equals(key) && value == null) {
			value = getJars()
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

	/**
	 * @return a collection of JARs which needs to be scanned and enhanced
	 */
	private String getJars() {
		StringBuilder jars = new StringBuilder()
		(this.class.getClassLoader() as URLClassLoader).getURLs().findAll { URL url ->
			return url.path?.contains(DEFAULT_PACKAGE)
		}?.each { URL url ->
			if (url.path) {
				log.trace("Add JARs : {}", url.path)
				String path = url.path
				if (path.endsWith('!/')) {
					// Embedded JAR
					path = url.path.substring(0, url.path.length()-2)
				}
				int position = path.lastIndexOf('/')
				jars.append(path.substring(position+1)).append(' ')
			}
		}
		return jars.toString()
	}

}
