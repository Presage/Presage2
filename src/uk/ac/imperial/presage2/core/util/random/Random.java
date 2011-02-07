/**
 * 
 */
package uk.ac.imperial.presage2.core.util.random;

import java.util.UUID;

import com.google.inject.Singleton;

/**
 * This is a wrapper for {@link java.util.Random} to provide static
 * access to most of it's methods via a singleton, and to control the initial
 * random seed.
 * 
 * 
 * @author Sam Macbeth
 *
 */
@Singleton
public class Random extends java.util.Random {

	private static final long serialVersionUID = 1L;
	
	private static Random INSTANCE = null;
	
	protected Random(long seed) {
		super(seed);
	}
	
	protected Random() {
		super();
	}
	
	/**
	 * Get the instance of {@link Random}.
	 * @return
	 */
	public static synchronized Random getInstance() {
		if(INSTANCE == null) {
			INSTANCE = RandomFactory.create();
		}
		return INSTANCE;
	}
	
	/**
	 * Wrapper for {@link UUID#randomUUID()}
	 * @return
	 */
	public static UUID randomUUID() {
		return UUID.randomUUID();
	}
	
	/**
	 * Wrapper for {@link java.util.Random#nextInt()}
	 * @return
	 */
	public static int randomInt() {
		return Random.getInstance().nextInt();
	}
	
	/**
	 * Wrapper for {@link java.util.Random#nextInt(int)}
	 * @return
	 */
	public static int randomInt(int n) {
		return Random.getInstance().nextInt(n);
	}
	
	/**
	 * Wrapper for {@link java.util.Random#nextDouble()}
	 * @return
	 */
	public static double randomDouble() {
		return Random.getInstance().nextDouble();
	}
	
}
