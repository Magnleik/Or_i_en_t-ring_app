package no.teacherspet.tring.Database.DAOs;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
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
    List<Point> getAll();

    @Query("SELECT * FROM point WHERE point_id LIKE :pointID")
    Point findById(int pointID);

    @Insert
    void insert(Point... points);

    @Update
    void update(Point... points);

    @Delete
    void delete(Point... points);

}
