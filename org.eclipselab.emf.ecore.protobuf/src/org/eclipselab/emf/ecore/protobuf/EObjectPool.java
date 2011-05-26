package org.eclipselab.emf.ecore.protobuf;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

public class EObjectPool {

	private Integer lastId = 1;
	private Map<EObject, Integer> objectToIdMap = new HashMap<EObject, Integer>();
	private Map<Integer, EObject> idToObjectMap = new HashMap<Integer, EObject>();
	
	public Integer getId(EObject eObject) {
		if(!objectToIdMap.containsKey(eObject)) {
			objectToIdMap.put(eObject, lastId++);
		}
		
		return objectToIdMap.get(eObject);
	}
	
	public EObject getObject(EClass eClass, Integer id) {
		if(!idToObjectMap.containsKey(id)) {
			idToObjectMap.put(id, EcoreUtil.create(eClass));
		}
		
		return idToObjectMap.get(id);
	}
}
