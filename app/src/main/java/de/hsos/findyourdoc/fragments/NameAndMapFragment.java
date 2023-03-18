package de.hsos.findyourdoc.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import de.hsos.findyourdoc.R;
import de.hsos.findyourdoc.storage.DatabaseHelper;
import de.hsos.findyourdoc.storage.SharedPreferencesEnum;

/**
 * A simple {@link Fragment} subclass.
 */
public class NameAndMapFragment extends Fragment {

    private DatabaseHelper databaseHelper;
    private EditText docNameEditText;
    private SharedPreferences sharedPreferences;

    public NameAndMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // No Arguments were passed
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_name_and_map, container, false);

        this.docNameEditText = view.findViewById(R.id.docNameTextInputFieldEditTextDocCreation);
        this.sharedPreferences = requireContext().getSharedPreferences(String.valueOf(SharedPreferencesEnum.SHARED_PREFERENCES_ID), MODE_PRIVATE);
        this.databaseHelper = new DatabaseHelper(getContext());

        Button searchOnGoogleMapsButton = view.findViewById(R.id.findOnGoogleMapsButton);
        searchOnGoogleMapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchOnGoogleMaps();
            }
        });

        Button nextFragmentButton = view.findViewById(R.id.buttonGoToCalenderFragment);
        nextFragmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (docNameEditText.getText().toString().length() > 20) {
                    Toast.makeText(getContext(), getString(R.string.numberOfCharsExceeded), Toast.LENGTH_LONG).show();
                    return;
                }

                if (!docNameEditText.getText().toString().isEmpty() && !docNameEditText.getText().toString().isBlank()) {
                    Bundle bundle = new Bundle();
                    String docName = docNameEditText.getText().toString();
                    if (!databaseHelper.checkExistence(docName)) {
                        bundle.putString("docname", docName);
                        Navigation.findNavController(view).navigate(R.id.action_nameAndMapFragment_to_calendarFragment, bundle);
                    } else {
                        Toast.makeText(getContext(), getString(R.string.doc_already_exists), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getContext(), getString(R.string.doc_input_mandatory), Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }

    private void searchOnGoogleMaps() {
        if (docNameEditText.getText().toString().length() > 20) {
            Toast.makeText(getContext(), getString(R.string.numberOfCharsExceeded), Toast.LENGTH_LONG).show();
            return;
        }

        if (!this.docNameEditText.getText().toString().isEmpty() && !docNameEditText.getText().toString().isBlank()) {
            Uri uri = Uri.parse("https://www.google.de/maps/search/" + this.docNameEditText.getText().toString() + " " +
                    this.sharedPreferences.getString(String.valueOf(SharedPreferencesEnum.CITY), getString(R.string.default_city)));
            Intent i = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(i);
        } else {
            Toast.makeText(getContext(), getString(R.string.doc_input_mandatory), Toast.LENGTH_LONG).show();
        }
    }
}