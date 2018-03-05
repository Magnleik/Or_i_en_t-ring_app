package no.teacherspet.tring.Database.DAOs;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Flowable;
import no.teacherspet.tring.Database.Entities.OEvent;
import no.teacherspet.tring.Database.Entities.Point;
import no.teacherspet.tring.Database.Entities.PointOEventJoin;

/**
 * Created by Hermann on 15.02.2018.
 */
@Dao
public interface PointOEventJoinDao {

    @Query("SELECT * FROM point INNER JOIN point_oevent_join ON point.id = point_oevent_join.pointID" +
            " WHERE point_oevent_join.oEventID = :oEventID")
    Flowable<List<Point>> getPointsForOEvent(int oEventID);

    @Query("SELECT * FROM point INNER JOIN point_oevent_join ON point.id = point_oevent_join.pointID" +
            " WHERE point_oevent_join.oEventID = :oEventID AND point_oevent_join.isStart = 1")
    Flowable<Point> getStartPoint(int oEventID);

    @Query("SELECT * FROM point INNER JOIN point_oevent_join ON point.id = point_oevent_join.pointID" +
            " WHERE point_oevent_join.oEventID = :oEventID AND point_oevent_join.isStart = 0")
    Flowable<List<Point>> getPointsNotStart(int oEventID);

    @Query("SELECT * FROM o_event INNER JOIN point_oevent_join ON o_event.id = point_oevent_join.oEventID" +
            " WHERE point_oevent_join.pointID = :pointID")
    Flowable<List<OEvent>> getOEventsForPoint(int pointID);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insert(PointOEventJoin... pointOEventJoins);

    @Delete
    void delete(PointOEventJoin... pointOEventJoins);

}
