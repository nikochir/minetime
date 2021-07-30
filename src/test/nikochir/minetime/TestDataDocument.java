/* package */

package nikochir.minetime;

/* include */

import nikochir.minetime.TestMain;
import nikochir.minetime.kernel.Data;

/** java **/

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

/** junit **/

import org.junit.Test;
import org.junit.Assert;

/** bson **/

import org.bson.Document;
import org.bson.BasicBSONObject;
import org.bson.BSONObject;

import org.bson.conversions.Bson;

/** mongodb **/

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.model.Indexes;

/* typedef */

/* TestDataDocument
 * > Description:
 * -> class which incapsulates database document tests;
*/
public class TestDataDocument {

    private final String strConnection = "mongodb+srv://"
        + "master:jq0MwUygeSUXkIsj@unit0.19rj5.mongodb.net/mdbstore"
        + "?retryWrites=true"
        + "&w=majority";
    private final String strDatabase = "room0";
    private final String strContainter = "chest0";

    private Bson objFilter;
    private Document objDocument = null;

    /* getters  */

    private Document get() {

        if (objDocument == null) {
            if (vet()) {
                objDocument = Data.getDocument(getFilter());
            } else {
                set();
            }
        }

        return objDocument;

    }

    private Bson getFilter() {

        if (objFilter == null) { setFilter(); }

        return objFilter;

    }

    private List<Document> getList(Object ... objArgs) {

        List<Document> objList = new ArrayList<Document>();

        int numI = 0;
        for (Object objArg : objArgs) {
            objList.add(new Document(Integer.toString(numI++), objArg));
        }

        return objList;
    }

    /* setters  */
    
    private boolean set() {
        
        objDocument = new Document();

        get().put("type", "gfix");
        get().put("name", "quad");
        get().put("idx",
            getList(0, 1, 2, 2, 3, 0)
        );
        get().put("vtx",
            getList(
                -0.5f, -0.5f, -0.5f, +0.5f,
                +0.5f, +0.5f, +0.5f, -0.5f
            )
        );

        return Data.setDocument(get());
    
    }

    private boolean set(Bson objWhere, Bson objWhat) {
        return Data.setDocument(objWhere, objWhat);
    }

    private boolean setFilter() {
        
        objFilter = Filters.and(
            Filters.eq("type", "gfix"),
            Filters.eq("name", "quad")
        );
        
        return true;
    
    }

    /* vetters  */

    private boolean vet() {
        return Data.vetDocument(getFilter());
    }
    private boolean vet(Bson objFilter) {
        return Data.vetDocument(objFilter);
    }
    private boolean vet(Bson ... objFilters) {
        return Data.vetDocument(objFilter);
    }

    private boolean vetKey(String strKey) {
        return get().containsKey(strKey);
    }
    private boolean vetKey(String ... strKeys) {
        boolean bitResult = true;
        for (String strKey : strKeys) { bitResult &= get().containsKey(strKey); }
        return bitResult;
    }

    /* actions  */

    @Test(timeout=10000)
    public void doSave() {

        if (Data.doInit(strConnection, strDatabase, strContainter)) {
            
            Assert.assertTrue(
                "error:save:document!",
                true
                && set()
                && vet()
            );
            
            Data.doQuit();

        } else {
            TestMain.doLogE("error:data:init");
        }

    }

    @Test(timeout=10000)
    public void doLoad() {
        
        if (Data.doInit(strConnection, strDatabase, strContainter)) {
            
            Assert.assertTrue(
                "error:load:document!",
                true
                && vet()
                && vetKey("type", "name", "vtx", "idx")
            );
                
            Data.doQuit();

        } else {
            TestMain.doLogE("error:data:init");
        }

    }

    @Test(timeout=30000)
    public void doUpdate() {
        
        if (Data.doInit(strConnection, strDatabase, strContainter)) {

            Assert.assertTrue(
                "error:load:document!",
                true
                && set()
            );
                
            Data.doQuit();

        } else {
            TestMain.doLogE("error:data:init");
        }

    }

}
/* endfile */