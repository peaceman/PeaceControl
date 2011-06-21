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
public class UserMapper extends DataMapper {

    public UserMapper(Connection db) {
        super(db, "user", User.class);
    }

    public User getByUsername(String username) {
        try {
            PreparedStatement stmt = this.getStatement("byUsername");
            stmt.setString(1, username);
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

                return (User)tmpObject;
            }
        } catch (SQLException e) {
            System.out.printf("Couldn't get a row with username %s from table %s\n", username, this.tableName);
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public User getByEmail(String email) {
        try {
            PreparedStatement stmt = this.getStatement("byEmail");
            stmt.setString(1, email);
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
                
                return (User)tmpObject;
            }
        } catch (SQLException e) {
            System.out.printf("Couldn't get a row with email %s from table %s\n", email, this.tableName);
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean deleteByUsername(String username) {
        try {
            PreparedStatement stmt = this.getStatement("delByUsername");
            stmt.setString(1, username);
            int deletedRows = stmt.executeUpdate();
            if (deletedRows != 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            System.out.printf("Couln't delete a row with username %s from table %s\n", username, this.tableName);
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteByEmail(String email) {
        try {
            PreparedStatement stmt = this.getStatement("delByEmail");
            stmt.setString(1, email);
            int deletedRows = stmt.executeUpdate();
            if (deletedRows != 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            System.out.printf("Couldn't delete a row with email %s from table %s\n", email, this.tableName);
            e.printStackTrace();
            return false;
        }
    }
    
    protected PreparedStatement prepareStatement(String name) {
        boolean found = false;
        StringBuilder sb = new StringBuilder();
        
        if (name.equals("byUsername")) {
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
                    .append(" WHERE username = ?");
            found = true;
        }
        
        if (name.equals("byEmail")) {
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
                    .append(" WHERE email = ?");
            found = true;
        }
        
        if (name.equals("delByUsername")) {
            sb.append("DELETE FROM ")
                    .append(this.tableName)
                    .append(" WHERE username = ?");
            found = true;
        }
        
        if (name.equals("delByEmail")) {
            sb.append("DELETE FROM ")
                    .append(this.tableName)
                    .append(" WHERE email = ?");
            found = true;
        }
        
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
