/* package */
package src.main.nikochir.minetime;
/* import */
/** java **/
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
/** database **/
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.internal.operation.OrderBy;
/** bson **/
import org.bson.Document;
import org.bson.conversions.Bson;
/* typedef */
/** Manager class
 * > Description:
 * -> ;
 */
public class Manager {
    private final MongoCollection<Document> playerCollections;
    private final ExecutorService executorService;
    /* codetor */
    public PlayerActivityManager(MongoCollection<Document> playerCollections, ExecutorService executorService) {
        this.playerCollections = playerCollections;
        this.executorService = executorService;
        createIndexes().thenRun(() -> System.out.println(TextConstants.CONSOLE_PREFIX + "Created player indexes."));
    }
    /* getters */
    /* setters */
    /* vetters */
    public CompletableFuture<Boolean> exists(UUID uuid) {
        return CompletableFuture.supplyAsync(()-> {
            playerCollections.countDocuments(Filters.eq("uuid", uuid.toString())) > 0, executorService);
        }
    }
    /* actions */
    private CompletableFuture<Void> createIndexes() {
        return CompletableFuture.runAsync(() -> {
            playerCollections.createIndex(new Document("uuid", OrderBy.DESC.getIntRepresentation()));
            playerCollections.createIndex(new Document("isPlayerOnline", OrderBy.DESC.getIntRepresentation()));
        }, executorService);
    }
    public CompletableFuture<Void> playerLogin(UUID uuid, String name) {
        return CompletableFuture.runAsync(() -> {

            Document document = new Document("uuid", uuid.toString());
            document.put("name", name);
            document.put("loginTime", System.currentTimeMillis());
            document.put("isPlayerOnline", 1);
            document.put("createdAt", new Date());

            playerCollections.insertOne(document);
        }, executorService);
    }
    public CompletableFuture<Void> playerLogout(UUID uuid, String name) {
        return CompletableFuture.runAsync(() -> {
            if (!exists(uuid).join()) {
                System.out.println("User: "+ uuid + ", name: "+ name +" do not have log-in details");
                return;
            }
            System.out.println("User: "+ uuid + ", name: "+ name +" logout");
            BasicDBObject query = new BasicDBObject();
            query.put("uuid", uuid.toString());
            query.put("isPlayerOnline", 1);
            System.out.println("before - " + query.toString());
            Document playerActivityDoc = playerCollections.find(query).first();
            System.out.println("after");
            long loginTime = (Long) playerActivityDoc.get("loginTime");
            System.out.println("time");
            long logoutTime = System.currentTimeMillis();
            long timeSpent = logoutTime - loginTime;
            System.out.println("time spent by user "+ uuid + " is: " +timeSpent);

            List<Bson> updates = new ArrayList<>();
            updates.add(Updates.set("logoutTime", logoutTime));
            updates.add(Updates.set("timeSpent", timeSpent));
            updates.add(Updates.set("isPlayerOnline", 0));

            playerCollections.updateOne(
                    Filters.and(Filters.eq("uuid", uuid.toString()),
                            Filters.eq("isPlayerOnline", 1)),
                    updates
            );

        }, executorService);
    }

    public CompletableFuture<Long> getActivityDuration(String name, int days) {
        return CompletableFuture.supplyAsync(() -> {

            BasicDBObject query = new BasicDBObject();
            query.put("name", name);
            query.put("isPlayerOnline", 0);
            query.put("loginTime", BasicDBObjectBuilder
                    .start("$gte", LocalDateTime.now().minusDays(days).toEpochSecond(ZoneOffset.UTC) * 1000)
            .add("$lte", LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) * 1000).get());
            long totalActiveTime = 0;
            System.out.println("time query - " + query.toString());
            MongoCursor cursor = playerCollections.find(query).cursor();
            while(cursor.hasNext()) {
                Document doc = (Document)cursor.next();
                totalActiveTime += doc.getLong("timeSpent");
            }
            return totalActiveTime;
        }, executorService);
    }

}
