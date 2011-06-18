package peaceman.peacecontrol.user;

import java.util.Date;
import peaceman.peacecontrol.DataObject;

/**
 *
 * @author peaceman
 */
public class User extends DataObject {
    private long _id;
    private String _username;
    private String _passhash;
    
    public String getUsername() {
        return this._username;
    }
    
    public String getPasshash() {
        return this._passhash;
    }
    
    public void setUsername(String username) {
        this._username = username;
    }
    
    public void setPasshash(String passhash) {
        this._username = passhash;
    }
}
