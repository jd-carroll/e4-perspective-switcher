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

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
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
			createHandlerFor(ShowPerspectiveHandler.class, 
					createCommandFor(ShowPerspectiveCommand.class));

			
			System.out.println("Done");
			
		} catch (Exception ex) {
			// do something
		}

	}

	@SuppressWarnings("unchecked")
	public <T> MCommand createCommandFor(Class<T> clazz) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Command _cmd = clazz.getAnnotation(Command.class);
		CommandName _nm = clazz.getAnnotation(CommandName.class);
		CommandDescription _dsc = clazz.getAnnotation(CommandDescription.class);
		
		if (_cmd != null && !_cmd.value().equals("")) {
			MCommand command = commandsFactory.createCommand();
			
			command.setContributorURI(CONTRIBUTOR_URI_PREFIX + instance_PLUGIN_ID);
			command.setElementId(_cmd.value());
			command.setCommandName(_nm != null ? _nm.value() : null);
			command.setDescription(_dsc != null ? _dsc.value() : null);
			
			T implementationClass = ContextInjectionFactory.make(clazz, application.getContext());
			Class<T> objectClass = (Class<T>) implementationClass.getClass();
			
			Field[] fields = objectClass.getDeclaredFields();
			for (int i=0; i<fields.length; i++) { 
				if (fields[i].isAnnotationPresent(CommandTags.class))
					command.getTags().addAll(Arrays.asList((String[]) fields[i].get(implementationClass)));
			}
				
			//
			{
				Method method = null;
				Method[] methods = objectClass.getDeclaredMethods();
				for (int i=0; i<methods.length && method==null; i++) {
					if (methods[i].isAnnotationPresent(CommandCategory.class))
						method = methods[i];
				}
				
				MCategory category = commandsFactory.createCategory();
				category.setElementId(method.getAnnotation(CommandCategory.class).value());
				HashMap<String,String> def = (HashMap<String,String>) method.invoke(implementationClass, _void);
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
			
			//
			{
				Method[] methods = objectClass.getDeclaredMethods();
				for (int i=0; i<methods.length; i++) {
					if (!methods[i].isAnnotationPresent(CommandParameters.class))
						continue;
					
					MCommandParameter parameter = commandsFactory.createCommandParameter();
					parameter.setElementId(methods[i].getAnnotation(CommandParameters.class).value());
					HashMap<String,String> def = (HashMap<String,String>) methods[i].invoke(implementationClass, _void);
					parameter.setName(def != null ? def.get(E4WorkbenchConstants.COMMAND_PARAMETER_NAME) : null);
					parameter.setTypeId(def != null ? def.get(E4WorkbenchConstants.COMMAND_PARAMETER_TYPEID) : null);
					parameter.setOptional(def != null ? Boolean.parseBoolean(def.get(E4WorkbenchConstants.COMMAND_PARAMETER_TYPEID)) : false);
					
					command.getParameters().add(parameter);					
				}
			}

			application.getCommands().add(command);
			return command;
		}
		return null;
	}

	public <T> MHandler createHandlerFor(Class<T> clazz, MCommand command) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return internalCreateFor(clazz, command, false);
	}
	
	public <T> MHandler createHandlerFor(Class<T> clazz) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return internalCreateFor(clazz, null, true);
	}
	
	@SuppressWarnings("unchecked")
	private <T> MHandler internalCreateFor(Class<T> clazz, MCommand command, boolean doSearch) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Handler _hlr = clazz.getAnnotation(Handler.class);
		HandlerCommand _cmd = clazz.getAnnotation(HandlerCommand.class);
		
		if (_hlr != null && !_hlr.value().equals("")) {
			MHandler handler = commandsFactory.createHandler();
			
			handler.setContributorURI(CONTRIBUTOR_URI_PREFIX + instance_PLUGIN_ID);
			handler.setElementId(_hlr.value());			
			handler.setContributionURI(CONTRIBUTION_URI_PREFIX + instance_PLUGIN_ID + URI_SEPERATOR 
					+ clazz.getCanonicalName());	
			
			if (command == null && doSearch)
				if (_cmd != null && !_cmd.value().equals(""))
					command = internalDoSearch(_cmd.value());
			handler.setCommand(command);

			T implementationClass = ContextInjectionFactory.make(clazz, application.getContext());
			Class<T> objectClass = (Class<T>) implementationClass.getClass();
			
			Field[] fields = objectClass.getDeclaredFields();
			for (int i=0; i<fields.length; i++) { 
				if (fields[i].isAnnotationPresent(HandlerTags.class))
					handler.getTags().addAll(Arrays.asList((String[]) fields[i].get(implementationClass)));
			}
			
			//
			{
				Method[] methods = objectClass.getDeclaredMethods();
				for (int i=0; i<methods.length; i++)
					if (methods[i].isAnnotationPresent(HandlerPersistedState.class)) {
						HashMap<String,String> def = (HashMap<String,String>) methods[i].invoke(implementationClass, _void);
						handler.getPersistedState().putAll(def);
						break;
					}
			}
			
			application.getHandlers().add(handler);
			return handler;
		}
		return null;
	}
	
	private MCommand internalDoSearch(final String commandID) {
		MCommand command = null;
		List<MCommand> appCommands = application.getCommands();
		for (int i=0; i<appCommands.size(); i++)
			if (appCommands.get(i).getElementId().equals(commandID)) {
				command = appCommands.get(i);
				break;
			}
		return command;
	}
}
