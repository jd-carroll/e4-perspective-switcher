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
package org.eclipse.e4.ui.workbench.perspectiveswitcher.tools;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;

public class E4Util {
	
	@SuppressWarnings("unchecked")
	public static <T extends MUIElement> List<T> getMatchingChildren(MElementContainer<?> container, Class<T> type) {
		List<T> matchingChildren = new ArrayList<T>();
		
		for (Object child : container.getChildren()) {
			if (type.isInstance(child))
				matchingChildren.add((T) child);
		}		
		
		return matchingChildren;
	}

}
