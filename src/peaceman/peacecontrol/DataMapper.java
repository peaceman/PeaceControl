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
    protected Map<Long, DataObject> newCache = new HashMap<Long, DataObject>();
    protected String tableName;
    protected Connection db;
    protected Class<? extends DataObject> dataObjectType;
    protected Map<String, PreparedStatement> statements = new HashMap<String, PreparedStatement>();

    public DataMapper(Connection db, String tableName, Class dataObjectType) {
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

                if (!this.persistantCache.containsKey(tmpObject.getId())) {
                    this.persistantCache.put(id, tmpObject);
                }

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
            long key = dataObject.hashCode();
            if (!this.newCache.containsKey(key)) {
                this.newCache.put(key, dataObject);
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
        long key = dataObject.hashCode();
        if (this.newCache.containsKey(key)) {
            this.newCache.remove(key);
        }
    }

    private PreparedStatement getStatement(String string) {
        if (this.statements.containsKey(string)) {
            return this.statements.get(string);
        }

        return this.prepareStatement(string);
    }

    private PreparedStatement prepareStatement(String string) {
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
                if (!field.getName().substring(1).equals("id")) {
                    dataFieldNames.add(field.getName().substring(1));
                    sb2.append("?");
                    if (iter.hasNext() && !iter.next().getName().substring(1).equals("id")) {
                        sb2.append(", ");
                        iter.previous();
                    }
                }
            }

            sb.append("INSERT INTO ").append(this.tableName).append(" (").append(this.implodeStringArray(dataFieldNames)).append(") VALUES (").append(sb2.toString()).append(")");

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

        for (Map.Entry<Long, DataObject> entry : this.newCache.entrySet()) {
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

    private char determineSqlPlaceholder(Class type) {
        try {
            if (type.getName().equals("java.lang.String")) {
                return 's';
            }

            if (type.getName().equals("int")) {
                return 'i';
            }

            if (type.getName().equals("long")) {
                return 'i';
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 's';
    }

    public DataObject getNewDataObject() {
        DataObject toReturn = null;
        try {
            toReturn = this.dataObjectType.getConstructor().newInstance();
            this.addToNewCache(toReturn);
        } catch (Exception e) {
            System.err.println("An error occurred while creating a new dataobject in datamapper class " + this.getClass().getName());
        }
        return toReturn;
    }

    public void invalidateDataObject(DataObject dataObject) {
        this.removeFromNewCache(dataObject);
    }

    protected void insertDataObject(DataObject value) {
        Map<String, Object> attributes = value.getData();
        Queue values = new ArrayBlockingQueue<Object>(attributes.size() - 1);

        for (String key : attributes.keySet()) {
            if (!key.equals("id"))
                values.add(attributes.get(key));
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
            }
        } catch (SQLException e) {
            System.err.println("An error occurred while inserting a dataobject");
            e.printStackTrace();
        }
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
