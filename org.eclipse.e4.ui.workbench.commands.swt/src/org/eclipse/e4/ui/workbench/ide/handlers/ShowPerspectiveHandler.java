/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Remy Chi Jian Suen <remy.suen@gmail.com> - Bug 218540 [Perspectives] Perspectives always open in same window, regardless of preference
 *******************************************************************************/
package org.eclipse.e4.ui.workbench.ide.handlers;

import java.lang.reflect.InvocationTargetException;

import javax.inject.Named;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.workbench.commands.annotations.Handler;
import org.eclipse.e4.ui.workbench.ide.commands.E4WorkbenchCommandConstants;
import org.eclipse.e4.ui.workbench.ide.internal.dialogs.SelectPerspectiveDialog;

@Handler("")
public final class ShowPerspectiveHandler {

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
}
