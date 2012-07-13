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
package org.eclipse.e4.ui.workbench.commands.internal.util;

import org.eclipse.osgi.service.debug.DebugOptions;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.internal.WorkbenchPlugin;

public class E4Util {

	// debug tracing
	private static final String OPTION_DEBUG_E4 = "org.eclipse.ui.workbench/debug/e4"; //$NON-NLS-1$;

	public final static boolean DEBUG_E4 = false;

//	static {
//		E4WBCommandsActivator activator = E4WBCommandsActivator.getDefault();
//		if (activator == null)
//			DEBUG_E4 = false;
//		else {
//			DebugOptions debugOptions = activator.getDebugOptions();
//			if (debugOptions == null)
//				DEBUG_E4 = false;
//			else
//				DEBUG_E4 = debugOptions.getBooleanOption(OPTION_DEBUG_E4, false);
//		}
//	}

	public static void unsupported(String msg) throws UnsupportedOperationException {
		if (DEBUG_E4)
			WorkbenchPlugin.log("unsupported: " + msg); //$NON-NLS-1$
		// UnsupportedOperationException ex = new
		// UnsupportedOperationException(msg);
		// throw ex;
	}

	public static void message(String msg) throws UnsupportedOperationException {
		if (DEBUG_E4)
			WorkbenchPlugin.log(msg);
	}

}
