package org.eclipse.e4.ui.workbench.commands.internal.util;

import java.util.List;

import org.eclipse.e4.ui.model.application.ui.advanced.MPerspectiveStack;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

public class ModelUtil {

	public static MPerspectiveStack getPerspectiveStack(EModelService modelService, MWindow window) {
		if (modelService != null && window != null) {
			List<MPerspectiveStack> psList = modelService.findElements(window, null, MPerspectiveStack.class, null);
			if (psList.size() > 0)
				return psList.get(0);
		}
		return null;
	}

}
