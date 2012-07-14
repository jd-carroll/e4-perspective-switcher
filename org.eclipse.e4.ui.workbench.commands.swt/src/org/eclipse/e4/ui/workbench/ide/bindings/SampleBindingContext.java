package org.eclipse.e4.ui.workbench.ide.bindings;

import org.eclipse.e4.ui.workbench.annotations.bindings.BindingContext;
import org.eclipse.e4.ui.workbench.annotations.bindings.BindingContextDescription;
import org.eclipse.e4.ui.workbench.annotations.bindings.BindingContextName;
import org.eclipse.e4.ui.workbench.annotations.bindings.BindingContextParent;
import org.eclipse.e4.ui.workbench.annotations.bindings.BindingContextTags;

@BindingContext(E4WorkbenchBindingConstants.BINDING_CONTEXT_SAMPLE)
@BindingContextName(E4WorkbenchBindingConstants.BINDING_CONTEXT_SAMPLE_NAME)
@BindingContextDescription(E4WorkbenchBindingConstants.BINDING_CONTEXT_SAMPLE_DESCRIPTION)
@BindingContextParent(E4WorkbenchBindingConstants.BINDING_CONTEXT_ECLIPSE_DEFAULT)
public class SampleBindingContext {
	
	@BindingContextTags
	public String[] tags = {"tag1", "tag2"};

}
