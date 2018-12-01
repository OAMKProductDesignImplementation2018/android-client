package android.productdesignmobile;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import java.util.HashMap;

import static android.productdesignmobile.LoginActivity.session;

public class DietarySettingsFragment extends DialogFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dietary_settings, container, false);

        CheckBox gluten = view.findViewById(R.id.checkBoxGlutenFree);
        CheckBox lactosefree = view.findViewById(R.id.checkBoxLactoseFree);
        CheckBox lowlactose = view.findViewById(R.id.checkBoxLowLactose);
        CheckBox milkless = view.findViewById(R.id.checkBoxMilkless);
        CheckBox vegan = view.findViewById(R.id.checkBoxVegan);

        HashMap<String, Boolean> dietary = session.getDietaryDetails();

        gluten.setChecked(dietary.get(SessionManager.KEY_DIETARY_GLUTEN));
        lactosefree.setChecked(dietary.get(SessionManager.KEY_DIETARY_LACTOSEFREE));
        lowlactose.setChecked(dietary.get(SessionManager.KEY_DIETARY_LACTOSEFREE));
        milkless.setChecked(dietary.get(SessionManager.KEY_DIETARY_LOWLACTOSE));
        vegan.setChecked(dietary.get(SessionManager.KEY_DIETARY_VEGAN));

        // Update dietary settings to database and sharedprefs
        final Button updateDietary = view.findViewById(R.id.buttonSaveDietary);
        updateDietary.setOnClickListener(v -> {
            // Update dietary settings to session sharedprefs
            HashMap<String, Boolean> temp = new HashMap<String, Boolean>();
            temp.put("gluten", gluten.isChecked());
            temp.put("lactosefree", lactosefree.isChecked());
            temp.put("lowlactose", lowlactose.isChecked());
            temp.put("milkless", milkless.isChecked());
            temp.put("vegan", vegan.isChecked());
            session.setDietaryDetails(temp);
            dismiss();

            //TODO update dietary settings to database
        });
        return view;
    }
}
