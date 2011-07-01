package peaceman.peacecontrol.command.user;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import peaceman.peacecontrol.PeaceControl;
import peaceman.peacecontrol.Session;
import peaceman.peacecontrol.command.CommandAction;
import peaceman.peacecontrol.user.User;

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
        if (args.length < 3) {
            this.showHelp(sender);
            return true;
        }
        
        String searchUserBy = args[1];
        String value = args[2];
        
        User user = null;
        
        if (searchUserBy.equalsIgnoreCase("e") || searchUserBy.equalsIgnoreCase("email")) {
            user = this.plugin.userManager.getUserByEmail(value);
            searchUserBy = "email";
        } else if (searchUserBy.equalsIgnoreCase("n") || searchUserBy.equalsIgnoreCase("name")) {
            user = this.plugin.userManager.getUser(value);
            searchUserBy = "name";
        }
        
        if (user == null) {
            sender.sendMessage("Cant find a user with " + searchUserBy + ": " + value);
            return true;
        }
        
        this.showUserInformation(sender, user);
        return true;
    }
    
    private void showHelp(CommandSender sender) {
        sender.sendMessage("Usage: /user info <name|email> <value>");
    }
    
    private void showUserInformation(CommandSender sender, User user) {
        StringBuilder sb = new StringBuilder();
        String username = user.getUsername();
        Date registeredSince = user.getRegisteredAt();
        String email = user.getEmail().isEmpty() ? "none" : user.getEmail();
        String ip = user.getLastSession() != null ? user.getLastSession().getIp() : "Hasnt logged in yet!";
        
        sb.append("Username: ").append(username).append("\n")
                .append("eMail: ").append(email).append("\n")
                .append("Registered since: ").append(registeredSince).append("\n")
                .append("Last IP: ").append(ip);
        sender.sendMessage(sb.toString());                
    }
    
    public static List<String> getAliases() {
        List<String> toReturn = new LinkedList<String>();
        Collections.addAll(toReturn, "info", "i");
        return toReturn;
    }
}
