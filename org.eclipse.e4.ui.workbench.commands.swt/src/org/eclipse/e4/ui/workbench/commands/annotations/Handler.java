package org.eclipse.e4.ui.workbench.commands.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

/**
 * Specifies that the target is a command handler.  You must specify a 
 * handler id in the value.
 * 
 * @author jcarroll
 *
 */
@Qualifier
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Handler {
	String value() default ""; // the handler ID
}
