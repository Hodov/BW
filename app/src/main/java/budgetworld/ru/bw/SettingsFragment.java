package budgetworld.ru.bw;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;


public class SettingsFragment extends android.support.v4.app.Fragment {

    private Switch mSwitch;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.settings_fragment,container,false);
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

                    addPushSettings(getString(R.string.switch_setting), true);
                } else {

                    addPushSettings(getString(R.string.switch_setting), false);
                }
            }
        });
        return true;
    }

    private void addPushSettings(String setting, Boolean value) {
        System.out.println("Записали");
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(setting, value);
        editor.commit();
    }

    private boolean getPushSettings(String setting) {
        System.out.println("Получили");
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        boolean defaultValue = true;
        return sharedPref.getBoolean(setting, defaultValue);
    }


}
