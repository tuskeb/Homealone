package hu.csanyzeg.android.homealone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import hu.csanyzeg.android.homealone.Data.Options;

public class OptionsActivity extends AppCompatActivity {

    private SharedPreferences.Editor writePref;
    private SharedPreferences readPref;


    private EditText serverUrl;
    private CheckBox notificationEnable;
    private Button defaultButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        writePref  = getSharedPreferences("pref", MODE_PRIVATE).edit();
        readPref = getSharedPreferences("pref", MODE_PRIVATE);

        serverUrl = findViewById(R.id.server_options);
        defaultButton = findViewById(R.id.options_default);
        notificationEnable = findViewById(R.id.options_notification);

        notificationEnable.setChecked(readPref.getBoolean(Options.OPTION_NOTIFICATION_ENABLE, Options.OPTION_NOTIFICATION_ENABLE_DEFAULT));
        serverUrl.setText(readPref.getString(Options.OPTION_SERVER_URL, Options.OPTION_SERVER_URL_DEFAULT));

        defaultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                notificationEnable.setChecked(Options.OPTION_NOTIFICATION_ENABLE_DEFAULT);
                serverUrl.setText(Options.OPTION_SERVER_URL_DEFAULT);

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        writePref.putBoolean(Options.OPTION_NOTIFICATION_ENABLE, notificationEnable.isChecked());
        writePref.putString(Options.OPTION_SERVER_URL, serverUrl.getText().toString());
        writePref.apply();
        generateUpdateSettingsBroadcast();
    }

    private void generateUpdateSettingsBroadcast() {
        Intent intent = new Intent(Options.NOTIFICATION);
        intent.putExtra(Options.BR_MESSAGE, Options.BR_UPDATE_SETTINGS);
        sendBroadcast(intent);
    }

}
