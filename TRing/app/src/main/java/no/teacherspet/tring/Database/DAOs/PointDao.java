package no.teacherspet.tring.Database.DAOs;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Maybe;
import no.teacherspet.tring.Database.Entities.RoomPoint;

/**
 * Created by Hermann on 13.02.2018.
 */
@Dao
public interface PointDao {

    @Query("SELECT * FROM point")
    Maybe<List<RoomPoint>> getAll();

    @Query("SELECT * FROM point WHERE id LIKE :id")
    Maybe<RoomPoint> findById(int id);

    @Query("SELECT MAX(id) FROM point")
    Maybe<Integer> getMaxID();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insert(RoomPoint... roomPoints);

    @Update
    void update(RoomPoint... roomPoints);

    @Query("DELETE FROM point WHERE id = :pointID")
    int deletePoint(int pointID);

    @Delete
    int delete(RoomPoint... roomPoints);

}
