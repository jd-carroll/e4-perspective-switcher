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
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspectiveStack;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.menu.MToolControl;
import org.eclipse.e4.ui.workbench.IResourceUtilities;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.commands.internal.util.E4WBCommandsActivator;
import org.eclipse.e4.ui.workbench.ide.commands.E4WorkbenchCommandConstants;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.accessibility.AccessibleListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.MenuEvent;
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
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class E4PerspectiveSwitcherToolControl {
	public static final String PERSPECTIVE_SWITCHER_CONTROL_ID = "org.eclipse.e4.ui.PerspectiveSwitcher"; //$NON-NLS-1$
	public static final String PERSPECTIVE_SWITCHER_CONTEXT_MENU_ID = "#PerspectiveSwitcher.popup";

	@Inject
	private Logger logger;
	
	@Inject
	protected IEventBroker eventBroker;

	@Inject
	EModelService modelService;

	@Inject
	private MWindow window;
	
	@Inject
	private EHandlerService handlerService;

	@Inject
	private ECommandService commandService;
	
	//
	Composite composite;
	ToolBar toolBar;
	Control toolControlParent;
	
	// 
	MToolControl toolControl;
	
	@Inject
	private IResourceUtilities<?> resourceUtilities;
	
	//
	static RGB defaultContainerCurveColor = new RGB(0, 0, 0);
	Image perspectiveDialogImage;
	//Image trimBackground;
	Color containerCurveColor;
	
	// Control the selection & de-selection of the buttons in the perspective switcher
	private EventHandler selectionHandler = new EventHandler() {
		public void handleEvent(Event event) {
			if (toolBar.isDisposed() || toolControl == null) // fail fast
				return;

			MUIElement changedElement = (MUIElement) event.getProperty(UIEvents.EventTags.ELEMENT);
			if (!(changedElement instanceof MPerspectiveStack))
				return;

			// fail fast
			MPerspectiveStack perspectiveStack = (MPerspectiveStack) changedElement;
			if (!perspectiveStack.isToBeRendered())
				return;
			
			// PerspectiveStack's in a snippet???
			// The objective is to ensure we are changing the perspective at the right level
			// Does the window with the perspective switcher actually contain the perspective, or
			// does the window contain the window with the perspective????
			// The better way is to utilize the model service and check the injected toolControl
			// with the "nearest" toolControl to the changedElement.			
			MWindow perspWin = modelService.getTopLevelWindowFor(perspectiveStack);
			MWindow switcherWin = modelService.getTopLevelWindowFor(toolControl);
			if (perspWin != switcherWin)
				return;

			MPerspective selectedElement = perspectiveStack.getSelectedElement();
			for (ToolItem ti : toolBar.getItems()) {
				// testing this way makes me nervous, yes all of the elements are coming
				// from the same location, it still makes me nervous....
				ti.setSelection(ti.getData() == selectedElement);
			}
		}
	};
	
	// listens for changes to the perspective stack
	private EventHandler toBeRenderedHandler = new EventHandler() {
		public void handleEvent(Event event) {
			if (toolControl == null || toolBar.isDisposed()) // fail fast
				return;

			MUIElement changedElement = (MUIElement) event.getProperty(UIEvents.EventTags.ELEMENT);
			if (!(changedElement instanceof MPerspective))
				return;

			// fail fast - if the window isn't around why bother
			MPerspective perspective = (MPerspective) changedElement;
			if (!perspective.getParent().isToBeRendered())
				return;
			
			// Same issue with the perspective stack...  A better way needs to be 
			// implemented
			MWindow perspWin = modelService.getTopLevelWindowFor(changedElement);
			MWindow switcherWin = modelService.getTopLevelWindowFor(toolControl);
			if (perspWin != switcherWin)
				return;

			if (changedElement.isToBeRendered())
				addPerspectiveShortcut(perspective);
			else
				removePerspectiveShortcut(perspective);
		}
	};

	private EventHandler labelHandler = new EventHandler() {
		public void handleEvent(Event event) {
			if (toolControl == null || toolBar.isDisposed()) // fail fast
				return;

			MUIElement changedElement = (MUIElement) event.getProperty(UIEvents.EventTags.ELEMENT);
			if (!(changedElement instanceof MPerspective))
				return;

			// fail fast
			MPerspective perspective = (MPerspective) changedElement;
			if (!perspective.isToBeRendered())
				return;

			// same issue as above... 
			MWindow perspWin = modelService.getTopLevelWindowFor(changedElement);
			MWindow switcherWin = modelService.getTopLevelWindowFor(toolControl);
			if (perspWin != switcherWin)
				return;

			String attName = (String) event.getProperty(UIEvents.EventTags.ATTNAME);
			Object newValue = event.getProperty(UIEvents.EventTags.NEW_VALUE);
			
			for (ToolItem ti : toolBar.getItems()) {
				// again uncomfortable... 
				if (ti.getData() == perspective)
					updateToolItem(ti, attName, newValue);
			}

			// update the layout
			toolBar.pack();
			toolBar.getShell().layout(new Control[] { toolBar }, SWT.DEFER);
		}

		// implement equivalent method for icon_uri	
		private void updateToolItem(ToolItem ti, String attName, Object newValue) {
			// wire up to new properties
			boolean showText = true;
			if (showText && UIEvents.UILabel.LABEL.equals(attName)) {
				String newName = (String) newValue;
				ti.setText(newName);
			} else if (UIEvents.UILabel.TOOLTIP.equals(attName)) {
				String newTTip = (String) newValue;
				ti.setToolTipText(newTTip);
			}
		}
	};

	// listens for new 'PerspectiveStack's 
	private EventHandler childrenHandler = new EventHandler() {
		public void handleEvent(Event event) {
			if (toolControl == null || toolBar.isDisposed()) // fail fast
				return;

			MUIElement changedObj = (MUIElement) event.getProperty(UIEvents.EventTags.ELEMENT);
			if (!(changedObj instanceof MPerspectiveStack))
				return;
			
			/*
			 * Consider the effects of the following...
			 * 
			 * // fail fast
			 * MPerspectiveStack perspectiveStack = (MPerspectiveStack) changedElement;
			 * if (!perspective.isToBeRendered())
			 * 	return;
			 */

			// again same thing...
			MWindow perspWin = modelService.getTopLevelWindowFor(changedObj);
			MWindow switcherWin = modelService.getTopLevelWindowFor(toolControl);
			if (perspWin != switcherWin)
				return;

			if (UIEvents.isADD(event)) {
				for (Object o : UIEvents.asIterable(event, UIEvents.EventTags.NEW_VALUE)) {
					MPerspective added = (MPerspective) o;
					// Adding invisible elements is a NO-OP
					if (!added.isToBeRendered())
						continue;

					addPerspectiveShortcut(added);
				}
			} else if (UIEvents.isREMOVE(event)) {
				for (Object o : UIEvents.asIterable(event, UIEvents.EventTags.OLD_VALUE)) {
					MPerspective removed = (MPerspective) o;
					// Removing invisible elements is a NO-OP
					if (!removed.isToBeRendered())
						continue;

					removePerspectiveShortcut(removed);
				}
			}
		}
	};

	@PostConstruct
	void init() {
		// that's a lot of unnecessary events...
		// investigate UIEvents.UIElement.Topic_All/Topic_Windows
		eventBroker.subscribe(UIEvents.UIElement.TOPIC_TOBERENDERED, toBeRenderedHandler);
		// is it really necessary to have a separate topic to change the button... No.
		// investigate piggy-backing onto toBeRenderedHandler (or similar)
		eventBroker.subscribe(UIEvents.ElementContainer.TOPIC_SELECTEDELEMENT, selectionHandler);
		
		eventBroker.subscribe(UIEvents.ElementContainer.TOPIC_CHILDREN, childrenHandler);

		
		eventBroker.subscribe(UIEvents.UILabel.TOPIC_ALL, labelHandler);	
	}

	@PreDestroy
	void cleanUp() {
		if (perspectiveDialogImage != null) {
			perspectiveDialogImage.dispose();
			perspectiveDialogImage = null;
		}

		eventBroker.unsubscribe(toBeRenderedHandler);
		eventBroker.unsubscribe(childrenHandler);
		eventBroker.unsubscribe(selectionHandler);
		eventBroker.unsubscribe(labelHandler);
	}
	
	@PostConstruct
	void createControl(Composite parent, MToolControl control) {
		composite = new Composite(parent, SWT.None);
		RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL);
		rowLayout.marginLeft = rowLayout.marginRight = 8;
		rowLayout.marginTop = 6;
		rowLayout.marginBottom = 4;
		composite.setLayout(rowLayout);
		
		// Paints the stylized container
		composite.addPaintListener(new PaintListener() {
			
			@Override
			public void paintControl(PaintEvent event) {
				paintContainer(event);				
			}
		});
		
		// Disposes of container and resources
		composite.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent event) {
				dispose(event);
			}
		});
		
		// XXX: Don't reach up, it would be better to have the background image
		//		of the trim bar injected than to reach up.
		toolControl = control;
		toolControlParent = (Control) toolControl.getParent().getWidget();
		
		toolBar = new ToolBar(composite, SWT.FLAT | SWT.WRAP | SWT.RIGHT);
				
		// Opens the PerspectiveBar context menu
		toolBar.addMenuDetectListener(new MenuDetectListener() {
			
			@Override
			public void menuDetected(MenuDetectEvent event) {
				ToolBar tb = (ToolBar) event.widget;
				Point p = new Point(event.x, event.y);
				
				p = toolBar.getDisplay().map(null, toolBar, p);
				ToolItem item = tb.getItem(p);
				if (item != null && item.getData() != null) 
					openMenuFor(item, (MPerspective) item.getData());
				else {
					// TODO: Be better about this....
					if (item == null)
						logger.error("PerspectiveSwitcher: No item found");
					else
						logger.error("PerspectiveSwitcher: Perspective not associated with item");
				}
			}
		});
		
		// Dispose of the toolbar specific resources
		toolBar.addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent event) {
				disposeToolBarImages(event);
			}
		});
		

		
		/// ////////////////////////////////////
		/// Create the toolbar controls
		///    ??? commands ???
		/// ////////////////////////////////////
		
		// Open perspective dialog
		ToolItem perspectiveDialog = new ToolItem(toolBar, SWT.PUSH);
		perspectiveDialog.setImage(getPerspectiveDialogImage());
		perspectiveDialog.setToolTipText("");
		perspectiveDialog.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent event) {
				openPerspectiveDialog();
			}
		});
		
		// The separator
		new ToolItem(toolBar, SWT.SEPARATOR);
		
		// The perspectives currently open
		MPerspectiveStack perspectiveStack = null;
		List<MPerspectiveStack> appPerspectives = modelService.findElements(window, null, MPerspectiveStack.class, null);
		if (appPerspectives.size() > 0)
			perspectiveStack = appPerspectives.get(0);
		if (perspectiveStack != null) {
			for (MPerspective perspective : perspectiveStack.getChildren()) {
				if (perspective.isToBeRendered()) {
					addPerspectiveShortcut(perspective);
				}
			}
		}
	}
	
	/**
	 * Paints the stylized container lines around the PerspectiveBar
	 * 
	 * @param event the paint event
	 */
	private void paintContainer(PaintEvent event) {
		Point size = composite.getSize();
		
		GC gc = event.gc;
		gc.setAdvanced(true);
		if (gc.getAdvanced())
			gc.setAntialias(SWT.ON);
		
		if (containerCurveColor == null)
			containerCurveColor = new Color(event.display, defaultContainerCurveColor);
		
		int h = size.y;
		int[] simpleCurve = new int[] {0,h-1, 1,h-1, 2,h-2, 2,1, 3,0};
		
		// draw border
		gc.setForeground(containerCurveColor);
		gc.drawPolyline(simpleCurve);
		
		// /////////////////////////////////////////////////////
		// Fills the container space in between the left edge of
		// the control and the container curve with the background
		// from the trim-bar.
		// /////////////////////////////////////////////////////

		// Create a description of the container 
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
		
		// Retrieve the area to the left of the container curve
		r.subtract(simpleCurveClose);
		
		// Retrieve the current renderable space in the GC
		Region clipping = new Region();
		gc.getClipping(clipping);
		
		// Set the renderable space only to that which has not been drawn on
		r.intersect(clipping);
		gc.setClipping(r);
		
		// XXX: Have the image injected!
		// 		Consider implementing a custom CSS styling feature for the background image
		// 
		// Better:
		// 		if (trimBackground != null && !trimBackground.isDisposed())
		// 			gc.drawImage(trimBackground, 0, 0);
		//
		// 		Then dispose of trimBackground in method dispose()
		
		// Paint the background image
		Image b = toolControlParent.getBackgroundImage();
		if (b != null && !b.isDisposed())
			gc.drawImage(b, 0, 0);

		clipping.dispose();
		r.dispose();
	}
	
	private void openMenuFor(ToolItem item, MPerspective perspective) {
		IEclipseContext context = perspective.getContext();
		Menu _menu = null;
		
		if (context.containsKey(PERSPECTIVE_SWITCHER_CONTEXT_MENU_ID))
			_menu = (Menu) context.get(PERSPECTIVE_SWITCHER_CONTEXT_MENU_ID);
		
		if (_menu == null) {
			logger.error("PerspectiveSwitcher: Context menu not available");
			return;
		}
		
		final Menu contextMenu = _menu;
		
		// Temporary
		new MenuItem(contextMenu, SWT.SEPARATOR);
		
		Rectangle bounds = item.getBounds();
		Point point = toolBar.toDisplay(bounds.x, bounds.y + bounds.height);
		contextMenu.setLocation(point.x, point.y);
		
		contextMenu.setVisible(true);
		
		contextMenu.addMenuListener(new MenuAdapter() {
			
			@Override
			public void menuHidden(MenuEvent e) {
				toolBar.getDisplay().asyncExec(new Runnable() {
					
					@Override
					public void run() {
						contextMenu.dispose();						
					}
				});
			}
		});		
	}
	
	/**
	 * Retrieves the 'open new perspective' icon
	 * 
	 * @return the image
	 */
	private Image getPerspectiveDialogImage() {
		if (perspectiveDialogImage == null || perspectiveDialogImage.isDisposed()) {
			ImageDescriptor descriptor = E4WBCommandsActivator
					.imageDescriptorFromPlugin(E4WBCommandsActivator.PLUGIN_ID, "icons/full/eview16/new_persp.gif");//$NON-NLS-1$

			perspectiveDialogImage = descriptor.createImage();
		}
		return perspectiveDialogImage;
	}
	
	/**
	 * Executes the workbench command to open the show perspectives
	 * dialog.
	 * @see IE4WorkbenchCommandConstants.PERSPECTIVES_SHOW_PERSPECTIVE
	 */
	private void openPerspectiveDialog() {
		ParameterizedCommand command = commandService
				.createCommand(E4WorkbenchCommandConstants.PERSPECTIVES_SHOW_PERSPECTIVE, Collections.EMPTY_MAP);
		handlerService.executeHandler(command);
	}
	
	private void addPerspectiveShortcut(MPerspective perspective) {
		final ToolItem shortcut = new ToolItem(toolBar, SWT.RADIO);
		shortcut.setData(perspective);
		
		String _uri = perspective.getIconURI();
		ImageDescriptor descriptor = null;
		
		try {
			URI iconURI = URI.createURI(_uri);
			descriptor = (ImageDescriptor) resourceUtilities.imageDescriptorFromURI(iconURI);
		} catch (RuntimeException ex) {
			logger.error("E4PerspectiveSwitcher: uri=" + _uri);
		}
		
		if (descriptor != null) {
			final Image icon = descriptor.createImage();
			
			shortcut.setImage(icon);
			
			shortcut.addDisposeListener(new DisposeListener() {
				
				@Override
				public void widgetDisposed(DisposeEvent e) {
					icon.dispose();
				}
			});
		}
		
		if (descriptor == null /*|| getShowTextPref == true*/) {
			shortcut.setText(perspective.getLocalizedLabel());
			shortcut.setToolTipText(perspective.getLocalizedTooltip());
		} else {
			shortcut.setToolTipText(perspective.getLocalizedLabel());
		}
		
		shortcut.setSelection(perspective == perspective.getParent().getSelectedElement());
		
		shortcut.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent event) {
				MPerspective perspective = (MPerspective) event.widget.getData();
				perspective.getParent().setSelectedElement(perspective);
			}
		});
		
		// XXX: update the layout
		toolBar.pack();
		toolBar.getShell().layout(new Control[] { toolBar }, SWT.DEFER);
	}
	
	private void removePerspectiveShortcut(MPerspective perspective) {
		
	}
	
	/**
	 * Disposes the PerspectiveBar container and all associated resources.
	 * The only resources left are the topics associated with the event
	 * broker.
	 *  
	 * @param event the dispose event
	 */
	private void dispose(DisposeEvent event) {
		
	}
	
	/**
	 * Disposes the tool bar specific resources.
	 * 
	 * @param event
	 */
	private void disposeToolBarImages(DisposeEvent event) {
		// Temporary, remove when externally managed
		if (perspectiveDialogImage != null ) {
			perspectiveDialogImage.dispose();
			perspectiveDialogImage = null;
		}
	}
	
}