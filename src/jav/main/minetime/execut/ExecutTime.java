/* package */

package main.minetime.execut;

/* include */

import main.minetime.Main;
import main.minetime.execut.Execut;
import main.minetime.kernel.Data;
import main.minetime.kernel.User;

/** bukkit **/

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;

/* typedef */

/* ExecutTime class
 * > Description:
 * -> basic plugin command to get user time;
*/
public class ExecutTime extends Execut {

    /* actions */

    private void doMessage(CommandSender objSender, String strName, int numDays, long numTime) {
        Main.doLogO(objSender,
            "name: \"%s\"; days: %d; time: %dd:%dh:%dm:%ds;",
            strName, numDays, (numTime / (60 * 60 * 24)), (numTime / (60 * 60)) % 24, (numTime / 60) % 60, numTime % 60
        );
    }

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
            
            String strName = strArgs[0];
            
            if (Data.vetUser(strName)) {
    
                long numTime = Data.getUserTime(strName, 1);

                doMessage(objSender, strName, 1, numTime);
                
            } else {
                
                Main.doLogE(objSender, "the user is not found! name: %s;", strName);
                return false;
                
            }
            
        } else if (strArgs.length == 2) {
            
            String strName = strArgs[0];
            String strDays = strArgs[1];

            if (Data.vetUser(strName)) {
                
                
                try {

                    Integer numDays = null;
                    numDays = Integer.parseInt(strDays);
                    
                    long numTime = Data.getUserTime(strName, numDays);
                
                    doMessage(objSender, strName, numDays, numTime);

                    return true;
                    
                } catch (Exception exception) {
                    Main.doLogE(objSender,
                        "invalid days input: %s",
                        strDays
                    );
                    return false;
                }

            } else {
            
                Main.doLogE(objSender, "the user is not found! name: %s;", strName);
                return false;
            
            }

        } else {
        
            Main.doLogE(objSender, "invalid argument count: %d;", strArgs.length);
            return false;
        
        }

        return true;
    }
}
