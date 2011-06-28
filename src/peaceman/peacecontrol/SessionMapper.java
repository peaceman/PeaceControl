package peaceman.peacecontrol;

import java.sql.Connection;

/**
 *
 * @author peaceman
 */
public class SessionMapper extends DataMapper {
    public SessionMapper(Connection db) {
        super(db, "session", Session.class);
    }
}
