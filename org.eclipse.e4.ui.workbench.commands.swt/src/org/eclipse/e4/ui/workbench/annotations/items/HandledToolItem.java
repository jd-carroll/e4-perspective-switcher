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

import org.eclipse.e4.ui.model.application.ui.menu.MHandledToolItem;

/**
 * Use this annotation to tag types that define a {@link MHandledToolItem}. The value returned by the 
 * annotation is the id for the handler. Supplying an id is not required but strongly encouraged.
 * @see ItemInteraction
 * @see ItemLabel
 * @see ItemMnemonics
 * @see ItemTooltip
 * @see ItemIcon
 * @see ItemEnabled
 * @see ItemSelected
 * @see ItemRender
 * @see ItemVisible
 * @see ItemAccessability
 * @see ItemTags
 */
@Qualifier
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface HandledToolItem {
	// the handled tool item id
	String value() default "";
}
