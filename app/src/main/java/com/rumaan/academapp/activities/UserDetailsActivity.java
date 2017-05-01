package com.rumaan.academapp.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rumaan.academapp.R;

import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Activity to get Extra User Details:
 * College Name
 * Sem
 * Class
 * Course
 */

public class UserDetailsActivity extends AppCompatActivity {

    private static final String USER_REF_STRING = "users";
    @BindView(R.id.root_view)
    LinearLayout rootView;
    @BindView(R.id.usn)
    TextInputLayout usnTextInput;
    @BindView(R.id.college_name)
    TextInputLayout collegeNameInput;
    @BindView(R.id.course_spinner)
    Spinner courseSpinner;
    @BindView(R.id.year_spinner)
    Spinner yearSpinner;
    private DatabaseReference mRef;

    @OnClick(R.id.btn_next)
    void onClick() {
        // Validate the fields before going to next activity
        Editable collegeName = collegeNameInput.getEditText().getText();
        Editable usn = usnTextInput.getEditText().getText();
        if (TextUtils.isEmpty(collegeName) || isCollegeNameValid(collegeName)) {
            collegeNameInput.setErrorEnabled(true);
            collegeNameInput.setError(getString(R.string.valid_college_name_error));
            return;
        }
        if (TextUtils.isEmpty(usn) || isUsnValid(usn) || usn.length() != 10) {
            usnTextInput.setErrorEnabled(true);
            usnTextInput.setError(getString(R.string.valid_usn_error));
            return;
        }
        collegeNameInput.setErrorEnabled(false);
        usnTextInput.setErrorEnabled(false);
        Toast.makeText(this, "All good!", Toast.LENGTH_SHORT).show();

        // update the values in the database
        updateFirebaseDatabase(collegeName, usn, yearSpinner.getSelectedItem().toString(), courseSpinner.getSelectedItem().toString());
        //    startActivity(new Intent(this, MainActivity.class));
    }

    private void updateFirebaseDatabase(Editable collegeName, Editable usn, String year, String course) {
        // set the values in Database
        mRef.child("college_name").setValue(collegeName.toString());
        mRef.child("usn").setValue(usn.toString());
        mRef.child("year").setValue(year);
        mRef.child("course").setValue(course);
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        setContentView(R.layout.activity_user_details);

        ButterKnife.bind(this);

        // set firebase stuffs

        mRef = FirebaseDatabase.getInstance().getReference(USER_REF_STRING).child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        ArrayAdapter coursesList = ArrayAdapter.createFromResource(this, R.array.courses_list, R.layout.spinner_item);
        ArrayAdapter yearList = ArrayAdapter.createFromResource(this, R.array.year_list, R.layout.spinner_item);

        courseSpinner.setAdapter(coursesList);
        yearSpinner.setAdapter(yearList);

        // add text watchers to input fields
        collegeNameInput.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (isCollegeNameValid(s)) {
                    collegeNameInput.setError(getString(R.string.valid_college_name_error));
                    collegeNameInput.setErrorEnabled(true);
                } else {
                    collegeNameInput.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        usnTextInput.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (isUsnValid(s)) {
                    usnTextInput.setErrorEnabled(true);
                    usnTextInput.setError(getString(R.string.valid_usn_error));
                } else {
                    usnTextInput.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    boolean isCollegeNameValid(CharSequence s) {
        // TODO: Check for more invalid characters
        return !Pattern.matches("^(a-z|A-Z|0-9)*[^#$%^&*()/+_-]*$", s.toString());
    }

    boolean isUsnValid(CharSequence s) {
        // TODO: create regExp particular to USN
        return !Pattern.matches("^(a-z|A-Z|0-9)*[^#$%^&*()' /+_-]*$", s.toString());

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
