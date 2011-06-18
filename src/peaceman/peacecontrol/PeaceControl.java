package peaceman.peacecontrol;

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
			Class <? extends CommandBase> clazz =
					this.getClass()
						.getClassLoader()
						.loadClass("peaceman.peacecontrol.command." + command.getName() + "Command")
						.asSubclass(CommandBase.class);
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
            String url = "jdbc:mysql://localhost:3306/pc";
            Connection con = DriverManager.getConnection(url, "root", "loladin");
            
            UserMapper userMapper = new UserMapper(con);
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PeaceControl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(PeaceControl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
