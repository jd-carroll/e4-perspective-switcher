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
package org.eclipse.e4.ui.workbench.annotations.commands;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import org.eclipse.e4.ui.model.application.commands.MCommand;
import org.eclipse.e4.ui.model.application.commands.MCommandParameter;
import org.eclipse.e4.ui.workbench.annotations.parameters.CommandParameter;

/**
 * Use this annotation to tag types that define a {@link MCommand}. The annotation's
 * value must return the command's id, otherwise the command is not created.
 * <p>
 * Instances of this type also accept one or more optional {@link MCommandParameter}. Each command 
 * parameter must be defined as a separate field tagged with the annotation {@link CommandParameter}. 
 * The field must be of type <code>String[]</code>.
 * </p>
 * @see CommandCategory
 * @see CommandDescription
 * @see CommandName
 * @see CommandTags
 * @see CommandParameter
 * @required The ID is a required value. 
 */
@Qualifier
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
	// the command id
	String value() default "";
}
