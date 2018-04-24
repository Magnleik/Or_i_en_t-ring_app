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
import no.teacherspet.tring.Database.Entities.RoomUser;

/**
 * Created by Hermann on 13.02.2018.
 */
@Dao
public interface UserDao {

    @Query("SELECT * FROM user")
    Maybe<List<RoomUser>> getAll();

    @Query("SELECT * FROM user WHERE personalProfile LIKE 0")
    Maybe<List<RoomUser>> getOtherUsers();

    @Query("SELECT * FROM user WHERE personalProfile LIKE 1")
    Maybe<List<RoomUser>> getPersonalUser();

    @Query("SELECT * FROM user WHERE id LIKE :id")
    Maybe<RoomUser> findById(int id);

    @Query("SELECT MAX(id) FROM user")
    Maybe<Integer> getMaxID();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insert(RoomUser... roomUsers);

    @Delete
    int delete(RoomUser... roomUsers);

    @Update
    void update(RoomUser... roomUsers);

}
