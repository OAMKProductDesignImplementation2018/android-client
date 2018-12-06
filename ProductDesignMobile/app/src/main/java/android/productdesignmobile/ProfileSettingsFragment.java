package android.productdesignmobile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

import static android.productdesignmobile.LoginActivity.session;

public class ProfileSettingsFragment extends android.support.v4.app.Fragment {

    private Button buttonDietarySettings;
    private Button buttonAddPicture;
    private Button buttonUpdateData;

    private EditText first_name;
    private EditText last_name;
    private EditText email;

    public int gender;

    public Spinner gender_spinner;

    //String updateDataUrlAddress="http://productdesign.westeurope.cloudapp.azure.com/android_api/update_user_data.php";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_settings, container, false);

        // Dropdown menu for gender
        String[] gender_array = getResources().getStringArray(R.array.gender_array);
        gender_spinner = view.findViewById(R.id.gender_spinner);
        ArrayAdapter<String> gender_adapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()), R.layout.spinner_item, gender_array);
        gender_adapter.setDropDownViewResource(R.layout.spinner_item);
        gender_spinner.setAdapter(gender_adapter);

        //Dropdown menu for language
        //String[] language_array = getResources().getStringArray(R.array.language_array);
        //Spinner language_spinner = (Spinner) view.findViewById(R.id.language_spinner);
        //ArrayAdapter<String> language_adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, language_array);
        //language_adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        //language_spinner.setAdapter(language_adapter);

        // Open dietary settings dialogfragment
        buttonDietarySettings = view.findViewById(R.id.buttonDietarySettings);
        buttonDietarySettings.setOnClickListener(v -> {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Fragment prev = getFragmentManager().findFragmentByTag("dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);
            DialogFragment dialogFragment = new DietarySettingsFragment();
            dialogFragment.show(ft, "dialog");
        });

        //Open PhotoOptionsFragment
        buttonAddPicture = view.findViewById(R.id.buttonAddPicture);
        buttonAddPicture.setOnClickListener(v -> {
            Fragment fragment = null;
            Class fragmentClass = PhotoOptionsFragment.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            FragmentManager fragmentManagerAddPicture = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
            fragmentManagerAddPicture.beginTransaction()
                    .replace(R.id.content_frame, Objects.requireNonNull(fragment))
                    .addToBackStack(null)
                    .commit();
        });

        // init
        first_name = view.findViewById(R.id.editTextFirstName);
        last_name = view.findViewById(R.id.editTextLastName);
        email = view.findViewById(R.id.editTextEmail);
        email.setFocusable(false);
        email.setAlpha(.5f);

        // Set current session's userdata
        HashMap<String, String> user = session.getUserDetails();
        last_name.setText(user.get(SessionManager.KEY_LAST_NAME));
        email.setText(user.get(SessionManager.KEY_EMAIL));


        // Update userdata
        buttonUpdateData = view.findViewById(R.id.buttonUpdateData);
        buttonUpdateData.setOnClickListener(v -> {
            //todo update data to session
            //todo update data to database
            //todo disable button until user makes some changes to data
            try {
                JSONObject jo = new JSONObject();
                jo.accumulate("first_name", first_name.getText().toString());
                jo.accumulate("last_name", last_name.getText().toString());
                jo.accumulate("gender", gender_spinner.getSelectedItemPosition());
                session.setUserDetails(jo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        return view;
    }
}
