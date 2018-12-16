package android.productdesignmobile;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import org.json.JSONException;

import java.util.HashMap;

import static android.productdesignmobile.LoginActivity.session;

public class DietarySettingsFragment extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dietary_settings, container, false);

        CheckBox gluten = view.findViewById(R.id.checkBoxGlutenFree);
        CheckBox lactosefree = view.findViewById(R.id.checkBoxLactoseFree);
        CheckBox lowlactose = view.findViewById(R.id.checkBoxLowLactose);
        CheckBox milkless = view.findViewById(R.id.checkBoxMilkless);
        CheckBox feel_well = view.findViewById(R.id.checkBoxFeelWell);
        CheckBox vegan = view.findViewById(R.id.checkBoxVegan);
        CheckBox garlic = view.findViewById(R.id.checkBoxGarlic);
        CheckBox allergen = view.findViewById(R.id.checkBoxAllergen);

        HashMap<String, Boolean> dietary = session.getDietaryDetails();

        gluten.setChecked(dietary.get(SessionManager.KEY_DIETARY_GLUTEN));
        lactosefree.setChecked(dietary.get(SessionManager.KEY_DIETARY_LACTOSEFREE));
        lowlactose.setChecked(dietary.get(SessionManager.KEY_DIETARY_LOWLACTOSE));
        milkless.setChecked(dietary.get(SessionManager.KEY_DIETARY_MILKLESS));
        feel_well.setChecked(dietary.get(SessionManager.KEY_FEEL_WELL));
        vegan.setChecked(dietary.get(SessionManager.KEY_DIETARY_VEGAN));
        garlic.setChecked(dietary.get(SessionManager.KEY_DIETARY_GARLIC));
        allergen.setChecked(dietary.get(SessionManager.KEY_DIETARY_ALLERGEN));

        // Update dietary settings to database and sharedprefs
        final Button updateDietary = view.findViewById(R.id.buttonSaveDietary);
        updateDietary.setOnClickListener(v -> {
            updateDietary.setEnabled(false);
            updateDietary.setAlpha(.5f);
            // Update dietary settings to session sharedprefs
            HashMap<String, Boolean> temp = new HashMap<>();
            temp.put("gluten", gluten.isChecked());
            temp.put("lactosefree", lactosefree.isChecked());
            temp.put("lowlactose", lowlactose.isChecked());
            temp.put("milkless", milkless.isChecked());
            temp.put("feel_well", feel_well.isChecked());
            temp.put("vegan", vegan.isChecked());
            temp.put("garlic", garlic.isChecked());
            temp.put("allergen", allergen.isChecked());
            session.setDietaryDetails(temp);
            UpdateUserData uud;
            try {
                uud = new UpdateUserData(getContext());
                uud.execute();
                updateDietary.setEnabled(true);
                updateDietary.setAlpha(.5f);
                dismiss();
                Toast.makeText(getActivity(), "Dietary settings uploaded!", Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                updateDietary.setEnabled(false);
                updateDietary.setAlpha(1f);
                e.printStackTrace();
                Toast.makeText(getActivity(), "Dietary settings upload failed.", Toast.LENGTH_SHORT).show();
            }

        });
        return view;
    }
}
