package uk.ac.imperial.presage2.core.simulator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Method annotation to mark that a method should be called each timestep by the
 * scheduler, <b>before</b> {@link Step} functions. The method may take a single
 * integer argument representing the current timestep number and return void. An
 * optional <code>nice</code> parameter can be set to specify priority in the
 * task queue.
 * 
 * @author Sam Macbeth
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PreStep {
	public int nice() default 0;
}
