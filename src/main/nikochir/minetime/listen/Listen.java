/* package */

package nikochir.minetime.listen;

/* include */

import nikochir.minetime.Main;
import nikochir.minetime.listen.*;

/* bukkit */

import org.bukkit.event.Listener;


/* typedef */

/* Listen class
 * > Description:
 * -> ;
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