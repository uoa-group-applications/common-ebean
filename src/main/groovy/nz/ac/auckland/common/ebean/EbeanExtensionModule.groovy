package nz.ac.auckland.common.ebean

import com.avaje.ebean.ExpressionList
import com.avaje.ebean.QueryIterator

/**
 * This adds methods to Groovy that are able to be used from @CompileStatic and its kin (as well as dynamic groovy)
 * author: Richard Vowles - http://gplus.to/RichardVowles
 */
class EbeanExtensionModule {
	/**
	 * As an iterator is a resource and *must* be closed, we use this mechanism to allow for a clean
	 * cleaning up
	 *
	 * @param expressionList
	 * @param iteratorClosure
	 */
	public static eachEntity(ExpressionList expressionList, Closure iteratorClosure) {
		QueryIterator queryIterator = expressionList.findIterate()

		try {
			while ( queryIterator.hasNext() ) {
				iteratorClosure.call( queryIterator.next() )
			}
		} finally {
			if (queryIterator != null) {
				queryIterator.close()
			}
		}
	}

	/**
	 * This kind of iterator expects the closure will return a boolean value, that if false, will cause the iterator
	 * to exit.
	 *
	 * @param expressionList
	 * @param iteratorClosure
	 */
	public static eachEntityBoolean(ExpressionList expressionList, Closure iteratorClosure) {
		QueryIterator queryIterator = expressionList.findIterate()

		boolean keepGoing = true

		try {
			while ( keepGoing && queryIterator.hasNext()) {
				keepGoing = iteratorClosure.call( queryIterator.next() )
			}
		} finally {
			if (queryIterator != null) {
				queryIterator.close()
			}
		}
	}
}
