package org.eclipse.e4.ui.workbench.ide.contributions;

import org.eclipse.e4.ui.workbench.annotations.contributions.ContributionAccessability;
import org.eclipse.e4.ui.workbench.annotations.contributions.ContributionParent;
import org.eclipse.e4.ui.workbench.annotations.contributions.ContributionPosition;
import org.eclipse.e4.ui.workbench.annotations.contributions.ContributionRender;
import org.eclipse.e4.ui.workbench.annotations.contributions.ContributionTags;
import org.eclipse.e4.ui.workbench.annotations.contributions.ContributionVisible;
import org.eclipse.e4.ui.workbench.annotations.contributions.MenuContribution;

@MenuContribution(E4WorkbenchContributionConstants.TOOLBAR_SAMPLE)
@ContributionAccessability(E4WorkbenchContributionConstants.TOOLBAR_SAMPLE$_ACCESSIBLE)
@ContributionParent(E4WorkbenchContributionConstants.TOOLBAR_SAMPLE$_PARENT)
@ContributionPosition(E4WorkbenchContributionConstants.TOOLBAR_SAMPLE$_POSITION)
@ContributionRender(E4WorkbenchContributionConstants.TOOLBAR_SAMPLE$_RENDER)
@ContributionVisible(E4WorkbenchContributionConstants.TOOLBAR_SAMPLE$_VISIBLE)
public class SampleToolbarContribution {

	@ContributionTags
	public String[] tags = {"tag1", "tag2"};
}
