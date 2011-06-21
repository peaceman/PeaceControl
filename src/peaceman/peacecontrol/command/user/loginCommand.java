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
public class loginCommand extends CommandBase {

    public loginCommand(PeaceControl plugin) {
        super(plugin);
    }

    @Override
    public boolean runCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You can't login from console");
            return true;
        }
        Player player = (Player) sender;

        if (!this.isRegistered(player.getName())) {
            sender.sendMessage("You have to register first");
            return true;
        }

        if (args.length != 1) {
            return false;
        }

        String password = args[0];
        try {
            if (!this.plugin.userManager.checkPassword(player.getName(), password)) {
                sender.sendMessage("Wrong password");
                return true;
            }
        } catch (Exception ex) {
            this.plugin.log.error("Couldn't compare the passhashes");
            ex.printStackTrace();
        }

        this.plugin.userManager.loginUser(player);
        return true;
    }

    private boolean isRegistered(String username) {
        return this.plugin.userManager.usernameExists(username);
    }
}
