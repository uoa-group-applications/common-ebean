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
	public void loadFromProperties(Properties properties) {
		loadSettings(new SystemPropertyConfigPropertyMap())
	}

	@Override
	protected void loadDataSourceSettings(PropertiesWrapper p) {
		dataSourceConfig.loadSettings(p);
	}

}
