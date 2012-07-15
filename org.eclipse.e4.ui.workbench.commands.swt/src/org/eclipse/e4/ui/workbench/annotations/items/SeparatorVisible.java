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
package org.eclipse.e4.ui.workbench.annotations.items;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import org.eclipse.e4.ui.model.application.ui.menu.MMenuSeparator;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBarSeparator;

/**
 * Use this annotation to tag element types that define whether the {@link MMenuSeparator} or
 * {@link MToolBarSeparator} is visible. The element type may be either a <tt>CLASS</tt> 
 * or <tt>FIELD</tt> type. The annotation's value returns the optionality.
 * @see MenuSeparator
 * @see ToolbarSeparator
 */
@Qualifier
@Documented
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SeparatorVisible {
	// whether the separator is visible
	boolean value() default false;
}
