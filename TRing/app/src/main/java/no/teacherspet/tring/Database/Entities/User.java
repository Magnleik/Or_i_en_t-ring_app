package no.teacherspet.tring.Database.Entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Hermann on 13.02.2018.
 */
@Entity(tableName = "user")
public class User {

    public User(int id, boolean personalProfile, String firstName, String lastName) {
        this.id = id;
        this.personalProfile = personalProfile;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @PrimaryKey //(autoGenerate = true)
    private int id;

    private boolean personalProfile;
    private String firstName;
    private String lastName;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
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

    public String getFullName() {
        return firstName + " " + lastName;
    }


}
