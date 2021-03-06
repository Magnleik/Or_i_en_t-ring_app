package no.teacherspet.tring.Database.Entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;

/**
 * Created by Hermann on 15.02.2018.
 * Class for the connection between each event and its points. Keeps track of if a point
 * is the start point, and whether it is visited or not in the current event
 * Have two primary keys, which are foreign keys to the point and oevent entities
 */
@Entity(tableName = "point_oevent_join",
        primaryKeys = {"pointID", "oEventID"},
        foreignKeys = {
                @ForeignKey(entity = RoomPoint.class,
                        parentColumns = "id",
                        childColumns = "pointID",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = RoomOEvent.class,
                        parentColumns = "id",
                        childColumns = "oEventID",
                        onDelete = ForeignKey.CASCADE)
                },
        indices = {@Index("pointID"),
                   @Index("oEventID")})
public class PointOEventJoin {

    public PointOEventJoin(int pointID, int oEventID, boolean isStart, boolean visited){
        this.pointID = pointID;
        this.oEventID = oEventID;
        this.isStart = isStart;
        this.visited = visited;
    }

    public final int pointID;
    public final int oEventID;
    private final boolean isStart;
    private final boolean visited;


    public int getPointID() {
        return pointID;
    }

    public int getoEventID() {
        return oEventID;
    }

    public boolean isStart() {
        return isStart;
    }

    public boolean isVisited() {
        return visited;
    }
}
