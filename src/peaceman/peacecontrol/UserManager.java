package peaceman.peacecontrol;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import peaceman.peacecontrol.user.User;

/**
 *
 * @author peaceman
 */
public class UserManager {

    public void loginUser(Player player) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public boolean usernameExists(String username) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public User getUser(String username) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public String genPasshash(String password) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public boolean createUser(String username, String password, String email) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    public boolean createUser(CommandSender sender, String password, String email) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public boolean emailExists(String email) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
