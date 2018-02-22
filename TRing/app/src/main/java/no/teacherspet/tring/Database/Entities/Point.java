package no.teacherspet.tring.Database.Entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Hermann on 13.02.2018.
 */
@Entity(tableName = "point")
public class Point {

    public Point(/*int id,*/ String description, LatLng latLng) {
        this.description = description;
        this.latLng = latLng;
    }

    @PrimaryKey(autoGenerate = true)
    private int id;

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
