package no.teacherspet.tring.Database.DAOs;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;
import no.teacherspet.tring.Database.Entities.User;

/**
 * Created by Hermann on 13.02.2018.
 */
@Dao
public interface UserDao {

    @Query("SELECT * FROM user")
    Flowable<List<User>> getAll();

    @Query("SELECT * FROM user WHERE personalProfile LIKE 0")
    Flowable<List<User>> getOtherUsers();

    @Query("SELECT * FROM user WHERE personalProfile LIKE 1")
    Flowable<User> getPersonalUser();

    @Query("SELECT * FROM user WHERE id LIKE :id")
    Flowable<User> findById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insert(User... users);

    @Delete
    int delete(User... users);

    @Update
    void update(User... users);

}
