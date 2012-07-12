package org.eclipse.e4.ui.workbench.ide.internal.dialogs;

import java.util.HashMap;
import java.util.Iterator;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.workbench.IResourceUtilities;
import org.eclipse.e4.ui.workbench.commands.internal.util.E4WBCommandsActivator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;


@Creatable
public class PerspectiveLabelProvider extends LabelProvider implements ITableLabelProvider {

	@Inject
	private Logger logger;
	
	@Inject
	private IResourceUtilities<?> resourceUtilities;
	
	@Inject
	private TranslationService translationService;
	
	/**
	 * List of all Image objects this label provider is responsible for.
	 */
	private HashMap<ImageDescriptor, Image> imageCache = new HashMap<ImageDescriptor, Image>(5);

	/**
	 * Indicates whether the default perspective is visually marked.
	 */
	private boolean markActive = true;
	

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
	 */
	@Override
	public final Image getImage(Object element) {
		Image icon = null;
		
		if (element instanceof MPerspective) {
			MPerspective perspective = (MPerspective) element;
			
			String _uri = perspective.getIconURI();
			ImageDescriptor descriptor = null;
			
			try {
				URI iconURI = URI.createURI(_uri);
				descriptor = (ImageDescriptor) resourceUtilities.imageDescriptorFromURI(iconURI);
			} catch (RuntimeException ex) {
				logger.error("PerspectiveLabelProvider: uri=" + _uri);
			}
			
			if (descriptor != null) {
				icon = imageCache.get(descriptor);
				
				if (icon == null) {
					icon = descriptor.createImage();
					imageCache.put(descriptor, icon);
				}
			}
		}
		
		return icon;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.BaseLabelProvider#dispose()
	 */
	@Override
	public final void dispose() {
		for (Iterator<Image> i = imageCache.values().iterator(); i.hasNext();) {
			i.next().dispose();
		}
		imageCache.clear();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	@Override
	public final String getText(Object element) {
		String label = translationService.translate("unknown", 
				"platform:/plugin/" + E4WBCommandsActivator.PLUGIN_ID); //$NON-NLS-1$ 
		
		if (element instanceof MPerspective) {
			MPerspective perspective = (MPerspective) element;
			String _lbl = perspective.getLocalizedLabel();
			
			if (_lbl != null && !_lbl.equals(""))
				label = _lbl;			
			
			if (markActive && perspective.isVisible()) {
				label = label + "(" + translationService.translate("active", 
						"platform:/plugin/" + E4WBCommandsActivator.PLUGIN_ID) + ")"; //$NON-NLS-1$
			}
		}
		return label;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	@Override
	public final Image getColumnImage(Object element, int columnIndex) {
		return getImage(element);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	@Override
	public final String getColumnText(Object element, int columnIndex) {
		return getText(element);
	}
}
