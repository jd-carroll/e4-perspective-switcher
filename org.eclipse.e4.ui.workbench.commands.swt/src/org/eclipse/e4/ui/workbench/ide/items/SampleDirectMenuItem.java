/*******************************************************************************
 * Copyright (c) 2012 Joseph Carroll.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Joseph Carroll <jdsalingerjr@gmail.com> - initial API and implementation
 ******************************************************************************/ 
package org.eclipse.e4.ui.workbench.ide.items;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.menu.ItemType;
import org.eclipse.e4.ui.workbench.annotations.items.DirectMenuItem;
import org.eclipse.e4.ui.workbench.annotations.items.ItemAccessability;
import org.eclipse.e4.ui.workbench.annotations.items.ItemEnabled;
import org.eclipse.e4.ui.workbench.annotations.items.ItemIcon;
import org.eclipse.e4.ui.workbench.annotations.items.ItemInteraction;
import org.eclipse.e4.ui.workbench.annotations.items.ItemLabel;
import org.eclipse.e4.ui.workbench.annotations.items.ItemMnemonics;
import org.eclipse.e4.ui.workbench.annotations.items.ItemRender;
import org.eclipse.e4.ui.workbench.annotations.items.ItemSelected;
import org.eclipse.e4.ui.workbench.annotations.items.ItemTags;
import org.eclipse.e4.ui.workbench.annotations.items.ItemTooltip;
import org.eclipse.e4.ui.workbench.annotations.items.ItemVisible;
import org.eclipse.e4.ui.workbench.annotations.parameters.CoreExpression;
import org.eclipse.e4.ui.workbench.annotations.parameters.CoreExpressionTags;
import org.eclipse.e4.ui.workbench.ide.parameters.E4WorkbenchParameterConstants;

@DirectMenuItem(E4WorkbenchItemConstants.SAMPLE_MENU_ITEM)
@ItemInteraction(ItemType.PUSH)
@ItemLabel(E4WorkbenchItemConstants.SAMPLE_MENU_ITEM$_LABEL)
@ItemMnemonics(E4WorkbenchItemConstants.SAMPLE_MENU_ITEM$_MNEMON)
@ItemTooltip(E4WorkbenchItemConstants.SAMPLE_MENU_ITEM$_TIP)
@ItemIcon(E4WorkbenchItemConstants.SAMPLE_MENU_ITEM$_ICON)
@ItemEnabled(true)
@ItemSelected(false)
@ItemRender(true)
@ItemVisible(true)
@ItemAccessability(E4WorkbenchItemConstants.SAMPLE_MENU_ITEM$_ACCESS)
public class SampleDirectMenuItem {

	@ItemTags
	public String[] tags = {"tag1", "tag2"};
	
	@CoreExpression(E4WorkbenchParameterConstants.SAMPLE_MENU_ITEM$_COREX)
	@CoreExpressionTags
	public String[] tagsex = {"tag1", "tag2"};
	
	@Execute
	public void doSomething() {
		
	}
}
