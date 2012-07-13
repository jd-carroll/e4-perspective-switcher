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

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;

public class CommandsProcessor {

	@Inject
	private MApplication application;
	
	@Execute
	public void execute() {
		application.getCommands();
		
		 //Reflections reflections = new Reflections("my.project.prefix");

		 //Set<Class<? extends Object>> allClasses = 
		   //  reflections.getSubTypesOf(Object.class);
		
	}
	
}
