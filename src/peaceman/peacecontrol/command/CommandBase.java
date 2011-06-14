/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package peaceman.peacecontrol.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import peaceman.peacecontrol.PeaceControl;

/**
 *
 * @author Naegele.Nico
 */
abstract public class CommandBase {
	private final PeaceControl plugin;
	
	public CommandBase(PeaceControl plugin) {
		this.plugin = plugin;
	}
	
	abstract public boolean runCommand(CommandSender sender, Command command, String commandLabel, String[] args);
}
