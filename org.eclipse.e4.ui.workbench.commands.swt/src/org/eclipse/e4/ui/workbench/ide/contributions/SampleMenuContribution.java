package org.eclipse.e4.ui.workbench.ide.contributions;

import org.eclipse.e4.ui.workbench.annotations.contributions.ContributionAccessability;
import org.eclipse.e4.ui.workbench.annotations.contributions.ContributionParent;
import org.eclipse.e4.ui.workbench.annotations.contributions.ContributionPosition;
import org.eclipse.e4.ui.workbench.annotations.contributions.ContributionRender;
import org.eclipse.e4.ui.workbench.annotations.contributions.ContributionTags;
import org.eclipse.e4.ui.workbench.annotations.contributions.ContributionVisible;
import org.eclipse.e4.ui.workbench.annotations.contributions.MenuContribution;

@MenuContribution(E4WorkbenchContributionConstants.MENU_SAMPLE)
@ContributionAccessability(E4WorkbenchContributionConstants.MENU_SAMPLE$_ACCESSIBLE)
@ContributionParent(E4WorkbenchContributionConstants.MENU_SAMPLE$_PARENT)
@ContributionPosition(E4WorkbenchContributionConstants.MENU_SAMPLE$_POSITION)
@ContributionRender(E4WorkbenchContributionConstants.MENU_SAMPLE$_RENDER)
@ContributionVisible(E4WorkbenchContributionConstants.MENU_SAMPLE$_VISIBLE)
public class SampleMenuContribution {

	@ContributionTags
	public String[] tags = {"tag1", "tag2"};
}
