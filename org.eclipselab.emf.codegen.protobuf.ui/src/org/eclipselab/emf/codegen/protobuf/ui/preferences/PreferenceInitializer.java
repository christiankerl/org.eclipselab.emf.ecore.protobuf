package org.eclipselab.emf.codegen.protobuf.ui.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import org.eclipselab.emf.codegen.protobuf.ui.internal.ProtobufGeneratorUiPlugin;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = ProtobufGeneratorUiPlugin.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.P_PROTOBUF_COMPILER_PATH, "protoc");
	}

}
