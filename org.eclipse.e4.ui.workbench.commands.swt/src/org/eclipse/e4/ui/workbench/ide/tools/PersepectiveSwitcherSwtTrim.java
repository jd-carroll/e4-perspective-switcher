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

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.menu.MToolControl;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class PersepectiveSwitcherSwtTrim implements IPerspectiveSwitcherControl {

	@Inject
	private Logger logger;
	
	@Inject 
	EPerspectiveSwitcher perspectiveSwitcher;
	
	
	//
	Composite composite;
	ToolBar toolBar;
	
	//
	boolean debug;
	
	@PostConstruct
	void init() {
		perspectiveSwitcher.setControlProvider(this);
	}
	
	@PreDestroy
	void cleanUp() {
		
	}
	
	@PostConstruct
	void createControl(Composite parent, MToolControl control) {
		composite = new Composite(parent, SWT.None);
		RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL);
		rowLayout.marginLeft = rowLayout.marginRight = 8;
		rowLayout.marginTop = 6;
		rowLayout.marginBottom = 4;
		composite.setLayout(rowLayout);
		
		composite.addPaintListener(new PaintListener() {
			
			@Override
			public void paintControl(PaintEvent event) {
				paintContainer(event);				
			}
		});
		
		composite.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent event) {
				dispose();
			}
		});
		
		toolBar = new ToolBar(composite, SWT.FLAT | SWT.WRAP | SWT.RIGHT);
		
		toolBar.addMenuDetectListener(new MenuDetectListener() {
			
			@Override
			public void menuDetected(MenuDetectEvent event) {
				ToolBar tb = (ToolBar) event.widget;
				Point p = new Point(event.x, event.y);
				
				p = toolBar.getDisplay().map(null, toolBar, p);
				ToolItem item = tb.getItem(p);
				if (item != null && item.getData() != null) 
					openMenuFor(item, (MPerspective) item.getData());
				else if (debug) {
					if (item == null)
						logger.error("PerspectiveSwitcher: No item found");
					else
						logger.error("PerspectiveSwitcher: Perspective not associated with item");
				}
			}
		});
	}
	
	void paintContainer(PaintEvent event) {
		
	}
	
	void openMenuFor(ToolItem item, MPerspective perspective) {
		
	}

	@Override
	public void addPerspectiveShortcut(MPerspective perspective) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void removePerspectiveShortcut(MPerspective perspective) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setSelectedElement(MPerspective perspective) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void updateAttributeFor(MPerspective perspective, String attName, Object newValue) {
		// implement equivalent method for icon_uri	
			// wire up to new properties
			boolean showText = true;
			if (showText && UIEvents.UILabel.LABEL.equals(attName)) {
				String newName = (String) newValue;
				//ti.setText(newName);
			} else if (UIEvents.UILabel.TOOLTIP.equals(attName)) {
				String newTTip = (String) newValue;
				//ti.setToolTipText(newTTip);
			}		
		
	}

	private void dispose() {
		
	}
	
}
