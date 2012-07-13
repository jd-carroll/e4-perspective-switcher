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
import org.eclipse.e4.ui.workbench.commands.internal.util.E4WBCommandsActivator;
import org.eclipse.e4.ui.workbench.ide.commands.ShowPerspectiveCommand;
import org.eclipse.e4.ui.workbench.ide.handlers.ShowPerspectiveHandler;

public class CommandsProcessor {

	final String CONTRIBUTION_URI_PREFIX = "bundleclass://"; //$NON-NLS-1$
	final String CONTRIBUTOR_URI_PREFIX = "platform:/plugin/"; //$NON-NLS-1$
	final String URI_SEPERATOR = "/"; //$NON-NLS-1$
	
	@Inject
	private MApplication application;

	MCommandsFactory commandsFactory = MCommandsFactory.INSTANCE;

	CommandsProcessor instance;
	String instance_PLUGIN_ID = E4WBCommandsActivator.PLUGIN_ID;
	
	final Object[] _void = new Object[] {};

	@Inject
	public CommandsProcessor() {
		instance = this;
	}

	@Execute
	public void execute() {
		try {
			createCommandFor(ShowPerspectiveCommand.class);
			createHandlerFor(ShowPerspectiveHandler.class);
			
			System.out.println("Done");
			
		} catch (Exception ex) {
			// do something
		}

	}

	@SuppressWarnings("unchecked")
	public void createCommandFor(Class<?> clazz) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {		
		Command _cmd = clazz.getAnnotation(Command.class);
		CommandName _nm = clazz.getAnnotation(CommandName.class);
		CommandDescription _dsc = clazz.getAnnotation(CommandDescription.class);
		
		if (_cmd != null && !_cmd.value().equals("")) {
			MCommand command = commandsFactory.createCommand();
			
			command.setContributorURI(CONTRIBUTOR_URI_PREFIX + instance_PLUGIN_ID);
			command.setElementId(_cmd.value());
			command.setCommandName(_nm != null ? _nm.value() : null);
			command.setDescription(_dsc != null ? _dsc.value() : null);
			
			
			
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
				HashMap<String,String> def = (HashMap<String,String>) method.invoke(clazz, _void);
				category.setName(def != null ? def.get(E4WorkbenchConstants.COMMAND_CATEGORY_NAME) : null);
				category.setDescription(def != null ? def.get(E4WorkbenchConstants.COMMAND_CATEGORY_DESCRIPTION) : null);
				
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
					HashMap<String,String> def = (HashMap<String,String>) methods[i].invoke(clazz, _void);
					parameter.setName(def != null ? def.get(E4WorkbenchConstants.COMMAND_PARAMETER_NAME) : null);
					parameter.setTypeId(def != null ? def.get(E4WorkbenchConstants.COMMAND_PARAMETER_TYPEID) : null);
					parameter.setOptional(def != null ? Boolean.parseBoolean(def.get(E4WorkbenchConstants.COMMAND_PARAMETER_TYPEID)) : false);
					
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
			
			handler.setContributorURI(CONTRIBUTOR_URI_PREFIX + instance_PLUGIN_ID);
			handler.setElementId(_hlr.value());			
			handler.setContributionURI(CONTRIBUTION_URI_PREFIX + instance_PLUGIN_ID + URI_SEPERATOR 
					+ clazz.getCanonicalName());
			//handler.setObject(clazz);			
			
			MCommand command = null;
			if (_cmd != null) {
				List<MCommand> appCommands = application.getCommands();
				for (int i=0; i<appCommands.size(); i++) {
					if (appCommands.get(i).getElementId().equals(_cmd.value())) {
						command = appCommands.get(i);
						break;
					}
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
				
				HashMap<String,String> def = (HashMap<String,String>) method.invoke(clazz, _void);
				handler.getPersistedState().putAll(def);
			}
			
			application.getHandlers().add(handler);
		}
	}
	
}
