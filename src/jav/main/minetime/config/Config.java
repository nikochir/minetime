/* package */

package main.minetime.config;

/* include */

import main.minetime.Main;
import main.minetime.config.*;

/** java **/

import java.util.Set;
import java.util.List;

/** bukkit **/

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

/* typedef */

/* Config class
 * > Description:
 * -> ;
*/
public class Config {
    
    /* members */
    
    static private FileConfiguration objConfig;
    
    static private String strNameOfMain;
    static private String strNameOfLogO;
    static private String strNameOfLogE;
    
    /* codetor */

    /* getters */

    static public Set<String> getKeys() { return objConfig.getKeys(true); }

    static public Boolean getBit(String strKey)           { return objConfig.getBoolean(strKey); }
    static public List<Boolean> getBitList(String strKey) { return objConfig.getBooleanList(strKey); }
    
    static public Integer getInt(String strKey)           { return objConfig.getInt(strKey); }
    static public List<Integer> getIntList(String strKey) { return objConfig.getIntegerList(strKey); }
    
    static public Double getNum(String strKey)           { return objConfig.getDouble(strKey); }
    static public List<Double> getNumList(String strKey) { return objConfig.getDoubleList(strKey); }
    
    static public String getStr(String strKey)           { return objConfig.getString(strKey); }
    static public List<String> getStrList(String strKey) { return objConfig.getStringList(strKey); }

    static public String getStrNameOfMain() { return strNameOfMain; }
    static public String getStrNameOfLogO() { return strNameOfLogO; }
    static public String getStrNameOfLogE() { return strNameOfLogE; }

    /* setters */
    
    /* vetters */

    /* actions */

    static public boolean doInit() {
        
        objConfig = Main.get().getConfig();

        objConfig.options().copyDefaults(true);
        Main.get().saveDefaultConfig();
        
        strNameOfMain = getStr("nameof_main");
        strNameOfLogO = getStr("nameof_logo");
        strNameOfLogE = getStr("nameof_loge");

        return true;

    }

    static public boolean doQuit() {
        
        strNameOfLogE = null;
        strNameOfLogO = null;
        strNameOfMain = null;

        objConfig = null;

        return true;

    }

}

/* endfile */