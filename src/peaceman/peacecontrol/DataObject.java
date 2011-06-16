package peaceman.peacecontrol;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Naegele.Nico
 */
public abstract class DataObject {
	protected List<String> changedFields = new ArrayList<String>();
	
	public boolean isChanged() {
		if (this.changedFields.isEmpty())
			return false;
		return true;
	}
	
	public Map<String, Object[]> getChangedFields() {
		Map<String, Object[]> updatedFieldValues = new HashMap<String, Object[]>();		
		
		Class <? extends DataObject> runtimeClass = this.getClass();
		for (String fieldName : this.changedFields) {
			try {
				Field tmpField = runtimeClass.getField("_" + fieldName);
				Object fieldValue = tmpField.get(this);
				
				Object[] fieldData = new Object[2];
				fieldData[0] = tmpField.getType();
				fieldData[1] = fieldValue;
				updatedFieldValues.put(fieldName, fieldData);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		return updatedFieldValues;
	}
	
	public void resetChangedFields() {
		this.changedFields.clear();
	}
	
	protected void markAsChanged(String fieldName) {
		if (!this.changedFields.contains(fieldName)) {
			this.changedFields.add(fieldName);
		}
	}
	
	public Class getFieldType(String fieldName) {
		Class <? extends DataObject> runtimeClass = this.getClass();
		try {
			return runtimeClass.getField("_" + fieldName).getType();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	public Map<String, Class> getDataFields() {
		return DataObject.getDataFieldsByDataObjectClass(this.getClass());
	}
	
	public static Map<String, Class> getDataFieldsByDataObjectClass(Class dataObjectClass) {
		Map<String, Class> fields = new HashMap<String, Class>();
		
		Field[] fieldArray = dataObjectClass.getDeclaredFields();
		for (Field field : fieldArray) {
			if (field.getName().startsWith("_")) {
				fields.put(field.getName().substring(1), field.getType());
			}
		}
		
		return fields;
	}

	public void publicate(Map<String, Object> attributes) {
		Class runtimeClass = this.getClass();
		for (Map.Entry<String, Object> entry : attributes.entrySet()) {
			try {
				Field tmpField  = runtimeClass.getField("_" + entry.getKey());
				tmpField.set(this, entry.getValue());
			} catch (Exception ex) {
				Logger.getLogger(DataObject.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
}
