/**
 * 
 */
package uk.ac.imperial.presage2.core.util.random;

/**
 * Factory Constructor for {@link Random}.
 * 
 * @author Sam Macbeth
 *
 */
class RandomFactory {

	/**
	 * <p>Creates an instance of {@link Random}</p>
	 * <p>Currently simply uses the default constructor, in future we will
	 * allow it to pull a previously specified random seed to initialise the 
	 * Random with</p>
	 * @return
	 */
	protected static Random create() {
		return new Random();
	}
	
}
