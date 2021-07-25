/* package */

package nikochir.minetime.execut;

/* include */

import nikochir.minetime.Main;

import nikochir.minetime.kernel.Data;
import nikochir.minetime.kernel.User;

/** bukkit **/

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;

/* typedef */

/* Executor class
 * > Description:
 * -> ;
*/
public class ExecutTime extends Execut {

    /* handles */

    @Override
    public boolean onCommand(
        CommandSender objSender,
        Command objCommand,
        String strLabel,
        String[] strArgs
    ) {
        if (strArgs.length == 0) {
            
            Main.doLogE(objSender, "invalid argument count: %d;", strArgs.length);
            return false;
        
        } else if (strArgs.length == 1) {
            
            Main.doLogE(objSender, "invalid argument count: %d;", strArgs.length);
            return false;

        } else if (strArgs.length == 2) {
            
            String strName = strArgs[0];
            
            if (Data.vetUser(strName).join()) {

                Integer numDaysArg = null;

                try {
                    numDaysArg = Integer.parseInt(strArgs[1]);
                } catch (Exception exception) {
                    numDaysArg = 1;
                    Main.doLogE(objSender, "invalid days input!");
                }
    
                final int numDays = numDaysArg;
                
                Data.getDuration(strName, numDays)
                    .thenAcceptAsync(
                        numOnlineTime -> {
                            Main.doLogO(objSender,
                                "total time of \"%s\": %d hours (%d minutes) for %d days;",
                                strName, numOnlineTime, numDays / 60000, numDays
                            );
                        }
                    );

            } else {
                Main.doLogE(objSender, "the user is not found! name: %s;", strName);
            }

        } else {
            Main.doLogE(objSender, "invalid argument count: %d;", strArgs.length);
            return false;
        }
        return true;
    }
}
