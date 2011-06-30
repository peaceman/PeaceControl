package peaceman.peacecontrol.command.user;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import peaceman.peacecontrol.PeaceControl;
import peaceman.peacecontrol.command.CommandAction;

/**
 *
 * @author Naegele.Nico
 */
public class infoAction extends CommandAction {
    
    public infoAction(PeaceControl plugin) {
        super(plugin);
    }

    @Override
    public boolean runCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        
        return false;
    }
    
    public static List<String> getAliases() {
        List<String> toReturn = new LinkedList<String>();
        Collections.addAll(toReturn, "info", "i");
        return toReturn;
    }
}
