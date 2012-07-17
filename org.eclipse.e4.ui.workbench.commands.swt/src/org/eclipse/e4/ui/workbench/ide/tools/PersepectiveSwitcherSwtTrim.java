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

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.ui.internal.workbench.WorkbenchLogger;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspectiveStack;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.menu.MToolControl;
import org.eclipse.e4.ui.workbench.IResourceUtilities;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.ide.commands.E4WorkbenchCommandConstants;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.accessibility.AccessibleListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.internal.IWorkbenchGraphicConstants;
import org.eclipse.ui.internal.WorkbenchImages;

public class PersepectiveSwitcherSwtTrim implements IPerspectiveSwitcherControl {

	@Inject private WorkbenchLogger logger;
	
	@Inject private EModelService modelService;
	
	@Inject private EPartService partService;
	
	@Inject private ECommandService commandService;
	
	@Inject private EHandlerService handlerService;
	
	@Inject private IResourceUtilities<?> resourceUtilities;
	
	@Inject private MWindow window;
	
	@Inject EPerspectiveSwitcher perspectiveSwitcher;
	
	//
	Composite composite;
	ToolBar toolBar;
	
	//
	@Inject @Preference(value=E4PerspectiveSwitcherPreferences.SHOW_TEXT, nodePath=E4PerspectiveSwitcherPreferences.NODE_PATH)
	private boolean showShortcutText;
	static String _null = ""; //$NON-NLS-1$
	
	//
	static RGB defaultContainerCurveColor = new RGB(0,0,0); 
	Image openPerspectiveImage;
	Color internalCrvColor; 
	
	// CSS Styles
	Color containerCurveColor;
	
	@PostConstruct
	void init() {
		perspectiveSwitcher.setControlProvider(this);
		
		// Start listening to control specific display property
		
	}
	
	@PreDestroy
	void cleanUp() {
		if (perspectiveSwitcher != null)
			perspectiveSwitcher.setControlProvider(null);
		perspectiveSwitcher = null;
		
		// Stop listening to control specific display property
		
		disposeToolBarImages();
		dispose();
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
				else if (item == null)
					logger.debug("No item found");
				else
					logger.debug("Perspective not associated with item");
			}
		});
		
		toolBar.addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent event) {
				disposeToolBarImages();
			}
		});
		
		// TODO: Implement accessibility features & I10n
		toolBar.getAccessible().addAccessibleListener(new AccessibleListener() {
			
			@Override
			public void getName(AccessibleEvent e) {
				
			}
			
			@Override
			public void getKeyboardShortcut(AccessibleEvent e) {
				
			}
			
			@Override
			public void getHelp(AccessibleEvent e) {
				
			}
			
			@Override
			public void getDescription(AccessibleEvent e) {
				
			}
		});

		ToolItem perspectiveDialog = new ToolItem(toolBar, SWT.PUSH);
		perspectiveDialog.setImage(getOpenPerspectiveImage());
		perspectiveDialog.setToolTipText(E4WorkbenchMessages.OpenSelectPerspectiveWindow);
		perspectiveDialog.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent event) {
				openPerspectiveDialog();
			}
		});
		
		// The separator
		new ToolItem(toolBar, SWT.SEPARATOR);
		
		// The perspectives currently open
		List<MPerspectiveStack> appPerspectiveStacks = modelService.findElements(window, null, MPerspectiveStack.class, null);
		if (appPerspectiveStacks.size() > 0) {
			for (MPerspectiveStack stack : appPerspectiveStacks)
				for (MPerspective perspective : stack.getChildren()) {
					if (perspective.isToBeRendered())
						addPerspectiveShortcut(perspective);
				}
		}
	}
	
	void paintContainer(PaintEvent event) {
		GC gc = event.gc;
		gc.setAdvanced(true);
		if (gc.getAdvanced())
			gc.setAntialias(SWT.ON);

		Point size = composite.getSize();
		int h = size.y;
		int[] simpleCurve = new int[] {0,h-1, 1,h-1, 2,h-2, 2,1, 3,0};
		
		gc.setForeground(getContainerCurveColor(event));
		gc.drawPolyline(simpleCurve);

		Rectangle bounds = ((Control) event.widget).getBounds();
		bounds.x = bounds.y = 0;
		Region r = new Region();
		r.add(bounds);
		
		int[] simpleCurveClose = new int[simpleCurve.length + 4];
		System.arraycopy(simpleCurve, 0, simpleCurveClose, 0, simpleCurve.length);
		int index = simpleCurve.length;
		simpleCurveClose[index++] = bounds.width;
		simpleCurveClose[index++] = 0;
		simpleCurveClose[index++] = bounds.width;
		simpleCurveClose[index++] = bounds.height;
		
		r.subtract(simpleCurveClose);
		
		Region clipping = new Region();
		gc.getClipping(clipping);
		r.intersect(clipping);
		gc.setClipping(r);

		clipping.dispose();
		r.dispose();
	}
	
	void openMenuFor(ToolItem item, MPerspective perspective) {
		final Menu menu = new Menu(toolBar);
		menu.setData(perspective);
		
		MElementContainer<MUIElement> parentStack =  perspective.getParent();
		boolean perspectiveSelected = parentStack.getSelectedElement() == perspective;
		boolean perspectiveStackSelected = parentStack.getParent().getSelectedElement() == parentStack;
		
		if (perspectiveSelected && perspectiveStackSelected) {
			addSaveAsMenuItem(menu);
			addResetMenuItem(menu);
		}
		
		if (perspective.isVisible()) {
			addCloseMenuItem(menu);
		}
		
		new MenuItem(menu, SWT.SEPARATOR);
		
		addShowTextMenuItem(menu);
		
		Rectangle bounds = item.getBounds();
		Point point = toolBar.toDisplay(bounds.x, bounds.y + bounds.height);
		menu.setLocation(point);
		menu.setVisible(true);
		menu.addMenuListener(new MenuListener() {
			
			@Override
			public void menuShown(MenuEvent e) {
				// do nothing
			}
			
			@Override
			public void menuHidden(MenuEvent e) {
				toolBar.getDisplay().asyncExec(new Runnable() {
					
					@Override
					public void run() {
						menu.dispose();
						
					}
				});
			}
		});
	}
	
	void openPerspectiveDialog() {
		ParameterizedCommand command = commandService.createCommand(
				E4WorkbenchCommandConstants.PERSPECTIVES_SHOW_PERSPECTIVE, Collections.EMPTY_MAP);
		handlerService.executeHandler(command);
	}

	@Override
	public void addPerspectiveShortcut(MPerspective perspective) {
		ToolItem shortcut = new ToolItem(toolBar, SWT.RADIO);
		shortcut.setData(perspective);
		
		ImageDescriptor descriptor = getIconFor(perspective.getIconURI());
				
		if (descriptor != null) {
			Image icon = descriptor.createImage();
			shortcut.setImage(icon);
		}
		
		if (descriptor == null || showShortcutText) {
			String label = perspective.getLocalizedLabel();
			shortcut.setText(label != null ? label : _null);
			shortcut.setToolTipText(perspective.getLocalizedTooltip());
		} else {
			shortcut.setText(_null);
			shortcut.setToolTipText(perspective.getLocalizedLabel());
		}
		
		//shortcut.setSelection(perspective == perspective.getParent().getSelectedElement());
		
		shortcut.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent event) {
				MPerspective perspective = (MPerspective) event.widget.getData();
				MElementContainer<MUIElement> parentStack =  perspective.getParent();
				parentStack.setSelectedElement(perspective);
				// set the perspective stack as the selected perspective stack
				parentStack.getParent().setSelectedElement(parentStack);
			}
		});
		
		// update the layout ???

	}

	@Override
	public void removePerspectiveShortcut(MPerspective perspective) {
		ToolItem item = getToolItemFor(perspective);
		if (item == null || item.isDisposed())
			return;
		
		Image icon = item.getImage();
		if (icon != null) {
			item.setImage(null);
			icon.dispose();
			icon = null;
		}
		
		item.dispose();
		
		// update the layout
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.e4.ui.workbench.ide.tools.IPerspectiveSwitcherControl#setSelectedElement(org.eclipse.e4.ui.model.application.ui.advanced.MPerspective)
	 */
	@Override
	public void setSelectedElement(MPerspective perspective) {
		for (ToolItem item : toolBar.getItems())
			item.setSelection(item.getData() == perspective);	
	}
	
	@Override
	public void updateAttributeFor(MPerspective perspective, String attName, Object newValue) {
		ToolItem item = getToolItemFor(perspective);
		
		if (showShortcutText && UIEvents.UILabel.LABEL.equals(attName)) {
			String newName = (String) newValue;
			item.setText(newName != null ? newName : _null);
		} else if (UIEvents.UILabel.TOOLTIP.equals(attName)) {
			String newTip = (String) newValue;
			item.setToolTipText(newTip);
		} else if (UIEvents.UILabel.ICONURI.equals(attName)) {
			ImageDescriptor descriptor = getIconFor((String) newValue);
			Image newIcon = null;			
			Image oldIcon = item.getImage();
			
			if (descriptor != null)
				newIcon = descriptor.createImage();
			item.setImage(newIcon);
			
			if (oldIcon != null) {
				oldIcon.dispose();
				oldIcon = null;
			}
			
			if (!showShortcutText) {
				String label = null;
				if (item.getData() instanceof MPerspective)
					label = ((MPerspective) item.getData()).getLocalizedLabel();
				else
					label = item.getText();
				
				item.setText(item.getImage() == null ? label : _null);
			}
		}
	}
	
	@Inject
	void setShowText(@Preference(value=E4PerspectiveSwitcherPreferences.SHOW_TEXT,
			nodePath=E4PerspectiveSwitcherPreferences.NODE_PATH) boolean showShortcutText) {
		this.showShortcutText = showShortcutText;
		
		for (ToolItem item : toolBar.getItems()) {
			if (!(item.getData() instanceof MPerspective))
				continue;
						
			MPerspective perspective = (MPerspective) item.getData();
			if (item.getImage() == null || this.showShortcutText) {
				String label = perspective.getLocalizedLabel(); 
				item.setText(label != null ? label : _null); 
				item.setToolTipText(perspective.getLocalizedTooltip());
			} else {
				item.setText(_null);
				item.setToolTipText(perspective.getLocalizedLabel());
			}
		}
		
		// update the layout
		
	}
	
	ToolItem getToolItemFor(MPerspective perspective) {
		if (toolBar == null || toolBar.isDisposed())
			return null;
		
		ToolItem toolItem = null;
		// Items 0 & 1 in the toolbar are never perspectives
		for (int i=2; i<toolBar.getItems().length && toolItem == null; i++) {
			if (toolBar.getItem(i).getData() == perspective)
				toolItem = toolBar.getItem(i);
		}
		
		return toolItem;
	}
	
	ImageDescriptor getIconFor(String iconURI) {
		ImageDescriptor descriptor = null;
		try {
			URI uri = URI.createURI(iconURI);
			descriptor = (ImageDescriptor) resourceUtilities.imageDescriptorFromURI(uri);
		} catch (RuntimeException ex) {
			logger.debug(ex, "icon uri=" + iconURI);
		}
		return descriptor;
	}
	
	/**
	 * Returns the open perspective image
	 * TODO: Evaluate whether this needs to ported into an 
	 * Eclipse4 friendly version.
	 *  
	 * @return the image
	 */
	Image getOpenPerspectiveImage() {
		if (openPerspectiveImage == null || openPerspectiveImage.isDisposed()) {
			ImageDescriptor desc = WorkbenchImages.getImageDescriptor(IWorkbenchGraphicConstants.IMG_ETOOL_NEW_PAGE);
			openPerspectiveImage = desc.createImage();
		}
		return openPerspectiveImage;
	}

	Color getContainerCurveColor(PaintEvent event) {
		Color curveColor = containerCurveColor;
		if (curveColor == null)
			curveColor = internalCrvColor != null ? internalCrvColor
					: new Color(event.display, defaultContainerCurveColor);
		
		return curveColor;
	}
	
	private void dispose() {
		if (internalCrvColor != null) {
			internalCrvColor.dispose();
			internalCrvColor = null;
		}
	}
	
	private void disposeToolBarImages() {
		if (toolBar == null || toolBar.isDisposed())
			return;
		
		for (ToolItem item : toolBar.getItems()) {
			Image icon = item.getImage();
			if (icon != null) {
				item.setImage(null);
				icon.dispose();
				icon = null;
			}
		}
	}
	
	// ///////////////////////////////////////////////////////////////
	// Menu Items
	// ///////////////////////////////////////////////////////////////
	
	private void addSaveAsMenuItem(Menu menu) {
		final MenuItem menuItem = new MenuItem(menu, SWT.Activate);
		menuItem.setText(E4WorkbenchCommandConstants.PERSPECTIVES_SAVE_AS$_NAME);
		
		// TODO: Integrate into help system
		
		menuItem.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent event) {
				ParameterizedCommand command = commandService
						.createCommand(E4WorkbenchCommandConstants.PERSPECTIVES_SAVE_AS, Collections.EMPTY_MAP);
				handlerService.executeHandler(command);
			}
		});		
	}
	
	private void addResetMenuItem(Menu menu) {
		final MenuItem menuItem = new MenuItem(menu, SWT.Activate);
		menuItem.setText(E4WorkbenchCommandConstants.PERSPECTIVES_RESET$_NAME);
		
		// TODO: Integrate into help system
		
		menuItem.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent event) {
				ParameterizedCommand command = commandService
						.createCommand(E4WorkbenchCommandConstants.PERSPECTIVES_RESET, Collections.EMPTY_MAP);
				handlerService.executeHandler(command);
			}
		});		
	}
	
	private void addCloseMenuItem(Menu menu) {
		final MenuItem menuItem = new MenuItem(menu, SWT.Activate);
		menuItem.setText(E4WorkbenchCommandConstants.PERSPECTIVES_CLOSE$_NAME);
		
		// TODO: Integrate into help system
		
		menuItem.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent event) {
				ParameterizedCommand command = commandService
						.createCommand(E4WorkbenchCommandConstants.PERSPECTIVES_CLOSE, Collections.EMPTY_MAP);
				handlerService.executeHandler(command);
			}
		});		
	}
	
	private void addShowTextMenuItem(Menu menu) {
		final MenuItem menuItem = new MenuItem(menu, SWT.Activate | SWT.CHECK);
		menuItem.setText(E4WorkbenchCommandConstants.PERSPECTIVES_SHOW_TEXT$_NAME);
		
		// TODO: Integrate into help system
		
		menuItem.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent event) {
				ParameterizedCommand command = commandService
						.createCommand(E4WorkbenchCommandConstants.PERSPECTIVES_SHOW_TEXT, Collections.EMPTY_MAP);
				handlerService.executeHandler(command);
			}
		});		
	}
	
}
