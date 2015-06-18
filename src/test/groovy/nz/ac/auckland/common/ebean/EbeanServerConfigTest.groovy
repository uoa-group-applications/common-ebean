package nz.ac.auckland.common.ebean

import org.junit.Test

/**
 * @author Kefeng Deng (k.deng@auckland.ac.nz)
 */
class EbeanServerConfigTest {

	@Test
	public void testDefault() {
		EbeanServerConfig serverConfig = new EbeanServerConfig()
		assert serverConfig.dataSourceConfig.heartbeatSql == "select count(*) from dual"
	}

}
