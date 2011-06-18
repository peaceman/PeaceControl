/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package peaceman.peacecontrol;

import java.sql.Connection;
import peaceman.peacecontrol.user.User;

/**
 *
 * @author peaceman
 */
public class UserMapper extends DataMapper {
    public UserMapper(Connection db) {
        super(db, "user", User.class);
    }
}
