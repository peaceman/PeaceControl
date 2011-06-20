package peaceman.peacecontrol;

import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import peaceman.peacecontrol.command.CommandBase;
import peaceman.peacecontrol.user.User;

/**
 *
 * @author peaceman
 */
public class PeaceControl extends JavaPlugin {

    public final static double version = 0.1;
    public final MyLogger log = new MyLogger();
    public final UserManager userManager = new UserManager();

    @Override
    public void onEnable() {
        this.log.info("PeaceControl v" + PeaceControl.version + " has been enabled.");
    }

    @Override
    public void onDisable() {
        this.log.info("PeaceControl has been disabled");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            Class<? extends CommandBase> clazz =
                    this.getClass().getClassLoader().loadClass("peaceman.peacecontrol.command." + command.getName() + "Command").asSubclass(CommandBase.class);
            CommandBase commandInstance = clazz.getConstructor(PeaceControl.class).newInstance(this);
            return commandInstance.runCommand(sender, command, label, args);
        } catch (Exception ex) {
            this.log.error("Catched Exception while executing command " + command.getName());
            this.log.error(ex.toString());
            ex.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/peacecontrol";
            Connection con = DriverManager.getConnection(url, "root", "loladin");

            UserMapper userMapper = new UserMapper(con);

//            User newUser = new User();
//            newUser.setUsername("test");
//            newUser.setPasshash("test123");
//            
//            userMapper.insertDataObject(newUser);
//            
//            System.out.println("Id of the new user " + newUser.getId());

            User user = (User) userMapper.getById(3);
            user.setUsername("lddol");

            User newUser = (User) userMapper.getNewDataObject();
            newUser.setUsername("newUser");
            newUser.setPasshash("omgwaseinhash");
            userMapper.persistCaches();
            newUser.setUsername("changedUser");
            userMapper.persistCaches();

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PeaceControl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(PeaceControl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String genMd5(String toHash) {
        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(toHash.getBytes("UTF8"));
            final byte[] resultByte = md.digest();
            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < resultByte.length; i++) {
                hexString.append(Integer.toHexString(0xFF & resultByte[i]));
            }
            return hexString.toString();
        } catch (Exception e) {
            System.err.println("An error occurred while md5-hashing a string");
            e.printStackTrace();
            return null;
        }
    }
}
