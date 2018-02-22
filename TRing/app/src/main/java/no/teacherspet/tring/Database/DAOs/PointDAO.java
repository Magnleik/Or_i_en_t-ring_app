package no.teacherspet.tring.Database.DAOs;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import no.teacherspet.tring.Database.Entities.Point;

/**
 * Created by Hermann on 13.02.2018.
 */
@Dao
public interface PointDAO {

    @Query("SELECT * FROM point")
    LiveData<List<Point>> getAll();

    @Query("SELECT * FROM point WHERE id LIKE :id")
    LiveData<Point> findById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insert(Point... points);

    @Update
    void update(Point... points);

    @Delete
    void delete(Point... points);

}
