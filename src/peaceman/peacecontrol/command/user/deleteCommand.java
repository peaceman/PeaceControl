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
public class deleteCommand extends CommandBase {

    @Override
    public boolean runCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (sender instanceof Player) {
            return this.handleCommandFromPlayer((Player)sender, command, commandLabel, args);
        } else {
            return this.handleCommandFromConsole(sender, command, commandLabel, args);
        }
    }
    
    private boolean handleCommandFromPlayer(Player player, Command command, String commandLabel, String[] args) {
        if (!this.isUserRegistered(player)) {
            player.sendMessage("You aren't registered!");
            return true;
        }
        
        if (args.length != 1) {
            return false;
        }
        
        if (this.plugin.userManager.checkPassword(player.getName(), args[0])) {
            this.plugin.userManager.deleteAndLogoutUser(player);
            player.sendMessage("Your account was successfully deleted");
            return true;
        } else {
            player.sendMessage("Wrong password");
            return true;
        }
    }
    
    private boolean isUserRegistered(Player player) {
        if (this.plugin.userManager.usernameExists(player.getName()))
            return true;
        else
            return false;
    }
    
    private boolean handleCommandFromConsole(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (args.length < 2) {
            this.sendConsoleSyntax(sender);
            return true;
        }
        
        if (!args[0].equals("name") && !args[0].equals("email")) {
            this.sendConsoleSyntax(sender);
            return true;
        }
        
        String deleteBy, value;
        deleteBy = args[0];
        value = args[1];
        
        if (deleteBy.equals("name")) {
            if (!this.plugin.userManager.usernameExists(value)) {
                sender.sendMessage("A user with name " + value + " doesn't exist");
                return true;
            }
            if (this.plugin.userManager.deleteUser(value))
                sender.sendMessage("Successfully deleted user with name " + value);
            else
                sender.sendMessage("Couldn't delete user with name " + value);
        }
        
        if (deleteBy.equals("email")) {
            if (!this.plugin.userManager.emailExists(value)) {
                sender.sendMessage("A user with email " + value + " doesn't exist");
                return true;
            }
            if (this.plugin.userManager.deleteUserByEmail(value))
                sender.sendMessage("Successfully deleted user with email " + value);
            else
                sender.sendMessage("Couldn't delete user with email " + value);
        }
        return true;
    }
    
    private void sendConsoleSyntax(CommandSender sender) {
        sender.sendMessage("Usage: deregister <name|email> <value>");
    }

    public deleteCommand(PeaceControl plugin) {
        super(plugin);
    }
}
