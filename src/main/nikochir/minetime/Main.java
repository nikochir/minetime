/* pakcage */

package src.main.nikochir.minetime;

/* include */

import src.main.nikochir.minetime.execut.Execut;

import src.main.nikochir.minetime.listen.Listen;

import src.main.nikochir.minetime.permit.Permit;

/** java **/

import java.util.ArrayList;
import java.util.List;

import java.util.Date;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import java.util.UUID;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** bukkit **/

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.runner.Computer;

/** google **/

import com.google.common.util.concurrent.ThreadFactoryBuilder;

/* mongodb */

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import com.mongodb.operation.OrderBy;

/* bson */

import org.bson.Document;

import org.bson.conversions.Bson;

/* typedef */

/** Main class
 * > Description:
 * -> ;
 */
public final class Main extends JavaPlugin {

    /* members */

    private static Main objInstance;
    
    private ExecutorService objExecutorService;
    
    private MongoClient objDBClient;
    private MongoClientURI objDBClientURI;
    private MongoDatabase objDBInstance;
    private MongoCollection<Document> objDBCollection;
    
    private String strDatabaseConnection;
    private String strNameofMain;
    private String strNameofLogo;

    /* getters */
    
    public static Main get() { return objInstance; }
    
    public Boolean getConfigBit(String strKey) { return this.getConfig().getBoolean(strKey); }
    public Integer getConfigInt(String strKey) { return this.getConfig().getInt(strKey); }
    public Double getConfigNum(String strKey) { return this.getConfig().getDouble(strKey); }
    public String getConfigStr(String strKey) { return this.getConfig().getString(strKey); }
    public String getConfigStrNameofMain()    { return this.strNameofMain; }
    public String getConfigStrNameofLogo()    { return this.strNameofLogo; }

    public CompletableFuture<Long> getDuration(String strName, int numDays) {
        return CompletableFuture.supplyAsync(() -> {
            
            BasicDBObject query = new BasicDBObject();
            query.put("name", strName);
            query.put("online", 0);
            query.put("time_join", BasicDBObjectBuilder.start(
                "$gte",
                LocalDateTime.now().minusDays(numDays)
                .toEpochSecond(ZoneOffset.UTC) * 1000
            ).add("$lte", LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) * 1000).get());
            
            long numActiveTime = 0;
            
            MongoCursor<Document> cursor = this.objDBCollection.find(query).iterator();
            
            while(cursor.hasNext()) {
                Document objDoc = (Document)cursor.next();
                numActiveTime += objDoc.getLong("time_play");
            }

            return numActiveTime;

        }, this.objExecutorService);
    }
    /* setters */
    
    /* vetters */
    
    public CompletableFuture<Boolean> vetUser(UUID objId) {
        Bson objFilter = Filters.eq("uuid", objId.toString());
        return CompletableFuture.supplyAsync(() -> {
            return this.objDBCollection.count(objFilter) > 0;
        }, this.objExecutorService);
    }
    
    /* actions */


    /** initting **/

    private boolean doInit() {

        objInstance = this;

        this.doInitExecut();
        this.doInitListen();
        this.doInitPermit();
        this.doInitConfig();
        this.doInitDatabase();
        
        return true;
    }

    private boolean doInitExecut() {
        
        this.getCommand("minetime").setExecutor(new Execut());;
        
        return true;
    }
    
    private boolean doInitListen() {

        this.getServer().getPluginManager().registerEvents(new Listen(), this);;
        
        return true;
    }
    
    private boolean doInitPermit() {
        
        this.getServer().getPluginManager().addPermission(new Permit());
        
        return true;
    }
    
    private boolean doInitConfig() {
        
        this.getConfig().options().copyDefaults(true);
        this.saveDefaultConfig();
        
        this.strNameofMain = this.getConfigStr("nameof_main");
        this.strNameofLogo = this.getConfigStr("nameof_logo");
        this.strDatabaseConnection = this.getConfigStr("database_connection");

        return true;
    }
    
    private boolean doInitDatabase() {

        this.objDBClientURI = new MongoClientURI(this.strDatabaseConnection);
        this.objDBClient = new MongoClient(this.objDBClientURI);
        this.objDBInstance = this.objDBClient.getDatabase(this.getConfig().getString("nameof_database"));
        this.objDBCollection = this.objDBInstance.getCollection(this.getConfig().getString("nameof_collection"));

        this.objExecutorService = Executors.newCachedThreadPool(
            (new ThreadFactoryBuilder()).setNameFormat("minetime T-%1$d").build()
        );

        CompletableFuture<Void> objIndex = CompletableFuture.runAsync(() -> {
            
            int numOrderDescend = OrderBy.DESC.getIntRepresentation();

            this.objDBCollection.createIndex(new Document("uuid", numOrderDescend) );
            this.objDBCollection.createIndex(new Document("online", numOrderDescend) );
            this.objDBCollection.createIndex(new Document("ofline", numOrderDescend) );
            
        }, this.objExecutorService);
    
        objIndex.thenRun(() -> this.doLogO("created player index"));
        
        return true;
    }

    /** quitting **/

    private boolean doQuit() {
        
        this.doQuitDatabase();
        this.doQuitConfig();
        this.doQuitPermit();
        this.doQuitListen();
        this.doQuitExecut();

        this.objInstance = null;

        return true;
    }

    private boolean doQuitExecut() {
        
        return true;
    }

    private boolean doQuitListen() {
        
        return true;
    }

    private boolean doQuitPermit() {
        
        return true;
    }
    
    private boolean doQuitConfig() {
        
        this.strDatabaseConnection = null;
        this.strNameofLogo = null;
        this.strNameofMain = null;

        return true;
    }
    
    private boolean doQuitDatabase() {
        
        this.objDBCollection = null;
        this.objDBInstance = null;
        this.objDBClient.close();
        this.objDBClient = null;
        this.objDBClientURI = null;

        this.objExecutorService.shutdown();
        
        return true;
    }
    
    /** logging **/

    public boolean doLogO(String strFormat, Object... objArgs) {
        
        System.out.println(this.getConfigStrNameofLogo() + String.format(strFormat, objArgs));

        return true;
    }
    
    public boolean doLogO(CommandSender objSender, String strFormat, Object... objArgs) {
        
        System.out.println(this.getConfigStrNameofLogo() + String.format(strFormat, objArgs));

        objSender.sendMessage(String.format(strFormat, objArgs));

        return true;
    }

    /* handles */

    @Override
    public void onEnable() {
        this.doInit();
    }

    @Override
    public void onDisable() {
        this.doQuit();
    }

    public CompletableFuture<Void> onUserJoin(UUID objId, String strName) {
        return CompletableFuture.runAsync(() -> {

            Document objDoc = new Document("uuid", objId.toString());
            objDoc.put("name", strName);
            objDoc.put("time_join", System.currentTimeMillis());
            objDoc.put("time_quit", 0.0);
            objDoc.put("online", true);
            objDoc.put("date_created", new Date());
            
            this.objDBCollection.insertOne(objDoc);
        }, this.objExecutorService);
    }
    
    public CompletableFuture<Void> onUserQuit(UUID objId, String strName) {
        return CompletableFuture.runAsync(() -> {
            if (this.vetUser(objId).join()) {
                
                BasicDBObject objQuery = new BasicDBObject();
                objQuery.put("uuid", objId.toString());
                objQuery.put("online", true);
                
                Document objActivityDoc = this.objDBCollection.find(objQuery).first();

                long numTimeJoin = (long) objActivityDoc.get("time_join");
                long numTimeQuit = System.currentTimeMillis();
                long numTimePlay = numTimeQuit - numTimeJoin;

                this.objDBCollection.updateOne(
                    Filters.and(
                        Filters.eq("uuid", objId.toString()),
                        Filters.eq("online", 1)
                    ), Updates.set("time_quit", numTimeQuit)
                );
                this.objDBCollection.updateOne(
                    Filters.and(
                        Filters.eq("uuid", objId.toString()),
                        Filters.eq("online", 1)
                    ), Updates.set("time_play", numTimePlay)
                );
                this.objDBCollection.updateOne(
                    Filters.and(
                        Filters.eq("uuid", objId.toString()),
                        Filters.eq("online", 1)
                    ), Updates.set("online", 0)
                );

            } else {
                this.doLogO("could not find user join data: name = %s; uuid = %d;", strName, objId.toString());
                return;
            }
        }, this.objExecutorService);
    }
}
/* endfile */