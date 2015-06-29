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
		loadSettings(new SystemPropertyWrapper())
	}

	@Override
	public boolean isDefaultServer() {
		return true
	}

	@Override
	public void loadFromProperties(Properties properties) {
		loadSettings(new SystemPropertyWrapper())
	}

	@Override
	protected void loadDataSourceSettings(PropertiesWrapper p) {
		dataSourceConfig.loadSettings(p);
	}

}
