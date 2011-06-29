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
    
    public List<Session> getSessionsByUser(User user) {
        return this.getSessionsByUserId(user.getId());
    }
    
    public List<Session> getSessionsByUserId(long userId) {
        List<Session> toReturn = new LinkedList<Session>();
        
        try {
            PreparedStatement stmt = this.getStatement("getByUserId");
            stmt.setLong(1, userId);
            
            ResultSet result = stmt.executeQuery();
            List<Field> dataFields = new LinkedList<Field>();
            DataObject.getDataFields(dataFields, this.dataObjectType);
            while (result.next()) {
                Map<String, Object> attributes = new HashMap<String, Object>();
                for (Field field : dataFields) {
                    String fieldName = field.getName().substring(1);
                    attributes.put(fieldName, result.getObject(fieldName));
                    
                    Session tmpObject = (Session)this.dataObjectType.getConstructor().newInstance();
                    tmpObject.publicate(attributes);
                    
                    this.addToPersistantCache(tmpObject);
                    toReturn.add(tmpObject);
                }
            }
        } catch (SQLException e) {
            System.out.printf("Couldnt get rows from table %s with user id %d", this.tableName, userId);
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return toReturn;
    }
}
