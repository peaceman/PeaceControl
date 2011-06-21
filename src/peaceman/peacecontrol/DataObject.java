package peaceman.peacecontrol;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.omg.CORBA.NamedValue;

/**
 *
 * @author Naegele.Nico
 */
public abstract class DataObject {

    protected List<String> changedFields = new ArrayList<String>();
    protected long _id;

    protected static class FieldComparator implements Comparator<Field> {

        @Override
        public int compare(Field f1, Field f2) {
            return f1.getName().compareTo(f2.getName());
        }
    }

    public boolean isChanged() {
        if (this.changedFields.isEmpty()) {
            return false;
        }
        return true;
    }

    public Map<String, Object[]> getChangedFields() {
        Map<String, Object[]> updatedFieldValues = new HashMap<String, Object[]>();

        Class<? extends DataObject> runtimeClass = this.getClass();
        for (String fieldName : this.changedFields) {
            try {
                Field tmpField = runtimeClass.getDeclaredField("_" + fieldName);
                tmpField.setAccessible(true);
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

    public List<ObjectProperty<Object>> getData() {
        //Map<String, Object> dataFields = new HashMap<String, Object>();
        List<Field> fields = new LinkedList<Field>();
        DataObject.getDataFields(fields, this.getClass());
        
        List<ObjectProperty<Object>> dataFields = new LinkedList<ObjectProperty<Object>>();

        for (Field field : fields) {
            if (field.getName().startsWith("_")) {
                try {
                    StringBuilder sb = new StringBuilder(field.getName().substring(1));
                    StringBuilder sbForMethod = new StringBuilder(sb.toString());
                    sbForMethod.setCharAt(0, Character.toUpperCase(sbForMethod.charAt(0)));
                    Method accessMethod = this.getClass().getMethod("get" + sbForMethod.toString());
                    Object result = accessMethod.invoke(this, null);
                    dataFields.add(new ObjectProperty<Object>(sb.toString(), result));
                } catch (Exception e) {
                    System.err.println("An error occured while exporting data from a dataobject");
                    e.printStackTrace();
                }
            }
        }
        return dataFields;
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
        Class<? extends DataObject> runtimeClass = this.getClass();
        try {
            return runtimeClass.getField("_" + fieldName).getType();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static List<Field> getDataFields(List<Field> fields, Class<?> type) {
        for (Field field : type.getDeclaredFields()) {
            if (field.getName().startsWith("_")) {
                fields.add(field);
            }
        }

        if (type.getSuperclass() != null) {
            fields = getDataFields(fields, type.getSuperclass());
        }

        Collections.sort(fields, new FieldComparator());
        return fields;
    }

    public void publicate(Map<String, Object> attributes) {
        Class runtimeClass = this.getClass();
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            try {
                Field tmpField = DataObject.getField("_" + entry.getKey(), runtimeClass);
                tmpField.setAccessible(true);
                tmpField.set(this, entry.getValue());
            } catch (Exception ex) {
                Logger.getLogger(DataObject.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static Field getField(String name, Class<?> type) {
        try {
            return type.getDeclaredField(name);
        } catch (NoSuchFieldException ex) {
            if (type.getSuperclass() != null) {
                return getField(name, type.getSuperclass());
            }
        } catch (SecurityException ex) {
            Logger.getLogger(DataObject.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public long getId() {
        return this._id;
    }

    public void setId(long id) {
        this._id = id;
    }
}
