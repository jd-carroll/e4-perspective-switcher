package org.eclipse.e4.ui.workbench.commands.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.commands.MCategory;
import org.eclipse.e4.ui.model.application.commands.MCommand;
import org.eclipse.e4.ui.model.application.commands.MCommandParameter;
import org.eclipse.e4.ui.model.application.commands.MCommandsFactory;
import org.eclipse.e4.ui.model.application.commands.MHandler;
import org.eclipse.e4.ui.workbench.commands.annotations.Command;
import org.eclipse.e4.ui.workbench.commands.annotations.CommandCategory;
import org.eclipse.e4.ui.workbench.commands.annotations.CommandDescription;
import org.eclipse.e4.ui.workbench.commands.annotations.CommandName;
import org.eclipse.e4.ui.workbench.commands.annotations.CommandParameters;
import org.eclipse.e4.ui.workbench.commands.annotations.CommandTags;
import org.eclipse.e4.ui.workbench.commands.annotations.Handler;
import org.eclipse.e4.ui.workbench.commands.annotations.HandlerCommand;
import org.eclipse.e4.ui.workbench.commands.annotations.HandlerPersistedState;
import org.eclipse.e4.ui.workbench.commands.annotations.HandlerTags;
import org.eclipse.e4.ui.workbench.ide.commands.ShowPerspectiveCommand;
import org.eclipse.e4.ui.workbench.ide.handlers.ShowPerspectiveHandler;

public class CommandsProcessor {

	@Inject
	private MApplication application;

	@Inject
	private MCommandsFactory commandsFactory;

	CommandsProcessor instance;

	@Inject
	public CommandsProcessor() {
		instance = this;
	}

	@Execute
	public void execute() {
		try {
			createCommandFor(ShowPerspectiveCommand.class);
			createHandlerFor(ShowPerspectiveHandler.class);
			
		} catch (Exception ex) {
			// do something
		}
		

		//Reflections reflections = new Reflections("my.project.prefix");

		//Set<Class<? extends Object>> allClasses = 
		//  reflections.getSubTypesOf(Object.class);

	}

	@SuppressWarnings("unchecked")
	public void createCommandFor(Class<?> clazz) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {		
		Command _cmd = clazz.getAnnotation(Command.class);
		CommandName _nm = clazz.getAnnotation(CommandName.class);
		CommandDescription _dsc = clazz.getAnnotation(CommandDescription.class);
		
		if (_cmd != null && !_cmd.value().equals("")) {
			MCommand command = commandsFactory.createCommand();
			
			command.setElementId(_cmd.value());
			command.setCommandName(_nm != null ? _nm.value() : "");
			command.setDescription(_dsc != null ? _dsc.value() : "");
			
			Field[] fields = clazz.getFields();
			for (int i=0; i<fields.length; i++) { 
				if (fields[i].isAnnotationPresent(CommandTags.class))
					command.getTags().addAll(Arrays.asList((String[]) fields[i].get(clazz)));
			}
					
			if (clazz.getAnnotation(CommandCategory.class) != null) {
				Method method = null;
				Method[] methods = clazz.getDeclaredMethods();
				for (int i=0; i<methods.length; i++) {
					if (methods[i].isAnnotationPresent(CommandCategory.class))
						method = methods[i];
				}
				
				MCategory category = commandsFactory.createCategory();
				category.setElementId(method.getAnnotation(CommandCategory.class).value());
				HashMap<String,String> def = (HashMap<String,String>) method.invoke(clazz, new Object[] {});
				category.setName(def.get(E4WorkbenchConstants.COMMAND_CATEGORY_NAME) != null 
						? def.get(E4WorkbenchConstants.COMMAND_CATEGORY_NAME) : "");
				category.setDescription(def.get(E4WorkbenchConstants.COMMAND_CATEGORY_DESCRIPTION) != null
						? def.get(E4WorkbenchConstants.COMMAND_CATEGORY_DESCRIPTION) : "");
				
				List<MCategory> appCategories = application.getCategories();
				for (int i=0; i<appCategories.size(); i++) {
					if (appCategories.get(i).getElementId().equals(category.getElementId())) {
						appCategories.remove(i); //category = appCategories.get(i);
						break;							
					}
				}
				
				command.setCategory(category);
				application.getCategories().add(category);
			}
			
			if (clazz.getAnnotation(CommandParameters.class) != null) {
				Method[] methods = clazz.getDeclaredMethods();
				for (int i=0; i<methods.length; i++) {
					if (!methods[i].isAnnotationPresent(CommandParameters.class))
						continue;
					
					MCommandParameter parameter = commandsFactory.createCommandParameter();
					parameter.setElementId(methods[i].getAnnotation(CommandParameters.class).value());
					HashMap<String,String> def = (HashMap<String,String>) methods[i].invoke(clazz, new Object[] {});
					parameter.setName(def.get(E4WorkbenchConstants.COMMAND_PARAMETER_NAME) != null 
							? def.get(E4WorkbenchConstants.COMMAND_PARAMETER_NAME) : "");
					parameter.setTypeId(def.get(E4WorkbenchConstants.COMMAND_PARAMETER_TYPEID) != null 
							? def.get(E4WorkbenchConstants.COMMAND_PARAMETER_TYPEID) : "");
					parameter.setOptional(def.get(E4WorkbenchConstants.COMMAND_PARAMETER_OPTIONAL) != null 
							? Boolean.parseBoolean(def.get(E4WorkbenchConstants.COMMAND_PARAMETER_TYPEID)) : false);
					
					command.getParameters().add(parameter);					
				}
			}

			application.getCommands().add(command);
		}
	}

	@SuppressWarnings("unchecked")
	public void createHandlerFor(Class<?> clazz) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Handler _hlr = clazz.getAnnotation(Handler.class);
		HandlerCommand _cmd = clazz.getAnnotation(HandlerCommand.class);
		
		if (_hlr != null && !_hlr.value().equals("")) {
			MHandler handler = commandsFactory.createHandler();
			
			handler.setElementId(_hlr.value());
			
			MCommand command = null;
			List<MCommand> appCommands = application.getCommands();
			for (int i=0; i<appCommands.size(); i++) {
				if (appCommands.get(i).equals(_cmd.value())) {
					command = appCommands.get(i);
					break;
				}
			}
			handler.setCommand(command);
			
			Field[] fields = clazz.getFields();
			for (int i=0; i<fields.length; i++) { 
				if (fields[i].isAnnotationPresent(HandlerTags.class))
					handler.getTags().addAll(Arrays.asList((String[]) fields[i].get(clazz)));
			}
			
			if (clazz.getAnnotation(HandlerPersistedState.class) != null) {
				Method method = null;
				Method[] methods = clazz.getDeclaredMethods();
				for (int i=0; i<methods.length; i++) {
					if (methods[i].isAnnotationPresent(HandlerPersistedState.class))
						method = methods[i];
				}
				
				HashMap<String,String> def = (HashMap<String,String>) method.invoke(clazz, new Object[] {});
				handler.getPersistedState().putAll(def);
			}
			
			application.getHandlers().add(handler);
		}
	}
}
