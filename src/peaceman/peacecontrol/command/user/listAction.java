package peaceman.peacecontrol.command.user;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import peaceman.peacecontrol.PeaceControl;
import peaceman.peacecontrol.command.CommandAction;
import peaceman.peacecontrol.user.User;

/**
 *
 * @author Naegele.Nico
 */
public class listAction extends CommandAction {
    private int pageNumber = 1;
    private int entriesPerPage = 10;
    public listAction(PeaceControl plugin) {
        super(plugin);
    }

    @Override
    public boolean runCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (args.length == 2) {
            try {
                int newPageNumber = Integer.parseInt(args[1]);
                if (newPageNumber <= this.getNrOfPages()) {
                    this.pageNumber = newPageNumber;
                }
            } catch (NumberFormatException e) {
                sender.sendMessage("Passed pageNr option isnt a valid number!");
                return true;
            }
        }
        
        int offset = (this.pageNumber - 1) * this.entriesPerPage;
        
        List<User> users = this.plugin.userManager.getUsers();

        StringBuilder sb = new StringBuilder();
        sb.append("Showing page no ")
                .append(this.pageNumber)
                .append("/")
                .append(this.getNrOfPages())
                .append("\n");
        
        int startIndex = offset;
        int endIndex = offset + this.entriesPerPage;
        if (endIndex >= users.size()) {
            endIndex = users.size() - 1;
        }
        for (User user : users.subList(startIndex, endIndex)) {
            sb.append("Name: ")
                    .append(user.getUsername())
                    .append(" eMail: ");
            if (user.getEmail() != null)
                sb.append(user.getEmail());
            sb.append("\n");
        }         
        sender.sendMessage(sb.toString());                
        
        return true;
    }
    
    private int getNrOfPages() {
        return (int)Math.ceil((double)this.plugin.userManager.getUserCount() / (double)this.entriesPerPage);
    }
    
    public static List<String> getAliases() {
        List<String> toReturn = new LinkedList<String>();
        Collections.addAll(toReturn, "list", "l");
        return toReturn;
    }
}
