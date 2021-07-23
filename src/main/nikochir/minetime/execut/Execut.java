/* package */
package src.main.nikochir.minetime.execut;
/* include */
import src.main.nikochir.minetime.Main;
/** bukkit **/
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
/* typedef */
/** Executor class
 * > Description:
 * -> ;
 */
public class Execut implements CommandExecutor {
    /* handles */
    @Override
    public boolean onCommand(
        @NotNull CommandSender objSender,
        @NotNull Command objCommand,
        @NotNull String strLabel,
        @NotNull String[] strArgs
    ) {
        Player objPlayer = (Player) objSender;
        if (strArgs.length == 0) {
            Main.get().doLogO("invalid argument count: %d;", strArgs.length);
            return false;
        } else if (strArgs.length == 1) {
            Main.get().doLogO("invalid argument count: %d;", strArgs.length);
            return false;
        } else if (strArgs.length == 2) {
                String strName = strArgs[0];
                Integer numDaysArg = null;
            try {
                numDaysArg = Integer.parseInt(strArgs[1]);
            } catch (Exception exception) {
                numDaysArg = 1;
                Main.get().doLogO("invalid days input!");
            }
            final int numDays = numDaysArg;
            Main.get().getDuration(strName, numDays)
                .thenAcceptAsync(
                    numOnlineTime -> {
                        Main.get().doLogO(objPlayer,
                            "total time of \"%s\": %d hours (%d minutes) for %d days;",
                            strName, numOnlineTime, numDays / 60000, numDays
                        );
                    }
                );
        } else {
            Main.get().doLogO("invalid argument count: %d;", strArgs.length);
            return false;
        }
        return true;
    }
}
