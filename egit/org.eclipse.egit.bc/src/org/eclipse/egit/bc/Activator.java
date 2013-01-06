package org.eclipse.egit.bc;

import org.eclipse.egit.bc.preferences.BeyondCompareEgitPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class Activator extends AbstractUIPlugin {
	private static Activator plugin;

	public Activator() {
		Activator.setActivator(this);
	}

	private static void setActivator(Activator a) {
		plugin = a;
	}
	
	public static Activator getDefault() {
		return plugin;
	}
	
	public static String getPluginId() {
		return getDefault().getBundle().getSymbolicName();
	}
	
	protected void initializeDefaultPreferences(IPreferenceStore store) {
		BeyondCompareEgitPreferencePage.initializeDefaultPreferences(store);
	}

}
