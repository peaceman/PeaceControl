package peaceman.peacecontrol;

import java.util.Date;

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
        return this._userId;
    }
    
    public User getUser() {
        return this.user;
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
            
        }
    }
}
