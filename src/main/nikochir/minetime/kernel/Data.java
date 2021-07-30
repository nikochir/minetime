/* package */

package nikochir.minetime.kernel;

/* include */

import nikochir.minetime.Main;
import nikochir.minetime.config.Config;
import nikochir.minetime.kernel.User;

/** java **/

import java.util.UUID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.util.Date;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/** mongodb **/

import com.mongodb.DBObject;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.MongoClientSettings;
import com.mongodb.ConnectionString;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.model.Indexes;

/** bson **/

import org.bson.Document;
import org.bson.conversions.Bson;

/* typedef */

/* Data class
 * > Description:
 * -> stores all relevant stuff for the database;
*/
public class Data {
    
    /* statics */
    
    static private ConnectionString objConnection;
    static private MongoClientSettings objSettings;
    static private MongoClient objDBClient;
    static private MongoDatabase objDBInstance;
    static private MongoCollection<Document> objDBCollection;
    
    /* getters */

    static public MongoCollection<Document> getCollection() { return objDBCollection; }
    static public Document getDocument(Bson objFilter)      { return objDBCollection.find(objFilter).first(); }
   
    static public long getUserTime(String strName, int numDays) {
        return User.getUser(getDocument(User.getFilter(strName)).getString("uuid")).getTime(numDays);
    }

    /* setters */
    
    static public boolean setupIndex(String... strIndexes) {
        objDBCollection.createIndex(Indexes.ascending(strIndexes));
        return true;
    }
    static public boolean resetIndex(String... strIndexes) {
        objDBCollection.dropIndex(Indexes.ascending(strIndexes));
        return true;
    }

    static public boolean setDocument(Document objDocument) {
        objDBCollection.insertOne(objDocument);
        return true;
    }
    static public boolean setDocument(Bson objWhere, Bson objWhat) {
        objDBCollection.updateOne(objWhere, objWhat);
        return true;
    }
    static public <TName> boolean setDocument(Bson objWhere, String strKey, TName objVal) {
        return setDocument(objWhere, Updates.set(strKey, objVal));
    }

    static public boolean setDocuments(List<Document> objDocuments) {
        objDBCollection.insertMany(objDocuments);
        return true;
    }
    static public boolean setDocuments(Document ... objDocuments) {
        return setDocuments(Arrays.asList(objDocuments));
    }
    
    /* vetters */
    
    static public boolean vetUser(UUID objUUID)                 { return vetDocument(User.getFilter(objUUID)); }
    static public boolean vetUser(String strName)               { return vetDocument(User.getFilter(strName)); }
    static public boolean vetUser(UUID objUUID, String strName) { return vetDocument(User.getFilter(objUUID, strName)); }
    
    static public boolean vetDocument(Bson objFilter)           { return objDBCollection.countDocuments(objFilter) > 0; }
    static public boolean vetDocument(Bson ... objFilters)      { return vetDocument(Filters.and(objFilters)); }

    /* actions */

    static public boolean doInit() {

        String strConnection = Config.getStr("nameof_connection");
        String strDatabase = Config.getStr("nameof_database");
        String strCollection = Config.getStr("nameof_collection");

        return doInit(strConnection, strDatabase, strCollection);
}
    static public boolean doInit(String strConnection, String strDatabase, String strCollection) {
        
        doInitDBClient(strConnection);
        doInitDBInstance(objDBClient, strDatabase);
        doInitDBCollection(objDBInstance, strCollection);

        return true;
    }
    
    static private boolean doInitDBClient(String strConnection) {
        if (strConnection == null) {
            Main.doLogE("string connection is invalid!");
            return false;
        } else {
            objConnection = new ConnectionString(strConnection);
            objSettings = MongoClientSettings.builder()
                .applyConnectionString(objConnection)
                .build();
            objDBClient = MongoClients.create(objSettings);
            return true;
        }
    }

    static private boolean doInitDBInstance(MongoClient objClient, String strDatabase) {
        if (objClient == null) {
            Main.doLogE("client is null!");
            return false;
        } else {
            objDBInstance = objClient.getDatabase(strDatabase);
            return true;
        }
    }
    
    static private boolean doInitDBCollection(MongoDatabase objInstance, String strCollection) {
        if (objInstance == null) {
            Main.doLogE("database is null!");
            return false;
        } else {
            objDBCollection = objInstance.getCollection(strCollection);
            return true;
        }
    }
    
    static public boolean doQuit() {
        
        objDBClient.close();
        
        objDBCollection = null;
        objDBInstance = null;
        objDBClient = null;
        objSettings = null;
        objConnection = null;
        
        return true;

    }

    /* handles */

}

/* endfile */