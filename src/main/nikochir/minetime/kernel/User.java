/* package */

package nikochir.minetime.kernel;

/* include */

import nikochir.minetime.Main;
import nikochir.minetime.config.Config;
import nikochir.minetime.kernel.Data;

/** java **/

import java.util.Collection;
import java.util.HashMap;

import java.util.UUID;

import java.time.Duration;

/** bukkit **/

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/* typedef */

/* User class
 * > Description:
 * -> ;
*/
public class User {
    
    /* members */
    
    static private HashMap<UUID, User> tab;

    private UUID objUUID;
    private String strName;

    private long numTimeJoin;
    private long numTimeQuit;
    private Duration numTimePlay;
    
    /* codetor */
    
    private User(UUID objUUID, String strName) {
        
        this.objUUID = objUUID;
        this.strName = strName;

        this.numTimeJoin = 0;
        this.numTimeQuit = 0;
        this.numTimePlay = null;
    
    }

    /* getters */

    static public User getUser(String strName) { return getUser(Main.getPlayer(strName)); }
    static public User getUser(Player objPlayer) { return getUser(objPlayer.getUniqueId()); }
    static public User getUser(UUID objUUID) {
        if (vetUser(objUUID)) {
            Main.doLogO("user does not exist! uuid: %s;", objUUID.toString());
            return null;
        } else {
            return tab.get(objUUID);
        }
    }

    public UUID getUUID()     { return this.objUUID; }
    public String getName()   { return this.strName; }
    public Player getPlayer() { return Main.getPlayer(this.getUUID()); }
    
    public long getTimeJoin()     { return this.numTimeJoin; }
    public long getTimeQuit()     { return this.numTimeQuit; }
    public long getTimePlaySecs() { return this.numTimePlay.toSeconds(); }
    public long getTimePlayMils() { return this.numTimePlay.toSeconds(); }
    public long getTimePlayMins() { return this.numTimePlay.toMillis(); }
    public long getTimePlayHors() { return this.numTimePlay.toHours(); }

    /* setters */

    static private User setUser(UUID objUUID, String strName) {
        
        if (vetUser(objUUID)) {
            
            Main.doLogE(
                "user has already been created! uuid: %s; name: %s;",
                objUUID.toString(), strName
            );
            
            return tab.get(objUUID);
            
        } else if (Main.vetPlayer(objUUID)) {
            
            User objUser = new User(objUUID, strName);
            tab.put(objUUID, objUser);
            
            return objUser;

        } else {
            
            Main.doLogE(
                "player does not exist! uuid: %s; name: %s;",
                objUUID.toString(), strName
            );
            return null;
        
        }

    }

    static public User setUser(Player objPlayer) {
        
        return setUser(objPlayer.getUniqueId(), objPlayer.getName());
    
    }
    
    /* vetters */

    static public boolean vetUser(UUID objUUID)     { return tab.containsKey(objUUID); }
    static public boolean vetUser(String strName)   { return Data.vetUser(strName).join(); }
    static public boolean vetUser(Player objPlayer) { return tab.containsKey(objPlayer.getUniqueId()); }
    
    /* actions */

    static public boolean doInit() {
        
        tab = new HashMap<UUID, User>();

        Collection<? extends Player> objPlayers = Main.get().getServer().getOnlinePlayers();

        for (Player itrObjPlayer : objPlayers) {
            if (vetUser(itrObjPlayer)) {
                continue;
            } else {
                setUser(itrObjPlayer);
            }
        }

        return true;
    }

    static public boolean doQuit() {
        
        tab.clear();
        tab = null;

        return true;

    }

    /* handles */
    
    public void onJoin() {
        
        this.numTimeJoin = System.currentTimeMillis();
        this.numTimePlay = Duration.ZERO;

        Data.onUserJoin(this);
        
    }
    
    public void onQuit() {
        
        this.numTimeQuit = System.currentTimeMillis();
        this.numTimePlay = Duration.ofMillis(this.numTimeQuit - this.numTimeJoin);
        
        Data.onUserQuit(this);
        
    }

}

/* endfile */