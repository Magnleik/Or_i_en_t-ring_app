package no.teacherspet.tring.Database.DAOs;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import io.reactivex.Flowable;
import no.teacherspet.tring.Database.Entities.Point;

/**
 * Created by Hermann on 13.02.2018.
 */
@Dao
public interface PointDao {

    @Query("SELECT * FROM point")
    Flowable<List<Point>> getAll();

    @Query("SELECT * FROM point WHERE id LIKE :id")
    Flowable<Point> findById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insert(Point... points);

    @Update
    void update(Point... points);

    @Delete
    int delete(Point... points);

}
