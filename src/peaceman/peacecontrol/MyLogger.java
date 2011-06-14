package peaceman.peacecontrol;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Naegele.Nico
 */
public class MyLogger {
	private final Logger logger = Logger.getLogger("Minecraft");
	
	public void info(String msg) {
		this.logger.log(Level.INFO, "PeaceControl | " + msg);
	}
	
	public void error(String msg) {
		this.logger.log(Level.SEVERE, "PeaceControl | " + msg);
	}
	
	public void warn(String msg) {
		this.logger.log(Level.WARNING, "PeaceControl | " + msg);
	}
}
