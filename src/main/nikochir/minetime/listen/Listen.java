/* package */
package nikochir.minetime.listen;
/* include */
import nikochir.Main;
/* bukkit */
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.entity.Player;
/* typedef */
public class Listen implements Listener {
    /* actions */
    /* handles */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        //Main.get().setPlayer(player.getUniqueId(), player.getName());
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        //Main.get().setPlayer(player.getUniqueId(), player.getName());
    }
}
/* endfile */