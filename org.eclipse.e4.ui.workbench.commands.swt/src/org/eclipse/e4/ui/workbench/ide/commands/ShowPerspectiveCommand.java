package org.eclipse.e4.ui.workbench.ide.commands;

import java.util.HashMap;

import org.eclipse.e4.ui.workbench.commands.annotations.Command;
import org.eclipse.e4.ui.workbench.commands.annotations.CommandCategory;
import org.eclipse.e4.ui.workbench.commands.annotations.CommandDescription;
import org.eclipse.e4.ui.workbench.commands.annotations.CommandName;
import org.eclipse.e4.ui.workbench.commands.annotations.CommandParameters;
import org.eclipse.e4.ui.workbench.commands.annotations.CommandTags;
import org.eclipse.e4.ui.workbench.commands.util.E4WorkbenchConstants;

@Command("")
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
