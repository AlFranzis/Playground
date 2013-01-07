package org.eclipse.egit.bc;

import org.eclipse.osgi.util.NLS;

public class UIText extends NLS {
	
	private static final String BUNDLE_NAME = "org.eclipse.egit.bc.uitext"; //$NON-NLS-1$
	
	public static String BeyondCompareEgit_PreferencePageTitle;
	
	public static String BeyondCompareEgit_ExecutablePath;
	
	static {
		initializeMessages(BUNDLE_NAME, UIText.class);
	}
}