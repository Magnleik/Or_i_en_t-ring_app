package no.teacherspet.tring.Database.Entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Hermann on 13.02.2018.
 */
@Entity(tableName = "o_event")
public class RoomOEvent {

    public RoomOEvent(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @PrimaryKey //(autoGenerate = true)
    public int id;

    private String name;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

}
