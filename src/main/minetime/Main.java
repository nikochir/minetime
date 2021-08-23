/* pakcage */

package main.minetime;

/** java **/

import java.util.UUID;

import org.bukkit.plugin.PluginManager;

/** bukkit **/

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import main.minetime.Main;
import main.minetime.config.Config;
import main.minetime.execut.Execut;
import main.minetime.kernel.Data;
import main.minetime.kernel.User;
import main.minetime.listen.Listen;
import main.minetime.permit.Permit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

import org.bukkit.entity.Player;
import org.bukkit.entity.HumanEntity;

/* typedef */

/* Main class
 * > Description:
 * -> core manager class;
*/
public final class Main extends JavaPlugin {

    /* statics */
    
    static public final int NUM_DAYS = 33;

    static private Main objInstance;
    
    /* vetters */

    static public boolean vetPlayer(Player objPlayer)      { return get().getServer().getPlayer(objPlayer.getUniqueId()) != null; }
    static public boolean vetPlayer(HumanEntity objEntity) { return get().getServer().getPlayer(objEntity.getUniqueId()) != null; }
    static public boolean vetPlayer(UUID objUUID)          { return get().getServer().getPlayer(objUUID) != null; }
    static public boolean vetPlayer(String strName)        { return get().getServer().getPlayer(strName) != null; }
    
    /* getters */
    
    static public Main get() { return objInstance; }
    
    static public Player getPlayer(HumanEntity objEntity) { return get().getServer().getPlayer(objEntity.getUniqueId()); }
    static public Player getPlayer(UUID objUUID)          { return get().getServer().getPlayer(objUUID); }
    static public Player getPlayer(String strName)        { return get().getServer().getPlayer(strName); }

    /* setters */
    
    static public boolean setExecut(String strName, Execut objExecut) {

        PluginCommand objCommand = get().getCommand(strName);

        if (objCommand == null) {
            doLogE(
                "command is not found! name: %s;",
                strName
            );
            return false;
        } else {
            objCommand.setExecutor(objExecut);
            return true;
        }

    }

    static public boolean setListen(String strName, Listen objListen) {

        PluginManager objManager = get().getServer().getPluginManager();

        objManager.registerEvents(objListen, get());

        return true;

    }
    
    static public boolean setPermit(String strName, Permit objPermit) {

        PluginManager objManager = get().getServer().getPluginManager();

        objManager.addPermission(objPermit);

        return true;

    }
    
    /* actions */

    static private boolean doInit() {

        Execut.doInit();
        Listen.doInit();
        Permit.doInit();
        Config.doInit();
        Data.doInit();
        User.doInit();
        
        return true;
    }

    static private boolean doQuit() {
        
        User.doQuit();
        Data.doQuit();
        Config.doQuit();
        Permit.doQuit();
        Listen.doQuit();
        Execut.doQuit();

        return true;
    }

    static public boolean doLogO(String strFormat, Object... objArgs) {
        
        System.out.println(Config.getStrNameOfLogO() + String.format(strFormat, objArgs));

        return true;
    }
    
    static public boolean doLogO(CommandSender objSender, String strFormat, Object... objArgs) {
        
        System.out.println(Config.getStrNameOfLogO() + String.format(strFormat, objArgs));

        objSender.sendMessage(String.format(strFormat, objArgs));

        return true;
    }

    static public boolean doLogE(String strFormat, Object... objArgs) {
        
        System.err.println(Config.getStrNameOfLogE() + String.format(strFormat, objArgs));

        return true;
    }
    
    static public boolean doLogE(CommandSender objSender, String strFormat, Object... objArgs) {
        
        System.err.println(Config.getStrNameOfLogE() + String.format(strFormat, objArgs));

        objSender.sendMessage(Config.getStrNameOfLogE() + String.format(strFormat, objArgs));

        return true;
    }

    /* handles */

    @Override
    public void onEnable() {
        
        objInstance = this;

        doInit();
    
    }

    @Override
    public void onDisable() {
    
        doQuit();
        
        objInstance = null;

    }

}
/* endfile */