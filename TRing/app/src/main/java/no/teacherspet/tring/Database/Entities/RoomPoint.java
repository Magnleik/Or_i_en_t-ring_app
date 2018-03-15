package no.teacherspet.tring.Database.Entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Hermann on 13.02.2018.
 */
@Entity(tableName = "point", indices = @Index(value = "id", unique = true))
public class RoomPoint {

    public RoomPoint(int id, String description, LatLng latLng) {
        this.id = id;
        this.description = description;
        this.latLng = latLng;
    }

    @PrimaryKey //(autoGenerate = true)
    public int id;

    private String description;
    private LatLng latLng;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public LatLng getLatLng() {
        return latLng;
    }
    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }


}
