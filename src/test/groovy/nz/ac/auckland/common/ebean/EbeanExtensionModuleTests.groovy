package nz.ac.auckland.common.ebean

import com.avaje.ebean.ExpressionList
import com.avaje.ebean.QueryIterator
import groovy.transform.CompileStatic
import org.junit.Test

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

/**
 *
 * @author: Richard Vowles - https://plus.google.com/+RichardVowles
 */
@CompileStatic
class EbeanExtensionModuleTests {
	@Test
	public void myTest() {
		ExpressionList el = mock(ExpressionList)
		QueryIterator qi = mock(QueryIterator)
		when(el.findIterate()).thenReturn(qi)
		when(qi.hasNext()).thenReturn(false)

		el.eachEntity {
		}
	}

}
