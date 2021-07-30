/* package */

package nikochir.minetime;

/* include */

import nikochir.minetime.TestMain;
import nikochir.minetime.kernel.Data;

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

/* TestDataIndexes
 * > Description:
 * -> incapsulation of index tests;
*/
public class TestDataIndex {

    private final String strConnection = "mongodb+srv://"
        + "master:jq0MwUygeSUXkIsj@unit0.19rj5.mongodb.net/mdbstore"
        + "?retryWrites=true"
        + "&w=majority";
    private final String strDatabase = "room0";
    private final String strContainter = "chest0";
    
    /* getters */
    
    /* setters */
    
    private boolean setup() {
        return Data.setupIndex("type", "name");
    }

    private boolean reset() {
        return Data.resetIndex("type", "name");
    }

    /* vetters */
    
    /* actions */

    @Test(timeout=30000)
    public void doSetup() {

        if (Data.doInit(strConnection, strDatabase, strContainter)) {

            Assert.assertTrue(
                "error:data:create:index",
                setup()
            );

            Data.doQuit();
        
        } else {
            TestMain.doLogE("error:data:init");
        }

    }

    @Test(timeout=30000)
    public void doReset() {

        if (Data.doInit(strConnection, strDatabase, strContainter)) {

            Assert.assertTrue(
                "error:data:create:index",
                reset()
            );

            Data.doQuit();
        
        } else {
            TestMain.doLogE("error:data:init");
        }

    }

}
/* endfile */