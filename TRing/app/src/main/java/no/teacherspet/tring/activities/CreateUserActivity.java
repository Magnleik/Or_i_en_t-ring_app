package no.teacherspet.tring.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import connection.ICallbackAdapter;
import connection.NetworkManager;
import no.teacherspet.tring.Database.Entities.RoomUser;
import no.teacherspet.tring.Database.LocalDatabase;
import no.teacherspet.tring.Database.ViewModels.UserViewModel;
import no.teacherspet.tring.R;
import no.teacherspet.tring.util.GeneralProgressDialog;

public class CreateUserActivity extends AppCompatActivity {

    EditText createUsername;
    EditText createPassword;
    EditText passwordCheck;
    Button saveButton;
    GeneralProgressDialog progressDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.create_user_title);
        createUsername = (EditText) findViewById(R.id.create_username);
        createPassword = (EditText) findViewById(R.id.create_password);
        passwordCheck = (EditText) findViewById(R.id.password_check_edittext);
        saveButton = (Button) findViewById(R.id.create_user_save_btn);
        progressDialog = new GeneralProgressDialog(this, this, true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }


    /**
     * Method to be called if password validity checks pass. Sends network call in order to create the user.
     * Handles response about whether the username was already taken, and if internet is connected.
     */
    private void createUser() {
        progressDialog.show();
        saveButton.setEnabled(false);

        NetworkManager.getInstance().createUser(createUsername.getText().toString(), createPassword.getText().toString(), new ICallbackAdapter<Boolean>() {
            @Override
            public void onResponse(Boolean object) {

                progressDialog.hide();

                if (object != null && object) {
                    //Successfully created user. Should probably redirect to the main view.
                    Toast.makeText(CreateUserActivity.this, R.string.new_user_created, Toast.LENGTH_SHORT).show();

                    if (NetworkManager.getInstance().isAuthenticated()) {
                        Toast.makeText(CreateUserActivity.this, R.string.logged_in, Toast.LENGTH_SHORT).show();

                        saveCredentialsToLocal();
                        backToMain();

                    } else {
                        Toast.makeText(CreateUserActivity.this, R.string.failed_log_in, Toast.LENGTH_SHORT).show();
                        finish(); //Send to log in
                    }

                } else if (object == null) {
                    Toast.makeText(CreateUserActivity.this, R.string.something_wrong_on_server_try_again, Toast.LENGTH_SHORT).show();
                    saveButton.setEnabled(true);
                } else {
                    Toast.makeText(CreateUserActivity.this, R.string.username_might_be_taken, Toast.LENGTH_SHORT).show();
                    saveButton.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Throwable t) {

                progressDialog.hide();
                Toast.makeText(CreateUserActivity.this, R.string.something_wrong_on_server_try_again, Toast.LENGTH_SHORT).show();

            }
        });
    }

    /**
     * Save the validated log in credentials to the local database - in order to not having to log in when opening the app again.
     */
    private void saveCredentialsToLocal() {
        String token = NetworkManager.getInstance().getToken();
        LocalDatabase database = LocalDatabase.getInstance(this);
        UserViewModel userViewModel = new UserViewModel(database.userDAO());
        Log.d("Room", "Started saving user");
        userViewModel.addUsers(new RoomUser(token)).subscribe(longs -> checkResult(longs));

    }

    /**
     * Checks to see if the login was saved in the local database correctly.
     */
    private void checkResult(long[] longs) {
        if (longs[0] < 0) {
            Log.d("Room", String.format("User not saved, error: %d", longs[0]));
            Toast.makeText(this, R.string.something_wrong_saving_user_locally, Toast.LENGTH_SHORT).show();
            //saveCredentialsToLocal();
        } else {
            Log.d("Room", "User saved successfully");
            Toast.makeText(this, R.string.user_saved_locally, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Method to be called when pressing the "Save" button
     *
     * @param v
     */
    public void saveButtonClick(View v) {

        if (createPassword.getText().toString().trim().length() > 0 && createUsername.getText().toString().trim().length() > 0 && passwordCheck.getText().toString().trim().length() > 0) {

            if (createPassword.getText().toString().equals(passwordCheck.getText().toString())) {
                createUser();
            } else {
                Toast.makeText(CreateUserActivity.this, R.string.password_do_not_match, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(CreateUserActivity.this, R.string.fill_in_all_fields_toast, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Go all the way back to the start screen, and clear the activity history.
     */
    private void backToMain() {
        Intent intent = new Intent(this, OrientationSelector.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
