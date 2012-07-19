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
package org.eclipse.e4.ui.workbench.perspectiveswitcher;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.commands.MCategory;
import org.eclipse.e4.ui.model.application.commands.MCommand;
import org.eclipse.e4.ui.model.application.commands.MCommandsFactory;
import org.eclipse.e4.ui.model.application.commands.MHandler;
import org.eclipse.e4.ui.workbench.perspectiveswitcher.commands.E4WorkbenchCommandConstants;
import org.eclipse.e4.ui.workbench.perspectiveswitcher.handlers.ClosePerspectiveHandler;
import org.eclipse.e4.ui.workbench.perspectiveswitcher.handlers.E4WorkbenchHandlerConstants;
import org.eclipse.e4.ui.workbench.perspectiveswitcher.handlers.ResetPerspectiveHandler;
import org.eclipse.e4.ui.workbench.perspectiveswitcher.handlers.SaveAsPerspectiveHandler;
import org.eclipse.e4.ui.workbench.perspectiveswitcher.handlers.ShowPerspectiveHandler;
import org.eclipse.e4.ui.workbench.perspectiveswitcher.handlers.ShowTextPerspectiveHandler;

public class E4PerspectiveSwitcherProcessor {

	@Inject private MApplication application;
	
	MCommandsFactory commandsFactory = MCommandsFactory.INSTANCE;
	
	String contributorURI = E4PerspectiveSwitcherActivator.getDefault().getPlatformURI();
	
	@Execute
	void init() {
		// Perspectives Category definition
		MCategory perspectivesCategory = commandsFactory.createCategory();
		perspectivesCategory.setContributorURI(contributorURI);
		perspectivesCategory.setDescription(E4WorkbenchCommandConstants.CATEGORY_PERSPECTIVES$_DESCRIPTION);
		perspectivesCategory.setElementId(E4WorkbenchCommandConstants.CATEGORY_PERSPECTIVES);
		perspectivesCategory.setName(E4WorkbenchCommandConstants.CATEGORY_PERSPECTIVES$_NAME);
		application.getCategories().add(perspectivesCategory);
				
		{ // SaveAs command & handler definition
			MCommand saveAsCommand = commandsFactory.createCommand();
			saveAsCommand.setContributorURI(contributorURI);
			saveAsCommand.setElementId(E4WorkbenchCommandConstants.PERSPECTIVES_SAVE_AS);
			saveAsCommand.setCategory(perspectivesCategory);
			saveAsCommand.setCommandName(E4WorkbenchCommandConstants.PERSPECTIVES_SAVE_AS$_NAME);
			saveAsCommand.setDescription(E4WorkbenchCommandConstants.PERSPECTIVES_SAVE_AS$_DESCRIPTION);
			application.getCommands().add(saveAsCommand);
			
			MHandler saveAsHandler = commandsFactory.createHandler();
			saveAsHandler.setContributorURI(contributorURI);
			saveAsHandler.setElementId(E4WorkbenchHandlerConstants.PERSPECTIVES_SAVE_AS);
			saveAsHandler.setCommand(saveAsCommand);
			saveAsHandler.setContributionURI(E4PerspectiveSwitcherActivator.getDefault().getResourceURI(SaveAsPerspectiveHandler.class));
			application.getHandlers().add(saveAsHandler);			
		}
		
		{ // Reset command & handler definition
			MCommand resetCommand = commandsFactory.createCommand();
			resetCommand.setContributorURI(contributorURI);
			resetCommand.setElementId(E4WorkbenchCommandConstants.PERSPECTIVES_RESET);
			resetCommand.setCategory(perspectivesCategory);
			resetCommand.setCommandName(E4WorkbenchCommandConstants.PERSPECTIVES_RESET$_NAME);
			resetCommand.setDescription(E4WorkbenchCommandConstants.PERSPECTIVES_RESET$_DESCRIPTION);
			application.getCommands().add(resetCommand);
			
			MHandler resetHandler = commandsFactory.createHandler();
			resetHandler.setContributorURI(contributorURI);
			resetHandler.setElementId(E4WorkbenchHandlerConstants.PERSPECTIVES_RESET);
			resetHandler.setCommand(resetCommand);
			resetHandler.setContributionURI(E4PerspectiveSwitcherActivator.getDefault().getResourceURI(ResetPerspectiveHandler.class));
		}
		
		{ // Close command & handler definition
			MCommand closeCommand = commandsFactory.createCommand();
			closeCommand.setContributorURI(contributorURI);
			closeCommand.setElementId(E4WorkbenchCommandConstants.PERSPECTIVES_CLOSE);
			closeCommand.setCategory(perspectivesCategory);
			closeCommand.setCommandName(E4WorkbenchCommandConstants.PERSPECTIVES_CLOSE$_NAME);
			closeCommand.setDescription(E4WorkbenchCommandConstants.PERSPECTIVES_CLOSE$_DESCRIPTION);
			application.getCommands().add(closeCommand);
			
			MHandler closeHandler = commandsFactory.createHandler();
			closeHandler.setContributorURI(contributorURI);
			closeHandler.setElementId(E4WorkbenchHandlerConstants.PERSPECTIVES_CLOSE);
			closeHandler.setCommand(closeCommand);
			closeHandler.setContributionURI(E4PerspectiveSwitcherActivator.getDefault().getResourceURI(ClosePerspectiveHandler.class));
			application.getHandlers().add(closeHandler);
		}
		
		{ // ShowText command & handler definition
			MCommand showTextCommand = commandsFactory.createCommand();
			showTextCommand.setContributorURI(contributorURI);
			showTextCommand.setElementId(E4WorkbenchCommandConstants.PERSPECTIVES_SHOW_TEXT);
			showTextCommand.setCategory(perspectivesCategory);
			showTextCommand.setCommandName(E4WorkbenchCommandConstants.PERSPECTIVES_SHOW_TEXT$_NAME);
			showTextCommand.setDescription(E4WorkbenchCommandConstants.PERSPECTIVES_SHOW_TEXT$_DESCRIPTION);
			application.getCommands().add(showTextCommand);
			
			MHandler showTextHandler = commandsFactory.createHandler();
			showTextHandler.setContributorURI(contributorURI);
			showTextHandler.setElementId(E4WorkbenchHandlerConstants.PERSPECTIVES_SHOW_TEXT);
			showTextHandler.setCommand(showTextCommand);
			showTextHandler.setContributionURI(E4PerspectiveSwitcherActivator.getDefault().getResourceURI(ShowTextPerspectiveHandler.class));
		}
		
		{ // Show Perspective command & handler
			MCommand showPerspectiveCommand = commandsFactory.createCommand();
			showPerspectiveCommand.setContributorURI(contributorURI);
			showPerspectiveCommand.setElementId(E4WorkbenchCommandConstants.PERSPECTIVES_SHOW_PERSPECTIVE);
			showPerspectiveCommand.setCategory(perspectivesCategory);
			showPerspectiveCommand.setCommandName(E4WorkbenchCommandConstants.PERSPECTIVES_SHOW_PERSPECTIVE$_NAME);
			showPerspectiveCommand.setDescription(E4WorkbenchCommandConstants.PERSPECTIVES_SHOW_PERSPECTIVE$_DESCRIPTION);
			application.getCommands().add(showPerspectiveCommand);
			
			MHandler showTextHandler = commandsFactory.createHandler();
			showTextHandler.setContributorURI(contributorURI);
			showTextHandler.setElementId(E4WorkbenchHandlerConstants.PERSPECTIVES_SHOW_PERSPECTIVE);
			showTextHandler.setCommand(showPerspectiveCommand);
			showTextHandler.setContributionURI(E4PerspectiveSwitcherActivator.getDefault().getResourceURI(ShowPerspectiveHandler.class));
			
		}
	}
	
	
}
