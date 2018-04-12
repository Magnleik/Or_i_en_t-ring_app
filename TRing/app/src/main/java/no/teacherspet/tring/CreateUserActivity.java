package no.teacherspet.tring;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import connection.ICallbackAdapter;
import connection.NetworkManager;
import io.reactivex.disposables.Disposable;
import no.teacherspet.tring.Database.Entities.User;
import no.teacherspet.tring.Database.LocalDatabase;
import no.teacherspet.tring.Database.ViewModels.UserViewModel;

public class CreateUserActivity extends AppCompatActivity {

    EditText createUsername;
    EditText createPassword;
    EditText passwordCheck;
    ProgressBar progressBar;
    Button saveButton;
    Integer userID;

    //UserViewModel userViewModel;
    //LocalDatabase localDatabase;

    //Disposable userList;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Create user");

        //localDatabase = LocalDatabase.getInstance(this);
        //userViewModel = new UserViewModel(localDatabase.userDAO());

        userID = 0;
        //Disposable idDisposable =  userViewModel.getMaxID().subscribe(integer -> userID = integer);

        createUsername = (EditText) findViewById(R.id.create_username);
        createPassword = (EditText) findViewById(R.id.create_password);
        passwordCheck = (EditText) findViewById(R.id.password_check_edittext);
        progressBar = (ProgressBar) findViewById(R.id.create_user_progressbar);
        progressBar.setVisibility(View.GONE);
        saveButton = (Button) findViewById(R.id.save_button);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //idDisposable.dispose();
               if(createPassword.getText().toString().trim().length() > 0 && createUsername.getText().toString().trim().length() > 0 && passwordCheck.getText().toString().trim().length() > 0 ){
                    //insertUser();

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

    private void insertUser() {
        saveButton.setEnabled(false);
        User user = new User(userID, true, createUsername.getText().toString(), createPassword.getText().toString());
        //userViewModel.addUsers(user).subscribe(longs -> changeActivity(longs));
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

    @Override
    protected void onDestroy() {
        //userList.dispose();
        //localDatabase.close();
        super.onDestroy();
    }
}
