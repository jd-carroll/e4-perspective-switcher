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

import java.util.HashMap;

import org.eclipse.e4.ui.workbench.annotations.commands.Command;
import org.eclipse.e4.ui.workbench.annotations.commands.CommandCategory;
import org.eclipse.e4.ui.workbench.annotations.commands.CommandDescription;
import org.eclipse.e4.ui.workbench.annotations.commands.CommandName;
import org.eclipse.e4.ui.workbench.annotations.commands.CommandTags;
import org.eclipse.e4.ui.workbench.annotations.parameters.CommandParameter;
import org.eclipse.e4.ui.workbench.commands.util.E4WorkbenchConstants;
import org.eclipse.e4.ui.workbench.ide.categories.E4WorkbenchCategoryConstants;

@Command(E4WorkbenchCommandConstants.PERSPECTIVES_SHOW_PERSPECTIVE)
@CommandName(E4WorkbenchCommandConstants.PERSPECTIVES_SHOW_PERSPECTIVE$_DESCRIP)
@CommandDescription(E4WorkbenchCommandConstants.PERSPECTIVES_SHOW_PERSPECTIVE$_DESCRIP)
@CommandCategory(E4WorkbenchCategoryConstants.CATEGORY_EXAMPLE)
public class ShowPerspectiveCommand {
	
	@CommandTags
	public String[] tags = {"tag1", "tag2"}; 
	
	@CommandParameter(E4WorkbenchCommandConstants.PERSPECTIVES_SHOW_PERSPECTIVE$_PARAM$_ID)
	public HashMap<String,String> setFirstParameter() {
		HashMap<String,String> def = new HashMap<String,String>(3);
		def.put(E4WorkbenchConstants.COMMAND_PARAMETER_NAME, "Perspective ID");
		//def.put(E4WorkbenchConstants.COMMAND_PARAMETER_TYPEID, ... );
		def.put(E4WorkbenchConstants.COMMAND_PARAMETER_OPTIONAL, "false");
		return def;
	}
	
	@CommandParameter(E4WorkbenchCommandConstants.PERSPECTIVES_SHOW_PERSPECTIVE$_PARAM$_WINDOW)
	public HashMap<String,String> setSecondParameter() {
		HashMap<String,String> def = new HashMap<String,String>(3);
		def.put(E4WorkbenchConstants.COMMAND_PARAMETER_NAME, "Open in New Window");
		//def.put(E4WorkbenchConstants.COMMAND_PARAMETER_TYPEID, ... );
		def.put(E4WorkbenchConstants.COMMAND_PARAMETER_OPTIONAL, "false");
		return def;
	}
	
}
