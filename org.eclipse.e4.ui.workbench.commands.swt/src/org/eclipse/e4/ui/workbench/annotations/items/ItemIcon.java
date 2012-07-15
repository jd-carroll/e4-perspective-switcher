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
 * Use this annotation to define the icon uri for a {@link MDirectMenuItem}, {@link MDirectToolItem} 
 * {@link MHandledMenuItem}, or {@link MTHandledToolItem}. The annotation returns the icon uri.
 * <p>
 * Note: The icon uri must meet platform naming stands for resource uri's. The processor will perform
 * a simple check if the uri has the string <tt>platform:/plugin/</tt>. If the resource indicator
 * is not present, the processor will prefix the string to the uri regardless of the string's content. 
 * No other processing or handling of the uri will be performed.
 * </p>
 * @see DirectMenuItem
 * @see DirectToolItem
 * @see HandledMenuItem
 * @see HandledToolItem
 */
@Qualifier
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ItemIcon {
	// the item icon uri
	String value() default "";
}

