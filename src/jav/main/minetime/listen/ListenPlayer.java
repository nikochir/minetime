/* package */

package main.minetime.listen;

/* include */

import main.minetime.Main;
import main.minetime.kernel.Data;
import main.minetime.kernel.User;
import main.minetime.listen.Listen;

/** bukkit **/

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import org.bukkit.entity.Player;

/* typedef */

/* ListenPlayer class
 * > Description:
 * -> listener for player related events:
 * --> join/quit event response;
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