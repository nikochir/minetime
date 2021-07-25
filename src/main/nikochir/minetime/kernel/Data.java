/* package */

package nikochir.minetime.kernel;

/* include */

import nikochir.minetime.Main;
import nikochir.minetime.config.Config;
import nikochir.minetime.kernel.User;

/** java **/

import java.util.UUID;

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

/* typedef */

/* Data class
 * > Description:
 * -> stores all relevant ot the database stuff;
*/
public class Data {
    
    /* members */
    
    static private MongoClient objDBClient;
    static private MongoClientURI objDBClientURI;
    static private MongoDatabase objDBInstance;
    static private MongoCollection<Document> objDBCollection;
    
    /* getters */

    static public MongoCollection<Document> getCollection() { return objDBCollection; }

    static public long getDuration(UUID objUUID, int numDays) {
    
        BasicDBObject objQuery = getDurationQuery(objUUID, numDays);
        MongoCursor<Document> objCursor = objDBCollection.find(objQuery).iterator();
        
        return getDurationTime(objCursor);
    
    }
    static public long getDuration(String strName, int numDays) {
        
        BasicDBObject objQuery = getDurationQuery(strName, numDays);
        MongoCursor<Document> objCursor = objDBCollection.find(objQuery).iterator();
        
        return getDurationTime(objCursor);
    
    }
    static public long getDuration(UUID objUUID, String strName, int numDays) {
        
        BasicDBObject objQuery = getDurationQuery(objUUID, strName, numDays);
        MongoCursor<Document> objCursor = objDBCollection.find(objQuery).iterator();
        
        return getDurationTime(objCursor);
    
    }
    
    static private long getDurationTime(MongoCursor<Document> objCursor) {
        long numActiveTime = 0;
        
        while(objCursor.hasNext()) {
            Document objDoc = (Document)objCursor.next();
            numActiveTime += objDoc.getLong("time_play");
        }

        return numActiveTime;
    }

    static private BasicDBObject getDurationQuery(String strName, int numDays) {
        
        BasicDBObject objQuery = getDurationQuery(numDays);
        
        objQuery.put("name", strName);
        
        return objQuery;

    }
    static private BasicDBObject getDurationQuery(UUID objUUID, int numDays) {
        
        BasicDBObject objQuery = getDurationQuery(numDays);
        
        objQuery.put("uuid", objUUID.toString());
        
        return objQuery;
    }
    static private BasicDBObject getDurationQuery(UUID objUUID, String strName, int numDays) {
        
        BasicDBObject objQuery = getDurationQuery(numDays);
        
        objQuery.put("uuid", objUUID.toString());
        objQuery.put("name", strName);
        
        return objQuery;
    }
    static private BasicDBObject getDurationQuery(int numDays) {
        
        BasicDBObject objQuery = new BasicDBObject();

        objQuery.put("online", false);
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

        /* greater_than/equal */
        BasicDBObjectBuilder objBuilder = BasicDBObjectBuilder.start("$gte", numTimeNowMinusDays);
        /* lesser_than/equal */
        objBuilder.add("$lte", numTimeNow);

        return objBuilder.get();
    }

    /* setters */
    
    /* vetters */
    
    static public boolean vetUser(UUID objUUID) { return vetUser(User.getFilter(objUUID)); }
    static public boolean vetUser(String strName) { return vetUser(User.getFilter(strName)); }
    static public boolean vetUser(UUID objUUID, String strName) { return vetUser(User.getFilter(objUUID, strName)); }
    static public boolean vetUser(Bson objFilter) { return objDBCollection.count(objFilter) > 0; }

    /* actions */

    static public boolean doInit() {
        
        objDBClientURI = new MongoClientURI(Config.getStr("nameof_connection"));
        objDBClient = new MongoClient(objDBClientURI);
        objDBInstance = objDBClient.getDatabase(Config.getStr("nameof_database"));
        objDBCollection = objDBInstance.getCollection(Config.getStr("nameof_collection"));

        int numOrderDescend = OrderBy.DESC.getIntRepresentation();
        objDBCollection.createIndex( new Document("uuid", numOrderDescend) );
        objDBCollection.createIndex( new Document("name", numOrderDescend) );


        return true;

    }

    static public boolean doQuit() {
        
        objDBClient.close();
        
        objDBCollection = null;
        objDBInstance = null;
        objDBClient = null;
        objDBClientURI = null;
        
        return true;

    }

    /* handles */

}

/* endfile */