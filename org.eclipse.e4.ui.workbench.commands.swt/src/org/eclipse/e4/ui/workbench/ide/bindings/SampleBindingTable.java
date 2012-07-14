package org.eclipse.e4.ui.workbench.ide.bindings;

import org.eclipse.e4.ui.workbench.annotations.bindings.BindingTable;
import org.eclipse.e4.ui.workbench.annotations.bindings.BindingTableContext;
import org.eclipse.e4.ui.workbench.annotations.bindings.BindingTableTags;

@BindingTable(E4WorkbenchBindingConstants.BINDING_TABLE_SAMPLE)
@BindingTableContext
public class SampleBindingTable {

	@BindingTableTags
	public String[] tags = {"tag1", "tag2"};
	
}
