package no.teacherspet.tring.Database.Entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.location.Location;

/**
 * Created by Hermann on 13.02.2018.
 */
@Entity(tableName = "point")
public class Point {

    public Point(/*int pointID,*/ String description, Location location) {
        //this.pointID = pointID;
        this.description = description;
        this.location = location;
        this.visited = false;
        this.isStart = false;
    }


    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "point_id")
    private int pointID;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "is_start")
    private boolean isStart;

    @ColumnInfo(name = "visited")
    private boolean visited;

    @ColumnInfo(name = "location")
    private Location location;


    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isStart() {
        return isStart;
    }
    public void setStart(boolean start) {
        isStart = start;
    }

    public boolean isVisited() {
        return visited;
    }
    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public Location getLocation() {
        return location;
    }
    public void setLocation(Location location) {
        this.location = location;
    }

}
