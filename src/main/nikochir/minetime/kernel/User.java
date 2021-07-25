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

import java.util.Date;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.Duration;

/** bukkit **/

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/** mongodb **/

import com.mongodb.DBObject;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

/** bson **/

import org.bson.Document;
import org.bson.conversions.Bson;

/* typedef */

/* User class
 * > Description:
 * -> ;
*/
public class User {
    
    /* members */
    
    static private HashMap<String, User> tab;

    private UUID objUUID;
    private String strName;
    
    private boolean bitOnline;
    
    private long numTimeJoin;
    private long numTimeQuit;
    private Duration numTimePlay;
    
    /* codetor */
    
    private User(UUID objUUID, String strName) {
        
        this.objUUID = objUUID;
        this.strName = strName;

        this.bitOnline = false;
        
        this.numTimeJoin = 0;
        this.numTimeQuit = 0;
        this.numTimePlay = Duration.ofSeconds(0);

        MongoCollection<Document> objDBCollection = Data.getCollection();
        Bson objFilter = this.getFilter();

        if (objDBCollection.count(objFilter) > 0) {
            Main.doLogE("user document is already created! name: %s;", this.getName());
        } else  {
            objDBCollection.insertOne(this.getDocument());
        }

    }

    /* getters */

    static public User getUser(String strName) { return getUser(Main.getPlayer(strName)); }
    static public User getUser(Player objPlayer) { return getUser(objPlayer.getUniqueId()); }
    static public User getUser(UUID objUUID) {
        if (vetUser(objUUID)) {
            return tab.get(objUUID.toString());
        } else {
            Main.doLogO("user does not exist! uuid: %s;", objUUID.toString());
            return null;
        }
    }

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
    static public BasicDBObject getQuery(boolean bitOnline) {
        BasicDBObject objQuery = new BasicDBObject();
        objQuery.put("online", bitOnline);
        return objQuery;
    }
    static public BasicDBObject getQuery(UUID objUUID, String strName) {
        BasicDBObject objQuery = getQuery(objUUID);
        objQuery.put("name", strName);
        return objQuery;
    }
    static public BasicDBObject getQuery(UUID objUUID, String strName, boolean bitOnline) {
        BasicDBObject objQuery = getQuery(objUUID, strName);
        objQuery.put("online", bitOnline);
        return objQuery;
    }
    static public BasicDBObject getQuery(User objUser) {
        BasicDBObject objQuery = getQuery(
            objUser.getUUID(),
            objUser.getName(),
            objUser.vetOnline()
        );
        return objQuery;
    }

    static public Bson getFilter(UUID objUUID)      { return Filters.eq("uuid", objUUID.toString()); }
    static public Bson getFilter(String strName)    { return Filters.eq("name", strName); }
    static public Bson getFilter(boolean bitOnline) { return Filters.eq("online", bitOnline); }
    static public Bson getFilter(UUID objUUID, String strName) {
        return Filters.and(getFilter(objUUID), getFilter(strName));
    }
    static public Bson getFilter(UUID objUUID, String strName, boolean bitOnline) {
        return Filters.and(getFilter(objUUID, strName), getFilter(bitOnline));
    }
    static public Bson getFilter(User objUser) {
        return getFilter(
            objUser.getUUID(),
            objUser.getName(),
            objUser.vetOnline()
        );
    }

    static public Document getDocument(User objUser) {
        Document objDocument = new Document();
        objDocument.put("uuid", objUser.getUUID().toString());
        objDocument.put("name", objUser.getName());
        objDocument.put("online", objUser.getName());
        objDocument.put("time_join", objUser.getTimeJoin());
        objDocument.put("time_quit", objUser.getTimeQuit());
        objDocument.put("time_play", objUser.getTimePlay());
        objDocument.put("date", new Date());
        return objDocument;
    }

    public Player getPlayer() { return Main.getPlayer(this.getUUID()); }
    
    public UUID getUUID()     { return this.objUUID; }
    public String getName()   { return this.strName; }

    public long getTimeJoin()     { return this.numTimeJoin; }
    public long getTimeQuit()     { return this.numTimeQuit; }
    public long getTimePlay()     { return this.numTimePlay.toSeconds(); }
    public long getTimePlaySecs() { return this.numTimePlay.toSeconds(); }
    public long getTimePlayMils() { return this.numTimePlay.toMillis(); }
    public long getTimePlayMins() { return this.numTimePlay.toMinutes(); }
    public long getTimePlayHors() { return this.numTimePlay.toHours(); }

    public BasicDBObject getQuery() {
        BasicDBObject objQuery = new BasicDBObject();
        objQuery.put("uuid", this.getUUID().toString());
        objQuery.put("name", this.getName());
        objQuery.put("online", this.vetOnline());
        return objQuery;
    }

    public Bson getFilter() {
        Bson objFilter = Filters.and(
            Filters.eq("uuid", this.getUUID().toString()),
            Filters.eq("name", this.getName()),
            Filters.eq("online", this.vetOnline())
        );
        return objFilter;
    }

    public Document getDocument() {
        Document objDocument = new Document();
        objDocument.put("uuid", this.getUUID().toString());
        objDocument.put("name", this.getName());
        objDocument.put("online", this.vetOnline());
        objDocument.put("time_join", this.getTimeJoin());
        objDocument.put("time_quit", this.getTimeQuit());
        objDocument.put("time_play", this.getTimePlay());
        objDocument.put("date", new Date());
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
    
    /* vetters */

    static public boolean vetUser(UUID objUUID)     { return tab.containsKey(objUUID.toString()); }
    static public boolean vetUser(String strName)   { return Data.vetUser(strName); }
    static public boolean vetUser(Player objPlayer) { return vetUser(objPlayer.getUniqueId()); }

    public boolean vetOnline() { return this.bitOnline; }
    
    /* actions */

    static public boolean doInit() {
        
        tab = new HashMap<String, User>();

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
        
        this.bitOnline = true;
        
        this.numTimeJoin = System.currentTimeMillis();
        this.numTimePlay = Duration.ZERO;

        this.onUpdate();

    }
    
    public void onQuit() {
        
        this.bitOnline = false;
        
        this.numTimeQuit = System.currentTimeMillis();
        this.numTimePlay = Duration.ofMillis(this.numTimeQuit - this.numTimeJoin);
        
        this.onUpdate();

    }

    public void onUpdate() { onUpdate(Data.getCollection()); }
    public void onUpdate(MongoCollection<Document> objDBCollection) {
        
        Bson objFilter = this.getFilter();

        if (objDBCollection.count(objFilter) > 0) {
            long numPlayTime = objDBCollection.find(objFilter).first().getLong("time_play");
            objDBCollection.updateOne( objFilter, Updates.set("online", this.vetOnline()) );
            objDBCollection.updateOne( objFilter, Updates.set("time_join", this.getTimeJoin()) );
            objDBCollection.updateOne( objFilter, Updates.set("time_quit", this.getTimeQuit()) );
            objDBCollection.updateOne( objFilter, Updates.set("time_play", this.getTimePlay() + numPlayTime) );
        } else  {
            Main.doLogE("user document is not found! name: %s;", this.getName());
            objDBCollection.insertOne(this.getDocument());
        }

    }

}

/* endfile */