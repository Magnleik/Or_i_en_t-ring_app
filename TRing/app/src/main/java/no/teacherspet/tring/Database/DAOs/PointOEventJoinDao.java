package no.teacherspet.tring.Database.DAOs;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Maybe;
import no.teacherspet.tring.Database.Entities.RoomOEvent;
import no.teacherspet.tring.Database.Entities.RoomPoint;
import no.teacherspet.tring.Database.Entities.PointOEventJoin;

/**
 * Created by Hermann on 15.02.2018.
 */
@Dao
public interface PointOEventJoinDao {

    @Query("SELECT * FROM point INNER JOIN point_oevent_join ON point.id = point_oevent_join.pointID" +
            " WHERE point_oevent_join.oEventID = :oEventID")
    Maybe<List<RoomPoint>> getPointsForOEvent(int oEventID);

    @Query("SELECT * FROM point INNER JOIN point_oevent_join ON point.id = point_oevent_join.pointID" +
            " WHERE point_oevent_join.oEventID = :oEventID AND point_oevent_join.isStart = 1")
    Maybe<RoomPoint> getStartPoint(int oEventID);

    @Query("SELECT * FROM point INNER JOIN point_oevent_join ON point.id = point_oevent_join.pointID" +
            " WHERE point_oevent_join.oEventID = :oEventID AND point_oevent_join.isStart = 0")
    Maybe<List<RoomPoint>> getPointsNotStart(int oEventID);

    @Query("SELECT * FROM o_event INNER JOIN point_oevent_join ON o_event.id = point_oevent_join.oEventID" +
            " WHERE point_oevent_join.pointID = :pointID")
    Maybe<List<RoomOEvent>> getOEventsForPoint(int pointID);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insert(PointOEventJoin... pointOEventJoins);

    @Query("DELETE FROM point_oevent_join WHERE point_oevent_join.pointID =:pointID " +
            "AND point_oevent_join.oEventID = :oEventID")
    int delete(Integer pointID, int oEventID);

    @Delete
    int delete(PointOEventJoin... pointOEventJoins);

}
