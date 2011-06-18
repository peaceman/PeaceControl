package peaceman.peacecontrol;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
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
				Map<String, Class> dataFields = DataObject.getDataFieldsByDataObjectClass(this.dataObjectType);
				
				for (Map.Entry<String, Class> entry : dataFields.entrySet()) {
					attributes.put(entry.getKey(), entry.getValue().cast(result.getObject(entry.getKey())));
				}
				
				DataObject tmpObject = this.dataObjectType.getConstructor().newInstance();
				tmpObject.publicate(attributes);
				return tmpObject;
			}
		} catch (SQLException e) {
			System.out.printf("Couldn't get a row with id %i from table %s\n", id, this.tableName);
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private PreparedStatement getStatement(String string) {
		throw new UnsupportedOperationException("Not yet implemented");
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
		Queue<Object> newValues = new ArrayBlockingQueue<Object>(changedValues.size());
		
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
            System.out.printf("Updated %i rows with query %s", updatedRows, sb.toString());
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
