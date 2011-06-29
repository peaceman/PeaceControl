package peaceman.peacecontrol;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;
import peaceman.peacecontrol.user.User;

/**
 *
 * @author peaceman
 */
public class UserManager {
    private UserMapper userMapper;
    private Map<Integer, User> loggedInUsers = new HashMap<Integer, User>();
    
    public UserManager(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public void loginUser(Player player) {
        User user = this.getUser(player.getName());
        this.loggedInUsers.put(player.getEntityId(), user);
    }
    
    public void logoutUser(Player player) {
        if (this.isLoggedIn(player)) {
            this.loggedInUsers.remove(player.getEntityId());
        }
    }
    
    public boolean isLoggedIn(Player player) {
        return this.loggedInUsers.containsKey(player.getEntityId());
    }

    public boolean usernameExists(String username) {
        User user = this.getUser(username);
        if (user == null) {
            return false;
        }
        return true;
    }

    public User getUser(String username) {
        return this.userMapper.getByUsername(username);
    }

    public String genPasshash(User user, String password) {
        return PeaceControl.genMd5(user.getSalt() + password);
    }

    public boolean createUser(String username, String password, String email) {
        User newUser = (User)this.userMapper.getNewDataObject();
        newUser.setSalt(User.genSalt());
        newUser.setPasshash(this.genPasshash(newUser, password));
        newUser.setEmail(email);
        newUser.setUsername(username);
        return this.userMapper.forceInsert(newUser);
    }

    public boolean createUser(Player player, String password, String email) {
        String ip = player.getAddress().getAddress().getHostAddress();
        User newUser = (User)this.userMapper.getNewDataObject();
        newUser.setSalt(User.genSalt());
        newUser.setPasshash(PeaceControl.genMd5(newUser.getSalt() + password));
        newUser.setEmail(email);
        newUser.setUsername(player.getName());
        return this.userMapper.forceInsert(newUser);
    }

    public boolean emailExists(String email) {
        User tmpUser = this.userMapper.getByEmail(email);
        if (tmpUser == null) {
            return false;
        }
        return true;
    }
    
    public boolean checkPassword(String username, String password) {
        User tmpUser = this.getUser(username);
        String hashedPassword = this.genPasshash(tmpUser, password);
        if (!tmpUser.getPasshash().equals(hashedPassword)) {
            return false;
        }
        return true;
    }

    public void deleteUser(Player player) {
        this.deleteUser(player.getName());
    }
    
    public boolean deleteUser(String username) {
        return this.userMapper.deleteByUsername(username);
    }
    
    public boolean deleteUserByEmail(String email) {
        return this.userMapper.deleteByEmail(email);
    }
    
    public void deleteAndLogoutUser(Player player) {
        this.logoutUser(player);
        this.deleteUser(player);
    }

    public boolean changePassword(String name, String newPassword) {
        User user = this.getUser(name);
        return this.changePassword(user, newPassword);
    }

    public boolean changePassword(User user, String newPassword) {
        if (user == null) {
            return false;
        }
        
        user.setPasshash(this.genPasshash(user, newPassword));
        return true;
    }

    public User getUserByEmail(String value) {
        return this.userMapper.getByEmail(value);
    }
}
