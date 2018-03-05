package no.teacherspet.tring;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import no.teacherspet.tring.Database.Entities.User;
import no.teacherspet.tring.Database.LocalDatabase;
import no.teacherspet.tring.Database.ViewModels.UserViewModel;

public class CreateUserActivity extends AppCompatActivity {

    EditText firstName;
    EditText lastName;
    Button saveButton;
    TextView showUser;

    UserViewModel userViewModel;
    LocalDatabase localDatabase;

    Disposable userList;

    private final CompositeDisposable disposable = new CompositeDisposable();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        localDatabase = LocalDatabase.getInstance(this);

        userViewModel = new UserViewModel(localDatabase.userDAO());

        firstName = (EditText) findViewById(R.id.first_name);
        lastName = (EditText) findViewById(R.id.last_name);
        saveButton = (Button) findViewById(R.id.save_button);

        showUser = (TextView) findViewById(R.id.show_user);

        createObservable();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(lastName.getText().toString().trim().length() > 0 && firstName.getText().toString().trim().length() > 0 ){
                    insertUser();
                    //createObservable();
                }
                else{
                    Toast.makeText(CreateUserActivity.this, "Please write first and last name", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
    }
    Integer position = 0;

    private void insertUser() {
        saveButton.setEnabled(false);
        User user = new User(position, true, firstName.getText().toString(), lastName.getText().toString());
        userViewModel.addUsers(user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(longs ->
                Toast.makeText(this, "Saved user: " + longs[0], Toast.LENGTH_LONG).show());
        saveButton.setEnabled(true);

    }

    public void createObservable(){
        userList = userViewModel.getAllUsers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<User>>() {
            @Override
            public void accept(List<User> users) throws Exception {
                if(users != null && users.size() > 0){
                    showUser.setText(users.get(0).getFullName());
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        userList.dispose();
        localDatabase.close();
        super.onDestroy();
    }
}
