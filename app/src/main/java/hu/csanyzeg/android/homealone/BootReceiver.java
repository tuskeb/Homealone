package hu.csanyzeg.android.homealone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by tanulo on 2018. 08. 02..
 */

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent myIntent = new Intent(context, DatabaseService.class);
        context.startService(myIntent);
//        context.startService(myIntent);
       /* if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            DatabaseService.enqueueWork(context, new Intent());
        }*/

    }
}