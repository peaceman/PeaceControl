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

/**
 *
 * @author peaceman
 */
public class PeaceControl extends JavaPlugin {

    public final static double version = 0.1;
    public final MyLogger log = new MyLogger();
    public UserManager userManager;
    public SessionManager sessionManager;
    private Factory factory;

    @Override
    public void onEnable() {        
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/peacecontrol?zeroDateTimeBehavior=convertToNull";
            Connection con = DriverManager.getConnection(url, "root", "loladin");
            this.factory = new Factory(con);
            
            this.userManager = new UserManager(this, (UserMapper)this.factory.getDataMapper("user"));
            this.sessionManager = new SessionManager((SessionMapper)this.factory.getDataMapper("session"));
            
            this.log.info("PeaceControl v" + PeaceControl.version + " has been enabled.");
        } catch (SQLException ex) {
            Logger.getLogger(PeaceControl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PeaceControl.class.getName()).log(Level.SEVERE, null, ex);
        }
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
