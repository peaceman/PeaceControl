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
import java.util.logging.Level;
import java.util.logging.Logger;
import peaceman.peacecontrol.user.User;

/**
 *
 * @author peaceman
 */
public class UserMapper extends DataMapper {

    public UserMapper(Factory factory, Connection db) {
        super(factory, db, "user", User.class);
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
                
                User tmpObject;
                
                if (this.persistantCache.containsKey(attributes.get("id"))) {
                    tmpObject = (User)this.persistantCache.get(attributes.get("id"));
                } else {
                    tmpObject = (User)this.factory.getDataObject("user");
                    tmpObject.publicate(attributes);
                    this.addToPersistantCache(tmpObject);
                }
                
                return tmpObject;
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
                
                User tmpObject;
                
                if (this.persistantCache.containsKey(attributes.get("id"))) {
                    tmpObject = (User)this.persistantCache.get(attributes.get("id"));
                } else {
                    tmpObject = (User)this.dataObjectType.getConstructor().newInstance();
                    tmpObject.publicate(attributes);
                    this.addToPersistantCache(tmpObject);
                }
                
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
    
    @Override
    public User getById(long id) {
        return (User)super.getById(id);
    }
    
    @Override
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
        
        if (name.equals("count")) {
            sb.append("SELECT COUNT(id) FROM ")
                    .append(this.tableName);
            found = true;
        }
        
        if (name.equals("all")) {
            List<Field> dataFields = new LinkedList<Field>();
            DataObject.getDataFields(dataFields, this.dataObjectType);
            
            List<String> dataFieldNames = new LinkedList<String>();
            for (Field dataField : dataFields) {
                dataFieldNames.add(dataField.getName().substring(1));
            }
            
            sb.append("SELECT ")
                    .append(this.implodeStringArray(dataFieldNames))
                    .append(" FROM ")
                    .append(this.tableName);
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

    public int getCount() {
        try {
            PreparedStatement stmt = this.getStatement("count");
            ResultSet result = stmt.executeQuery();
            
            if (result.next()) {
                return result.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserMapper.class.getName()).log(Level.SEVERE, null, ex);
            
        }
        return 0;
    }

    public List<User> getAll() {
        List<User> toReturn = new LinkedList<User>();
        
        try {
            PreparedStatement stmt = this.getStatement("all");
            ResultSet result = stmt.executeQuery();

            User tmpObject;
            List<Field> dataFields = new LinkedList<Field>();
            DataObject.getDataFields(dataFields, this.dataObjectType);

            while (result.next()) {
                Map<String, Object> attributes = new HashMap<String, Object>();
                for (Field field : dataFields) {
                    String fieldName = field.getName().substring(1);
                    attributes.put(fieldName, result.getObject(fieldName));
                }

                if (this.persistantCache.containsKey(attributes.get("id"))) {
                    tmpObject = (User)this.persistantCache.get(attributes.get("id"));
                } else {
                    tmpObject = (User)this.factory.getDataObject("user");
                    tmpObject.publicate(attributes);
                }

                toReturn.add(tmpObject);
            }
        } catch (SQLException e) {
            System.out.printf("Couldnt get rows from table %s", this.tableName);
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return toReturn;
    }
    
    public User getNewDataObject() {
        User toReturn = null;
        toReturn = (User)this.factory.getDataObject("user");
        this.addToNewCache(toReturn);
        return toReturn;
    }
}
