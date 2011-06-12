package peaceman.peacecontrol.user;

import java.util.Date;

/**
 *
 * @author peaceman
 */
public class User {
    private long id;
    private String username;
    private String passhash;
    private Date registered_at;
    private String email;
    
    public User() {
        
    }
    
    public long getId() {
        return this.id;        
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public String getPasshash() {
        return this.passhash;
    }
    
    public Date getRegisteredAt() {
        return this.registered_at;
    }
    
    public String getEmail() {
        return this.email;
    }
    
    private void setId(long id) {
        this.id = id;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public void setPasshash(String passhash) {
        this.passhash = passhash;
    }
    
    public void setRegisteredAt(Date registered_at) {
        this.registered_at = registered_at;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
}
