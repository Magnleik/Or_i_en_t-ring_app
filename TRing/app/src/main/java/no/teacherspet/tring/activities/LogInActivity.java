package no.teacherspet.tring.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import connection.ICallbackAdapter;
import connection.NetworkManager;
import no.teacherspet.tring.Database.Entities.RoomUser;
import no.teacherspet.tring.Database.LocalDatabase;
import no.teacherspet.tring.Database.ViewModels.UserViewModel;
import no.teacherspet.tring.R;
import no.teacherspet.tring.util.GeneralProgressDialog;

/**
 * Created by Eirik on 24-Apr-18.
 */

public class LogInActivity extends AppCompatActivity {

    EditText username;
    EditText password;
    Button logInBtn;
    Button createUserBtn;
    GeneralProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.log_in_title);
        username = (EditText) findViewById(R.id.login_username);
        password = (EditText) findViewById(R.id.login_password);
        logInBtn = (Button) findViewById(R.id.login_btn);
        createUserBtn = (Button) findViewById(R.id.login_create_btn);

        progressDialog = new GeneralProgressDialog(this,this, true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    public void logInBtn(View v){
        progressDialog.show();
        logInBtn.setEnabled(false);

        NetworkManager.getInstance().logIn(username.getText().toString(), password.getText().toString(), new ICallbackAdapter<Boolean>() {

            @Override
            public void onResponse(Boolean object) {

                progressDialog.hide();

                if (object!=null && object){

                    if(NetworkManager.getInstance().isAuthenticated()) {
                        Toast.makeText(LogInActivity.this, R.string.successfully_logged_in, Toast.LENGTH_LONG).show();

                        saveCredentialsToLocal();

                        finish();

                    }else{
                        Toast.makeText(LogInActivity.this, R.string.user_verified_login_failed, Toast.LENGTH_LONG).show();

                        logInBtn.setEnabled(true);
                    }

                }else if(object==null){
                    Toast.makeText(LogInActivity.this, R.string.something_wrong_on_server_try_again, Toast.LENGTH_LONG).show();

                    logInBtn.setEnabled(true);
                }else{
                    Toast.makeText(LogInActivity.this, R.string.wrong_login_credentials, Toast.LENGTH_LONG).show();

                    logInBtn.setEnabled(true);
                }

            }

            @Override
            public void onFailure(Throwable t) {

                progressDialog.hide();
                logInBtn.setEnabled(true);

                Toast.makeText(LogInActivity.this, R.string.something_wrong_on_server_try_again, Toast.LENGTH_LONG).show();

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
            Toast.makeText(this, R.string.something_wrong_saving_user_locally, Toast.LENGTH_SHORT).show();
            //saveCredentialsToLocal();
        }
        else{
            Toast.makeText(this, R.string.user_saved_locally, Toast.LENGTH_SHORT).show();
        }
    }

    public void createUserClick(View v){
        Intent intent = new Intent(LogInActivity.this,CreateUserActivity.class);
        startActivity(intent);
    }

}
