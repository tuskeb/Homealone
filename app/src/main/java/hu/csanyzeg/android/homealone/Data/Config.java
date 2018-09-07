package hu.csanyzeg.android.homealone.Data;

import java.io.Serializable;
import java.util.Collection;

/**
 * Az RPI-től kapott konfiguráció tárolását megvalósító osztály.
 */
public class Config implements Serializable {

    /*
    ----------- Globális konfigurációs adatok, amelyek az alkalmazás működésére vannak hatással. A SYSTEM szekcióban található bejegyzések -----------
     */

    /**
     *
     */
    public static String name = "Config";

    /**
     * A konfigurációs fájl verziója
     */
    public static String version = null;

    /**
     * Megadja másodpercben, hogy az Android mennyi időközönként kérdezgesse le az RPI-től az adatokat.
     */
    public static Integer polling = 10;

    /**
     * Amikor a felhasználó valamilyen módosítást végez a felületen, a polling gyorsabbá válik. Mp-ben megadva.
     * Ez az érték a pbálkozások számát adja meg.
     */
    public static Integer fastPollingCount = 8;

    /**
     * Amikor a felhasználó valamilyen módosítást végez a felületen, a polling gyorsabbá válik. Mp-ben megadva.
     * Ez az érték a gyors próbálkozások közti időt adja meg.
     */
    public static Double fastPollingInterval = 0.5;

    /**
     * A háttérben futó szál ennyi időközönként kérdezgeti le a szenzorokat. A szál végtelen ciklusában lévő időzítés. mp-ben megadva.
     */
    public static Double serviceThreadSleepInterval = 0.4;

    /**
     * Mennyi adatot tároljon (másodpercben megadva) a szolgáltatás szál a grafikonokhoz.
     */
    public static Integer dataStoreInterval = 1200;

    /**
     * A ház képének URL-je. Majd a ház nézethez kell.
     */
    public static String map = null;

    /**
     * A ház GPS koordinátája.
     */
    public static Double gpsLatitude = null;

    /**
     * A ház GPS koordinátája.
     */
    public static Double gpsLongitude = null;

    public static String gpscomment = null;

    /**
     * A bejelentkezett felhasználó admin jogú-e?
     */
    public static boolean admin = false;





/**
 * ------------------- Egy szenzorhoz tartozó adatok. ------------------------------------------
 */


    /**
     * A grafikonon lévő érték színe. Ha nincs megadva, akkor generál egyet.
     */
    public Integer color = null;

    /**
     * A szenzor/kapcsoló azonosítója, pl XC
     */
    public String id = null;

    /**
     * Az eszköz típusa
     */
    public String device = "";

    /**
     * A képernyőn megjelenő név
     */
    public String display = "";

    /**
     * A felhasználó módosíthatja-e az értékét. Pl kazán hőmérséklet beállítható. Vagy a riasztó élesíthető.
     */
    public boolean write = false;

    /**
     *
     */
    public String label = "";

    /**
     * A szenzor/kapcsoló engedélyezve van-e. Ha false, akkor nem kell vele foglalkozni.
     */
    public boolean enabled = false;

    /**
     * Alapértelmezett érték.
     * Itt minden érték Double típus, logikai mezők esetében 0 a hamis, 1 az igaz.
     */
    public double default_value = 0;//

    /**
     * A képernyőn megjelenő mértékegység.
     */
    public String suffix = "";//

    /**
     * Az ábrázolási tartomány minimuma.
     * Abban az esetben ha ez meg van adva, a mező szám típusú. Min és Max elhagyása esetén pedig logikai típusú. Van hozzá getter.
     */
    public Double min = null;//

    /**
     * Az ábrázolási tartomány maximuma.
     * Abban az esetben ha ez meg van adva, a mező szám típusú. Min és Max elhagyása esetén pedig logikai típusú. Van hozzá getter.
     */
    public Double max = null;//

    /**
     * A képernyőn meggjelenő ikon URL-je, amely a neve mellett jelenik meg.
     * Nincs jelenleg használatban.
     */
    public String icon = "";

    /**
     * Bekapcsolt állapotban megjelenő ikon URL-je, amely a ház térképén jelenik meg. Logikai típus esetében van értelme.
     * Nincs jelenleg használatban.
     */
    public String iconOn = "";

    /**
     * Kikapcsolt állapotban megjelenő ikon URL-je, amely a ház térképén jelenik meg. Logikai típus esetében van értelme.
     * Nincs jelenleg használatban.
     */
    public String iconOff = "";

    /**
     * A ház képén megjelenő ikon pozíciója.
     */
    public Integer posX = null;
    public Integer pozX = null;

    /**
     * A ház képén megjelenő ikon pozíciója.
     */
    public Integer posY = null;
    public Integer pozY = null;

    /**
     * A szenzorok csoportosíthatók, azaz a konfigurációs fájlban meghatározható olyan bejegyzés, amely csoportot definiál.
     * Ekkor ehhez a csoporthoz adhatunk szenzorokat.
     * Attól, hogy a groupValue értéket megadjuk, még ugyan úgy szenzorként viselkedik.
     * A szenzorként való viselkedés letiltását (azaz nem kap értéket) a sensor = false beállítással lehet elvégezni.
     * Jelenleg az Android felületen csak a szám típusú szenzorokat gyűjtő csoportok támogatottak.
     *
     * Amelyik szenzor csoport, ott a groupValue értékét be kell állítani a következők valamelyikére:
     *      min - Ekkor a csoport értéke a legalacsonyabb szenzorérték lesz.
     *      max - Ekkor a csoport értéke a legmagasabb szenzorérték lesz.
     *      avg - Ekkor a csoport értéke az átlagos szenzorérték lesz.
     *      {id} - Itt egy olyan szenzor idje adható meg, amely a csoportnak tagja. Ekkor a csoport értéke ennek a szenzornak az értéke lesz.
     *
     * A csoport tagjainál pedig a parent értéket kell meghatározni. Ide a csoport id-jét kell írni.
     *      */
    public String groupValue = null;

    /**
     * A szenzorok csoportosíthatók, azaz a konfigurációs fájlban meghatározható olyan bejegyzés, amely csoportot definiál.
     * Ekkor ehhez a csoporthoz adhatunk szenzorokat.
     * Attól, hogy a groupValue értéket megadjuk, még ugyan úgy szenzorként viselkedik.
     * A szenzorként való viselkedés letiltását (azaz nem kap értéket) a sensor = false beállítással lehet elvégezni.
     * Jelenleg az Android felületen csak a szám típusú szenzorokat gyűjtő csoportok támogatottak.
     *
     * Amelyik szenzor csoport, ott a groupValue értékét be kell állítani a következők valamelyikére:
     *      min - Ekkor a csoport értéke a legalacsonyabb szenzorérték lesz.
     *      max - Ekkor a csoport értéke a legmagasabb szenzorérték lesz.
     *      avg - Ekkor a csoport értéke az átlagos szenzorérték lesz.
     *      {id} - Itt egy olyan szenzor idje adható meg, amely a csoportnak tagja. Ekkor a csoport értéke ennek a szenzornak az értéke lesz.
     *
     * A csoport tagjainál pedig a parent értéket kell meghatározni. Ide a csoport id-jét kell írni.
     *      */
    public String parent = null;

    /**
     * A szenzorként való viselkedés letiltását (azaz nem kap értéket) a sensor = false beállítással lehet elvégezni.
     */
    public boolean sensor = true;

    /**
     * Kapcsolók esetében (write=true és min=null, max=null) használandó. Ha az értéke 0, akkor nem kapcsol le automatikusan.
     * Amennyiben ettől eltérő, pozitív érték, akkor automatikusan lekapcsol. Az értékét másodpercben kell megadni.
     * A lekapcsolását az Android pollingon keresztül kapja meg.
     */
    public Integer monostab = 0;

    /**
     * A szenzorokhoz riasztás is beállítható. Amennyiben az alarmset értéke true, a szenzor riasztását a kliens figyeli.
     */
    public boolean alarmSet = false;//alarm

    /**
     * A szenzorokhoz riasztás is beállítható. Amennyiben az alarmset értéke true, a szenzor riasztását a kliens figyeli.
     * A riasztási minimum megadása.
     * Lehetőség van csak a min vagy csak a max megadására is.
     */
    public Double alarmMinValue = null;//alarm

    /**
     * A szenzorokhoz riasztás is beállítható. Amennyiben az alarmset értéke true, a szenzor riasztását a kliens figyeli.
     * A riasztási maximum megadása.
     * Lehetőség van csak a min vagy csak a max megadására is.
     */
    public Double alarmMaxValue = null;//alarm

    /**
     * A szenzorokhoz riasztás is beállítható. Amennyiben az alarmset értéke true, a szenzor riasztását a kliens figyeli.
     * A szenzorhoz kapcsolható riasztás gomb.
     * Ha az értéke null, akkor minden esetben riaszt. Pl a tűzjelző riasztása nem kikapcsolható.
     * Ha az értéke valamelyik  kapcsoló (write=true, min=null, max=null) ID-je, akkor a kapcsoló értéke alapján riaszt vagy nem.
     */
    public String alarmSwitch = null;//alarm

    /**
     * A szenzorokhoz riasztás is beállítható. Amennyiben az alarmset értéke true, a szenzor riasztását a kliens figyeli.
     * A riasztás szövege. Behelyettesíthatók az értékek is. Pl.:
     * A lakásban a hőmérséklet %current°C!
     * %min és %max is behelyettesíthető.
     */
    public String alarmText = "";//alarm

    /**
     * A szenzorokhoz riasztás is beállítható. Amennyiben az alarmset értéke true, a szenzor riasztását a kliens figyeli.
     * A riasztási értékeket a felhasználó is módosíthatja a felületen.
     * Android program esetében támogatott, viszont a szerver még nem tudja.
     */
    public boolean alarmWrite = false;


    public String alarmComment = null;//alarm
    public String alarmComment2 = null;//alarm

    /**
     * A tizedes jegyek száma az ábrázolásban.
     */
    public Integer precision = 0;



    public String pozikon = null; //Ikon
    public String pozcomment = null; //Ikon
    public String comment = null;

    /**
     * Megadja, hogy mennyi változás esetén tároljon az RPI adatokat a szenzorról. Android számára nem fontos.
     */
    public Integer sensibilitypercent = 1;

    public String sensibilitycomment = null;

    /**
     * Ha az otthonról mért távolság nagyobb, mint az itt megadott, akkor a módosítás előtt feltesz egy kérdést. Méterben megadva.
     */
    public Double distance = null;


    /**
     * Visszaadja, hogy a bejegyzés logikai vagy szám típusú-e?
     * @return
     */
    public boolean isSwitch() {
        return min == null || max == null;
    }

    /**
     * Automatikusan lekapcsol-e?
     * @return
     */
    public boolean isBistabil(){
        return monostab !=0;
    }

    /**
    Az értéke módosítható-e?
     */
    public boolean isWrite(){
        return write;
    }

    /**
     * Használatban van-e? Ha nincs, akkor kihagyható a bejegyzés.
     * @return
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * A szolgáltatásban tárolt adatok időintervalluma milisec-ben megadva.
     * @return
     */
    public static long getDataStoreIntervalMs() {
        return (long)dataStoreInterval * 1000L;
    }

    /**
     * A szolgátatás időzítése ms-ben megadva.
     * @return
     */
    public static long getServiceThreadSleepIntervalMs() {
        return (long)(serviceThreadSleepInterval * 1000L);
    }


    public static long getFastPollingIntervalMs() {
        return (long)(fastPollingInterval * 1000L);
    }

    /**
     * A konfigurációból meghatározza, hogy a szenzor alapján mindig kell-e riaztani, vagy csak élesített riasztás esetén vagy pedig nem kell.
     * @return
     */
    public AlarmEvent getAlarmEvent(){
        if (alarmMaxValue !=null || alarmMinValue != null){
            if (alarmSwitch !=null) {
                return AlarmEvent.ifSwitchOn;
            }
            return AlarmEvent.always;
        }
        return AlarmEvent.never;
    }

    /**
     * Riasztás lehet minimum vagy maximum vagy egyszerre mindkét érték alapján.
     * @return
     */
    public AlarmType getAlarmType(){
        if (alarmMaxValue != null && alarmMinValue != null){
            return AlarmType.minmax;
        }
        if ((alarmMinValue != null) && (alarmMaxValue == null)) {
            return AlarmType.min;
        }
        if ((alarmMinValue == null) && (alarmMaxValue != null)) {
            return AlarmType.max;
        }
        return AlarmType.none;
    }


    public static Config getConfigByID(Collection<Config> configs, String id){
        for(Config c : configs){
            if (c.id.equals(id)){
                return c;
            }
        }
        return  null;
    }

    /**
     * A szenzor csak akkor jelenik meg, ha nem a csoportnak a tagja (mert akkor a csoport jeleníti meg).
     * A nem megjelenő szenzorok is aktívak!
     * @return
     */
    public boolean isVisible(){
        return parent == null || parent.equals(id);
    }

    public String getGroupValue() {
        if (groupValue ==null){
            return id;
        }
        return groupValue;
    }

    public boolean isSensor() {
        return sensor;
    }



}
