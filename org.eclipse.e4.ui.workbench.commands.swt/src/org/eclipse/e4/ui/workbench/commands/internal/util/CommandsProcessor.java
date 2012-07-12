package org.eclipse.e4.ui.workbench.commands.internal.util;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;

public class CommandsProcessor {

	@Inject
	private MApplication application;
	
	@Execute
	public void execute() {
		application.getCommands();
		
		 Reflections reflections = new Reflections("my.project.prefix");

		 Set<Class<? extends Object>> allClasses = 
		     reflections.getSubTypesOf(Object.class);
		
	}
	
}
