package org.eclipse.e4.ui.workbench.ide.commands;

public interface E4WorkbenchCommandConstants {

    /**
     * Id for command "Show Perspective" in category "Perspectives"
     * (value is <code>"org.eclipse.e4.ui.perspectives.showPerspective"</code>).
     */
    public static final String PERSPECTIVES_SHOW_PERSPECTIVE = "org.eclipse.e4.ui.perspectives.showPerspective"; //$NON-NLS-1$
    
	/**
	 * Id for parameter "Perspective Id" in command "Show Perspective" in
	 * category "Perspectives" (value is
	 * <code>"org.eclipse.e4.ui.perspectives.showPerspective.commandparamerter.perspectiveId"</code>).
	 */
	public static final String PERSPECTIVES_SHOW_PERSPECTIVE_PARM_ID = 
			"org.eclipse.e4.ui.perspectives.showPerspective.commandparamerter.perspectiveId"; //$NON-NLS-1$

	/**
	 * Id for parameter "In New Window" in command "Show Perspective" in
	 * category "Perspectives" (value is
	 * <code>"org.eclipse.e4.ui.perspectives.showPerspective.commandparamerter.newWindow"</code>).
	 * Optional.
	 */
	public static final String PERSPECTIVES_SHOW_PERSPECTIVE_PARM_NEWWINDOW = 
			"org.eclipse.e4.ui.perspectives.showPerspective.commandparamerter.newWindow"; //$NON-NLS-1$
}
