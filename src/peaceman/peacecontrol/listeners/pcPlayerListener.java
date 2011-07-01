package peaceman.peacecontrol.listeners;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import peaceman.peacecontrol.PeaceControl;
import peaceman.peacecontrol.Session;
import peaceman.peacecontrol.user.User;

/**
 *
 * @author Naegele.Nico
 */
public class pcPlayerListener extends PlayerListener {
    private final PeaceControl plugin;
    private final String[] allowedCommands = {"l", "login", "register", "reg"};
    
    public pcPlayerListener(PeaceControl plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (!event.getResult().equals(Result.ALLOWED))
            return;
        
        this.plugin.sessionManager.createSession(event.getPlayer());
    }
    
    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        if (!this.plugin.userManager.usernameExists(player.getName())) {
            player.sendMessage("Before you can play on this server you have to register yourself with the following command\n/register <password> [<email>]");
            return;
        } else {
            User user = this.plugin.userManager.getUser(player.getName());
            Session lastSession = user.getLastSession();
            Session actualSession = this.plugin.sessionManager.getSession(player);
            if (lastSession != null) {
                Date actDate = new Date();
                Calendar cal = Calendar.getInstance();
                cal.setTime(lastSession.getEndedAt());
                cal.add(Calendar.MINUTE, 10);
                if (actDate.before(cal.getTime()) && actualSession.getIp().equals(lastSession.getIp())) {
                    this.plugin.userManager.loginUser(player);
                    actualSession.setUser(user);
                    player.sendMessage("You was automatically logged in!");
                    return;
                }
            }
            player.sendMessage("/login <password>");
        }
    }
    
    @Override
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.plugin.userManager.logoutUser(event.getPlayer());
        this.plugin.sessionManager.endSession(event.getPlayer());
    }
    
    @Override
    public void onPlayerChat(PlayerChatEvent event) {
        if (event.isCancelled())
            return;
        
        if (!this.plugin.userManager.isLoggedIn(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
    
    @Override
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (event.isCancelled())
            return;
        
        if (!this.plugin.userManager.isLoggedIn(event.getPlayer())) {
            String command = event.getMessage().split(" ")[0].replaceFirst("/", "");
            
            if (!Arrays.asList(this.allowedCommands).contains(command)) {
                event.setCancelled(true);
            }
        }
    }
    
    @Override
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.isCancelled())
            return;
        
        if (!this.plugin.userManager.isLoggedIn(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
    
    @Override
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (event.isCancelled())
            return;
        
        if (!this.plugin.userManager.isLoggedIn(event.getPlayer()))
            event.setCancelled(true);
    }
    
    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.isCancelled())
            return;
        
        if (this.plugin.userManager.isLoggedIn(event.getPlayer()))
            event.setCancelled(true);
    }
}
