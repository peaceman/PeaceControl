package peaceman.peacecontrol.user;

import java.util.Date;
import peaceman.peacecontrol.DataObject;

/**
 *
 * @author peaceman
 */
public class User extends DataObject {
    private String _username = new String();
    private String _passhash = new String();
    
    public String getUsername() {
        return this._username;
    }
    
    public String getPasshash() {
        return this._passhash;
    }
    
    public void setUsername(String username) {
		if (!this._username.equals(username)) {
			this.markAsChanged("username");
			this._username = username;
		}
    }
    
    public void setPasshash(String passhash) {
		if (!this._passhash.equals(passhash)) {
			this.markAsChanged("passhash");
			this._passhash = passhash;
		}
    }
}
