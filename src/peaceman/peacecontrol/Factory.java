package peaceman.peacecontrol;

import java.sql.Connection;
import java.util.HashMap;
import peaceman.peacecontrol.user.User;

/**
 *
 * @author Naegele.Nico
 */
public class Factory {
    private HashMap<String, Object> objectCache = new HashMap<String, Object>();
    private Connection db;
    
    public Factory(Connection db) {
        this.db = db;
    }
    
    public DataMapper getDataMapper(String type) {
        DataMapper toReturn = null;
        
        if (type.equals("user")) {
            if (this.objectCache.containsKey("userMapper")) {
                toReturn = (DataMapper)this.objectCache.get("userMapper");
            } else {
                toReturn = new UserMapper(this, this.db);
            }
        }
        
        if (type.equals("session")) {
           if (this.objectCache.containsKey("sessionMapper")) {
               toReturn = (DataMapper)this.objectCache.get("sessionMapper");
           } else {
               toReturn = new SessionMapper(this, this.db);
           }
        }
        
        return toReturn;
    }
    
    public DataObject getDataObject(String type) {
        DataObject toReturn = null;
        
        if (type.equals("user")) {
            toReturn = new User((SessionMapper)this.getDataMapper("session"));
        }
        
        if (type.equals("session")) {
            toReturn = new Session((UserMapper)this.getDataMapper("user"));
        }
        
        return toReturn;
    }
}
