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

import javax.inject.Named;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.workbench.commands.annotations.Handler;
import org.eclipse.e4.ui.workbench.commands.annotations.HandlerCommand;
import org.eclipse.e4.ui.workbench.commands.annotations.HandlerPersistedState;
import org.eclipse.e4.ui.workbench.commands.annotations.HandlerTags;
import org.eclipse.e4.ui.workbench.ide.commands.E4WorkbenchCommandConstants;
import org.eclipse.e4.ui.workbench.ide.internal.dialogs.SelectPerspectiveDialog;

@Handler(E4WorkbenchHandlerConstants.PERSPECTIVES_SHOW_PERSPECTIVE)
@HandlerCommand(E4WorkbenchCommandConstants.PERSPECTIVES_SHOW_PERSPECTIVE)
public final class ShowPerspectiveHandler {
	
	@HandlerTags
	String[] tags = {"tag1", "tag2"};

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
//		final IWorkbench workbench = PlatformUI.getWorkbench();
//		try {
//			IAdaptable input = ((Workbench) workbench).getDefaultPageInput();
//			workbench.openWorkbenchWindow(perspectiveId, input);
//		} catch (WorkbenchException e) {
//			ErrorDialog.openError(activeWorkbenchWindow.getShell(),
//					WorkbenchMessages.ChangeToPerspectiveMenu_errorTitle, e
//							.getMessage(), e.getStatus());
//		}
	}

	/**
	 * Opens a view selection dialog, allowing the user to chose a view.
	 * 
	 * @throws ExecutionException
	 *             If the perspective could not be opened.
	 */
	private final void openSelectionDialog(IEclipseContext context) {
		SelectPerspectiveDialog dialog = context.get(SelectPerspectiveDialog.class);
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
//		final IWorkbench workbench = PlatformUI.getWorkbench();
//
//		final IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
//		IPerspectiveDescriptor desc = activeWorkbenchWindow.getWorkbench()
//				.getPerspectiveRegistry().findPerspectiveWithId(perspectiveId);
//		if (desc == null) {
//			throw new ExecutionException("Perspective " + perspectiveId //$NON-NLS-1$
//					+ " cannot be found."); //$NON-NLS-1$
//		}
//
//		try {
//			if (activePage != null) {
//				activePage.setPerspective(desc);
//			} else {
//				IAdaptable input = ((Workbench) workbench)
//						.getDefaultPageInput();
//				activeWorkbenchWindow.openPage(perspectiveId, input);
//			}
//		} catch (WorkbenchException e) {
//			throw new ExecutionException("Perspective could not be opened.", e); //$NON-NLS-1$
//		}
	}
	
	@HandlerPersistedState
	HashMap<String,String> retrievePersistedState() {
		HashMap<String,String> def = new HashMap<String,String>(2);
		return def;
	}
}
