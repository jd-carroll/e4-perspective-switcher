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
package org.eclipse.e4.ui.workbench.ide.handlers;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;

import javax.inject.Named;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspectiveStack;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.commands.annotations.Handler;
import org.eclipse.e4.ui.workbench.commands.annotations.HandlerCommand;
import org.eclipse.e4.ui.workbench.commands.annotations.HandlerPersistedState;
import org.eclipse.e4.ui.workbench.commands.annotations.HandlerTags;
import org.eclipse.e4.ui.workbench.ide.commands.E4WorkbenchCommandConstants;
import org.eclipse.e4.ui.workbench.ide.internal.dialogs.SelectPerspectiveDialog;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

@Handler(E4WorkbenchHandlerConstants.PERSPECTIVES_SHOW_PERSPECTIVE)
@HandlerCommand(E4WorkbenchCommandConstants.PERSPECTIVES_SHOW_PERSPECTIVE)
public final class ShowPerspectiveHandler {
	
	@HandlerTags
	public String[] tags = {"tag1", "tag2"};

	@Execute
	public void execute(IEclipseContext context, 
			@Optional @Named(E4WorkbenchCommandConstants.PERSPECTIVES_SHOW_PERSPECTIVE_PARM_ID) String perspectiveID,
			@Optional @Named(E4WorkbenchCommandConstants.PERSPECTIVES_SHOW_PERSPECTIVE_PARM_NEWWINDOW) String newWindow) 
					throws InvocationTargetException, InterruptedException {
		
		if (perspectiveID == null || perspectiveID.equals("")) 
			openSelectionDialog(context);
		else if (Boolean.parseBoolean(newWindow))
			openNewWindowPerspective(context, perspectiveID);
		else
			openPerspective(context, perspectiveID);
	}

	/**
	 * Opens the specified perspective in a new window.
	 * 
	 * @param perspectiveId
	 *            The perspective to open; must not be <code>null</code>
	 * @throws ExecutionException
	 *             If the perspective could not be opened.
	 */
	private void openNewWindowPerspective(IEclipseContext context, String perspectiveID) {
		MApplication application = context.get(MApplication.class);
		EPartService partService = context.get(EPartService.class);
		EModelService modelService = context.get(EModelService.class);
		
		List<MPerspective> perspectives = modelService.findElements(application, perspectiveID, MPerspective.class, null);
		partService.switchPerspective(perspectives.get(0));
	}

	/**
	 * Opens a view selection dialog, allowing the user to chose a view.
	 * 
	 * @throws ExecutionException
	 *             If the perspective could not be opened.
	 */
	private final void openSelectionDialog(IEclipseContext context) {
		SelectPerspectiveDialog dialog = ContextInjectionFactory.make(SelectPerspectiveDialog.class, context);
		dialog.open();
	}

	/**
	 * Opens the perspective with the given identifier.
	 * 
	 * @param perspectiveId
	 *            The perspective to open; must not be <code>null</code>
	 * @throws ExecutionException
	 *             If the perspective could not be opened.
	 */
	private final void openPerspective(IEclipseContext context, String perspectiveID) {
		MApplication application = context.get(MApplication.class);
		EPartService partService = context.get(EPartService.class);
		EModelService modelService = context.get(EModelService.class);
		
		List<MPerspective> perspectives = modelService.findElements(application, perspectiveID, MPerspective.class, null);
		partService.switchPerspective(perspectives.get(0));
	}
	
	@HandlerPersistedState
	public HashMap<String,String> retrievePersistedState() {
		HashMap<String,String> def = new HashMap<String,String>(2);
		return def;
	}
}
