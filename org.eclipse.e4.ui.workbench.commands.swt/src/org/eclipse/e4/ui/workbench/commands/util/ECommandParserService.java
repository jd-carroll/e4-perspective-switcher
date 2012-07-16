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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.MApplicationElement;
import org.eclipse.e4.ui.model.application.commands.MBindingContext;
import org.eclipse.e4.ui.model.application.commands.MBindingTable;
import org.eclipse.e4.ui.model.application.commands.MCategory;
import org.eclipse.e4.ui.model.application.commands.MCommand;
import org.eclipse.e4.ui.model.application.commands.MCommandParameter;
import org.eclipse.e4.ui.model.application.commands.MCommandsFactory;
import org.eclipse.e4.ui.model.application.commands.MHandler;
import org.eclipse.e4.ui.model.application.commands.MKeyBinding;
import org.eclipse.e4.ui.model.application.commands.MParameter;
import org.eclipse.e4.ui.model.application.ui.MCoreExpression;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.MUiFactory;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimElement;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectToolItem;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledToolItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuContribution;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuSeparator;
import org.eclipse.e4.ui.model.application.ui.menu.MOpaqueMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MOpaqueMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MOpaqueMenuSeparator;
import org.eclipse.e4.ui.model.application.ui.menu.MOpaqueToolItem;
import org.eclipse.e4.ui.model.application.ui.menu.MPopupMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MRenderedMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MRenderedMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MRenderedToolBar;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBar;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBarContribution;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBarElement;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBarSeparator;
import org.eclipse.e4.ui.model.application.ui.menu.MToolControl;
import org.eclipse.e4.ui.model.application.ui.menu.MTrimContribution;
import org.eclipse.e4.ui.workbench.annotations.bindings.BindingContext;
import org.eclipse.e4.ui.workbench.annotations.bindings.BindingContextDescription;
import org.eclipse.e4.ui.workbench.annotations.bindings.BindingContextName;
import org.eclipse.e4.ui.workbench.annotations.bindings.BindingContextParent;
import org.eclipse.e4.ui.workbench.annotations.bindings.BindingContextTags;
import org.eclipse.e4.ui.workbench.annotations.bindings.BindingTable;
import org.eclipse.e4.ui.workbench.annotations.bindings.BindingTableContext;
import org.eclipse.e4.ui.workbench.annotations.bindings.KeyBinding;
import org.eclipse.e4.ui.workbench.annotations.bindings.KeyBindingCommand;
import org.eclipse.e4.ui.workbench.annotations.bindings.KeyBindingSequence;
import org.eclipse.e4.ui.workbench.annotations.bindings.KeyBindingTable;
import org.eclipse.e4.ui.workbench.annotations.bindings.KeyBindingTags;
import org.eclipse.e4.ui.workbench.annotations.categories.Category;
import org.eclipse.e4.ui.workbench.annotations.categories.CategoryDescription;
import org.eclipse.e4.ui.workbench.annotations.categories.CategoryName;
import org.eclipse.e4.ui.workbench.annotations.categories.CategoryTags;
import org.eclipse.e4.ui.workbench.annotations.commands.Command;
import org.eclipse.e4.ui.workbench.annotations.commands.CommandCategory;
import org.eclipse.e4.ui.workbench.annotations.commands.CommandDescription;
import org.eclipse.e4.ui.workbench.annotations.commands.CommandName;
import org.eclipse.e4.ui.workbench.annotations.commands.CommandTags;
import org.eclipse.e4.ui.workbench.annotations.contributions.ContributionAccessability;
import org.eclipse.e4.ui.workbench.annotations.contributions.ContributionParent;
import org.eclipse.e4.ui.workbench.annotations.contributions.ContributionPosition;
import org.eclipse.e4.ui.workbench.annotations.contributions.ContributionRender;
import org.eclipse.e4.ui.workbench.annotations.contributions.ContributionTags;
import org.eclipse.e4.ui.workbench.annotations.contributions.ContributionVisible;
import org.eclipse.e4.ui.workbench.annotations.contributions.MenuContribution;
import org.eclipse.e4.ui.workbench.annotations.contributions.ToolbarContribution;
import org.eclipse.e4.ui.workbench.annotations.contributions.TrimContribution;
import org.eclipse.e4.ui.workbench.annotations.handlers.Handler;
import org.eclipse.e4.ui.workbench.annotations.handlers.HandlerCommand;
import org.eclipse.e4.ui.workbench.annotations.handlers.HandlerPersistedState;
import org.eclipse.e4.ui.workbench.annotations.handlers.HandlerTags;
import org.eclipse.e4.ui.workbench.annotations.items.DirectMenuItem;
import org.eclipse.e4.ui.workbench.annotations.items.DirectToolItem;
import org.eclipse.e4.ui.workbench.annotations.items.HandledMenuItem;
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
import org.eclipse.e4.ui.workbench.annotations.items.MenuSeparator;
import org.eclipse.e4.ui.workbench.annotations.items.SeparatorAccessability;
import org.eclipse.e4.ui.workbench.annotations.items.SeparatorRender;
import org.eclipse.e4.ui.workbench.annotations.items.SeparatorTags;
import org.eclipse.e4.ui.workbench.annotations.items.SeparatorVisible;
import org.eclipse.e4.ui.workbench.annotations.items.ToolbarSeparator;
import org.eclipse.e4.ui.workbench.annotations.menus.Menu;
import org.eclipse.e4.ui.workbench.annotations.menus.MenuAccessability;
import org.eclipse.e4.ui.workbench.annotations.menus.MenuIcon;
import org.eclipse.e4.ui.workbench.annotations.menus.MenuLabel;
import org.eclipse.e4.ui.workbench.annotations.menus.MenuMnemonics;
import org.eclipse.e4.ui.workbench.annotations.menus.MenuRender;
import org.eclipse.e4.ui.workbench.annotations.menus.MenuTooltip;
import org.eclipse.e4.ui.workbench.annotations.menus.MenuVisible;
import org.eclipse.e4.ui.workbench.annotations.parameters.CommandParameter;
import org.eclipse.e4.ui.workbench.annotations.parameters.CommandParameterName;
import org.eclipse.e4.ui.workbench.annotations.parameters.CommandParameterOptional;
import org.eclipse.e4.ui.workbench.annotations.parameters.CommandParameterTags;
import org.eclipse.e4.ui.workbench.annotations.parameters.CommandParameterType;
import org.eclipse.e4.ui.workbench.annotations.parameters.CoreExpression;
import org.eclipse.e4.ui.workbench.annotations.parameters.CoreExpressionTags;
import org.eclipse.e4.ui.workbench.annotations.parameters.Parameter;
import org.eclipse.e4.ui.workbench.annotations.parameters.ParameterName;
import org.eclipse.e4.ui.workbench.annotations.parameters.ParameterTags;
import org.eclipse.e4.ui.workbench.annotations.parameters.ParameterValue;
import org.eclipse.e4.ui.workbench.annotations.tools.ToolControl;
import org.eclipse.e4.ui.workbench.annotations.tools.ToolControlAccessability;
import org.eclipse.e4.ui.workbench.annotations.tools.ToolControlPersistedState;
import org.eclipse.e4.ui.workbench.annotations.tools.ToolControlRender;
import org.eclipse.e4.ui.workbench.annotations.tools.ToolControlVisible;
import org.eclipse.e4.ui.workbench.annotations.tools.Toolbar;
import org.eclipse.e4.ui.workbench.annotations.tools.ToolbarAccessability;
import org.eclipse.e4.ui.workbench.annotations.tools.ToolbarRender;
import org.eclipse.e4.ui.workbench.annotations.tools.ToolbarTags;
import org.eclipse.e4.ui.workbench.annotations.tools.ToolbarVisible;
import org.eclipse.e4.ui.workbench.commands.internal.util.E4WBCommandsActivator;
import org.eclipse.e4.ui.workbench.ide.commands.ShowPerspectiveCommand;
import org.eclipse.e4.ui.workbench.ide.handlers.ShowPerspectiveHandler;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

public class ECommandParserService {

	final String CONTRIBUTION_URI_PREFIX = "bundleclass://"; //$NON-NLS-1$
	final String CONTRIBUTOR_URI_PREFIX = "platform:/plugin/"; //$NON-NLS-1$
	final String URI_SEPERATOR = "/"; //$NON-NLS-1$
	
	@Inject
	private Logger logger;
	
	@Inject
	private IEclipseContext eclipseContext;
	
	@Inject
	private EModelService modelService;
	
	@Inject
	private MApplication application;

	MCommandsFactory commandsFactory = MCommandsFactory.INSTANCE;
	MMenuFactory menuFactory = MMenuFactory.INSTANCE;
	MUiFactory uiFactory = MUiFactory.INSTANCE;

	ECommandParserService instance;
	String instance_PLUGIN_ID = E4WBCommandsActivator.PLUGIN_ID;
	
	final Object[] _void = new Object[] {};

	@Execute
	public void execute() {
		MCommand perspectiveCommand = createCommandFor(ShowPerspectiveCommand.class, null);
		@SuppressWarnings("unused")
		MHandler perspectiveHandler = createHandlerFor(ShowPerspectiveHandler.class, perspectiveCommand);
		
	}
	
	/**
	 * Creates a new {@link MBindingContext} from the provided definition and adds it to the model. The context
	 * is only created if a valid definition is supplied. If the definition is invalid or if the context is
	 * unable to be added to the model, the method returns <tt>null</tt>.
	 * <p>
	 * <b>NOTE:</b> When using this method outside of the bundle <tt>org.eclipse.ui.workbench.commands.swt</tt>
	 * it is imperative that <tt>ECommandParserService.getInstance()</tt> is called with the valid <tt>Bundle-SymbolicName</tt> 
	 * of the invoking class. Failing to do so may lead to non-deterministic results.
	 * </p> 
	 * 
	 * @param clazz the class defining the binding context
	 * @return the binding context added to the model
	 * @see BindingContext
	 */
	public <T> MBindingContext createBindingContextFor(Class<T> clazz) {
		BindingContext _ctx = clazz.getAnnotation(BindingContext.class);
		BindingContextName _nm = clazz.getAnnotation(BindingContextName.class);
		BindingContextDescription _dsc = clazz.getAnnotation(BindingContextDescription.class);
		BindingContextParent _par = clazz.getAnnotation(BindingContextParent.class);
		
		if (_ctx != null && _ctx.value() != null && !_ctx.value().equals("")) {
			MBindingContext context = commandsFactory.createBindingContext();
			
			context.setContributorURI(CONTRIBUTOR_URI_PREFIX + instance_PLUGIN_ID);
			context.setElementId(_ctx.value());
			context.setName(_nm != null ? _nm.value() : null);
			context.setDescription(_dsc != null ? _dsc.value() : null);
			
			T implementationClass = ContextInjectionFactory.make(clazz, eclipseContext);
			
			internalFindTags(implementationClass, context, BindingContextTags.class);
			
			boolean isAssigned = false;
			if (_par != null && _par.value() != null && !_par.value().equals("")) {
				List<MBindingContext> appContexts = application.getBindingContexts();
				for (int i=0; i<appContexts.size(); i++)
					if (_par.value().equals(appContexts.get(i))) {
						isAssigned = appContexts.get(i).getChildren().add(context);
						break;
					}
			}
			
			if (!isAssigned)
				isAssigned = application.getBindingContexts().add(context);
			
			return isAssigned ? context : null; 			
		}
		return null;
	}
	
	/**
	 * Creates a new {@link MBindingTable} from the provided definition and adds it to the model. The table
	 * is only created if a valid definition is supplied. If the definition is invalid or if the table is
	 * unable to be added to the model, the method returns <tt>null</tt>.
	 * <p>
	 * The method accepts an optional {@link MBindingContext} parameter to use as the parent binding context. The
	 * binding table may only be assigned to one parent binding context per created instance. If a valid parent context
	 * parameter is supplied, preference will be given to the parent context over any context provided in the definition.
	 * </p>
	 * <p>
	 * <b>NOTE:</b> When using this method outside of the bundle <tt>org.eclipse.ui.workbench.commands.swt</tt>
	 * it is imperative that <tt>ECommandParserService.getInstance()</tt> is called with the valid <tt>Bundle-SymbolicName</tt> 
	 * of the invoking class. Failing to do so may lead to non-deterministic results.
	 * </p> 
	 * 
	 * @param clazz the class defining the binding table
	 * @param appContext the parent binding context (Optional)
	 * @return the binding table added to the model
	 * @see BindingTable
	 */
	public <T> MBindingTable createBindingTableFor(Class<T> clazz, MBindingContext appContext) {
		BindingTable _tbl = clazz.getAnnotation(BindingTable.class);
		BindingTableContext _ctx = clazz.getAnnotation(BindingTableContext.class);
		
		if (appContext != null || (_ctx != null && _ctx.value() != null && !_ctx.value().equals(""))) {
			MBindingTable table = commandsFactory.createBindingTable();
			
			table.setContributorURI(CONTRIBUTOR_URI_PREFIX + instance_PLUGIN_ID);
			table.setElementId(_tbl != null ? _tbl.value() : null);
			
			boolean isAssigned = false;
			if (appContext != null) {
				List<MBindingContext> verified = modelService.findElements(application, appContext.getElementId(), MBindingContext.class, null);
				if (verified.size() > 0 && verified.get(0) != null) 
					table.setBindingContext(verified.get(0));
				isAssigned = verified.size() > 0 && verified.get(0) != null ? application.getBindingTables().add(table) : false;
			}
			
			if (!isAssigned && _ctx != null && _ctx.value() != null && !_ctx.value().equals("")) {
				List<MBindingContext> verified = modelService.findElements(application, _ctx.value(), MBindingContext.class, null);
				if (verified.size() > 0 && verified.get(0) != null) 
					table.setBindingContext(verified.get(0));
				isAssigned = verified.size() > 0 && verified.get(0) != null ? application.getBindingTables().add(table) : false;
			}
			
			return isAssigned ? table : null;
		}	
		return null;
	}
	
	/**
	 * Creates a new {@link MKeyBinding} from the provided definition and adds it to the model. The key binding
	 * is only created if a valid definition is supplied. If the definition is invalid or if the key binding is
	 * unable to be added to the model, the method returns <tt>null</tt>.
	 * <p>
	 * The method accepts an optional {@link MBindingTable} parameter to use as the assigned binding table. The
	 * key binding may only be assigned to one binding table per created instance. If a valid parent table parameter
	 * is supplied, preference will be given to the parent table over any table provided in the definition.
	 * </p>
	 * <p>
	 * The method accepts an optional {@link MCommand} parameter to use as the assigned command. The key binding 
	 * may only be assigned to one command per created instance. If a valid command parameter is supplied, 
	 * preference will be given to the command over any command provided in the definition.
	 * </p>
	 * <p>
	 * <b>NOTE:</b> When using this method outside of the bundle <tt>org.eclipse.ui.workbench.commands.swt</tt>
	 * it is imperative that <tt>ECommandParserService.getInstance()</tt> is called with the valid <tt>Bundle-SymbolicName</tt> 
	 * of the invoking class. Failing to do so may lead to non-deterministic results.
	 * </p> 
	 * 
	 * @param clazz the class defining the binding table
	 * @param appTable the parent binding table (Optional)
	 * @param appCommand the associated command (Optional)
	 * @return the key binding added to the model
	 */
	public <T> MKeyBinding createKeyBindingFor(Class<T> clazz, MBindingTable appTable, MCommand appCommand) {
		KeyBinding _bnd = clazz.getAnnotation(KeyBinding.class);
		KeyBindingTable _tbl = clazz.getAnnotation(KeyBindingTable.class);
		KeyBindingSequence _seq = clazz.getAnnotation(KeyBindingSequence.class);
		KeyBindingCommand _cmd = clazz.getAnnotation(KeyBindingCommand.class);
		
		if (_seq != null && _seq.value() != null && !_seq.value().equals("")) {
			MKeyBinding binding = commandsFactory.createKeyBinding();
			
			binding.setContributorURI(CONTRIBUTOR_URI_PREFIX + instance_PLUGIN_ID);
			binding.setElementId(_bnd != null ? _bnd.value() : null);
			binding.setKeySequence(_seq.value());
			
			T implementationClass = ContextInjectionFactory.make(clazz, eclipseContext);
			
			internalFindTags(implementationClass, binding, KeyBindingTags.class);
			
			List<MParameter> parameters = internalFindParameters(implementationClass, Parameter.class);
			if (parameters.size() > 0)
				binding.getParameters().addAll(parameters);
			
			boolean hasCommand = false;
			if (appCommand != null) {
				List<MCommand> verified = modelService.findElements(application, appCommand.getElementId(), MCommand.class, null);
				if (verified.size() > 0 && verified.get(0) != null) 
					binding.setCommand(verified.get(0));
				hasCommand = verified.size() > 0 && verified.get(0) != null;
			}
			
			if (!hasCommand && _cmd != null && _cmd.value() != null && !_cmd.value().equals("")) {
				List<MCommand> verified = modelService.findElements(application, _cmd.value(), MCommand.class, null);
				if (verified.size() > 0 && verified.get(0) != null) 
					binding.setCommand(verified.get(0));
			}
			
			boolean isAssigned = false;
			if (appTable != null) {
				List<MBindingTable> verified = modelService.findElements(application, appTable.getElementId(), MBindingTable.class, null);
				if (verified.size() > 0 && verified.get(0) != null)
					isAssigned = verified.get(0).getBindings().add(binding);
			}
			
			if (!isAssigned && _tbl != null && _tbl.value() != null && !_tbl.value().equals("")) {
				List<MBindingTable> verified = modelService.findElements(application, _tbl.value(), MBindingTable.class, null);
				if (verified.size() > 0 && verified.get(0) != null) 
					isAssigned = verified.get(0).getBindings().add(binding);
			}
			
			return isAssigned ? binding : null;
		}
		return null;
	}
	
	/**
	 * Creates a new {@link MCategory} from the provided definition and adds it to the model. The category
	 * is only created if a valid definition is supplied. If the definition is invalid or if the category is
	 * unable to be added to the model, the method returns <tt>null</tt>.
	 * <p>
	 * <b>NOTE:</b> When using this method outside of the bundle <tt>org.eclipse.ui.workbench.commands.swt</tt>
	 * it is imperative that <tt>ECommandParserService.getInstance()</tt> is called with the valid <tt>Bundle-SymbolicName</tt> 
	 * of the invoking class. Failing to do so may lead to non-deterministic results.
	 * </p> 
	 * 
	 * @param clazz the class defining the category
	 * @return the category added to the model
	 * @modelsafe A new category is only added to the model if there is no existing element with the same id.
	 */
	public <T> MCategory createCategoryFor(Class<T> clazz) {
		Category _cat = clazz.getAnnotation(Category.class);
		CategoryName _nm = clazz.getAnnotation(CategoryName.class);
		CategoryDescription _dsc = clazz.getAnnotation(CategoryDescription.class);
		
		if (_cat != null && _cat.value() != null && !_cat.value().equals("")) {
			MCategory category = getModelSafeInstance(MCategory.class, _cat.value());
			
			category.setName(_nm != null ?_nm.value() : null);
			category.setDescription(_dsc != null ? _dsc.value() : null);
			
			T implementationClass = ContextInjectionFactory.make(clazz, eclipseContext);
			
			internalFindTags(implementationClass, category, CategoryTags.class);
			
			List<MCategory> appCategories = application.getCategories();
			
			boolean isAssigned = modelSafeAdd(category, appCategories, MCategory.class);
			return isAssigned ? category : null;
		}
		return null;
	}
	
	/**
	 * Creates a new {@link MCommand} from the provided definition and adds it to the model. The command
	 * is only created if a valid definition is supplied. If the definition is invalid or if the command is
	 * unable to be added to the model, the method returns <tt>null</tt>.
	 * <p>
	 * The method accepts an optional {@link MCategory} parameter to use as the assigned command category. The
	 * command may only be assigned to one command category per created instance. If a valid command category parameter
	 * is supplied, preference will be given to the command category over any category provided in the definition.
	 * </p>
	 * <p>
	 * <b>NOTE:</b> When using this method outside of the bundle <tt>org.eclipse.ui.workbench.commands.swt</tt>
	 * it is imperative that <tt>ECommandParserService.getInstance()</tt> is called with the valid <tt>Bundle-SymbolicName</tt> 
	 * of the invoking class. Failing to do so may lead to non-deterministic results.
	 * </p> 
	 * 
	 * @param clazz the class defining the command
	 * @param appCategory the category to associate the command with (Optional)
	 * @return the command added to the model
	 * @modelsafe A new command is only added to the model if there is no existing element with the same id.
	 */
	public <T> MCommand createCommandFor(Class<T> clazz, MCategory appCategory) {
		Command _cmd = clazz.getAnnotation(Command.class);
		CommandName _nm = clazz.getAnnotation(CommandName.class);
		CommandDescription _dsc = clazz.getAnnotation(CommandDescription.class);
		CommandCategory _cat = clazz.getAnnotation(CommandCategory.class);
		
		if (_cmd != null && _cmd.value() != null && !_cmd.value().equals("")) {
			MCommand command = getModelSafeInstance(MCommand.class, _cmd.value());
			
			command.setCommandName(_nm != null ? _nm.value() : null);
			command.setDescription(_dsc != null ? _dsc.value() : null);
			
			boolean hasCategory = false;
			if (appCategory != null) {
				List<MCategory> verified = modelService.findElements(application, appCategory.getElementId(), MCategory.class, null);
				if (verified.size() > 0 && verified.get(0) != null) 
					command.setCategory(verified.get(0));
				hasCategory = verified.size() > 0 && verified.get(0) != null;
			}
			
			if (!hasCategory && _cat != null && _cat.value() != null && !_cat.value().equals("")) {
				// if a category with the same id is not present a base category is created and used
				MCategory verified = getModelSafeInstance(MCategory.class, _cat.value());
				if (verified != null) 
					command.setCategory(verified);
			}
			
			T implementationClass = ContextInjectionFactory.make(clazz, eclipseContext);
			
			internalFindTags(implementationClass, command, CommandTags.class);
			
			List<MCommandParameter> parameters = internalFindCommandParameters(implementationClass, CommandParameter.class);
			if (parameters.size() > 0)
				command.getParameters().addAll(parameters);
			
			List<MCommand> appCommands = application.getCommands();
			
			boolean isAssigned = modelSafeAdd(command, appCommands, MCommand.class);
			return isAssigned ? command : null;
		}
		return null;
	}
	
	/**
	 * Creates a new {@link MMenuContribution} from the provided definition and adds it to the model. The contribution
	 * is only created if a valid definition is supplied. If the definition is invalid or if the contribution is
	 * unable to be added to the model, the method returns <tt>null</tt>.
	 * <p>
	 * <b>NOTE:</b> When using this method outside of the bundle <tt>org.eclipse.ui.workbench.commands.swt</tt>
	 * it is imperative that <tt>ECommandParserService.getInstance()</tt> is called with the valid <tt>Bundle-SymbolicName</tt> 
	 * of the invoking class. Failing to do so may lead to non-deterministic results.
	 * </p> 
	 * 
	 * @param clazz the class defining the menu contribution
	 * @return the menu contribution added to the model
	 * @modelsafe A new contribution is only added to the model if there is no existing element with the same id.
	 */
	public <T> MMenuContribution createMenuContributionFor(Class<T> clazz) {
		MenuContribution _mctb = clazz.getAnnotation(MenuContribution.class);
		ContributionAccessability _acs = clazz.getAnnotation(ContributionAccessability.class);
		ContributionParent _par = clazz.getAnnotation(ContributionParent.class);
		ContributionPosition _pos = clazz.getAnnotation(ContributionPosition.class);
		ContributionRender _rnd = clazz.getAnnotation(ContributionRender.class);
		ContributionVisible _vis = clazz.getAnnotation(ContributionVisible.class);
		
		if (_mctb != null && _mctb.value() != null && !_mctb.value().equals("")) {
			MMenuContribution contribution = getModelSafeInstance(MMenuContribution.class, _mctb.value());
			
			contribution.setAccessibilityPhrase(_acs != null ? _acs.value() : null);
			contribution.setParentId(_par != null ? _par.value() : null);
			contribution.setPositionInParent(_pos != null ? _pos.value() : null);
			contribution.setToBeRendered(_rnd != null ? _rnd.value() : false);
			contribution.setVisible(_vis != null ? _vis.value() : false);
			
			T implementationClass = ContextInjectionFactory.make(clazz, eclipseContext);

			internalFindTags(implementationClass, contribution, ContributionTags.class);
			
			List<MMenuContribution> appContributions = application.getMenuContributions();
			
			boolean isAssigned = modelSafeAdd(contribution, appContributions, MMenuContribution.class);
			return isAssigned ? contribution : null;
		}		
		return null;
	}
	
	/**
	 * Creates a new {@link MToolBarContribution} from the provided definition and adds it to the model. The contribution
	 * is only created if a valid definition is supplied. If the definition is invalid or if the contribution is
	 * unable to be added to the model, the method returns <tt>null</tt>.
	 * <p>
	 * <b>NOTE:</b> When using this method outside of the bundle <tt>org.eclipse.ui.workbench.commands.swt</tt>
	 * it is imperative that <tt>ECommandParserService.getInstance()</tt> is called with the valid <tt>Bundle-SymbolicName</tt> 
	 * of the invoking class. Failing to do so may lead to non-deterministic results.
	 * </p> 
	 * 
	 * @param clazz the class defining the toolbar contribution
	 * @return the toolbar contribution added to the model
	 * @modelsafe A new contribution is only added to the model if there is no existing element with the same id.
	 */
	public <T> MToolBarContribution createToolbarContributionFor(Class<T> clazz) {
		ToolbarContribution _tctb = clazz.getAnnotation(ToolbarContribution.class);
		ContributionAccessability _acs = clazz.getAnnotation(ContributionAccessability.class);
		ContributionParent _par = clazz.getAnnotation(ContributionParent.class);
		ContributionPosition _pos = clazz.getAnnotation(ContributionPosition.class);
		ContributionRender _rnd = clazz.getAnnotation(ContributionRender.class);
		ContributionVisible _vis = clazz.getAnnotation(ContributionVisible.class);
		
		if (_tctb != null && _tctb.value() != null && !_tctb.value().equals("")) {
			MToolBarContribution contribution = getModelSafeInstance(MToolBarContribution.class, _tctb.value());
			
			contribution.setAccessibilityPhrase(_acs != null ? _acs.value() : null);
			contribution.setParentId(_par != null ? _par.value() : null);
			contribution.setPositionInParent(_pos != null ? _pos.value() : null);
			contribution.setToBeRendered(_rnd != null ? _rnd.value() : false);
			contribution.setVisible(_vis != null ? _vis.value() : false);
			
			T implementationClass = ContextInjectionFactory.make(clazz, eclipseContext);

			internalFindTags(implementationClass, contribution, ContributionTags.class);
			
			List<MToolBarContribution> appContributions = application.getToolBarContributions();
			
			boolean isAssigned = modelSafeAdd(contribution, appContributions, MToolBarContribution.class);
			return isAssigned ? contribution : null;
		}		
		return null;
	}
	
	/**
	 * Creates a new {@link MTrimContribution} from the provided definition and adds it to the model. The contribution
	 * is only created if a valid definition is supplied. If the definition is invalid or if the contribution is
	 * unable to be added to the model, the method returns <tt>null</tt>.
	 * <p>
	 * <b>NOTE:</b> When using this method outside of the bundle <tt>org.eclipse.ui.workbench.commands.swt</tt>
	 * it is imperative that <tt>ECommandParserService.getInstance()</tt> is called with the valid <tt>Bundle-SymbolicName</tt> 
	 * of the invoking class. Failing to do so may lead to non-deterministic results.
	 * </p> 
	 * 
	 * @param clazz the class defining the trim contribution
	 * @return the trim contribution added to the model
	 * @modelsafe A new contribution is only added to the model if there is no existing element with the same id.
	 */
	public <T> MTrimContribution createTrimContributionFor(Class<T> clazz) {
		TrimContribution _rctb = clazz.getAnnotation(TrimContribution.class);
		ContributionAccessability _acs = clazz.getAnnotation(ContributionAccessability.class);
		ContributionParent _par = clazz.getAnnotation(ContributionParent.class);
		ContributionPosition _pos = clazz.getAnnotation(ContributionPosition.class);
		ContributionRender _rnd = clazz.getAnnotation(ContributionRender.class);
		ContributionVisible _vis = clazz.getAnnotation(ContributionVisible.class);
		
		if (_rctb != null && _rctb.value() != null && !_rctb.value().equals("")) {
			MTrimContribution contribution = getModelSafeInstance(MTrimContribution.class, _rctb.value());
			
			contribution.setAccessibilityPhrase(_acs != null ? _acs.value() : null);
			contribution.setParentId(_par != null ? _par.value() : null);
			contribution.setPositionInParent(_pos != null ? _pos.value() : null);
			contribution.setToBeRendered(_rnd != null ? _rnd.value() : false);
			contribution.setVisible(_vis != null ? _vis.value() : false);
			
			T implementationClass = ContextInjectionFactory.make(clazz, eclipseContext);

			internalFindTags(implementationClass, contribution, ContributionTags.class);
			
			List<MTrimContribution> appContributions = application.getTrimContributions();
			
			boolean isAssigned = modelSafeAdd(contribution, appContributions, MTrimContribution.class);
			return isAssigned ? contribution : null;
		}		
		return null;
	}
	
	/**
	 * Creates a new {@link MHandler} from the provided definition and adds it to the model. The handler
	 * is only created if a valid definition is supplied. If the definition is invalid or if the handler is
	 * unable to be added to the model, the method returns <tt>null</tt>.
	 * <p>
	 * The method accepts an optional {@link MCommand} parameter to use as the assigned command. The
	 * handler may only be assigned to one command category per created instance. If a valid command
	 * is supplied, preference will be given to the command over any command provided in the definition.
	 * </p>
	 * <p>
	 * <b>NOTE:</b> When using this method outside of the bundle <tt>org.eclipse.ui.workbench.commands.swt</tt>
	 * it is imperative that <tt>ECommandParserService.getInstance()</tt> is called with the valid <tt>Bundle-SymbolicName</tt> 
	 * of the invoking class. Failing to do so may lead to non-deterministic results.
	 * </p> 
	 * 
	 * @param clazz the class defining the handler
	 * @param appCommand the associated command (Optional)
	 * @return the handler added to the model
	 */
	public <T> MHandler createHandlerFor(Class<T> clazz, MCommand appCommand) {
		Handler _hlr = clazz.getAnnotation(Handler.class);
		HandlerCommand _cmd = clazz.getAnnotation(HandlerCommand.class);
		
		if (appCommand != null || (_cmd != null && _cmd.value() != null && !_cmd.value().equals(""))) {
			MHandler handler = commandsFactory.createHandler();
			
			handler.setContributorURI(CONTRIBUTOR_URI_PREFIX + instance_PLUGIN_ID);
			handler.setElementId(_hlr.value());			
			handler.setContributionURI(CONTRIBUTION_URI_PREFIX + instance_PLUGIN_ID + URI_SEPERATOR 
					+ clazz.getCanonicalName());	
			
			boolean hasCommand = false;
			if (appCommand != null) {
				List<MCommand> verified = modelService.findElements(application, appCommand.getElementId(), MCommand.class, null);
				if (verified.size() > 0 && verified.get(0) != null) 
					handler.setCommand(verified.get(0));
				hasCommand = verified.size() > 0 && verified.get(0) != null;
			}
			
			if (!hasCommand && _cmd != null && _cmd.value() != null && !_cmd.value().equals("")) {
				// if a command with the same id is not present a base command is created and used
				MCommand verified = getModelSafeInstance(MCommand.class, _cmd.value());
				if (verified != null) 
					handler.setCommand(verified);
			}

			T implementationClass = ContextInjectionFactory.make(clazz, eclipseContext);
			
			internalFindTags(implementationClass, handler, HandlerTags.class);
			
			internalFindPersistedState(implementationClass, handler, HandlerPersistedState.class);
			
			boolean isAssigned = application.getHandlers().add(handler);
			return isAssigned ? handler : null;
		}
		return null;
	}
	
	/**
	 * Creates a new {@link MBindingTable} from the provided definition and adds it to the model. The table
	 * is only created if a valid definition is supplied. If the definition is invalid or if the table is
	 * unable to be added to the model, the method returns <tt>null</tt>.
	 * <p>
	 * <b>NOTE:</b> When using this method outside of the bundle <tt>org.eclipse.ui.workbench.commands.swt</tt>
	 * it is imperative that <tt>ECommandParserService.getInstance()</tt> is called with the valid <tt>Bundle-SymbolicName</tt> 
	 * of the invoking class. Failing to do so may lead to non-deterministic results.
	 * </p> 
	 * 
	 * @param clazz the class defining the binding table
	 * @return the binding table added to the model
	 */
	public <T> MDirectMenuItem createDirectMenuItemFor(Class<T> clazz, MElementContainer<MMenuElement> appMenuContribution) {
		DirectMenuItem _drmi = clazz.getAnnotation(DirectMenuItem.class);
		ItemInteraction _typ = clazz.getAnnotation(ItemInteraction.class);
		ItemLabel _lbl = clazz.getAnnotation(ItemLabel.class);
		ItemMnemonics _mnm = clazz.getAnnotation(ItemMnemonics.class);
		ItemTooltip _tip = clazz.getAnnotation(ItemTooltip.class);
		ItemIcon _ico = clazz.getAnnotation(ItemIcon.class);
		ItemEnabled _enb = clazz.getAnnotation(ItemEnabled.class);
		ItemSelected _sel = clazz.getAnnotation(ItemSelected.class);
		ItemRender _rnd = clazz.getAnnotation(ItemRender.class);
		ItemVisible _viz = clazz.getAnnotation(ItemVisible.class);
		ItemAccessability _acc = clazz.getAnnotation(ItemAccessability.class);
		
		MDirectMenuItem directItem = menuFactory.createDirectMenuItem();
		
		directItem.setContributorURI(CONTRIBUTOR_URI_PREFIX + instance_PLUGIN_ID);
		directItem.setElementId(_drmi != null ? _drmi.value() : null);
		directItem.setContributionURI(CONTRIBUTION_URI_PREFIX + instance_PLUGIN_ID + URI_SEPERATOR 
				+ clazz.getCanonicalName());
		
		directItem.setType(_typ != null ? _typ.value() : null);
		directItem.setLabel(_lbl != null ? _lbl.value() : null);
		directItem.setMnemonics(_mnm != null ? _mnm.value() : null);
		directItem.setTooltip(_tip != null ? _tip.value() : null);
		directItem.setIconURI(_ico != null ? _ico.value() : null);
		directItem.setEnabled(_enb != null ? _enb.value() : false);
		directItem.setSelected(_sel != null ? _sel.value() : false);
		directItem.setToBeRendered(_rnd != null ? _rnd.value() : false);
		directItem.setVisible(_viz != null ? _viz.value() : false);
		directItem.setAccessibilityPhrase(_acc != null ? _acc.value() : null);
		
		T implementationClass = ContextInjectionFactory.make(clazz, eclipseContext);
		
		internalFindTags(implementationClass, directItem, ItemTags.class);
		
		internalFindCoreExpression(implementationClass, directItem);
		
		if (appMenuContribution != null)
			appMenuContribution.getChildren().add(directItem);
		
		return directItem;
	}
	
	/**
	 * Creates a new {@link MBindingTable} from the provided definition and adds it to the model. The table
	 * is only created if a valid definition is supplied. If the definition is invalid or if the table is
	 * unable to be added to the model, the method returns <tt>null</tt>.
	 * <p>
	 * <b>NOTE:</b> When using this method outside of the bundle <tt>org.eclipse.ui.workbench.commands.swt</tt>
	 * it is imperative that <tt>ECommandParserService.getInstance()</tt> is called with the valid <tt>Bundle-SymbolicName</tt> 
	 * of the invoking class. Failing to do so may lead to non-deterministic results.
	 * </p> 
	 * 
	 * @param clazz the class defining the binding table
	 * @return the binding table added to the model
	 */
	public <T> MDirectToolItem createDirectToolItemFor(Class<T> clazz, MToolBarContribution appToolbarContribution) {
		DirectToolItem _drti = clazz.getAnnotation(DirectToolItem.class);
		ItemInteraction _typ = clazz.getAnnotation(ItemInteraction.class);
		ItemLabel _lbl = clazz.getAnnotation(ItemLabel.class);
		ItemTooltip _tip = clazz.getAnnotation(ItemTooltip.class);
		ItemIcon _ico = clazz.getAnnotation(ItemIcon.class);
		ItemEnabled _enb = clazz.getAnnotation(ItemEnabled.class);
		ItemSelected _sel = clazz.getAnnotation(ItemSelected.class);
		ItemRender _rnd = clazz.getAnnotation(ItemRender.class);
		ItemVisible _viz = clazz.getAnnotation(ItemVisible.class);
		ItemAccessability _acc = clazz.getAnnotation(ItemAccessability.class);
		
		MDirectToolItem directItem = menuFactory.createDirectToolItem();
		
		directItem.setContributorURI(CONTRIBUTOR_URI_PREFIX + instance_PLUGIN_ID);
		directItem.setElementId(_drti != null ? _drti.value() : null);
		directItem.setContributionURI(CONTRIBUTION_URI_PREFIX + instance_PLUGIN_ID + URI_SEPERATOR 
				+ clazz.getCanonicalName());
		
		directItem.setType(_typ != null ? _typ.value() : null);
		directItem.setLabel(_lbl != null ? _lbl.value() : null);
		directItem.setTooltip(_tip != null ? _tip.value() : null);
		directItem.setIconURI(_ico != null ? _ico.value() : null);
		directItem.setEnabled(_enb != null ? _enb.value() : false);
		directItem.setSelected(_sel != null ? _sel.value() : false);
		directItem.setToBeRendered(_rnd != null ? _rnd.value() : false);
		directItem.setVisible(_viz != null ? _viz.value() : false);
		directItem.setAccessibilityPhrase(_acc != null ? _acc.value() : null);
		
		T implementationClass = ContextInjectionFactory.make(clazz, eclipseContext);
		
		internalFindTags(implementationClass, directItem, ItemTags.class);
		
		internalFindCoreExpression(implementationClass, directItem);
				
		if (appToolbarContribution != null)
			appToolbarContribution.getChildren().add(directItem);
		
		return directItem;
	}
	
	/**
	 * Creates a new {@link MBindingTable} from the provided definition and adds it to the model. The table
	 * is only created if a valid definition is supplied. If the definition is invalid or if the table is
	 * unable to be added to the model, the method returns <tt>null</tt>.
	 * <p>
	 * <b>NOTE:</b> When using this method outside of the bundle <tt>org.eclipse.ui.workbench.commands.swt</tt>
	 * it is imperative that <tt>ECommandParserService.getInstance()</tt> is called with the valid <tt>Bundle-SymbolicName</tt> 
	 * of the invoking class. Failing to do so may lead to non-deterministic results.
	 * </p> 
	 * 
	 * @param clazz the class defining the binding table
	 * @return the binding table added to the model
	 */
	public <T> MHandledMenuItem ecreateHandledMenuItemFor(Class<T> clazz, MCommand appCommand, 
			MElementContainer<MMenuElement> appMenuContribution) {
		HandledMenuItem _hmi = clazz.getAnnotation(HandledMenuItem.class);
		ItemInteraction _typ = clazz.getAnnotation(ItemInteraction.class);
		ItemLabel _lbl = clazz.getAnnotation(ItemLabel.class);
		ItemMnemonics _mnm = clazz.getAnnotation(ItemMnemonics.class);
		ItemTooltip _tip = clazz.getAnnotation(ItemTooltip.class);
		ItemIcon _ico = clazz.getAnnotation(ItemIcon.class);
		ItemEnabled _enb = clazz.getAnnotation(ItemEnabled.class);
		ItemSelected _sel = clazz.getAnnotation(ItemSelected.class);
		ItemRender _rnd = clazz.getAnnotation(ItemRender.class);
		ItemVisible _viz = clazz.getAnnotation(ItemVisible.class);
		ItemAccessability _acc = clazz.getAnnotation(ItemAccessability.class);
		
		MHandledMenuItem handledItem = menuFactory.createHandledMenuItem();
		
		handledItem.setContributorURI(CONTRIBUTOR_URI_PREFIX + instance_PLUGIN_ID);
		handledItem.setElementId(_hmi != null ? _hmi.value() : null);
		
		handledItem.setType(_typ != null ? _typ.value() : null);
		handledItem.setLabel(_lbl != null ? _lbl.value() : null);
		handledItem.setMnemonics(_mnm != null ? _mnm.value() : null);
		handledItem.setTooltip(_tip != null ? _tip.value() : null);
		handledItem.setIconURI(_ico != null ? _ico.value() : null);
		handledItem.setEnabled(_enb != null ? _enb.value() : false);
		handledItem.setSelected(_sel != null ? _sel.value() : false);
		handledItem.setToBeRendered(_rnd != null ? _rnd.value() : false);
		handledItem.setVisible(_viz != null ? _viz.value() : false);
		handledItem.setAccessibilityPhrase(_acc != null ? _acc.value() : null);
		
		boolean hasCommand = false;
		if (appCommand != null) {
			List<MCommand> verified = modelService.findElements(application, appCommand.getElementId(), MCommand.class, null);
			if (verified.size() > 0 && verified.get(0) != null) 
				handledItem.setCommand(verified.get(0));
			hasCommand = verified.size() > 0 && verified.get(0) != null;
		}
		
		if (!hasCommand && _hmi != null && _hmi.value() != null && !_hmi.value().equals("")) {
			// if a command with the same id is not present a base command is created and used
			MCommand verified = getModelSafeInstance(MCommand.class, _hmi.value());
			if (verified != null) 
				handledItem.setCommand(verified);
		}
		
		T implementationClass = ContextInjectionFactory.make(clazz, eclipseContext);
		
		internalFindTags(implementationClass, handledItem, ItemTags.class);
		
		internalFindCoreExpression(implementationClass, handledItem);
		
		List<MParameter> parameters = internalFindParameters(implementationClass, Parameter.class);
		if (parameters.size() > 0)
			handledItem.getParameters().addAll(parameters);
		
		if (appMenuContribution != null)
			appMenuContribution.getChildren().add(handledItem);
		
		return handledItem;
	}
	
	/**
	 * Creates a new {@link MBindingTable} from the provided definition and adds it to the model. The table
	 * is only created if a valid definition is supplied. If the definition is invalid or if the table is
	 * unable to be added to the model, the method returns <tt>null</tt>.
	 * <p>
	 * <b>NOTE:</b> When using this method outside of the bundle <tt>org.eclipse.ui.workbench.commands.swt</tt>
	 * it is imperative that <tt>ECommandParserService.getInstance()</tt> is called with the valid <tt>Bundle-SymbolicName</tt> 
	 * of the invoking class. Failing to do so may lead to non-deterministic results.
	 * </p> 
	 * 
	 * @param clazz the class defining the binding table
	 * @return the binding table added to the model
	 */
	public <T> MHandledToolItem createHandledToolItemFor(Class<T> clazz, MCommand appCommand, MToolBarContribution appToolbarContribution) {
		HandledMenuItem _hti = clazz.getAnnotation(HandledMenuItem.class);
		ItemInteraction _typ = clazz.getAnnotation(ItemInteraction.class);
		ItemLabel _lbl = clazz.getAnnotation(ItemLabel.class);
		ItemTooltip _tip = clazz.getAnnotation(ItemTooltip.class);
		ItemIcon _ico = clazz.getAnnotation(ItemIcon.class);
		ItemEnabled _enb = clazz.getAnnotation(ItemEnabled.class);
		ItemSelected _sel = clazz.getAnnotation(ItemSelected.class);
		ItemRender _rnd = clazz.getAnnotation(ItemRender.class);
		ItemVisible _viz = clazz.getAnnotation(ItemVisible.class);
		ItemAccessability _acc = clazz.getAnnotation(ItemAccessability.class);
		
		MHandledToolItem handledItem = menuFactory.createHandledToolItem();
		
		handledItem.setContributorURI(CONTRIBUTOR_URI_PREFIX + instance_PLUGIN_ID);
		handledItem.setElementId(_hti != null ? _hti.value() : null);
		
		handledItem.setType(_typ != null ? _typ.value() : null);
		handledItem.setLabel(_lbl != null ? _lbl.value() : null);
		handledItem.setTooltip(_tip != null ? _tip.value() : null);
		handledItem.setIconURI(_ico != null ? _ico.value() : null);
		handledItem.setEnabled(_enb != null ? _enb.value() : false);
		handledItem.setSelected(_sel != null ? _sel.value() : false);
		handledItem.setToBeRendered(_rnd != null ? _rnd.value() : false);
		handledItem.setVisible(_viz != null ? _viz.value() : false);
		handledItem.setAccessibilityPhrase(_acc != null ? _acc.value() : null);
		
		boolean hasCommand = false;
		if (appCommand != null) {
			List<MCommand> verified = modelService.findElements(application, appCommand.getElementId(), MCommand.class, null);
			if (verified.size() > 0 && verified.get(0) != null) 
				handledItem.setCommand(verified.get(0));
			hasCommand = verified.size() > 0 && verified.get(0) != null;
		}
		
		if (!hasCommand && _hti != null && _hti.value() != null && !_hti.value().equals("")) {
			// if a command with the same id is not present a base command is created and used
			MCommand verified = getModelSafeInstance(MCommand.class, _hti.value());
			if (verified != null) 
				handledItem.setCommand(verified);
		}
		
		T implementationClass = ContextInjectionFactory.make(clazz, eclipseContext);
		
		internalFindTags(implementationClass, handledItem, ItemTags.class);
		
		internalFindCoreExpression(implementationClass, handledItem);
		
		List<MParameter> parameters = internalFindParameters(implementationClass, Parameter.class);
		if (parameters.size() > 0)
			handledItem.getParameters().addAll(parameters);
		
		if (appToolbarContribution != null)
			appToolbarContribution.getChildren().add(handledItem);
		
		return handledItem;
	}
	
	/**
	 * Creates a new {@link MBindingTable} from the provided definition and adds it to the model. The table
	 * is only created if a valid definition is supplied. If the definition is invalid or if the table is
	 * unable to be added to the model, the method returns <tt>null</tt>.
	 * <p>
	 * <b>NOTE:</b> When using this method outside of the bundle <tt>org.eclipse.ui.workbench.commands.swt</tt>
	 * it is imperative that <tt>ECommandParserService.getInstance()</tt> is called with the valid <tt>Bundle-SymbolicName</tt> 
	 * of the invoking class. Failing to do so may lead to non-deterministic results.
	 * </p> 
	 * 
	 * @param clazz the class defining the binding table
	 * @return the binding table added to the model
	 */
	public <T> MMenuSeparator createMenuSeparatorFor(Class<T> clazz, MElementContainer<MMenuElement> appMenuContribution) {
		MenuSeparator _sep = clazz.getAnnotation(MenuSeparator.class);
		SeparatorAccessability _acc = clazz.getAnnotation(SeparatorAccessability.class);
		SeparatorRender _rnd = clazz.getAnnotation(SeparatorRender.class);
		SeparatorVisible _viz = clazz.getAnnotation(SeparatorVisible.class);
		
		MMenuSeparator separator = menuFactory.createMenuSeparator();
		
		separator.setContributorURI(CONTRIBUTOR_URI_PREFIX + instance_PLUGIN_ID);
		separator.setElementId(_sep != null ? _sep.value() : null);
		separator.setAccessibilityPhrase(_acc != null ? _acc.value() : null);
		separator.setToBeRendered(_rnd != null ? _rnd.value() : false);
		separator.setVisible(_viz != null ? _viz.value() : false);
		
		T implementationClass = ContextInjectionFactory.make(clazz, eclipseContext);
		
		internalFindTags(implementationClass, separator, SeparatorTags.class);
		
		if (appMenuContribution != null)
			appMenuContribution.getChildren().add(separator);
		
		return separator;
	}
	
	/**
	 * Creates a new {@link MBindingTable} from the provided definition and adds it to the model. The table
	 * is only created if a valid definition is supplied. If the definition is invalid or if the table is
	 * unable to be added to the model, the method returns <tt>null</tt>.
	 * <p>
	 * <b>NOTE:</b> When using this method outside of the bundle <tt>org.eclipse.ui.workbench.commands.swt</tt>
	 * it is imperative that <tt>ECommandParserService.getInstance()</tt> is called with the valid <tt>Bundle-SymbolicName</tt> 
	 * of the invoking class. Failing to do so may lead to non-deterministic results.
	 * </p> 
	 * 
	 * @param clazz the class defining the binding table
	 * @return the binding table added to the model
	 */
	public <T> MToolBarSeparator createToolbarSeparatorFor(Class<T> clazz, MToolBarContribution appToolbarContribution) {
		ToolbarSeparator _sep = clazz.getAnnotation(ToolbarSeparator.class);
		SeparatorAccessability _acc = clazz.getAnnotation(SeparatorAccessability.class);
		SeparatorRender _rnd = clazz.getAnnotation(SeparatorRender.class);
		SeparatorVisible _viz = clazz.getAnnotation(SeparatorVisible.class);
		
		MToolBarSeparator separator = menuFactory.createToolBarSeparator();
		
		separator.setContributorURI(CONTRIBUTOR_URI_PREFIX + instance_PLUGIN_ID);
		separator.setElementId(_sep != null ? _sep.value() : null);
		separator.setAccessibilityPhrase(_acc != null ? _acc.value() : null);
		separator.setToBeRendered(_rnd != null ? _rnd.value() : false);
		separator.setVisible(_viz != null ? _viz.value() : false);
		
		T implementationClass = ContextInjectionFactory.make(clazz, eclipseContext);
		
		internalFindTags(implementationClass, separator, SeparatorTags.class);
		
		if (appToolbarContribution != null)
			appToolbarContribution.getChildren().add(separator);
		
		return separator;
	}	
	
	/**
	 * Creates a new {@link MBindingTable} from the provided definition and adds it to the model. The table
	 * is only created if a valid definition is supplied. If the definition is invalid or if the table is
	 * unable to be added to the model, the method returns <tt>null</tt>.
	 * <p>
	 * <b>NOTE:</b> When using this method outside of the bundle <tt>org.eclipse.ui.workbench.commands.swt</tt>
	 * it is imperative that <tt>ECommandParserService.getInstance()</tt> is called with the valid <tt>Bundle-SymbolicName</tt> 
	 * of the invoking class. Failing to do so may lead to non-deterministic results.
	 * </p> 
	 * 
	 * @param clazz the class defining the binding table
	 * @return the binding table added to the model
	 */
	public <T> MMenu createMenuFor(Class<T> clazz, MElementContainer<MMenuElement> appMenuContribution) {
		Menu _mnu = clazz.getAnnotation(Menu.class);
		MenuLabel _lbl = clazz.getAnnotation(MenuLabel.class);
		MenuMnemonics _mnm = clazz.getAnnotation(MenuMnemonics.class);
		MenuTooltip _tip = clazz.getAnnotation(MenuTooltip.class);
		MenuIcon _icn = clazz.getAnnotation(MenuIcon.class);
		MenuRender _rnd = clazz.getAnnotation(MenuRender.class);
		MenuVisible _viz = clazz.getAnnotation(MenuVisible.class);
		MenuAccessability _acc = clazz.getAnnotation(MenuAccessability.class);
		
		MMenu menu = menuFactory.createMenu();
		
		menu.setContributorURI(CONTRIBUTOR_URI_PREFIX + instance_PLUGIN_ID);
		menu.setElementId(_mnu != null ? _mnu.value() : null);
		menu.setLabel(_lbl != null ? _lbl.value() : null);
		menu.setMnemonics(_mnm != null ? _mnm.value() : null);
		menu.setTooltip(_tip != null ? _tip.value() : null);
		menu.setIconURI(_icn != null ? _icn.value() : null);
		menu.setToBeRendered(_rnd != null ? _rnd.value() : false);
		menu.setVisible(_viz != null ? _viz.value() : false);
		menu.setAccessibilityPhrase(_acc != null ? _acc.value() : null);
		
		T implementationClass = ContextInjectionFactory.make(clazz, eclipseContext);
		
		internalFindTags(implementationClass, menu, ItemTags.class);
		
		internalFindCoreExpression(implementationClass, menu);
		
		if (appMenuContribution != null)
			appMenuContribution.getChildren().add(menu);
		
		return menu;
	}
	
	/**
	 * Creates a new {@link MBindingTable} from the provided definition and adds it to the model. The table
	 * is only created if a valid definition is supplied. If the definition is invalid or if the table is
	 * unable to be added to the model, the method returns <tt>null</tt>.
	 * <p>
	 * <b>NOTE:</b> When using this method outside of the bundle <tt>org.eclipse.ui.workbench.commands.swt</tt>
	 * it is imperative that <tt>ECommandParserService.getInstance()</tt> is called with the valid <tt>Bundle-SymbolicName</tt> 
	 * of the invoking class. Failing to do so may lead to non-deterministic results.
	 * </p> 
	 * 
	 * @param clazz the class defining the binding table
	 * @return the binding table added to the model
	 */
	public <T> MCommandParameter createCommandParameterFor(Class<T> clazz) {
		CommandParameter _prm = clazz.getAnnotation(CommandParameter.class);
		CommandParameterName _nm = clazz.getAnnotation(CommandParameterName.class);
		CommandParameterType _typ = clazz.getAnnotation(CommandParameterType.class);
		CommandParameterOptional _opt = clazz.getAnnotation(CommandParameterOptional.class);
		
		if (_prm != null && _prm.value() != null && !_prm.value().equals("")) {
			MCommandParameter parameter = commandsFactory.createCommandParameter();
			
			parameter.setContributorURI(CONTRIBUTOR_URI_PREFIX + instance_PLUGIN_ID);
			parameter.setElementId(_prm.value());
			parameter.setName(_nm != null ? _nm.value() : null);
			parameter.setTypeId(_typ != null ? _typ.value() : null);
			parameter.setOptional(_opt != null ? _opt.value() : false);
			
			T implementationClass = ContextInjectionFactory.make(clazz, eclipseContext);
			
			internalFindTags(implementationClass, parameter, CommandParameterTags.class);			
			
			return parameter;
		}
		return null;
	}
	
	/**
	 * Creates a new {@link MBindingTable} from the provided definition and adds it to the model. The table
	 * is only created if a valid definition is supplied. If the definition is invalid or if the table is
	 * unable to be added to the model, the method returns <tt>null</tt>.
	 * <p>
	 * <b>NOTE:</b> When using this method outside of the bundle <tt>org.eclipse.ui.workbench.commands.swt</tt>
	 * it is imperative that <tt>ECommandParserService.getInstance()</tt> is called with the valid <tt>Bundle-SymbolicName</tt> 
	 * of the invoking class. Failing to do so may lead to non-deterministic results.
	 * </p> 
	 * 
	 * @param clazz the class defining the binding table
	 * @return the binding table added to the model
	 */
	public <T> MCoreExpression createCoreExpressionFor(Class<T> clazz) {
		CoreExpression _xpr = clazz.getAnnotation(CoreExpression.class);
		
		// Model safe???
		MCoreExpression expression = uiFactory.createCoreExpression();
		
		expression.setContributorURI(CONTRIBUTOR_URI_PREFIX + instance_PLUGIN_ID);
		expression.setElementId(_xpr.value());
		expression.setCoreExpressionId(_xpr.value());
		
		T implementationClass = ContextInjectionFactory.make(clazz, eclipseContext);
		
		internalFindTags(implementationClass, expression, CoreExpressionTags.class);
		
		return expression;
	}
	
	/**
	 * Creates a new {@link MBindingTable} from the provided definition and adds it to the model. The table
	 * is only created if a valid definition is supplied. If the definition is invalid or if the table is
	 * unable to be added to the model, the method returns <tt>null</tt>.
	 * <p>
	 * <b>NOTE:</b> When using this method outside of the bundle <tt>org.eclipse.ui.workbench.commands.swt</tt>
	 * it is imperative that <tt>ECommandParserService.getInstance()</tt> is called with the valid <tt>Bundle-SymbolicName</tt> 
	 * of the invoking class. Failing to do so may lead to non-deterministic results.
	 * </p> 
	 * 
	 * @param clazz the class defining the binding table
	 * @return the binding table added to the model
	 */
	public <T> MParameter createParameterFor(Class<T> clazz) {
		Parameter _prm = clazz.getAnnotation(Parameter.class);
		ParameterName _nm = clazz.getAnnotation(ParameterName.class);
		ParameterValue _val = clazz.getAnnotation(ParameterValue.class);
		
		if (_prm != null && _prm.value() != null && !_prm.value().equals("")) {
			MParameter parameter = commandsFactory.createParameter();
			
			parameter.setContributorURI(CONTRIBUTOR_URI_PREFIX + instance_PLUGIN_ID);
			parameter.setElementId(_prm.value());
			parameter.setName(_nm != null ? _nm.value() : null);
			parameter.setValue(_val != null ? _val.value() : null);
			
			T implementationClass = ContextInjectionFactory.make(clazz, eclipseContext);
			
			internalFindTags(implementationClass, parameter, ParameterTags.class);			
			
			return parameter;
		}
		return null;
	}
	
	/**
	 * Creates a new {@link MBindingTable} from the provided definition and adds it to the model. The table
	 * is only created if a valid definition is supplied. If the definition is invalid or if the table is
	 * unable to be added to the model, the method returns <tt>null</tt>.
	 * <p>
	 * <b>NOTE:</b> When using this method outside of the bundle <tt>org.eclipse.ui.workbench.commands.swt</tt>
	 * it is imperative that <tt>ECommandParserService.getInstance()</tt> is called with the valid <tt>Bundle-SymbolicName</tt> 
	 * of the invoking class. Failing to do so may lead to non-deterministic results.
	 * </p> 
	 * 
	 * @param clazz the class defining the binding table
	 * @return the binding table added to the model
	 */
	public <T> MToolBar createToolbarFor(Class<T> clazz, MElementContainer<MTrimElement> appTrimContribution) {
		Toolbar _mnu = clazz.getAnnotation(Toolbar.class);
		ToolbarRender _rnd = clazz.getAnnotation(ToolbarRender.class);
		ToolbarVisible _viz = clazz.getAnnotation(ToolbarVisible.class);
		ToolbarAccessability _acc = clazz.getAnnotation(ToolbarAccessability.class);
		
		MToolBar toolbar = menuFactory.createToolBar();
		
		toolbar.setContributorURI(CONTRIBUTOR_URI_PREFIX + instance_PLUGIN_ID);
		toolbar.setElementId(_mnu != null ? _mnu.value() : null);
		toolbar.setToBeRendered(_rnd != null ? _rnd.value() : false);
		toolbar.setVisible(_viz != null ? _viz.value() : false);
		toolbar.setAccessibilityPhrase(_acc != null ? _acc.value() : null);
		
		T implementationClass = ContextInjectionFactory.make(clazz, eclipseContext);
		
		internalFindTags(implementationClass, toolbar, ToolbarTags.class);
		
		if (appTrimContribution != null)
			appTrimContribution.getChildren().add(toolbar);
		
		return toolbar;
	}
	
	/**
	 * Creates a new {@link MBindingTable} from the provided definition and adds it to the model. The table
	 * is only created if a valid definition is supplied. If the definition is invalid or if the table is
	 * unable to be added to the model, the method returns <tt>null</tt>.
	 * <p>
	 * <b>NOTE:</b> When using this method outside of the bundle <tt>org.eclipse.ui.workbench.commands.swt</tt>
	 * it is imperative that <tt>ECommandParserService.getInstance()</tt> is called with the valid <tt>Bundle-SymbolicName</tt> 
	 * of the invoking class. Failing to do so may lead to non-deterministic results.
	 * </p> 
	 * 
	 * @param clazz the class defining the binding table
	 * @return the binding table added to the model
	 */
	public <T> MToolControl createToolControlFor(Class<T> clazz, MElementContainer<MTrimElement> appTrimContribution, 
			MElementContainer<MToolBarElement> appToolbarContribution) {
		ToolControl _mnu = clazz.getAnnotation(ToolControl.class);
		ToolControlRender _rnd = clazz.getAnnotation(ToolControlRender.class);
		ToolControlVisible _viz = clazz.getAnnotation(ToolControlVisible.class);
		ToolControlAccessability _acc = clazz.getAnnotation(ToolControlAccessability.class);
		
		MToolControl control = menuFactory.createToolControl();
		
		control.setContributorURI(CONTRIBUTOR_URI_PREFIX + instance_PLUGIN_ID);
		control.setElementId(_mnu != null ? _mnu.value() : null);
		control.setToBeRendered(_rnd != null ? _rnd.value() : false);
		control.setVisible(_viz != null ? _viz.value() : false);
		control.setAccessibilityPhrase(_acc != null ? _acc.value() : null);
		
		T implementationClass = ContextInjectionFactory.make(clazz, eclipseContext);
		
		internalFindTags(implementationClass, control, ItemTags.class);
		
		internalFindPersistedState(implementationClass, control, ToolControlPersistedState.class);
		
		if (appTrimContribution != null)
			appTrimContribution.getChildren().add(control);
		if (appToolbarContribution != null)
			appToolbarContribution.getChildren().add(control);
		
		return control;
	}

	/*
	 * Checks whether or not an element exists in the application model with the same elementId and type.
	 * If an instance already exists, the existing instance is returned, otherwise a new instance is created
	 * and returned with the contributor id appropriately set.
	 */
	<T extends MApplicationElement> T getModelSafeInstance(Class<T> type, String elementId) {
		T instance = null;
		List<T> elements = modelService.findElements(application, elementId, type, null);
		if (elements.size() > 0 && elements.get(0) != null)
			instance = elements.get(0);
		else {
			instance = buildType(type);
			instance.setContributorURI(CONTRIBUTOR_URI_PREFIX + instance_PLUGIN_ID);
			instance.setElementId(elementId);
		}
		return instance;
	}
	
	/*
	 * Returns the given type from the static factory methods.
	 */
	@SuppressWarnings("unchecked")
	<T> T buildType(Class<T> type) {
		
		// /////////////////////////////////////////////////////////
		// Command Factory
		// DO NOT CHANGE THE ORDER!!!
		// /////////////////////////////////////////////////////////
		
		if (MBindingContext.class.isAssignableFrom(type)) 
			return (T) commandsFactory.createBindingContext();
		if (MBindingTable.class.isAssignableFrom(type)) 
			return (T) commandsFactory.createBindingTable();
		if (MCategory.class.isAssignableFrom(type)) 
			return (T) commandsFactory.createCategory();
		if (MCommand.class.isAssignableFrom(type)) 
			return (T) commandsFactory.createCommand();
		if (MCommandParameter.class.isAssignableFrom(type)) 
			return (T) commandsFactory.createCommandParameter();
		if (MHandler.class.isAssignableFrom(type)) 
			return (T) commandsFactory.createHandler();
		if (MKeyBinding.class.isAssignableFrom(type)) 
			return (T) commandsFactory.createKeyBinding();
		if (MParameter.class.isAssignableFrom(type)) 
			return (T) commandsFactory.createParameter();

		// /////////////////////////////////////////////////////////
		// Menu Factory
		// DO NOT CHANGE THE ORDER!!!
		// /////////////////////////////////////////////////////////
		
		// MMenuItem's
		if (MDirectMenuItem.class.isAssignableFrom(type)) 
			return (T) menuFactory.createDirectMenuItem();
		if (MDirectToolItem.class.isAssignableFrom(type)) 
			return (T) menuFactory.createDirectToolItem();
		if (MHandledMenuItem.class.isAssignableFrom(type)) 
			return (T) menuFactory.createHandledMenuItem();
		if (MHandledToolItem.class.isAssignableFrom(type)) 
			return (T) menuFactory.createHandledToolItem();
		if (MOpaqueMenuItem.class.isAssignableFrom(type)) 
			return (T) menuFactory.createOpaqueMenuItem();
		if (MRenderedMenuItem.class.isAssignableFrom(type)) 
			return (T) menuFactory.createRenderedMenuItem();
		
		if (MOpaqueToolItem.class.isAssignableFrom(type)) 
			return (T) menuFactory.createOpaqueToolItem();
		if (MToolControl.class.isAssignableFrom(type)) 
			return (T) menuFactory.createToolControl();
		
		// MOpaqueMenuSeparator must come before MMenuSeparator
		if (MOpaqueMenuSeparator.class.isAssignableFrom(type)) 
			return (T) menuFactory.createOpaqueMenuSeparator();
		if (MMenuSeparator.class.isAssignableFrom(type)) 
			return (T) menuFactory.createMenuSeparator();
		if (MToolBarSeparator.class.isAssignableFrom(type)) 
			return (T) menuFactory.createToolBarSeparator();
		
		// MMenu must come after all three
		if (MOpaqueMenu.class.isAssignableFrom(type)) 
			return (T) menuFactory.createOpaqueMenu();
		if (MPopupMenu.class.isAssignableFrom(type)) 
			return (T) menuFactory.createPopupMenu();
		if (MRenderedMenu.class.isAssignableFrom(type)) 
			return (T) menuFactory.createRenderedMenu();
		if (MMenu.class.isAssignableFrom(type)) 
			return (T) menuFactory.createMenu();
				
		// MRenderedToolBar must come before MToolBar
		if (MRenderedToolBar.class.isAssignableFrom(type)) 
			return (T) menuFactory.createRenderedToolBar();
		if (MToolBar.class.isAssignableFrom(type)) 
			return (T) menuFactory.createToolBar();
		
		if (MMenuContribution.class.isAssignableFrom(type)) 
			return (T) menuFactory.createMenuContribution();
		if (MToolBarContribution.class.isAssignableFrom(type)) 
			return (T) menuFactory.createToolBarContribution();
		if (MTrimContribution.class.isAssignableFrom(type)) 
			return (T) menuFactory.createTrimContribution();		
		
		return null;
	}
	
	/*
	 * Checks whether or not the list of application elements already contains an element with the same elementId
	 * and type as element. If there is, it is assumed that or one of the others was given as the 'getModelSafeInstance'
	 * and does not need to be readded.  If the list does not contain the element, the element is added to the lis.
	 */
	<T extends MApplicationElement> boolean modelSafeAdd(T element, List<T> appElements, Class<T> type) {
		List<T> verified = modelService.findElements(application, element.getElementId(), type, null);
		boolean isAssigned = verified.size() > 0 && verified.get(0) != null;
		if (!isAssigned) 
			isAssigned = appElements.add(element);
		return isAssigned;
	}
	
	/*
	 * Searches the fields of the implementation class for the annotation.
	 * The first field that matches the annotation is used, no other fields are processed.
	 */
	private void internalFindTags(Object implementationClass, MApplicationElement element, Class<? extends Annotation> annotation) { 
		try {
			Field[] fields = implementationClass.getClass().getDeclaredFields();
			for (int i=0; i<fields.length; i++) 
				if (fields[i].isAnnotationPresent(annotation)) {
					element.getTags().addAll(Arrays.asList((String[]) fields[i].get(implementationClass)));
					break;
				}
		} catch (IllegalAccessException ex) {
			logger.error(ex, CommandsProcessorMessages.ILLEGAL_ACCESS_TAGS + " : " + implementationClass);
		}
		
	}
	
	/*
	 * Searches all of the fields of the implementation class for parameters and builds each one.
	 */
	private List<MParameter> internalFindParameters(Object implementationClass, Class<? extends Annotation> annotation) {
		List<MParameter> list = new ArrayList<MParameter>(3);
		Field[] fields = implementationClass.getClass().getDeclaredFields();
		for (int i=0; i<fields.length; i++)
			if (fields[i].isAnnotationPresent(annotation)) {
				MParameter param = fieldBuildParameter(fields[i], implementationClass);
				if (param != null)
					list.add(param);
			}
		return list;
	}
	
	/*
	 * Searches all of the fields of the implementation class for command parameters and builds each one.
	 */
	private List<MCommandParameter> internalFindCommandParameters(Object implementationClass, Class<? extends Annotation> annotation) {
		List<MCommandParameter> list = new ArrayList<MCommandParameter>(3);
		Field[] fields = implementationClass.getClass().getDeclaredFields();
		for (int i=0; i<fields.length; i++)
			if (fields[i].isAnnotationPresent(annotation)) {
				MCommandParameter param = fieldBuildCommandParameter(fields[i], implementationClass);
				if (param != null)
					list.add(param);
			}
		return list;
	}
	
	/*
	 * Builds a parameter from a field definition
	 */
	private MParameter fieldBuildParameter(Field field, Object implementationClass) {
		Parameter _prm = field.getAnnotation(Parameter.class);
		ParameterName _nm = field.getAnnotation(ParameterName.class);
		ParameterValue _val = field.getAnnotation(ParameterValue.class);
		
		if (_prm != null && _prm.value() != null && !_prm.value().equals("")) {
			MParameter parameter = commandsFactory.createParameter();
			
			parameter.setContributorURI(CONTRIBUTOR_URI_PREFIX + instance_PLUGIN_ID);
			parameter.setElementId(_prm.value());
			parameter.setName(_nm != null ? _nm.value() : null);
			parameter.setValue(_val != null ? _val.value() : null);
			
			internalFindTags(implementationClass, parameter, ParameterTags.class);			
			
			return parameter;
		}
		return null;
	}
	
	/*
	 * Builds a command parameter from a field definition
	 */
	private MCommandParameter fieldBuildCommandParameter(Field field, Object implementationClass) {
		CommandParameter _prm = field.getAnnotation(CommandParameter.class);
		CommandParameterName _nm = field.getAnnotation(CommandParameterName.class);
		CommandParameterType _typ = field.getAnnotation(CommandParameterType.class);
		CommandParameterOptional _opt = field.getAnnotation(CommandParameterOptional.class);
		
		if (_prm != null && _prm.value() != null && !_prm.value().equals("")) {
			MCommandParameter parameter = commandsFactory.createCommandParameter();
			
			parameter.setContributorURI(CONTRIBUTOR_URI_PREFIX + instance_PLUGIN_ID);
			parameter.setElementId(_prm.value());
			parameter.setName(_nm != null ? _nm.value() : null);
			parameter.setTypeId(_typ != null ? _typ.value() : null);
			parameter.setOptional(_opt != null ? _opt.value() : false);
			
			internalFindTags(implementationClass, parameter, CommandParameterTags.class);			
			
			return parameter;
		}
		return null;
	}
	
	/*
	 * Searches the methods of the implementation class for the annotation.
	 * The first method that matches the annotation is use, no other methods will be processed after.
	 */
	@SuppressWarnings("unchecked")
	private void internalFindPersistedState(Object implementationClass, MApplicationElement element, Class<? extends Annotation> annotation) { 
		try {
			Method[] methods = implementationClass.getClass().getDeclaredMethods();
			for (int i=0; i<methods.length; i++) 
				if (methods[i].isAnnotationPresent(annotation)) {
					element.getPersistedState().putAll((Map<String,String>) methods[i].invoke(implementationClass, _void));
					break;
				}
		} catch (IllegalAccessException ex) {
			logger.error(ex, CommandsProcessorMessages.ILLEGAL_ACCESS_PERSISTED + " : " + implementationClass);
		} catch (InvocationTargetException ex) {
			logger.error(ex, CommandsProcessorMessages.INVOCATION_EXCEPTION_PERSISTED + " : " + implementationClass);
		}
	}

	private void internalFindCoreExpression(Object implementationClass, MUIElement element) {
		Method[] methods = implementationClass.getClass().getDeclaredMethods();
		for (int i=0; i<methods.length; i++) 
			if (methods[i].isAnnotationPresent(CoreExpression.class)) {
				// Model safe???
				MCoreExpression expression = uiFactory.createCoreExpression();
				CoreExpression _xpr = methods[i].getAnnotation(CoreExpression.class);				
				expression.setContributorURI(CONTRIBUTOR_URI_PREFIX + instance_PLUGIN_ID);
				expression.setElementId(_xpr.value());
				expression.setCoreExpressionId(_xpr.value());
				internalFindTags(implementationClass, expression, CoreExpressionTags.class);
				element.setVisibleWhen(expression);
				break;
			}
	}
		
}
