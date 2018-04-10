package no.teacherspet.tring.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import no.teacherspet.tring.Database.Entities.RoomUser;
import no.teacherspet.tring.Database.LocalDatabase;
import no.teacherspet.tring.Database.ViewModels.UserViewModel;

public class CreateUserActivity extends AppCompatActivity {

    EditText firstName;
    EditText lastName;
    Button saveButton;
    Integer userID;
    /*
    TextView textView;
    */
    UserViewModel userViewModel;
    LocalDatabase localDatabase;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Create user");

        localDatabase = LocalDatabase.getInstance(this);
        userViewModel = new UserViewModel(localDatabase.userDAO());

        userID = 0;

        firstName = (EditText) findViewById(R.id.first_name);
        lastName = (EditText) findViewById(R.id.last_name);
        saveButton = (Button) findViewById(R.id.save_button);
    /*
        textView = (TextView) findViewById(R.id.testingtext);
        textView.setText("empty");
        userViewModel.getPersonalUser().subscribe(roomUser -> setText(roomUser));
    */
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(lastName.getText().toString().trim().length() > 0 && firstName.getText().toString().trim().length() > 0 ){
                    //insertUser();
                    saveButton.setEnabled(false);
                    RoomUser roomUser = new RoomUser(userID, true, firstName.getText().toString(), lastName.getText().toString());
                    userViewModel.addUsers(roomUser).subscribe(longs -> changeActivity(longs));
                }
                else{
                    Toast.makeText(CreateUserActivity.this, "Please write first and last name", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    /*
    private void setText(List<RoomUser> users){
        if(users.size() > 0){
            textView.setText(users.get(0).getFirstName());
        }
    }
    */
    private void changeActivity(long[] longs){
        if(longs[0] >= 0){
            Toast.makeText(this, "User successfully saved", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, StartupMenu.class));
        }
        else{
            Toast.makeText(this, "Something went wrong, please try again", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, CreateUserActivity.class));
        }
    }

    @Override
    protected void onDestroy() {
        localDatabase.close();
        super.onDestroy();
    }
}
