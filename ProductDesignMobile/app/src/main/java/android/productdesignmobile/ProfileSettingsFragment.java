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
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

import static android.productdesignmobile.LoginActivity.session;

public class ProfileSettingsFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    private Button buttonDietarySettings;
    private Button buttonAddPicture;
    private Button buttonUpdateData;

    private EditText first_name;
    private EditText last_name;
    private EditText email;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_settings, container, false);

        /*
        // Dropdown menu for gender
        String[] gender_array = getResources().getStringArray(R.array.gender_array);
        gender_spinner = view.findViewById(R.id.gender_spinner);
        ArrayAdapter<String> gender_adapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()), R.layout.spinner_item, gender_array);
        gender_adapter.setDropDownViewResource(R.layout.spinner_item);
        gender_spinner.setAdapter(gender_adapter);*/

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
                session.setUserDetails(jo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        // init
        first_name = view.findViewById(R.id.editTextFirstName);
        first_name.setFocusable(false);
        first_name.setAlpha(.5f);
        last_name = view.findViewById(R.id.editTextLastName);
        last_name.setFocusable(false);
        last_name.setAlpha(.5f);
        email = view.findViewById(R.id.editTextEmail);
        email.setFocusable(false);
        email.setAlpha(.5f);

        // Set current session's userdata
        HashMap<String, String> user = session.getUserDetails();
        last_name.setText(user.get(SessionManager.KEY_LAST_NAME));
        email.setText(user.get(SessionManager.KEY_EMAIL));


        return view;
    }

    @Override
    public void onClick(View v) {
        Fragment fragment = null;
        Class fragmentClass;
        switch (v.getId()) {
            case R.id.buttonAddPicture:
                Log.d("Add picture", "button clicked");
                fragmentClass = PhotoOptionsFragment.class;
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
                break;
            case R.id.buttonUpdateData:
                break;
        }
    }
}
