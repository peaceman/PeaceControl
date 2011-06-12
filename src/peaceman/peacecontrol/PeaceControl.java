package peaceman.peacecontrol;

import org.bukkit.plugin.java.JavaPlugin;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 *
 * @author peaceman
 */
public class PeaceControl extends JavaPlugin {
    private final SessionFactory sessionFactory;
    
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void onDisable() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
