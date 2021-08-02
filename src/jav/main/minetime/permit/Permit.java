/* package */

package main.minetime.permit;

/* include */

import main.minetime.Main;
import main.minetime.permit.*;

/* bukkit */

import org.bukkit.permissions.Permission;

/* typedef */

/* Permit class
 * > Description:
 * -> basic plugin permission;
*/
public class Permit extends Permission {
    
    /* codetor */

    public Permit(String strName) { super(strName); }

    /* actions */

    static public boolean doInit() {
        
        /*
        Main.setPermit("user", new PermitUser());
        Main.setPermit("oper", new PermitOper());
        Main.setPermit("over", new PermitOver());
        */

        return true;

    }

    static public boolean doQuit() {
        
        return true;

    }

}

/* endfile */