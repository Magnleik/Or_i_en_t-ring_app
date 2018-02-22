package no.teacherspet.tring.Database.DAOs;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import no.teacherspet.tring.Database.Entities.User;

/**
 * Created by Hermann on 13.02.2018.
 */
@Dao
public interface UserDAO {

    @Query("SELECT * FROM user")
    LiveData<List<User>> getAll();

    @Query("SELECT * FROM user WHERE personalProfile LIKE 0")
    LiveData<List<User>> getOtherUsers();

    @Query("SELECT * FROM user WHERE personalProfile LIKE 1")
    LiveData<User> getPersonalUser();

    @Query("SELECT * FROM user WHERE id LIKE :id")
    LiveData<User> findById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insert(User... users);

    @Update
    void update(User... users);

    @Delete
    void delete(User... Users);

}
