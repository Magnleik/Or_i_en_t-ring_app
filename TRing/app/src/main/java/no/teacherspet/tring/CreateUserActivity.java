package no.teacherspet.tring;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import io.reactivex.disposables.Disposable;
import no.teacherspet.tring.Database.Entities.RoomUser;
import no.teacherspet.tring.Database.LocalDatabase;
import no.teacherspet.tring.Database.ViewModels.UserViewModel;

public class CreateUserActivity extends AppCompatActivity {

    EditText firstName;
    EditText lastName;
    Button saveButton;
    Integer userID;

    UserViewModel userViewModel;
    LocalDatabase localDatabase;

    Disposable userList;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Create user");

        localDatabase = LocalDatabase.getInstance(this);
        userViewModel = new UserViewModel(localDatabase.userDAO());

        userID = 0;
        Disposable idDisposable =  userViewModel.getMaxID().subscribe(integer -> userID = integer);

        firstName = (EditText) findViewById(R.id.first_name);
        lastName = (EditText) findViewById(R.id.last_name);
        saveButton = (Button) findViewById(R.id.save_button);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idDisposable.dispose();
               if(lastName.getText().toString().trim().length() > 0 && firstName.getText().toString().trim().length() > 0 ){
                    insertUser();
                }
                else{
                    Toast.makeText(CreateUserActivity.this, "Please write first and last name", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void insertUser() {
        saveButton.setEnabled(false);
        RoomUser roomUser = new RoomUser(userID, true, firstName.getText().toString(), lastName.getText().toString());
        userViewModel.addUsers(roomUser).subscribe(longs -> changeActivity(longs));
    }

    private void changeActivity(long[] longs){
        if(longs[0] >= 0){
            Toast.makeText(this, "RoomUser successfully saved", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, StartupMenu.class));
        }
        else{
            Toast.makeText(this, "Something went wrong, please try again", Toast.LENGTH_LONG).show();
            saveButton.setEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        userList.dispose();
        localDatabase.close();
        super.onDestroy();
    }
}
