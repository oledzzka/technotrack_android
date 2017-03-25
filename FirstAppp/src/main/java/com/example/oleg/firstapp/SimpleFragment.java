package com.example.oleg.firstapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;



/**
 * Created by oleg on 13.03.17.
 */

public class SimpleFragment extends Fragment {
    public static final String TAG = SimpleFragment.class.getSimpleName();
    public static final String GET_DATE = "GET_DATA";
    public static final String GET_FIRST_NAME = "GET_FIRST_NAME";
    public static final String GET_LAST_NAME = "GET_LAST_NAME";
    private static final String STATE_DATE = "STATE_DATE";
    private static final int REQUEST_DATA = 1;
    private String currentDate = null;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final String DEFAULT_STRING = getString(R.string.edit_date);
        final RelativeLayout l = (RelativeLayout) inflater.inflate(R.layout.simple_fragment, container, false);
        final Button save = (Button)l.findViewById(R.id.save);
        final TextView date = (TextView)l.findViewById(R.id.date);
        if (savedInstanceState != null) {
            currentDate = savedInstanceState.getString(STATE_DATE);
        }
        if (currentDate != null) {
            date.setText(currentDate);
        }
        final DialogFragment newFragment = new DateFragment();
        final EditText firstName = (EditText) l.findViewById(R.id.editFirstName);
        final EditText lastName = (EditText) l.findViewById(R.id.editLastName);
        save.setEnabled(false);
        newFragment.setTargetFragment(this, REQUEST_DATA);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(GET_DATE, date.getText().toString());
                bundle.putString(GET_LAST_NAME, lastName.getText().toString());
                bundle.putString(GET_FIRST_NAME, firstName.getText().toString());
                Intent intent = new Intent(getActivity(), SecondActivity.class);
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
            }
        });
        date.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
            }
        });
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!firstName.getText().toString().isEmpty() && !lastName.getText().toString().isEmpty()
                        && (date.getText().toString().compareTo(DEFAULT_STRING)!=0)) {
                    save.setEnabled(true);
                }
                else {
                    save.setEnabled(false   );
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        firstName.addTextChangedListener(textWatcher);
        lastName.addTextChangedListener(textWatcher);
        date.addTextChangedListener(textWatcher);
        return l;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Activity activity = getActivity();
        if (activity != null){
            final TextView date_view = (TextView) getView().findViewById(R.id.date);
            currentDate = data.getStringExtra(DateFragment.DATE);
            date_view.setText(currentDate);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_DATE, currentDate);
        super.onSaveInstanceState(outState);
    }
}
