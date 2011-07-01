package peaceman.peacecontrol;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 *
 * @author Naegele.Nico
 */
public abstract class DataMapper {

    protected Map<Long, DataObject> persistantCache = new HashMap<Long, DataObject>();
    protected Map<Integer, DataObject> newCache = new HashMap<Integer, DataObject>();
    protected String tableName;
    protected Connection db;
    protected Factory factory;
    protected Class<? extends DataObject> dataObjectType;
    protected Map<String, PreparedStatement> statements = new HashMap<String, PreparedStatement>();

    public DataMapper(Factory factory, Connection db, String tableName, Class dataObjectType) {
        this.factory = factory;
        this.db = db;
        this.tableName = tableName;
        this.dataObjectType = dataObjectType;
    }

    public DataObject getById(long id) {
        if (this.persistantCache.containsKey(id)) {
            return this.persistantCache.get(id);
        }
        try {
            PreparedStatement stmt = this.getStatement("byId");
            stmt.setLong(1, id);
            ResultSet result = stmt.executeQuery();

            if (result.next()) {
                Map<String, Object> attributes = new HashMap<String, Object>();
                List<Field> dataFields = new LinkedList<Field>();
                DataObject.getDataFields(dataFields, this.dataObjectType);

                for (Field field : dataFields) {
                    String fieldName = field.getName().substring(1);
                    attributes.put(fieldName, result.getObject(fieldName));
                }

                DataObject tmpObject = this.dataObjectType.getConstructor().newInstance();
                tmpObject.publicate(attributes);
                
                this.addToPersistantCache(tmpObject);

                return tmpObject;
            }
        } catch (SQLException e) {
            System.out.printf("Couldn't get a row with id %d from table %s\n", id, this.tableName);
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    protected void addToPersistantCache(DataObject dataObject) {
        if (dataObject.getId() != 0) {
            if (!this.persistantCache.containsKey(dataObject.getId())) {
                this.persistantCache.put(dataObject.getId(), dataObject);
            }
        }
    }

    protected void addToNewCache(DataObject dataObject) {
        if (dataObject.getId() == 0) {
            if (!this.newCache.containsKey(dataObject.hashCode())) {
                this.newCache.put(dataObject.hashCode(), dataObject);
            }
        }
    }

    protected void removeFromPersistantCache(DataObject dataObject) {
        if (dataObject.getId() != 0) {
            if (this.persistantCache.containsKey(dataObject.getId())) {
                this.persistantCache.remove(dataObject.getId());
            }
        }
    }

    protected void removeFromNewCache(DataObject dataObject) {
        if (this.newCache.containsKey(dataObject.hashCode())) {
            this.newCache.remove(dataObject.hashCode());
        }
    }

    protected PreparedStatement getStatement(String string) {
        if (this.statements.containsKey(string)) {
            return this.statements.get(string);
        }

        return this.prepareStatement(string);
    }
    
    public boolean forceInsert(DataObject dataObject ) {
        if (dataObject.getId() == 0) {
            if (this.newCache.containsKey(dataObject.hashCode())) {
                return this.insertDataObject(dataObject);
            }
        }
        return false;
    }

    protected PreparedStatement prepareStatement(String string) {
        StringBuilder sb = new StringBuilder();
        if (string.equals("byId")) {
            List<Field> dataFields = new LinkedList<Field>();
            DataObject.getDataFields(dataFields, this.dataObjectType);

            List<String> dataFieldNames = new LinkedList<String>();
            ListIterator<Field> iter = dataFields.listIterator();
            while (iter.hasNext()) {
                dataFieldNames.add(iter.next().getName().substring(1));
            }

            sb.append("SELECT ").append(this.implodeStringArray(dataFieldNames)).append(" FROM ").append(this.tableName).append(" WHERE id = ?");
        } else if (string.equals("insert")) {
            List<Field> dataFields = new LinkedList<Field>();
            DataObject.getDataFields(dataFields, this.dataObjectType);

            List<String> dataFieldNames = new LinkedList<String>();
            ListIterator<Field> iter = dataFields.listIterator();

            StringBuilder sb2 = new StringBuilder();
            while (iter.hasNext()) {
                Field field = iter.next();
                if (field.getName().equals("_id"))
                    continue;
                
                dataFieldNames.add(field.getName().substring(1));
                sb2.append("?");
                if (iter.hasNext()) {
                    sb2.append(", ");
                }
            }

            sb.append("INSERT INTO ")
                    .append(this.tableName)
                    .append(" (")
                    .append(this.implodeStringArray(dataFieldNames))
                    .append(") VALUES (")
                    .append(sb2.toString())
                    .append(")");
        } else {
            sb.append(string);
        }

        try {
            PreparedStatement stmt = this.db.prepareStatement(sb.toString());
            this.statements.put(string, stmt);
            System.out.println("Created a prepared statement with the following sql " + sb.toString());
            return stmt;
        } catch (SQLException e) {
            System.err.println("An error occurred while preparing a statement");
            e.printStackTrace();
            return null;
        }
    }

    public void persistCaches() {
        for (Map.Entry<Long, DataObject> entry : this.persistantCache.entrySet()) {
            if (entry.getValue().isChanged()) {
                this.updateDataObject(entry.getValue());
            }
        }

        for (Map.Entry<Integer, DataObject> entry : this.newCache.entrySet()) {
            this.insertDataObject(entry.getValue());
        }
    }

    protected void updateDataObject(DataObject value) {
        Map<String, Object[]> changedValues = value.getChangedFields();
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE ");
        sb.append(this.tableName).append(" SET ");
        Queue<Object> newValues = new ArrayBlockingQueue<Object>(changedValues.size() + 1);

        Iterator<Map.Entry<String, Object[]>> iter = changedValues.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, Object[]> entry = iter.next();
            Object[] values = entry.getValue();

            sb.append(entry.getKey()).append(" = ").append("?");

            if (iter.hasNext()) {
                sb.append(", ");
            }

            newValues.add(values[1]);
        }

        sb.append(" WHERE id = ?");
        newValues.add(value.getId());
        try {
            PreparedStatement stmt = this.getStatement(sb.toString());
            int counter = 1;

            Object tmpObject = null;
            while (!newValues.isEmpty()) {
                tmpObject = newValues.poll();
                stmt.setObject(counter, tmpObject);
                counter++;
            }

            int updatedRows = stmt.executeUpdate();
            value.resetChangedFields();
            System.out.printf("Updated %d rows with query %s\n", updatedRows, sb.toString());
        } catch (SQLException e) {
            System.err.println("An error occured while preparing a sql query");
            e.printStackTrace();
        }
    }

    public abstract DataObject getNewDataObject();

    public void delete(DataObject dataObject) {
        if (dataObject.getId() == 0) {
            this.removeFromNewCache(dataObject);
        } else {
            this.removeFromPersistantCache(dataObject);
            
            this.deleteById(dataObject.getId());
        }
    }
    
    public void deleteById(long id) {
        try {
            PreparedStatement stmt = this.getStatement("delById");
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Couldn't delete a row with id " + id + " from " + this.tableName);
        }
    }

    protected boolean insertDataObject(DataObject value) {
        List<ObjectProperty<Object>> attributes = value.getData();
        Queue values = new ArrayBlockingQueue<Object>(attributes.size() - 1);
        
        for (ObjectProperty<Object> property : attributes) {
            if (property.getName().equals("id"))
                continue;
            
            values.add(property.getProperty());
        }

        try {
            PreparedStatement stmt = this.getStatement("insert");
            int counter = 1;
            while (!values.isEmpty()) {
                Object tmpObject = values.poll();
                stmt.setObject(counter, tmpObject);
                counter++;
            }
            int affectedRows = stmt.executeUpdate();

            if (affectedRows != 0) {
                ResultSet rs = stmt.executeQuery("SELECT LAST_INSERT_ID() AS id");
                rs.next();
                value.setId(rs.getLong("id"));
                this.removeFromNewCache(value);
                this.addToPersistantCache(value);
                value.resetChangedFields();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("An error occurred while inserting a dataobject");
            e.printStackTrace();
        }
        return false;
    }

    public static String implodeStringArray(Collection<String> strings) {
        if (strings.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        Iterator iter = strings.iterator();
        sb.append(iter.next());
        while (iter.hasNext()) {
            sb.append(", ");
            sb.append(iter.next());
        }
        return sb.toString();
    }
}
