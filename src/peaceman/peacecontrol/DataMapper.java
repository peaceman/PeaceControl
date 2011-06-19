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
                
                if (!this.persistantCache.containsKey(tmpObject.getId()))
                    this.persistantCache.put(id, tmpObject);
                
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

	private PreparedStatement getStatement(String string) {
        if (this.statements.containsKey(string)) {
            return this.statements.get(string);
        }
        
        return this.prepareStatement(string);
	}
    
    private PreparedStatement prepareStatement(String string) {
        StringBuilder sb = new StringBuilder();
        if (string.equals("byId")) {
            List<Field> dataFields =  new LinkedList<Field>();
            DataObject.getDataFields(dataFields, this.dataObjectType);
            
            List<String> dataFieldNames = new LinkedList<String>();
            ListIterator<Field> iter = dataFields.listIterator();
            while (iter.hasNext()) {
                dataFieldNames.add(iter.next().getName().substring(1));
            }
            
            sb.append("SELECT ")
                    .append(this.implodeStringArray(dataFieldNames))
                    .append(" FROM ")
                    .append(this.tableName)
                    .append(" WHERE id = ?");
        }
        
        try {
            PreparedStatement stmt = this.db.prepareStatement(sb.toString());
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

	private void updateDataObject(DataObject value) {
		Map<String, Object[]> changedValues = value.getChangedFields();
		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE ");
		sb.append(this.tableName).append(" SET ");
		Queue<Object> newValues = new ArrayBlockingQueue<Object>(changedValues.size() + 1);
		
		Iterator<Map.Entry<String, Object[]>> iter = changedValues.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, Object[]> entry = iter.next();
			Object[] values = entry.getValue();
			
			sb.append(entry.getKey())
					.append(" = ")
                    .append("?");
			
			if (iter.hasNext()) {
				sb.append(", ");
			}
			
			newValues.add(values[1]);
		}
		
		sb.append(" WHERE id = ?");
		newValues.add(value.getId());
		try {
            PreparedStatement stmt = this.db.prepareStatement(sb.toString());
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

	public void insertDataObject(DataObject value) {
        Map<String, Object> attributes = value.getData();
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ")
                .append(this.tableName)
                .append(" (")
                .append(this.implodeStringArray(attributes.keySet()))
                .append(") VALUES (");
        
        Queue values = new ArrayBlockingQueue<Object>(attributes.size() );
        
        Iterator<Object> iter = attributes.values().iterator();
        while (iter.hasNext()) {
            Object tmpObject = iter.next();
            sb.append('?');
            if (iter.hasNext()) {
                sb.append(", ");
            }
            values.add(tmpObject);
        }
        sb.append(")");
        
        try {
            PreparedStatement stmt = this.db.prepareStatement(sb.toString());        
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
            }
        } catch (SQLException e) {
            System.err.println("An error occured while inserting a dataobject");
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
