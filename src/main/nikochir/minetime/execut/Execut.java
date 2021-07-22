package minetime.nikochir.minetime.execut

import me.kolossa.playeractivitystat.PlayerActivityStat;
import me.kolossa.playeractivitystat.TextConstants;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerActivityCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        System.out.println("inside that");
        Player player = (Player) sender;

        System.out.println("total time spent out chk: ");
        if (player.hasPermission("PlayerActivityStat.viewOnlineTime") ) {
            if (args.length != 2) {
                System.out.println(TextConstants.CONSOLE_PREFIX + "/playerActivity <Name> <Days>");
                return false;
            }
            System.out.println("inside this");
            String name = args[0];
            int days;
            try {
                days = Integer.parseInt(args[1]);
            } catch (Exception exception) {
                days = 1;
                System.out.println(TextConstants.CONSOLE_PREFIX + "Invalid input.");
            }

            PlayerActivityStat.getInstance().getPlayerActivityManager().getActivityDuration(name, days).thenAcceptAsync(onlineTime -> {
                System.out.println("total time spent: by " + name + " - " + onlineTime );
                double activeTimeInMinutes = onlineTime/60000;
                player.sendMessage(TextConstants.CHAT_PREFIX + "§7Online time in minutes: §6" + activeTimeInMinutes);
            });
            System.out.println("total time spent: by " + name + " in " + days + " days");
        } else {
            System.out.println("nope");
            player.sendMessage(TextConstants.CHAT_PREFIX + "§7Sorry! You are not authorized for this operation §6");
        }

        return true;
    }
}
