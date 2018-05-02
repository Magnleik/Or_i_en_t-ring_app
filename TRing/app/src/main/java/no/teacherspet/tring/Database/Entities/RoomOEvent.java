package no.teacherspet.tring.Database.Entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Map;

/**
 * Created by Hermann on 13.02.2018.
 * Class for the events. Contains hashmap of name and description, as well as if
 * the event is currently active
 */
@Entity(tableName = "o_event")
public class RoomOEvent {

    public RoomOEvent(int id, Map<String, String> properties) {
        this.id = id;
        this.properties = properties;
        active = false;
    }

    @PrimaryKey //(autoGenerate = true)
    private int id;

    private boolean active;

    private Map<String, String> properties;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Map<String, String> getProperties() {
        return properties;
    }
    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

}
