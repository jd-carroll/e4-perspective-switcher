package org.eclipse.e4.ui.workbench.ide.categories;

import org.eclipse.e4.ui.workbench.annotations.categories.Category;
import org.eclipse.e4.ui.workbench.annotations.categories.CategoryDescription;
import org.eclipse.e4.ui.workbench.annotations.categories.CategoryName;
import org.eclipse.e4.ui.workbench.annotations.categories.CategoryTags;

@Category(E4WorkbenchCategoryConstants.CATEGORY_EXAMPLE)
@CategoryName(E4WorkbenchCategoryConstants.CATEGORY_EXAMPLE$NAME)
@CategoryDescription(E4WorkbenchCategoryConstants.CATEGORY_EXAMPLE$DESCRIPTION)
public class SampleCategory {

	@CategoryTags
	public String[] tags = {"tag1", "tag2"};
}
