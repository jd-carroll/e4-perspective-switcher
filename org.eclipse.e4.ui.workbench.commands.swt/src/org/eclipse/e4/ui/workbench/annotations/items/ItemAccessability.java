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

import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectToolItem;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;

/**
 * Use this annotation to define the accessability phrase for a {@link MDirectMenuItem}, {@link MDirectToolItem} 
 * {@link MHandledMenuItem}, or {@link MTHandledToolItem}. The annotation returns the accessability phrase.
 * @see DirectMenuItem
 * @see DirectToolItem
 * @see HandledMenuItem
 * @see HandledToolItem
 */
@Qualifier
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ItemAccessability {
	// the accessability phrase
	String value() default "";
}
