package peaceman.peacecontrol.command.user;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import peaceman.peacecontrol.PeaceControl;
import peaceman.peacecontrol.command.CommandBase;

/**
 *
 * @author Naegele.Nico
 */
public class registerCommand extends CommandBase {
	public registerCommand(PeaceControl plugin) {
		super(plugin);
	}
	
	@Override
	public boolean runCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		try {
			if (!(sender instanceof Player)) {
				return this.handleCommandFromConsole(sender, command, commandLabel, args);
			} else {
				return this.handleCommandFromPlayer((Player)sender, command, commandLabel, args);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
	private boolean handleCommandFromConsole(CommandSender sender, Command command, String commandLabel, String[] args) throws Exception {
		if (args.length < 2) {
			// to create an account from console a username and password is needed
			sender.sendMessage("Usage: register <username> <password> [<email>]");
			return true;
		}
		String username = null, password = null, email = null;
		username = args[0];
		password = args[1];
		if (args.length == 3) {
			email = args[2];
		}
		
		if (this.isUserRegistered(username, email)) {
			sender.sendMessage("A user with this name is already registered");
			return true;
		}
		
		if (this.plugin.userManager.createUser(username, password, email)) {
			sender.sendMessage("Successfully created the user " + username);
		}
		return true;
	}
	
	private boolean handleCommandFromPlayer(Player sender, Command command, String commandLabel, String[] args) throws Exception {
		if (args.length < 1) {
			// at least a password is needed
			return false;
		}
		String password = null, email = null;
		password = args[0];
		if (args.length == 2) {
			email = args[1];
		}
		
		if (this.isUserRegistered(sender, email)) {
			sender.sendMessage("You are already registered");
			return true;
		}
		
		boolean result = this.plugin.userManager.createUser(sender, password, email);
		if (result == true) {
			this.plugin.userManager.loginUser(sender);
			sender.sendMessage("Successfully registered");
		}
		return true;
	}
	
	private boolean isUserRegistered(String username, String email) {
		boolean result = false;
		
		if (this.plugin.userManager.usernameExists(username)) {
			result = true;
		}
		
		if (email == null) {
			return result;
		}
		
		if (this.plugin.userManager.emailExists(email)) {
			result = true;
		}		
		
		return result;
	}
	
	private boolean isUserRegistered(Player player, String email) {
		return this.isUserRegistered(player.getDisplayName(), email);
	}
}
