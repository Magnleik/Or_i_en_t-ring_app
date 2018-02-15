package no.teacherspet.tring.Database.Entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Hermann on 13.02.2018.
 */
@Entity(tableName = "user")
public class User {

    public User(/*int userID,*/ boolean personalProfile, String firstName, String lastName) {
//        this.userID = userID;
        this.personalProfile = personalProfile;
        this.firstName = firstName;
        this.lastName = lastName;
    }


    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "user_id")
    private int userID;

    @ColumnInfo(name = "personal_profile")
    private boolean personalProfile;

    @ColumnInfo(name = "first_name")
    private String firstName;

    @ColumnInfo(name = "last_name")
    private String lastName;


    public int getUserID() {
        return userID;
    }
    public void setUserID(int userID) {
        this.userID = userID;
    }

    public boolean isPersonalProfile(){
        return personalProfile;
    }
    public void setPersonalProfile(boolean personalProfile){
        this.personalProfile = personalProfile;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /*
    public String fullName() {
        return firstName + " " + lastName;
    }
    */

}
