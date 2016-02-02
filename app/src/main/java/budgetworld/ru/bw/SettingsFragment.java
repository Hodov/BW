package budgetworld.ru.bw;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;


public class SettingsFragment extends android.support.v4.app.Fragment {

    private Switch mSwitch;
    private Tracker mTracker;
    AppConfig appConfig;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.settings_fragment,container,false);
        appConfig  = new AppConfig();
        AnalyticsApplication application = (AnalyticsApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
        if (appConfig.releaseBuild) {
            sendScreenName();
        }

        // Initializing switch1
        switch_push(v);
        return v;
    }

    private boolean switch_push(View v) {

        mSwitch = (Switch) v.findViewById(R.id.switch1);
        mSwitch.setChecked(getPushSettings(getString(R.string.switch_setting)));
        // добавляем слушателя
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // в зависимости от значения isChecked выводим нужное сообщение
                if (isChecked) {
                    if (appConfig.releaseBuild) {
                        sendGoogleAction("Action", "Push_on");
                    }

                    addPushSettings(getString(R.string.switch_setting), true);
                } else {
                    if (appConfig.releaseBuild) {
                        sendGoogleAction("Action", "Push_off");
                    }
                    addPushSettings(getString(R.string.switch_setting), false);
                }
            }
        });
        return true;
    }

    private void addPushSettings(String setting, Boolean value) {
        //SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(setting, value);
        editor.commit();
    }

    private boolean getPushSettings(String setting) {
        //SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean defaultValue = true;
        return sharedPref.getBoolean(setting, defaultValue);
    }

    private void sendScreenName() {
        String name = "Settings_BW";
        // [START screen_view_hit]
        mTracker.setScreenName(name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        // [END screen_view_hit]
    }

    public void sendGoogleAction(String category, String description) {
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(description)
                .build());
    }



}
