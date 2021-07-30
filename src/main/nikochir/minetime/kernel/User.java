/* package */

package nikochir.minetime.kernel;

/* include */

import nikochir.minetime.Main;
import nikochir.minetime.config.Config;
import nikochir.minetime.kernel.Data;
import nikochir.minetime.kernel.User;

/** java **/

import java.util.UUID;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import java.util.Date;

import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/** bukkit **/

import org.bukkit.entity.Player;

/** mongodb **/

import com.mongodb.BasicDBObject;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

/** bson **/

import org.bson.Document;
import org.bson.conversions.Bson;

/* typedef */

/* User class
 * > Description:
 * -> uuid: unique identifier of the actual player;
 * -> name: displayed name of the actual player;
 * -> date and time information about joining, quitting and playing;
 * -> array of played seconds for each day of the last month;
 * --> by default the time is measured in seconds;
*/
public class User implements Serializable {
    
    /* statics */
    
    static private HashMap<String, User> tab;
    
    /* members */

    private UUID objUUID;
    private String strName;
    
    private boolean bitLive;
    
    private Date objDateJoin;
    private Date objDateQuit;
    
    private long numTimePlay;
    private long numTimeDays[];
    
    /* codetor */
    
    private User(UUID objUUID, String strName) {

        this.objUUID = objUUID;
        this.strName = strName;

        this.bitLive = false;
        
        this.objDateJoin = new Date();
        this.objDateQuit = new Date();

        this.numTimePlay = 0;
        this.numTimeDays = new long[Main.NUM_DAYS];

        Bson objFilter = this.getFilter();

        if (Data.vetDocument(objFilter)) {
            Document objDocument = Data.getDocument(objFilter);
            this.setTimeDays(objDocument.getList("time_days", Long.class));
            this.objDateJoin = objDocument.getDate("date_join");
            this.objDateQuit = objDocument.getDate("date_quit");
        } else  {
            Data.setDocument(this.getDocument());
            for (int numI = 0; numI < Main.NUM_DAYS; numI++) { this.numTimeDays[numI] = 0; }
        }

    }

    /* getters */

    static public User getUser(Player objPlayer) { return getUser(objPlayer.getUniqueId()); }
    static public User getUser(UUID objUUID) { return getUser(objUUID.toString()); }
    static public User getUser(String strUUID) {
        if (vetUser(strUUID)) {
            return tab.get(strUUID);
        } else {
            Main.doLogO("user does not exist! uuid: %s;", strUUID);
            return null;
        } 
    }

    static public Player getPlayer(User objUser) { return objUser.getPlayer(); }
    static public Player getPlayer(UUID objUUID) { return Main.getPlayer(objUUID); }

    static public BasicDBObject getQuery(UUID objUUID) {
        BasicDBObject objQuery = new BasicDBObject();
        objQuery.put("uuid", objUUID.toString());
        return objQuery;
    }
    static public BasicDBObject getQuery(String strName) {
        BasicDBObject objQuery = new BasicDBObject();
        objQuery.put("name", strName);
        return objQuery;
    }
    static public BasicDBObject getQuery(UUID objUUID, String strName) {
        BasicDBObject objQuery = new BasicDBObject();
        objQuery.put("uuid", objUUID.toString());
        objQuery.put("name", strName);
        return objQuery;
    }
    static public BasicDBObject getQuery(User objUser) {
        BasicDBObject objQuery = objUser.getQuery();
        return objQuery;
    }

    static public Bson getFilter(UUID objUUID)                 { return Filters.eq("uuid", objUUID.toString()); }
    static public Bson getFilter(String strName)               { return Filters.eq("name", strName); }
    static public Bson getFilter(UUID objUUID, String strName) { return Filters.and(getFilter(objUUID), getFilter(strName)); }
    static public Bson getFilter(User objUser)                 { return objUser.getFilter(); }

    static public Document getDocument(UUID objUUID)                 { return Data.getDocument(getFilter(objUUID)); }
    static public Document getDocument(String strName)               { return Data.getDocument(getFilter(strName)); }
    static public Document getDocument(UUID objUUID, String strName) { return Data.getDocument(getFilter(objUUID, strName)); }
    static public Document getDocument(User objUser)                 { return objUser.getDocument(); }

    public Player getPlayer() { return Main.getPlayer(this.getUUID()); }
    
    public UUID getUUID()   { return this.objUUID; }
    public String getName() { return this.strName; }

    public Date getDateJoin() { return this.objDateJoin; }
    public Date getDateQuit() { return this.objDateQuit; }

    public long getTimePlay()   { return this.numTimePlay; }
    public long[] getTimeDays() { return this.numTimeDays; }

    public long getTime()            { return this.numTimeDays[0]; }
    public long getTime(int numDays) { return this.numTimeDays[numDays]; }

    public BasicDBObject getQuery() {
        BasicDBObject objQuery = new BasicDBObject();
        objQuery.put("uuid", this.getUUID().toString());
        objQuery.put("name", this.getName());
        return objQuery;
    }

    public Bson getFilter() {
        Bson objFilter = Filters.and(
            Filters.eq("uuid", this.getUUID().toString()),
            Filters.eq("name", this.getName())
        );
        return objFilter;
    }

    public Document getDocument() {
        Document objDocument = new Document();

        objDocument.put("uuid", this.getUUID().toString());
        objDocument.put("name", this.getName());
        
        objDocument.put("live", this.vetLive());

        objDocument.put("date_join", this.getDateJoin());
        objDocument.put("date_quit", this.getDateQuit());

        objDocument.put("time_play", this.getTimePlay());
        objDocument.put("time_days", this.getTimeDays());

        return objDocument;
    }

    /* setters */
    
    static public User setUser(Player objPlayer) {
        return setUser(objPlayer.getUniqueId(), objPlayer.getName());
    }
    static private User setUser(UUID objUUID, String strName) {
        
        if (vetUser(objUUID)) {

            Main.doLogE(
                "user has already been created! uuid: %s; name: %s;",
                objUUID.toString(), strName
            );
            return tab.get(objUUID.toString());

        } else if (Main.vetPlayer(objUUID)) {

            User objUser = new User(objUUID, strName);
            tab.put(objUUID.toString(), objUser);
            return objUser;

        } else {
            
            Main.doLogE(
                "player does not exist! uuid: %s; name: %s;",
                objUUID.toString(), strName
            );
            return null;
        
        }

    }
    
    private boolean setTimeDays(long numTimeDays[]) { return setTimeDays(numTimeDays, 0); }
    private boolean setTimeDays(long numTimeDays[], long numTimePlay) {
        
        if (numTimeDays.length != Main.NUM_DAYS) {
            Main.doLogE(
                "invalid number of days: %d;",
                numTimeDays
                );
            return false;
        }
        
        for (int numI = 0; numI < Main.NUM_DAYS; numI++) {
            this.numTimeDays[numI] = numTimeDays[numI] + numTimePlay;
        }

        return true;
    }
    private boolean setTimeDays(List<Long> numTimeDays) { return setTimeDays(numTimeDays, 0); }
    private boolean setTimeDays(List<Long> numTimeDays, long numTimePlay) {
        
        if (numTimeDays == null) { Main.doLogE("null argument!"); return false; }

        if (numTimeDays.size() != Main.NUM_DAYS) {
            Main.doLogE("invalid number of days: %d;", numTimeDays);
            return false;
        }
        
        for (int numI = 0; numI < Main.NUM_DAYS; numI++) {
            this.numTimeDays[numI] = numTimeDays.get(numI) + numTimePlay;
        }
        
        return true;
    }

    private boolean setTimeDaysNext() { return this.setTimeDaysNext(this.getDateJoin().getDay() - this.getDateQuit().getDay()); }
    private boolean setTimeDaysNext(int numDaysPassed) {
        if (numDaysPassed == 0) {
            return false;
        } else {
            numDaysPassed = Integer.min(numDaysPassed, Main.NUM_DAYS);
            for (int numI = Main.NUM_DAYS; numI > numDaysPassed; numI--) { this.numTimeDays[numI] = this.numTimeDays[numI - 1]; }
            for (int numI = 0; numI < numDaysPassed; numI++) { this.numTimeDays[numI] = 0; }
            return true;
        }
    }

    /* vetters */

    static public boolean vetUser(String strUUID)   { return tab.containsKey(strUUID); }
    static public boolean vetUser(UUID objUUID)     { return tab.containsKey(objUUID.toString()); }
    static public boolean vetUser(Player objPlayer) { return tab.containsKey(objPlayer.getUniqueId().toString()); }

    public boolean vetLive() { return this.bitLive; }
    
    /* actions */

    static public boolean doInit() {
        
        tab = new HashMap<String, User>();

        Collection<? extends Player> objPlayers = Main.get().getServer().getOnlinePlayers();

        for (Player itrObjPlayer : objPlayers) {
            if (vetUser(itrObjPlayer)) { continue; }
            else { setUser(itrObjPlayer); }
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
        
        if (this.vetLive()) {
            
            Main.doLogE(
                "the player is already live! uuid=%s; name=%s;",
                getUUID().toString(), getName()
            );
            return;

        } else {

            this.bitLive = true;
        
            this.objDateJoin = new Date();
            this.numTimePlay = 0;

            this.setTimeDaysNext();
            
            this.onUpdate();
        
        }

    }
    
    public void onQuit() {
        
        if (this.vetLive()) {
            
            this.bitLive = false;
        
            this.objDateQuit = new Date();
        
            this.numTimePlay = this.objDateQuit.getTime() - this.objDateJoin.getTime();
            
            this.setTimeDaysNext();
            this.setTimeDays(this.numTimeDays, this.numTimePlay);
            
            this.onUpdate();

        } else {
            
            Main.doLogE(
                "the player is not live! uuid=%s; name=%s;",
                this.getUUID().toString(), this.getName()
            );
            return;

        }

    }

    public void onUpdate() {
        
        Bson objFilter = this.getFilter();

        if (Data.vetDocument(objFilter)) {
            
            Data.setDocument( objFilter, Updates.set("live", this.vetLive()) );

            Data.setDocument( objFilter, Updates.set("date_join", this.getDateJoin()) );
            Data.setDocument( objFilter, Updates.set("date_quit", this.getDateQuit()) );

            Data.setDocument( objFilter, Updates.set("time_play", this.getTimePlay()) );
            Data.setDocument( objFilter, Updates.set("time_days", this.getTimeDays()) );

        } else  {

            Data.setDocument(this.getDocument());
        
        }

    }

    /* casters */

    @Override
    public String toString() {
        return String.format(
            "{uuid:%s;name:%s;days:%s};",
            this.getUUID().toString(), this.getName(), this.getTimeDays().toString()
        );
    }

}

/* endfile */