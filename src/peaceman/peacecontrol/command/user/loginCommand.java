package peaceman.peacecontrol.command.user;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import peaceman.peacecontrol.PeaceControl;
import peaceman.peacecontrol.command.CommandBase;

/**
 *
 * @author Naegele.Nico
 */
public class loginCommand extends CommandBase {
	public loginCommand(PeaceControl plugin) {
		super(plugin);
	}
	
	@Override
	public boolean runCommand(CommandSender sender, Command command, String commandLabel, String[]  args) {
		return false;
	}
}
