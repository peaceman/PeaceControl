package peaceman.peacecontrol;

import java.util.Date;
import peaceman.peacecontrol.user.User;

/**
 *
 * @author peaceman
 */
public class Session extends DataObject {
    
    private Date _startedAt = new Date();
    private Date _endedAt = new Date();
    private String _ip = new String();
    private User user;
    private long _userId;
    private UserMapper userMapper;
    
    public Date getStartedAt() {
        return this._startedAt;
    }
    
    public Date getEndedAt() {
        return this._endedAt;
    }
    
    public String getIp() {
        return this._ip;
    }
    
    public long getUserId() {
        if (this.user != null) {
            return this.user.getId();
        } else {
            return this._userId;
        }
    }
    
    public User getUser() {
        if (this.user == null) {
            this.lazyLoadUser();
        }
        
        return this.user;
    }
    
    private void lazyLoadUser() {
        if (this._userId == 0) {
            return;
        }
        
        this.user = this.userMapper.getById(this._userId);
    }
    
    public void setStartedAt(Date startedAt) {
        if (startedAt != null && !this._startedAt.equals(startedAt)) {
            this._startedAt = startedAt;
            this.markAsChanged("startedAt");
        }
    }
    
    public void setEndedAt(Date endedAt) {
        if (endedAt != null && !this._endedAt.equals(endedAt)) {
            this._endedAt = endedAt;
            this.markAsChanged("endedAt");
        }
    }
    
    public void setIp(String ip) {
        if (ip != null && this._ip.equals(ip)) {
            this._ip = ip;
            this.markAsChanged("ip");
        }
    }
    
    public void setUserId(long id) {
        if (this.user != null) {
            if (this.user.getId() != id) {
                this.user = null;
                this._userId = id;
                this.markAsChanged("userId");
            }
        } else {
            if (this._userId != id) {
                this._userId = id;
                this.markAsChanged("userId");
            }
        }
    }
    
    public void setUser(User user) throws IllegalArgumentException {
        if (user != null) {
            if (user.getId() == 0) {
                throw new IllegalArgumentException("Cant set a user as property if he doesnt has a valid id");
            }
            
            if (!this.user.equals(user)) {
                this.user = user;
                if (this._userId != user.getId()) {
                    this._userId = user.getId();
                    this.markAsChanged("userId");
                }
            }
        }
    }
}
