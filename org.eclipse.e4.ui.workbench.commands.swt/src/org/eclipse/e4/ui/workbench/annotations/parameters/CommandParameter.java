/*******************************************************************************
 * Copyright (c) 2012 Joseph Carroll and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Joseph Carroll <jdsalingerjr@gmail.com> - initial API and implementation
 ******************************************************************************/ 
package org.eclipse.e4.ui.workbench.annotations.parameters;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import org.eclipse.e4.ui.model.application.commands.MCommandParameter;

/**
 * Use this annotation to tag element types that define a {@link MCommandParameter}. The element type
 * may be either a <tt>CLASS</tt> or <tt>FIELD</tt> type. The annotation's value must 
 * return the parameter's id, otherwise the parameter is not created.  
 * @see CommandParameterName
 * @see CommandParameterOptional
 * @see CommandParameterTags
 * @see CommandParameterType
 * @required The ID is a required value.
 */
@Qualifier
@Documented
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandParameter {
	// the command parameter id
	String value() default "";
}
