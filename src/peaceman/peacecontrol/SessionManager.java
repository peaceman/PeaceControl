package peaceman.peacecontrol;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;

/**
 *
 * @author Naegele.Nico
 */
public class SessionManager {
    private SessionMapper sessionMapper;
    private Map<Integer, Session> activeSessions = new HashMap<Integer, Session>();
    
    public SessionManager(SessionMapper sessionMapper) {
        this.sessionMapper = sessionMapper;
    }
    
    public Session createSession(Player player) {
        if (this.activeSessions.containsKey(player.getEntityId()))
            return this.activeSessions.get(player.getEntityId());
        
        Session newSession = this.sessionMapper.getNewDataObject();
        newSession.setStartedAt(new Date());
        newSession.setIp(player.getAddress().getAddress().getHostAddress());
        
        this.activeSessions.put(player.getEntityId(), newSession);
        
        return newSession;
    }
    
    public Session getSession(Player player) {
        if (this.activeSessions.containsKey(player.getEntityId()))
            return this.activeSessions.get(player.getEntityId());
        return null;
    }
    
    public void endSession(Player player) {
        Session session = this.activeSessions.get(player.getEntityId());
        if (session != null) {
            session.setEndedAt(new Date());
            this.activeSessions.remove(player.getEntityId());
        }
    }
}
