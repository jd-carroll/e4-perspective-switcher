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
package org.eclipse.e4.ui.workbench.ide.commands;

import org.eclipse.e4.ui.workbench.annotations.commands.Command;
import org.eclipse.e4.ui.workbench.annotations.commands.CommandCategory;
import org.eclipse.e4.ui.workbench.annotations.commands.CommandDescription;
import org.eclipse.e4.ui.workbench.annotations.commands.CommandName;
import org.eclipse.e4.ui.workbench.annotations.commands.CommandTags;
import org.eclipse.e4.ui.workbench.annotations.parameters.CommandParameter;
import org.eclipse.e4.ui.workbench.annotations.parameters.CommandParameterName;
import org.eclipse.e4.ui.workbench.annotations.parameters.CommandParameterOptional;
import org.eclipse.e4.ui.workbench.annotations.parameters.CommandParameterTags;
import org.eclipse.e4.ui.workbench.ide.categories.E4WorkbenchCategoryConstants;
import org.eclipse.e4.ui.workbench.ide.parameters.E4WorkbenchParameterConstants;

@Command(E4WorkbenchCommandConstants.PERSPECTIVES_SHOW_PERSPECTIVE)
@CommandName(E4WorkbenchCommandConstants.PERSPECTIVES_SHOW_PERSPECTIVE$_DESCRIP)
@CommandDescription(E4WorkbenchCommandConstants.PERSPECTIVES_SHOW_PERSPECTIVE$_DESCRIP)
@CommandCategory(E4WorkbenchCategoryConstants.CATEGORY_EXAMPLE)
public class ShowPerspectiveCommand {
	
	@CommandTags
	public String[] tags = {"tag1", "tag2"}; 
	
	@CommandParameter(E4WorkbenchParameterConstants.COMMAND_SHOW_PERSPECTIVE$_ID)
	@CommandParameterName(E4WorkbenchParameterConstants.COMMAND_SHOW_PERSPECTIVE$_ID$_NAME)
	//@CommandParameterType(E4WorkbenchParameterConstants.COMMAND_SHOW_PERSPECTIVE$_ID$_TYPE)
	@CommandParameterOptional(E4WorkbenchParameterConstants.COMMAND_SHOW_PERSPECTIVE$_ID$_OPTION)
	@CommandParameterTags
	public String[] param1_tags = {"tag1", "tag2"};
	
	@CommandParameter(E4WorkbenchParameterConstants.COMMAND_SHOW_PERSPECTIVE$_WINDOW)
	@CommandParameterName(E4WorkbenchParameterConstants.COMMAND_SHOW_PERSPECTIVE$_WINDOW$_NAME)
	//@CommandParameterType(E4WorkbenchParameterConstants.COMMAND_SHOW_PERSPECTIVE$_WINDOW$_TYPE)
	@CommandParameterOptional(E4WorkbenchParameterConstants.COMMAND_SHOW_PERSPECTIVE$_WINDOW$_OPTION)
	@CommandParameterTags
	public String[] param2_tags = {"tag1", "tag2"};
}
