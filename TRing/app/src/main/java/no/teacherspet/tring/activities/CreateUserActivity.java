package no.teacherspet.tring.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

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



    private void createUser(){

        //TODO: Check for already being logged in? (NetworkManager.getInstance.isAuthenticated())

        progressDialog.show();
        saveButton.setEnabled(false);

        NetworkManager.getInstance().createUser(createUsername.getText().toString(), createPassword.getText().toString(), new ICallbackAdapter<Boolean>() {
            @Override
            public void onResponse(Boolean object) {

                progressDialog.hide();

                if(object!=null && object){
                    //Successfully created user. Should probably redirect to the main view.
                    Toast.makeText(CreateUserActivity.this,"New user created",Toast.LENGTH_LONG).show();

                    if (NetworkManager.getInstance().isAuthenticated()){
                        Toast.makeText(CreateUserActivity.this, "Logged in", Toast.LENGTH_LONG).show();

                        saveCredentialsToLocal();
                        backToMain();

                    }else{
                        Toast.makeText(CreateUserActivity.this, "Failed to log in", Toast.LENGTH_LONG).show();
                        finish(); //Send to log in
                    }


                }
                else if(object==null){
                    Toast.makeText(CreateUserActivity.this,"Something went wrong on the server, please try again",Toast.LENGTH_LONG).show();
                    saveButton.setEnabled(true);
                }else{
                    Toast.makeText(CreateUserActivity.this,"Username might be taken, please try another name",Toast.LENGTH_LONG).show();
                    saveButton.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Throwable t) {

                progressDialog.hide();
                Toast.makeText(CreateUserActivity.this,"Something went wrong on the server, please try again",Toast.LENGTH_LONG).show();

            }
        });
    }

    //FIXME: Change this to work for new implementation
    private void changeActivity(long[] longs){
        if(longs[0] >= 0){
            Toast.makeText(this, "User successfully saved", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, MapsActivity.class));
        }
        else{
            Toast.makeText(this, "Something went wrong, please try again", Toast.LENGTH_LONG).show();
            saveButton.setEnabled(true);
        }
    }

    private void saveCredentialsToLocal(){
        String token =  NetworkManager.getInstance().getToken();
        LocalDatabase database = LocalDatabase.getInstance(this);
        UserViewModel userViewModel = new UserViewModel(database.userDAO());
        Log.d("Room","Started saving user");
        userViewModel.addUsers(new RoomUser(token)).subscribe(longs -> checkResult(longs));

    }
    private void checkResult(long[] longs){
        if(longs[0] < 0){
            Log.d("Room",String.format("User not saved, error: %d", longs[0]));
            Toast.makeText(this, "Something went wrong when saving the user locally", Toast.LENGTH_SHORT).show();
            //saveCredentialsToLocal();
        }
        else{
            Log.d("Room","User saved successfully");
            Toast.makeText(this, "User saved locally", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void saveButtonClick(View v) {

        if(createPassword.getText().toString().trim().length() > 0 && createUsername.getText().toString().trim().length() > 0 && passwordCheck.getText().toString().trim().length() > 0 ){

            if(createPassword.getText().toString().equals(passwordCheck.getText().toString())){
                createUser();
            }
            else{
                Toast.makeText(CreateUserActivity.this,"Passwords do not match",Toast.LENGTH_LONG).show();
            }
        }
        else{
            Toast.makeText(CreateUserActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
        }
    }

    private void backToMain(){
        Intent intent = new Intent(this, OrientationSelector.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
