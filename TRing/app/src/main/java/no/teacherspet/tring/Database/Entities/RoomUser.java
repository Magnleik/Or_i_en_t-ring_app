package no.teacherspet.tring.Database.Entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Hermann on 13.02.2018.
 */
@Entity(tableName = "user")
public class RoomUser {

    public RoomUser(String token) {
        this.token = token;
    }

    @PrimaryKey
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
