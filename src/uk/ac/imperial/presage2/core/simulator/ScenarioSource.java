/**
 * 
 */
package uk.ac.imperial.presage2.core.simulator;

import com.google.inject.BindingAnnotation;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

/**
 * 
 * <p>Binding annotation for the database connection provided to the Simulator.</p>
 * 
 * <p>By using this we allow a separate binding to be used for databases used within plugins etc inside the simulation.</p>
 * 
 * @author Sam Macbeth
 *
 */

@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
public @interface ScenarioSource {

}
