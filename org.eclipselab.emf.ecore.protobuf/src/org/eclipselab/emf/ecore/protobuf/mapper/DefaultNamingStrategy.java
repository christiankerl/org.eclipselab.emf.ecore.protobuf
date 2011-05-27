package org.eclipselab.emf.ecore.protobuf.mapper;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;

/**
 * DefaultNamingStrategy provides a standard implementation of {@link NamingStrategy}.
 * 
 * @author Christian Kerl
 */
public class DefaultNamingStrategy implements NamingStrategy {

	@Override
	public String getInternalIdField() {
		return "__id";
	}

	@Override
	public String getSubTypeField() {
		return "subtype";
	}

	@Override
	public String getSubTypeEnum() {
		return "SubType";
	}

	@Override
	public String getMessage(EClass eClass) {
		return eClass.getName();
	}

	@Override
	public String getEnum(EEnum eEnum) {
		return eEnum.getName();
	}

	@Override
	public String getRefMessage(EClass eClass) {
		return getMessage(eClass) + "Ref";
	}

	@Override
	public String getRefMessageField(EClass eClass) {
		String msg = getMessage(eClass);
		
		return msg.substring(0, 1).toLowerCase() + msg.substring(1);
	}

	@Override
	public boolean isRefMessage(String name) {
		return name.endsWith("Ref");
	}
}
