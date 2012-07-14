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
package org.eclipse.e4.ui.workbench.annotations.bindings;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import org.eclipse.e4.ui.model.application.commands.MCommand;

/**
 * Use this annotation to define the key-binding's associated {@link MCommand}. The annotation's value
 * must return the associated command id. 
 * @see KeyBinding
 */
@Qualifier
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface KeyBindingCommand {
	// the command id
	String value() default "";
}
