package org.eclipselab.emf.codegen.protobuf.ui.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipselab.emf.codegen.protobuf.ui.internal.ProtobufGeneratorUiPlugin;

/**
 * @author Christian Kerl
 */
public class ProtobufGeneratorPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public static final String PAGE_ID = "org.eclipselab.emf.codegen.protobuf.ui.preferences.ProtobufGeneratorPreferencePage";
    
	public ProtobufGeneratorPreferencePage() {
		super(GRID);
		setPreferenceStore(ProtobufGeneratorUiPlugin.getDefault().getPreferenceStore());
		setDescription("ProtoBuf Preferences");
	}
	
	@Override
	public void createFieldEditors() 
	{
		addField(new FileFieldEditor(PreferenceConstants.P_PROTOBUF_COMPILER_PATH, "&ProtoBuf Compiler (protoc) path:", getFieldEditorParent()));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) 
	{
	}
}