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

import org.eclipse.e4.ui.workbench.commands.annotations.Command;
import org.eclipse.e4.ui.workbench.commands.annotations.CommandCategory;
import org.eclipse.e4.ui.workbench.commands.annotations.CommandDescription;
import org.eclipse.e4.ui.workbench.commands.annotations.CommandName;
import org.eclipse.e4.ui.workbench.commands.annotations.CommandParameters;
import org.eclipse.e4.ui.workbench.commands.annotations.CommandTags;
import org.eclipse.e4.ui.workbench.commands.util.E4WorkbenchConstants;

@Command(E4WorkbenchCommandConstants.PERSPECTIVES_SHOW_PERSPECTIVE)
@CommandName("")
@CommandDescription("")
public class ShowPerspectiveCommand {

	@CommandTags
	String[] tags = {"tag1", "tag2"}; 
	
	@CommandCategory("some.id")
	HashMap<String,String> setCategory() {
		HashMap<String,String> def = new HashMap<String,String>(2);
		def.put(E4WorkbenchConstants.COMMAND_CATEGORY_NAME, "some name");
		def.put(E4WorkbenchConstants.COMMAND_CATEGORY_DESCRIPTION, "some description");
		return def;
	}
	
	@CommandParameters("some.id.other")
	HashMap<String,String> setFirstParameter() {
		HashMap<String,String> def = new HashMap<String,String>(3);
		def.put(E4WorkbenchConstants.COMMAND_PARAMETER_NAME, "some name");
		def.put(E4WorkbenchConstants.COMMAND_PARAMETER_TYPEID, "a given type id");
		def.put(E4WorkbenchConstants.COMMAND_PARAMETER_OPTIONAL, "false");
		return def;
	}
	
	@CommandParameters("some.id.other.2")
	HashMap<String,String> setSecondParameter() {
		HashMap<String,String> def = new HashMap<String,String>(3);
		def.put(E4WorkbenchConstants.COMMAND_PARAMETER_NAME, "some name");
		def.put(E4WorkbenchConstants.COMMAND_PARAMETER_TYPEID, "a given type id");
		def.put(E4WorkbenchConstants.COMMAND_PARAMETER_OPTIONAL, "false");
		return def;
	}
	
}
