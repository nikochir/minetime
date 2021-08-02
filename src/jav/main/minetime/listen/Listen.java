/* package */

package main.minetime.listen;

/* include */

import main.minetime.Main;
import main.minetime.listen.*;

/* bukkit */

import org.bukkit.event.Listener;


/* typedef */

/* Listen class
 * > Description:
 * -> basic plugin event listener;
*/
public class Listen implements Listener {

    /* actions */

    static public boolean doInit() {
        
        Main.setListen("player", new ListenPlayer());

        return true;
    
    }

    static public boolean doQuit() {
        
        return true;
    
    }

}
/* endfile */