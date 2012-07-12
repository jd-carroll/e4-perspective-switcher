package org.eclipse.e4.ui.workbench.ide.tools;

import java.util.Collections;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspectiveStack;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.menu.MToolControl;
import org.eclipse.e4.ui.workbench.IResourceUtilities;
import org.eclipse.e4.ui.workbench.commands.internal.util.E4WBCommandsActivator;
import org.eclipse.e4.ui.workbench.commands.internal.util.ModelUtil;
import org.eclipse.e4.ui.workbench.commands.util.IE4WorkbenchCommandConstants;
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
import org.eclipse.ui.IWorkbenchCommandConstants;

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
	
	
	@PostConstruct
	void init() {
		// Subscribe to appropriate EventBroker events
		
		// Subsribe to property change events
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
		MPerspectiveStack perspectiveStack = ModelUtil.getPerspectiveStack(modelService, window);
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
				// TODO: Remove dependency on org.eclipse.ui.workbench
				.createCommand(IWorkbenchCommandConstants.PERSPECTIVES_SHOW_PERSPECTIVE, Collections.EMPTY_MAP);
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