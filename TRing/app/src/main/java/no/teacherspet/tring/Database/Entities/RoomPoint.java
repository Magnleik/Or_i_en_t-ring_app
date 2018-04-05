package no.teacherspet.tring.Database.Entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;

import java.util.Map;

/**
 * Created by Hermann on 13.02.2018.
 */
@Entity(tableName = "point", indices = @Index(value = "id", unique = true))
public class RoomPoint {

    public RoomPoint(int id, Map<String, String> properties, LatLng latLng) {
        this.id = id;
        this.properties = properties;
        this.latLng = latLng;
        this.visited = false;
    }

    @PrimaryKey //(autoGenerate = true)
    private int id;

    private LatLng latLng;
    private Map<String, String> properties;
    private boolean visited;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public LatLng getLatLng() {
        return latLng;
    }
    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public Map<String, String> getProperties() {
        return properties;
    }
    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public boolean isVisited() {
        return visited;
    }
    public void setVisited(boolean visited) {
        this.visited = visited;
    }
}
