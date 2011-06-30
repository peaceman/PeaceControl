
package peaceman.peacecontrol.command;

import java.util.List;
import peaceman.peacecontrol.PeaceControl;

/**
 *
 * @author Naegele.Nico
 */
public abstract class CommandAction extends CommandBase {
    public CommandAction(PeaceControl plugin) {
        super(plugin);
    }
    
    public static List<String> getAliases() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
