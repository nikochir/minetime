/* package */

package nikochir.minetime.listen;

/* include */

import nikochir.minetime.Main;
import nikochir.minetime.kernel.Data;
import nikochir.minetime.kernel.User;

/** bukkit **/

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import org.bukkit.entity.Player;

/* typedef */

/* ListenPlayer class
 * > Description:
 * -> ;
*/
public class ListenPlayer extends Listen {
    
    /* handles */

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent objEvent) {
        
        Player objPlayer = objEvent.getPlayer();
        
        if (User.vetUser(objPlayer)) {
            User.getUser(objPlayer).onJoin();
        } else {
            User.setUser(objPlayer).onJoin();
        }

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent objEvent) {

        Player objPlayer = objEvent.getPlayer();
        
        if (User.vetUser(objPlayer)) {
            User.getUser(objPlayer).onQuit();
        } else {
            Main.doLogE(
                "player is not registered! uuid: %s; name: %s;",
                objPlayer.getUniqueId().toString(), objPlayer.getName()
            );
        }

    }

}

/* endfile */