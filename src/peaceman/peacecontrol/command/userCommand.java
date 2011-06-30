package peaceman.peacecontrol.command;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import peaceman.peacecontrol.DataMapper;
import peaceman.peacecontrol.PeaceControl;

/**
 *
 * @author Naegele.Nico
 */
public class userCommand extends CommandBase {
    private Map<String, Class<? extends CommandAction>> actions = new HashMap<String, Class<? extends CommandAction>>();
    private Map<Class<? extends CommandAction>, List<String>> aliases = new HashMap<Class<? extends CommandAction>, List<String>>();
    
    public userCommand(PeaceControl plugin) {
        super(plugin);
        this.fillActions();
    }
    
    private void fillActions() {
        List<String> actionNames = new LinkedList<String>();
        //TODO find a way to automatically search for matching classes
        Collections.addAll(actionNames, "list", "info");
        
        for (String actionName : actionNames) {
            try {
                Class <? extends CommandAction> clazz = this.getClass()
                        .getClassLoader()
                        .loadClass("peaceman.peacecontrol.command.user." + actionName + "Action")
                        .asSubclass(CommandAction.class);
                Method getAliases = clazz.getMethod("getAliases");
                List<String> aliases = (List<String>)getAliases.invoke(null);
                this.aliases.put(clazz, aliases);
                for (String alias : aliases) {
                    this.actions.put(alias, clazz);
                }
            } catch (Exception e) {
                System.out.printf("An error occurred while loading action class %s for user command", actionName);
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean runCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (args.length == 0) {
            this.showHelp(sender);
            return true;
        }
        
        String action = args[0];
        if (this.actions.containsKey(action)) {
            try {
                Class<? extends CommandAction> clazz = this.actions.get(action);
                CommandAction actionInstance = clazz.getConstructor(PeaceControl.class).newInstance(this.plugin);
                return actionInstance.runCommand(sender, command, commandLabel, args);
            } catch (Exception e) {
                sender.sendMessage("Couldnt execute action " + action + " of command user");
                e.printStackTrace();
                return true;
            }
        }
        
        return false;
    }
    
    public void showHelp(CommandSender sender) {
        StringBuilder sb = new StringBuilder();
        sb.append("possible actions:\n");
        for (Map.Entry<Class<? extends CommandAction>, List<String>>entry : this.aliases.entrySet()) {
            sb.append(DataMapper.implodeStringArray(entry.getValue()))
                    .append("\n");
        }
        sender.sendMessage(sb.toString());
    }
    
}
