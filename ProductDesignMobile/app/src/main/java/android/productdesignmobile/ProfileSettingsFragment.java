package android.productdesignmobile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class ProfileSettingsFragment extends Fragment implements View.OnClickListener {

    private Button buttonDietarySettings;
    private Button buttonAddPicture;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_settings, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        //Dropdown menu for gender
        String[] gender_array = getResources().getStringArray(R.array.gender_array);
        Spinner gender_spinner = (Spinner) getView().findViewById(R.id.gender_spinner);
        ArrayAdapter<String> gender_adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, gender_array);
        gender_adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        gender_spinner.setAdapter(gender_adapter);

        //Dropdown menu for language
        String[] language_array = getResources().getStringArray(R.array.language_array);
        Spinner language_spinner = (Spinner) getView().findViewById(R.id.language_spinner);
        ArrayAdapter<String> language_adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, language_array);
        language_adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        language_spinner.setAdapter(language_adapter);

        buttonDietarySettings = (Button) getActivity().findViewById(R.id.buttonDietarySettings);
        buttonDietarySettings.setOnClickListener(this);
        buttonAddPicture = (Button) getActivity().findViewById(R.id.buttonAddPicture);
        buttonAddPicture.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        Fragment fragment = null;
        Class fragmentClass;

        switch (v.getId()) {
            case R.id.buttonDietarySettings:
                fragmentClass = DietarySettingsFragment.class;
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                FragmentManager fragmentManagerDietarySettings = getActivity().getSupportFragmentManager();
                fragmentManagerDietarySettings.beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.buttonAddPicture:
                fragmentClass = PhotoOptionsFragment.class;
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                FragmentManager fragmentManagerAddPicture = getActivity().getSupportFragmentManager();
                fragmentManagerAddPicture.beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .addToBackStack(null)
                        .commit();
                break;
            default:
                break;
        }
    }
}
