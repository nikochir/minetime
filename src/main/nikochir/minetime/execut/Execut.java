/* package */

package nikochir.minetime.execut;

/* include */

import nikochir.minetime.Main;

/** bukkit **/

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/* typedef */

/* Executor class
 * > Description:
 * -> ;
*/
public class Execut implements CommandExecutor {
    
    /* actions */
    
    static public boolean doInit() {
        
        Main.setExecut("minetime", new ExecutTime());;
        
        return true;

    }

    static public boolean doQuit() {
        
        return true;

    }

    public boolean onCommand(
        CommandSender objSender,
        Command objCommand,
        String strLabel,
        String strArgs[]
    ) {
        return false;
    }

}

/* endfile */