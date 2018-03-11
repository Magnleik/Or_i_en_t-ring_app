package no.teacherspet.tring.Database.Entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;

/**
 * Created by Hermann on 15.02.2018.
 */
@Entity(tableName = "point_oevent_join",
        primaryKeys = {"pointID", "oEventID"},
        foreignKeys = {
                @ForeignKey(entity = Point.class,
                        parentColumns = "id",
                        childColumns = "pointID",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = OEvent.class,
                        parentColumns = "id",
                        childColumns = "oEventID",
                        onDelete = ForeignKey.CASCADE)
                },
        indices = {@Index("pointID"),
                   @Index("oEventID")})
public class PointOEventJoin {

    public PointOEventJoin(int pointID, int oEventID, boolean isStart){
        this.pointID = pointID;
        this.oEventID = oEventID;
        this.isStart = isStart;
    }

    public final int pointID;
    public final int oEventID;
    private final boolean isStart;

    public int getPointID() {
        return pointID;
    }

    public int getoEventID() {
        return oEventID;
    }

    public boolean isStart() {
        return isStart;
    }
}
