package peaceman.peacecontrol.command.user;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import peaceman.peacecontrol.PeaceControl;
import peaceman.peacecontrol.command.CommandBase;
import peaceman.peacecontrol.user.User;

/**
 *
 * @author Naegele.Nico
 */
public class logoutCommand extends CommandBase {

    public logoutCommand(PeaceControl plugin) {
        super(plugin);
    }

    @Override
    public boolean runCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (sender instanceof Player) {
            return this.handleCommandFromPlayer((Player)sender, command, commandLabel, args);
        } else {
            return this.handleCommandFromConsole(sender, command, commandLabel, args);
        }
    }
    
    private boolean handleCommandFromConsole(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (!this.checkConsoleSyntax(args)) {
            this.showConsoleSyntax(sender);
            return true;
        }
        
        String searchUserBy, value;
        searchUserBy = args[0];
        value = args[1];
        
        User user = null;
        if (searchUserBy.equals("name")) {
            if (!this.plugin.userManager.usernameExists(value)) {
                sender.sendMessage("A user with " + searchUserBy + " " + value + " doesn't exist");
                return true;
            }
            user = this.plugin.userManager.getUser(value);
        } else {
            if (!this.plugin.userManager.emailExists(value)) {
                sender.sendMessage("A user with " + searchUserBy + " " + value + " doesn't exist");
                return true;
            }
            user = this.plugin.userManager.getUserByEmail(value);
        }
        
        Player player = this.plugin.getServer().getPlayer(user.getUsername());
        if (player == null || this.plugin.userManager.isLoggedIn(player)) {
            sender.sendMessage("This player isn't logged in");
            return true;
        }
        
        this.plugin.userManager.logoutUser(player);
        
        player.sendMessage("You were logged out by the console");
        sender.sendMessage("The player " + player.getName() + " was successfully logged out");
        return true;
    }
    
    private boolean checkConsoleSyntax(String[] args) {
        if (args.length < 2)
            return false;
        
        if (!args[0].equals("name") && !args[0].equals("email"))
            return false;
        
        return true;
    }
    
    private void showConsoleSyntax(CommandSender sender) {
        sender.sendMessage("Usage: logout <name|email> <value>");
    }
    
    private boolean handleCommandFromPlayer(Player player, Command command, String commandLabel, String[] args) {
        if (!this.plugin.userManager.isLoggedIn(player)) {
            player.sendMessage("You aren't logged in");
            return true;
        }
        
        this.plugin.userManager.logoutUser(player);
        player.sendMessage("Successfully logged you out");
        
        return true;
    }
}
