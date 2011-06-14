package peaceman.peacecontrol;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import peaceman.peacecontrol.command.CommandBase;

/**
 *
 * @author peaceman
 */
public class PeaceControl extends JavaPlugin {
    private final SessionFactory sessionFactory;
	public final static double version = 0.1;
	public final MyLogger log = new MyLogger();
    
    public PeaceControl() {
        this.sessionFactory = this.buildSessionFactory();
    }
    
    private final SessionFactory buildSessionFactory() {
        try {
            return new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
	
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
		}
		return false;
	}
}
