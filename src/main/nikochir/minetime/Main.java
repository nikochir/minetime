/* pakcage */
package nikochir.minetime;
/* include */
import nikochir.minetime.execut.Execut;
/** java **/
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
/** database **/
import com.mongodb.client.MongoDatabase;
import me.kolossa.mongoapi.spigot.MongoAPI;
import me.kolossa.playeractivitystat.listeners.InitListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
/* typedef */
/** Main class
 * > Description:
 * -> ;
 */
public final class Main extends JavaPlugin {
    private static PlayerActivityStat instance;
    private ExecutorService executorService;
    private PlayerActivityManager playerActivityManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        executorService = Executors.newCachedThreadPool(
                (new ThreadFactoryBuilder()).setNameFormat("PlayerActivityStat T-%1$d").build()
        );

        MongoDatabase database = MongoAPI.getManager().getDatabase(getConfig().getString("mongodb.database"));
        playerActivityManager = new PlayerActivityManager(
                database.getCollection("playerActivityStat"),
                executorService
        );

        getCommand("playerActivity").setExecutor(new PlayerActivityCommand());
        Bukkit.getPluginManager().registerEvents(new InitListener(), this);
    }

    @Override
    public void onDisable() {
        executorService.shutdown();
    }

    public PlayerActivityManager getPlayerActivityManager() {
        return playerActivityManager;
    }

    public static PlayerActivityStat getInstance() {
        return instance;
    }
}
