package peaceman.peacecontrol.user;

import java.util.Date;
import java.util.Random;
import peaceman.peacecontrol.DataObject;
import peaceman.peacecontrol.PeaceControl;

/**
 *
 * @author peaceman
 */
public class User extends DataObject {

    private String _username = new String();
    private String _passhash = new String();
    private Date _registeredAt = new Date();
    private String _salt = new String();
    private String _email = new String();
    private String _ip = new String();

    public String getUsername() {
        return this._username;
    }

    public String getPasshash() {
        return this._passhash;
    }
    
    public Date getRegisteredAt() {
        return this._registeredAt;
    }
    
    public String getSalt() {
        return this._salt;
    }
    
    public String getEmail() {
        return this._email;
    }
    
    public String getIp() {
        return this._ip;
    }

    public void setUsername(String username) {
        if (username != null && !this._username.equals(username)) {
            this.markAsChanged("username");
            this._username = username;
        }
    }

    public void setPasshash(String passhash) {
        if (passhash != null && !this._passhash.equals(passhash)) {
            this.markAsChanged("passhash");
            this._passhash = passhash;
        }
    }
    
    public void setRegisteredAt(Date registeredAt) {
        if (registeredAt != null && !this._registeredAt.equals(registeredAt)) {
            this._registeredAt = registeredAt;
            this.markAsChanged("registeredAt");
        }
    }
    
    public void setSalt(String salt) {
        if (salt != null && !this._salt.equals(salt)) {
            this.markAsChanged("salt");
            this._salt = salt;
        }
    }
    
    public void setEmail(String email) {
        if (email != null && !this._email.equals(email)) {
            this.markAsChanged("email");
            this._email = email;
        }
    }
    
    public void setIp(String ip) {
        if (ip != null && !this._ip.equals(ip)) {
            this.markAsChanged("ip");
            this._ip = ip;
        }
    }
    
    public static String genSalt() {
        Random generator = new Random();
        Integer r = generator.nextInt();
        return PeaceControl.genMd5(r.toString());
    }
}
