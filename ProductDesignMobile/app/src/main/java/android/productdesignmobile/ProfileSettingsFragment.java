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

import java.util.HashMap;
import java.util.Objects;

import static android.productdesignmobile.MainActivity.session;

public class ProfileSettingsFragment extends android.support.v4.app.Fragment implements View.OnClickListener, UserDataInterface {

    private Button buttonDietarySettings;
    private Button buttonAddPicture;
    private Button buttonUpdateData;

    private EditText first_name;
    private EditText last_name;
    private EditText email;

    public int gender;

    public Spinner gender_spinner;

    //String fetchDataUrlAddress="http://productdesign.westeurope.cloudapp.azure.com/android_api/fetch_user_data.php";
    String updateDataUrlAddress="http://productdesign.westeurope.cloudapp.azure.com/android_api/update_user_data.php";

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
        buttonAddPicture = view.findViewById(R.id.buttonAddPicture);
        buttonAddPicture.setOnClickListener(this);
        buttonUpdateData = view.findViewById(R.id.buttonUpdateData);
        buttonUpdateData.setOnClickListener(this);
        first_name = view.findViewById(R.id.editTextFirstName);
        last_name = view.findViewById(R.id.editTextLastName);
        email = view.findViewById(R.id.editTextEmail);

        HashMap<String, String> user = session.getUserDetails();
        first_name.setText(user.get(SessionManager.KEY_FIRSTNAME));
        last_name.setText(user.get(SessionManager.KEY_LASTNAME));
        email.setText(user.get(SessionManager.KEY_EMAIL));
        /*
        FetchUserData fetchdata = new FetchUserData(getContext(),fetchDataUrlAddress, "1");
        fetchdata.setTestInterface(this);
        fetchdata.execute();
        */

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
                Log.d("Update data", "button clicked");
                setGender();
                UpdateUserData updatedata = new UpdateUserData(getContext(), updateDataUrlAddress, gender, first_name, last_name, email);
                updatedata.execute();
                break;
        }
    }

    private void setGender() {
        int temp = gender_spinner.getSelectedItemPosition();
        Log.d("Gender spinner", "Get selected item position" + temp);
        if (temp == 0){
            Log.d("Set gender", "female");
            gender = 1;
        }
        else {
            Log.d("Set gender", "male");
            gender = 2;
        }
    }

    @Override
    public void setUserData(String firstname, String lastname, String email, int gender) {
        this.first_name.setText(firstname);
        this.last_name.setText(lastname);
        this.email.setText(email);
        gender_spinner.setSelection(gender - 1);
    }
}
