package peaceman.peacecontrol;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import peaceman.peacecontrol.user.User;

/**
 *
 * @author peaceman
 */
public class SessionMapper extends DataMapper {
    public SessionMapper(Connection db) {
        super(db, "session", Session.class);
    }
    
    public List<Session> getSessionsByIp(String ip) {
        List<Session> toReturn = new LinkedList<Session>();
        
        try {
            PreparedStatement stmt = this.getStatement("byIp");
            stmt.setString(1, ip);
            
            ResultSet result = stmt.executeQuery();
            List<Field> dataFields = new LinkedList<Field>();
            DataObject.getDataFields(dataFields, this.dataObjectType);
            while (result.next()) {
                Map<String, Object> attributes = new HashMap<String, Object>();
                for (Field field : dataFields) {
                    String fieldName = field.getName().substring(1);
                    attributes.put(fieldName, result.getObject(fieldName));
                }
                
                Session tmpObject;
                
                if (this.persistantCache.containsKey(attributes.get("id"))) {
                    tmpObject = (Session)this.persistantCache.get(attributes.get("id"));
                } else {
                    tmpObject = (Session)this.dataObjectType.getConstructor().newInstance();
                    tmpObject.publicate(attributes);
                    this.addToPersistantCache(tmpObject);
                }
                
                toReturn.add(tmpObject);
            }
        } catch (SQLException e) {
            System.out.printf("Couldnt get rows from table %s by ip %s", this.tableName, ip);
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return toReturn;
    }
    
    public LinkedList<Session> getSessionsByUser(User user) {
        return this.getSessionsByUserId(user.getId());
    }
    
    public LinkedList<Session> getSessionsByUserId(long userId) {
        LinkedList<Session> toReturn = new LinkedList<Session>();
        
        try {
            PreparedStatement stmt = this.getStatement("byUserId");
            stmt.setLong(1, userId);
            
            ResultSet result = stmt.executeQuery();
            List<Field> dataFields = new LinkedList<Field>();
            DataObject.getDataFields(dataFields, this.dataObjectType);
            while (result.next()) {
                Map<String, Object> attributes = new HashMap<String, Object>();
                for (Field field : dataFields) {
                    String fieldName = field.getName().substring(1);
                    attributes.put(fieldName, result.getObject(fieldName));
                }
                
                Session tmpObject;
                
                if (this.persistantCache.containsKey(attributes.get("id"))) {
                    tmpObject = (Session)this.persistantCache.get(attributes.get("id"));
                } else {
                    tmpObject = (Session)this.dataObjectType.getConstructor().newInstance();
                    tmpObject.publicate(attributes);
                    this.addToPersistantCache(tmpObject);
                }
                
                toReturn.add(tmpObject);            
            }
        } catch (SQLException e) {
            System.out.printf("Couldnt get rows from table %s with user id %d", this.tableName, userId);
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return toReturn;
    }
    
    protected PreparedStatement preparedStatement(String name) {
        boolean found = false;
        StringBuilder sb = new StringBuilder();
        
        if (name.equals("byUserId")) {
            List<Field> dataFields = new LinkedList<Field>();
            DataObject.getDataFields(dataFields, this.dataObjectType);
            
            List<String> dataFieldNames = new LinkedList<String>();
            for (Field dataField : dataFields) {
                dataFieldNames.add(dataField.getName().substring(1));
            }
            
            sb.append("SELECT ")
                    .append(this.implodeStringArray(dataFieldNames))
                    .append(" FROM ")
                    .append(this.tableName)
                    .append(" WHERE userId = ?");
            found = true;
        }
        
        if (name.equals("byIp")) {
            List<Field> dataFields = new LinkedList<Field>();
            DataObject.getDataFields(dataFields, this.dataObjectType);
            
            List<String> dataFieldNames = new LinkedList<String>();
            for (Field dataField : dataFields) {
                dataFieldNames.add(dataField.getName().substring(1));
            }
            
            sb.append("SELECT ")
                    .append(this.implodeStringArray(dataFieldNames))
                    .append(" FROM ")
                    .append(this.tableName)
                    .append(" WHERE ip = ?");
            found = true;
        }
        
        sb.append(" ORDER BY startedAt");
        
        if (found == true) {
            try {
                PreparedStatement stmt = this.db.prepareStatement(sb.toString());
                this.statements.put(name, stmt);
                System.out.println("Created a prepared statement with the following sql " + sb.toString());
                return stmt;
            } catch (SQLException e) {
                System.err.println("An error occurred while preparing a statement");
                e.printStackTrace();
                return null;
            }
        } else {
            return super.prepareStatement(name);
        }        
    }
}
