package hu.csanyzeg.android.homealone.Data;

/**
 * Created by tanulo on 2018. 08. 26..
 */

public class Options {
    public static final String PREFERENCES_ID = "pref";
    public static final String OPTION_NOTIFICATION_ENABLE = "notificationEnable";
    public static final boolean OPTION_NOTIFICATION_ENABLE_DEFAULT = true;

    public static final String OPTION_SERVER_URL = "serverUrl";
    public static final String OPTION_SERVER_URL_DEFAULT = "http://zwl.strangled.net:9002";

    public static final String OPTION_USER_NAME = "userName";
    public static final String OPTION_USER_NAME_DEFAULT = "admin";


    public static final String OPTION_USER_PASSWORD = "userPassword";
    public static final String OPTION_USER_PASSWORD_DEFAULT = "AD654321MIN";


    public static final int BR_UPDATE_SETTINGS = 1;
    public static final String BR_MESSAGE = "BR_MESSAGE";
    public static final String NOTIFICATION = "com.example.tanulo.szenzor.settings.receiver";

}
