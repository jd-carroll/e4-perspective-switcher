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
package org.eclipse.e4.ui.workbench.ide.tools;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

public class E4PerspectiveSwitcherPreferences {

	public static final String NODE_PATH = "org.eclipse.e4.ui.workbench"; //$NON-NLS-1$
	
	public static final String SHOW_TEXT = "perspective_switcher_show_text"; //$NON-NLS-1$
	
	static {
		IEclipsePreferences node = DefaultScope.INSTANCE.getNode(NODE_PATH);
		node.putBoolean(SHOW_TEXT, true);
	}
}
