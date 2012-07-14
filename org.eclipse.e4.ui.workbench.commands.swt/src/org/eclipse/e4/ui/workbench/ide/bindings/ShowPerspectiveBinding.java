package org.eclipse.e4.ui.workbench.ide.bindings;

import org.eclipse.e4.ui.workbench.annotations.bindings.KeyBinding;
import org.eclipse.e4.ui.workbench.annotations.bindings.KeyBindingCommand;
import org.eclipse.e4.ui.workbench.annotations.bindings.KeyBindingSequence;
import org.eclipse.e4.ui.workbench.annotations.bindings.KeyBindingTags;
import org.eclipse.e4.ui.workbench.annotations.parameters.Parameter;
import org.eclipse.e4.ui.workbench.annotations.parameters.ParameterName;
import org.eclipse.e4.ui.workbench.annotations.parameters.ParameterTags;
import org.eclipse.e4.ui.workbench.annotations.parameters.ParameterValue;
import org.eclipse.e4.ui.workbench.ide.commands.E4WorkbenchCommandConstants;
import org.eclipse.e4.ui.workbench.ide.parameters.E4WorkbenchParameterConstants;

@KeyBinding(E4WorkbenchBindingConstants.KEY_BINDING_SHOW_PERSPECTIVE)
@KeyBindingSequence("M1+M3+P")
@KeyBindingCommand(E4WorkbenchCommandConstants.PERSPECTIVES_SHOW_PERSPECTIVE)
public class ShowPerspectiveBinding {

	@KeyBindingTags
	public String[] tags = {"tag1", "tag2"};
	
	@Parameter(E4WorkbenchParameterConstants.PARAMETER_SHOW_PERSPECTIVE_BINDING)
	@ParameterName(E4WorkbenchParameterConstants.PARAMETER_SHOW_PERSPECTIVE_BINDING_NAME)
	@ParameterValue(E4WorkbenchParameterConstants.PARAMETER_SHOW_PERSPECTIVE_BINDING_VALUE)
	@ParameterTags
	public String[] parameter1 = {"param1", "param2"};
}
