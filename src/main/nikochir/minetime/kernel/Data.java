/* package */

package nikochir.minetime.kernel;

/* include */

import nikochir.minetime.Main;
import nikochir.minetime.config.Config;
import nikochir.minetime.kernel.User;

/** java **/

import java.util.UUID;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.util.Date;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/** mongodb **/

import com.mongodb.DBObject;
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

/** bson **/

import org.bson.Document;

import org.bson.conversions.Bson;

/** google **/

import com.google.common.util.concurrent.ThreadFactoryBuilder;

/* typedef */

/* Data class
 * > Description:
 * -> stores all relevant ot the database stuff;
*/
public class Data {
    
    /* members */
    
    static private ExecutorService objExecutorService;
    
    static private MongoClient objDBClient;
    static private MongoClientURI objDBClientURI;
    static private MongoDatabase objDBInstance;
    static private MongoCollection<Document> objDBCollection;
    
    /* getters */

    static public CompletableFuture<Long> getDuration(UUID objUUID, int numDays) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCursor<Document> objCursor = getDurationCursor(objUUID, numDays);
            return getDurationTime(objCursor);
        }, objExecutorService);
    }
    static public CompletableFuture<Long> getDuration(String strName, int numDays) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCursor<Document> objCursor = getDurationCursor(strName, numDays);
            return getDurationTime(objCursor);
        }, objExecutorService);
    }
    static public CompletableFuture<Long> getDuration(UUID objUUID, String strName, int numDays) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCursor<Document> objCursor = getDurationCursor(objUUID, strName, numDays);
            return getDurationTime(objCursor);
        }, objExecutorService);
    }
    
    static private long getDurationTime(MongoCursor<Document> objCursor) {
        long numActiveTime = 0;
        while(objCursor.hasNext()) {
            Document objDoc = (Document)objCursor.next();
            numActiveTime += objDoc.getLong("time_play");
        }
        return numActiveTime;
    }

    static private MongoCursor<Document> getDurationCursor(UUID objUUID, int numDays) {
        BasicDBObject objQuery = getDurationQuery(objUUID, numDays);
        return objDBCollection.find(objQuery).iterator();
    }
    static private MongoCursor<Document> getDurationCursor(String strName, int numDays) {
        BasicDBObject objQuery = getDurationQuery(strName, numDays);
        return objDBCollection.find(objQuery).iterator();
    }
    static private MongoCursor<Document> getDurationCursor(UUID objUUID, String strName, int numDays) {
        BasicDBObject objQuery = getDurationQuery(objUUID, strName, numDays);
        return objDBCollection.find(objQuery).iterator();
    }

    static private BasicDBObject getDurationQuery(String strName, int numDays) {
        BasicDBObject objQuery = getDurationQueryBase(numDays);
        objQuery.put("name", strName);
        return objQuery;
    }
    static private BasicDBObject getDurationQuery(UUID objUUID, int numDays) {
        BasicDBObject objQuery = getDurationQueryBase(numDays);
        objQuery.put("uuid", objUUID.toString());
        return objQuery;
    }
    static private BasicDBObject getDurationQuery(UUID objUUID, String strName, int numDays) {
        BasicDBObject objQuery = getDurationQueryBase(numDays);
        objQuery.put("uuid", objUUID.toString());
        objQuery.put("name", strName);
        return objQuery;
    }

    static private BasicDBObject getDurationQueryBase(int numDays) {
        
        BasicDBObject objQuery = new BasicDBObject();
        
        objQuery.put("online", 0);
        objQuery.put("time_join", getDurationDBObject(numDays));

        return objQuery;
    }

    static private DBObject getDurationDBObject(int numDays) {
    
        LocalDateTime objTimeNow = LocalDateTime.now();
        
        long numScale = 1000;
        
        long numTimeNow = objTimeNow.toEpochSecond(ZoneOffset.UTC);
        numTimeNow *= numScale;
        
        long numTimeNowMinusDays = objTimeNow.minusDays(numDays).toEpochSecond(ZoneOffset.UTC);
        numTimeNowMinusDays *= numScale;

        BasicDBObjectBuilder objBuilder = BasicDBObjectBuilder.start("$gte", numTimeNowMinusDays);
        objBuilder.add("$lte", numTimeNow);

        return objBuilder.get();
    }

    /* setters */
    
    /* vetters */
    
    static public CompletableFuture<Boolean> vetUser(UUID objId) {
        return vetUser( Filters.eq("uuid", objId.toString()) );
    }

    static public CompletableFuture<Boolean> vetUser(String strName) {
        return vetUser( Filters.eq("name", strName) );
    }
    static public CompletableFuture<Boolean> vetUser(Bson objFilter) {
        return CompletableFuture.supplyAsync(() -> {
            return objDBCollection.count(objFilter) > 0;
        }, objExecutorService);
    }

    /* actions */

    static public boolean doInit() {
        
        objDBClientURI = new MongoClientURI(Config.getStr("nameof_connection"));
        objDBClient = new MongoClient(objDBClientURI);
        objDBInstance = objDBClient.getDatabase(Config.getStr("nameof_database"));
        objDBCollection = objDBInstance.getCollection(Config.getStr("nameof_collection"));

        objExecutorService = Executors.newCachedThreadPool(
            (new ThreadFactoryBuilder()).setNameFormat("minetime T-%1$d").build()
        );

        CompletableFuture<Void> objIndex = CompletableFuture.runAsync(() -> {
            
            int numOrderDescend = OrderBy.DESC.getIntRepresentation();

            objDBCollection.createIndex(new Document("uuid", numOrderDescend) );
            objDBCollection.createIndex(new Document("name", numOrderDescend) );
            
        }, objExecutorService);
    
        objIndex.thenRun(() -> Main.doLogO("created player index"));

        return true;

    }

    static public boolean doQuit() {
        
        objDBClient.close();
        
        objDBCollection = null;
        objDBInstance = null;
        objDBClient = null;
        objDBClientURI = null;
        
        objExecutorService.shutdown();
        objExecutorService = null;

        return true;

    }

    /* handles */
    
    static public CompletableFuture<Void> onUserJoin(User objUser) {
        return onUserJoin(objUser.getUUID(), objUser.getName());
    }
    static public CompletableFuture<Void> onUserJoin(UUID objUUID, String strName) {
        return CompletableFuture.runAsync(() -> {
            Document objDoc = onUserJoinGetDocument(objUUID, strName);
            objDBCollection.insertOne(objDoc);
        }, objExecutorService);
    }

    static private Document onUserJoinGetDocument(UUID objUUID, String strName) {
        Document objDoc = onUserJoinGetDocument();
        objDoc.put("uuid", objUUID.toString());
        objDoc.put("name", strName);
        return objDoc;
    }
    static private Document onUserJoinGetDocument() {
        Document objDoc = new Document();
        objDoc.put("time_join", System.currentTimeMillis());
        objDoc.put("time_quit", 0);
        objDoc.put("online", true);
        objDoc.put("date_created", new Date());
        return objDoc;
    }

    static public CompletableFuture<Void> onUserQuit(User objUser) {
        return onUserQuit(objUser.getUUID(), objUser.getName());
    }
    static public CompletableFuture<Void> onUserQuit(UUID objUUID, String strName) {
        
        return CompletableFuture.runAsync(() -> {
            
            if (vetUser(objUUID).join()) {
                
                BasicDBObject objQuery = onUserQuitGetQuery(objUUID, strName);
                
                Document objActivityDoc = objDBCollection.find(objQuery).first();

                long numTimeJoin = (long) objActivityDoc.get("time_join");
                long numTimeQuit = System.currentTimeMillis();
                long numTimePlay = numTimeQuit - numTimeJoin;

                onUserQuitUpdate(onUserQuitGetFilter(objUUID, strName), numTimeQuit, numTimePlay);

            } else {
                Main.doLogE(
                    "could not find user join data: name = %s; uuid = %d;",
                    strName, objUUID.toString()
                );
                return;
            }
            
        }, objExecutorService);
        
    }
    
    static private BasicDBObject onUserQuitGetQuery(UUID objUUID, String strName) {
        BasicDBObject objQuery = new BasicDBObject();
        objQuery.put("uuid", objUUID.toString());
        objQuery.put("name", strName);
        return objQuery;
    }

    static private Bson onUserQuitGetFilter(UUID objUUID, String strName) {
        Bson objFilter = Filters.and(
            Filters.eq("uuid", objUUID.toString()),
            Filters.eq("name", strName),
            Filters.eq("online", true)
        );
        return objFilter;
    }

    static private void onUserQuitUpdate(Bson objFilter, long numTimeQuit, long numTimePlay) {
        objDBCollection.updateOne(objFilter, Updates.set("time_quit", numTimeQuit));
        objDBCollection.updateOne(objFilter, Updates.set("time_play", numTimePlay));
        objDBCollection.updateOne(objFilter, Updates.set("online", false));
    }

}

/* endfile */