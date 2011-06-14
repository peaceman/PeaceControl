/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package peaceman.peacecontrol.command.user;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import peaceman.peacecontrol.PeaceControl;
import peaceman.peacecontrol.command.CommandBase;

/**
 *
 * @author Naegele.Nico
 */
public class changePasswordCommand extends CommandBase {
	public changePasswordCommand(PeaceControl plugin) {
		super(plugin);
	}
	
	@Override
	public boolean runCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
}
