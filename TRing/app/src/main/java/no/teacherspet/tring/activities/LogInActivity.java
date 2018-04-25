package no.teacherspet.tring.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import connection.ICallbackAdapter;
import connection.NetworkManager;
import no.teacherspet.tring.Database.Entities.RoomUser;
import no.teacherspet.tring.Database.LocalDatabase;
import no.teacherspet.tring.Database.ViewModels.UserViewModel;
import no.teacherspet.tring.R;

/**
 * Created by Eirik on 24-Apr-18.
 */

public class LogInActivity extends AppCompatActivity {

    ProgressBar progressBar;
    EditText username;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        username = (EditText) findViewById(R.id.login_username);
        password = (EditText) findViewById(R.id.login_password);
        progressBar = (ProgressBar) findViewById(R.id.login_progressBar);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    public void logInBtn(View v){
        progressBar.setVisibility(View.VISIBLE);

        NetworkManager.getInstance().logIn(username.getText().toString(), password.getText().toString(), new ICallbackAdapter<Boolean>() {

            @Override
            public void onResponse(Boolean object) {

                progressBar.setVisibility(View.GONE);

                if (object!=null && object){

                    if(NetworkManager.getInstance().isAuthenticated()) {
                        Toast.makeText(LogInActivity.this, "Successfully logged in", Toast.LENGTH_LONG).show();

                        saveCredentialsToLocal();

                    }else{
                        Toast.makeText(LogInActivity.this, "User verified, but log in failed", Toast.LENGTH_LONG).show();
                    }

                }else if(object==null){
                    Toast.makeText(LogInActivity.this, "Something went wrong on the server", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(LogInActivity.this, "Wrong log in credentials", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Throwable t) {

                progressBar.setVisibility(View.GONE);

                Toast.makeText(LogInActivity.this, "FAILURE: Something went wrong on the server, please try again", Toast.LENGTH_LONG).show();

            }
        });
    }

    private void saveCredentialsToLocal(){
        String token =  NetworkManager.getInstance().getToken();
        LocalDatabase database = LocalDatabase.getInstance(this);
        UserViewModel userViewModel = new UserViewModel(database.userDAO());
        userViewModel.getAllUsers().subscribe(roomUsers -> {
            if (roomUsers.size() > 0) {
                userViewModel.deleteUsers(roomUsers.get(0))
                        .subscribe(longs->{
                                userViewModel.addUsers(new RoomUser(token)).subscribe(longs1 -> checkResult(longs1));
                                });
            }
            else{
                userViewModel.addUsers(new RoomUser(token)).subscribe(longs -> checkResult(longs));
                }
        });

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


}
