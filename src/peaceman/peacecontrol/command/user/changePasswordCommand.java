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
public class changePasswordCommand extends CommandBase {

    public changePasswordCommand(PeaceControl plugin) {
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
    
    private boolean handleCommandFromPlayer(Player player, Command command, String commandLabel, String[] args) {
        if (args.length < 2) {
            return false;
        }
        
        String oldPassword, newPassword;
        oldPassword = args[0];
        newPassword = args[1];
        
        if (!this.plugin.userManager.checkPassword(player.getName(), oldPassword)) {
            player.sendMessage("Wrong password");
            return true;
        }
        
        if (this.plugin.userManager.changePassword(player.getName(), newPassword)) {
            player.sendMessage("Successfully changed password");
        }
        return true;
    }
    
    private boolean handleCommandFromConsole(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (!this.checkConsoleCommandSyntax(args)) {
            this.showConsoleCommandSyntax(sender);
            return true;
        }
        
        String searchUserBy, value, newPassword;
        searchUserBy = args[0];
        value = args[1];
        newPassword = args[2];
        
        User user = null;
        if (searchUserBy.equals("name")) {
            user = this.plugin.userManager.getUser(value);
        } else {
            user = this.plugin.userManager.getUserByEmail(value);
        }
        
        if (user == null) {
            sender.sendMessage("A user with " + searchUserBy + " " + value + " doesn't exist");
            return true;
        }
        
        if (this.plugin.userManager.changePassword(user, newPassword)) {
            sender.sendMessage("Successfully changed password for user with " + searchUserBy + " " + value);
        }
        
        return true;
    }
    
    private boolean checkConsoleCommandSyntax(String[] args) {
        if (args.length < 3)
            return false;
        String searchUserBy = args[0];
        
        if (!searchUserBy.equals("name") && !searchUserBy.equals("email")) {
            return false;
        }
        
        return true;
    }
    
    private void showConsoleCommandSyntax(CommandSender sender) {
        sender.sendMessage("Usage: changepw <name|email> <value> <newpassword>");
    }
}
