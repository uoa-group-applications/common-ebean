package nz.ac.auckland.common.ebean

import com.avaje.ebean.config.PropertiesWrapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author Kefeng Deng (deng@51any.com)
 */
public class SystemPropertyWrapper extends PropertiesWrapper {

	public static final Logger log = LoggerFactory.getLogger(SystemPropertyWrapper)

	/**
	 * Default properties for University
	 */
	public final Map<String, String> UOA_DEFAULTS = [
			"databaseDriver": "oracle.jdbc.driver.OracleDriver",
			"minConnections": "1",
			"maxConnections": "25",
			"heartbeatsql"  : "select count(*) from dual",
			"isolationlevel": "read_committed",
			"classpathreader": "nz.ac.auckland.common.ebean.BatheClassPathSearch"
	]

	public static final String DEFAULT_PREFIX_EBEAN = "ebean"

	public static final String DEFAULT_PREFIX_UOA = "dataSource"

	public static final String DEFAULT_SERVER = "db"

	/**
	 * The property key for jars
	 */
	public static final String JARS = "jars"

	/**
	 * The property key for search.jars
	 */
	public static final String SEARCH_JARS = "search.jars"

	/**
	 * Default Group ID for University Module
	 */
	public static final String DEFAULT_DOMAIN_PACKAGE = "domain"

	public SystemPropertyWrapper() {
		super(DEFAULT_PREFIX_UOA, DEFAULT_SERVER, new Properties())
		propertyMap.putEvalAll(UOA_DEFAULTS)
		properties.putAll(UOA_DEFAULTS)
	}

	@Override
	public SystemPropertyWrapper withPrefix(String prefix) {
		return new SystemPropertyWrapper()
	}

	@Override
	public String get(String key, String defaultValue) {

		if (key == null) {
			return defaultValue;
		}

		String value = getValueWithPrefix(DEFAULT_PREFIX_UOA, key)

		if (value == null) {
			// Try to compatible with previous configuration
			value = getValueWithPrefix(DEFAULT_PREFIX_EBEAN, key)
		}

		// This is an additional behaviour to automatically
		if (JARS.equals(key) && value == null) {
			value = getJarsWithUniversityGroupId()
		}

		if (log.isDebugEnabled() && SEARCH_JARS.equals(key)) {
			log.debug("Ebean will scan entity classes from : {}", value == null ? defaultValue : value)
		}

		return value == null ? defaultValue : value;
	}

	/**
	 * Get a value by a combination of prefix and key
	 */
	public String getValueWithPrefix(String proPrefix, String key) {

		// System Property is priority
		String value = getValueFromMapWithPrefix(proPrefix, System.properties, key);

		if (value == null) {
			value = getValueFromMapWithPrefix(proPrefix, propertyMap.asProperties(), key);
		}

		if (value == null) {
			value = getValueFromMapWithPrefix(proPrefix, propertyMap.asProperties(), key.toLowerCase());
		}

		return value;
	}

	/**
	 * get a value from map by the combination of prefix, server name, and key
	 */
	public String getValueFromMapWithPrefix(String propertyPrefix, Map map, String key) {
		String value = null;
		if (serverName != null && prefix != null) {
			value = map.get(propertyPrefix + "." + serverName + "." + key);
		}
		if (value == null && prefix != null) {
			value = map.get(propertyPrefix + "." + key);
		}
		if (value == null) {
			value = map.get(key);
		}
		return value;
	}

	/**
	 * @return a collection of JARs which needs to be scanned and enhanced
	 */
	protected String getJarsWithUniversityGroupId() {
		StringBuilder jars = new StringBuilder()
		List<URL> domainPackages = findAllDomainPackages(this.class.classLoader as URLClassLoader)
		if (domainPackages) {
			buildSearchJars(jars, domainPackages)
		}
		log.info("Scan all packages for Entity : {}", jars.toString())
		return jars.toString()
	}

	/**
	 * Find and return a collection of package in current class loader which contains "domain" in path
	 *
	 * @param loader is the current class loader
	 * @return a collection of URLs
	 */
	protected List<URL> findAllDomainPackages(URLClassLoader loader) {
		return loader.URLs.findAll { URL url ->
			return url.path?.toLowerCase().contains(DEFAULT_DOMAIN_PACKAGE)
		}
	}

	/**
	 * Build search.jars based on all domain packages
	 *
	 * @param jars is the search.jars value
	 * @param packages is a collection of domain packages
	 */
	protected void buildSearchJars(StringBuilder jars, List<URL> packages) {
		packages.each { URL url ->
			String fullPath = url.path
			log.debug("Checking JAR for ClasspathSearch : {}", fullPath)
			if ("jar".equalsIgnoreCase(url.protocol)) {
				if (fullPath.endsWith("!/")) {
					fullPath = fullPath.substring(0, fullPath.length() - 2)
				} else if (fullPath.endsWith("/")) {
					fullPath = fullPath.substring(0, fullPath.length() - 1)
				}
			}
			int position = fullPath.lastIndexOf('/')
			fullPath = fullPath.substring(position + 1)
			if (fullPath.trim().length() > 0) {
				log.debug("Registering JAR for ClasspathSearch : {}", fullPath)
				jars.append(fullPath).append(';')
			}
		}
	}

}
