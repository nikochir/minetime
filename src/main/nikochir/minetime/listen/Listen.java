/* package */

package src.main.nikochir.minetime.listen;

/* include */

import src.main.nikochir.minetime.Main;

/* bukkit */

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import org.bukkit.entity.Player;

/* typedef */

/** Listener class
 * > Description:
 * -> ;
 */
public class Listen implements Listener {

    /* actions */

    /* handles */

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player objPlayer = event.getPlayer();
        Main.get().onUserJoin(objPlayer.getUniqueId(), objPlayer.getName());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player objPlayer = event.getPlayer();
        Main.get().onUserQuit(objPlayer.getUniqueId(), objPlayer.getName());
    }
}
/* endfile */