package no.teacherspet.tring.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
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

public class CreateUserActivity extends AppCompatActivity {

    EditText createUsername;
    EditText createPassword;
    EditText passwordCheck;
    ProgressBar progressBar;
    Button saveButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        createUsername = (EditText) findViewById(R.id.create_username);
        createPassword = (EditText) findViewById(R.id.create_password);
        passwordCheck = (EditText) findViewById(R.id.password_check_edittext);
        progressBar = (ProgressBar) findViewById(R.id.create_user_progressbar);
        progressBar.setVisibility(View.GONE);
        saveButton = (Button) findViewById(R.id.save_button);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }



    private void createUser(){

        //TODO: Check for already being logged in? (NetworkManager.getInstance.isAuthenticated())

        progressBar.setVisibility(View.VISIBLE);

        NetworkManager.getInstance().createUser(createUsername.getText().toString(), createPassword.getText().toString(), new ICallbackAdapter<Boolean>() {
            @Override
            public void onResponse(Boolean object) {

                progressBar.setVisibility(View.GONE);

                if(object!=null && object){
                    //Successfully created user. Should probably redirect to the main view.
                    Toast.makeText(CreateUserActivity.this,"New user created",Toast.LENGTH_LONG).show();

                    if (NetworkManager.getInstance().isAuthenticated()){
                        Toast.makeText(CreateUserActivity.this, "Logged in", Toast.LENGTH_LONG).show();

                        saveCredentialsToLocal();

                    }else{
                        Toast.makeText(CreateUserActivity.this, "Failed to log in", Toast.LENGTH_LONG).show();
                    }


                }
                else if(object==null){
                    Toast.makeText(CreateUserActivity.this,"Something went wrong on the server, please try again",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(CreateUserActivity.this,"Username might be taken, please try another name",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {

                progressBar.setVisibility(View.GONE);

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
        userViewModel.addUsers(new RoomUser(token)).subscribe(longs -> checkResult(longs));

    }
    private void checkResult(long[] longs){
        if(longs[0] < 0){
            Toast.makeText(this, "Something went wrong when saving the user locally", Toast.LENGTH_SHORT).show();
            //saveCredentialsToLocal();
        }
        else{
            Toast.makeText(this, "User saved locally", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
