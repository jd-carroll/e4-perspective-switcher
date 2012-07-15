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
package org.eclipse.e4.ui.workbench.annotations.contributions;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import org.eclipse.e4.ui.model.application.ui.menu.MMenuContribution;
import org.eclipse.e4.ui.workbench.annotations.items.DirectMenuItem;
import org.eclipse.e4.ui.workbench.annotations.items.HandledMenuItem;
import org.eclipse.e4.ui.workbench.annotations.items.MenuSeparator;
import org.eclipse.e4.ui.workbench.annotations.items.ToolbarSeparator;
import org.eclipse.e4.ui.workbench.annotations.menus.Menu;

/**
 * Use this annotation to tag types that define a {@link MMenuContribution}. The annotation's
 * value must return the contribution's id, otherwise the contribution is not created.
 * @see Menu
 * @see DirectMenuItem
 * @see HandledMenuItem
 * @see MenuSeparator
 * @see ToolbarSeparator
 * @required The ID is a required value. 
 */
@Qualifier
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MenuContribution {
	// the menu contribution id
	String value() default "";
}
