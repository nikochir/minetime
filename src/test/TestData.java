/* package */

package test;

/* include */

import main.minetime.TestMain;
import main.minetime.kernel.Data;

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

/* typedef */

/* TestData
 * > Description:
 * -> ;
*/
public class TestData {

    private final String strConnection = "mongodb+srv://"
        + "master:jq0MwUygeSUXkIsj@unit0.19rj5.mongodb.net/mdbstore"
        + "?retryWrites=true"
        + "&w=majority";
    private final String strDatabase = "room0";
    private final String strContainter = "chest0";
    
    /* actions */

    @Test(timeout=10000)
    public void doInitQuit() {
        
        Assert.assertTrue(
            "error:data:init!",
            Data.doInit(strConnection, strDatabase, strContainter)
        );
        
        Assert.assertTrue(
            "error:data:quit!",
            Data.doQuit()
        );

    }

}
/* endfile */