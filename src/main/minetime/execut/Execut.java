/* package */

package main.minetime.execut;

/* include */

import main.minetime.Main;
import main.minetime.execut.ExecutTime;

/** bukkit **/

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/* typedef */

/* Executor class
 * > Description:
 * -> core plugin command manager;
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