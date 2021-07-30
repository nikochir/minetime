/* pakcage */

package nikochir.minetime;

/* include */

import nikochir.minetime.TestMain;
import nikochir.minetime.config.Config;
import nikochir.minetime.execut.Execut;
import nikochir.minetime.kernel.Data;
import nikochir.minetime.kernel.User;
import nikochir.minetime.listen.Listen;
import nikochir.minetime.permit.Permit;


/** java **/

import java.util.UUID;
import java.util.concurrent.Callable;

/** junit **/

import org.junit.Test;
import org.junit.Assert;

/** mongodb **/

/** bson **/

import org.bson.Document;

/* typedef */

/** TestMain class
 * > Description:
 * -> custom testing class with usual "main" method;
*/
public final class TestMain {

    /* members */

    /* vetters */

    /* getters */
    
    /* setters */
    
    /* actions */

    static public void main(String[] strArgs) {
        doLogO("Hello, %s!\n", "Native World");
        
        doDataInitWorkQuit();

        doLogO("Goodbye, %s!\n", "Cruel World");
    }

    public static void doLogO(String strMesg, Object ... objArgs) {
        System.out.printf(strMesg + "\n", objArgs);
    }
    public static void doLogE(String strMesg, Object ... objArgs) {
        System.err.printf(strMesg + "\n", objArgs);
    }

    private static boolean doDataInitWorkQuit() {

        String strConnection = "mongodb+srv://"
        + "keeper:db17399713@unit0.19rj5.mongodb.net/mdbstore"
        + "?retryWrites=true"
        + "&w=majority";
        
        Data.doInit(strConnection, "room0", "chest0");

        doDataWork();

        Data.doQuit();

        return true;
    }

    private static boolean doDataWork() {
        
        Document objDocument = new Document();

        objDocument.put("mesg", "success");

        Data.setDocument(objDocument);

        return true;
    }

    @Test(timeout=10000)
    public void doWork() {
        System.out.printf("Keep working...\n");
    }

}
/* endfile */