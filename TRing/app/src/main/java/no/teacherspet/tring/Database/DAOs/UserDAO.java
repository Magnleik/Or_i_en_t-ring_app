package no.teacherspet.tring.Database.DAOs;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import no.teacherspet.tring.Database.Entities.User;

/**
 * Created by Hermann on 13.02.2018.
 */
@Dao
public interface UserDAO {

    @Query("SELECT * FROM User")
    List<User> getAll();

    @Query("SELECT * FROM user WHERE personal_profile LIKE 0")
    List<User> getAllOtherUsers();

    @Query("SELECT * FROM user WHERE user_id LIKE :UserID")
    User findById(int UserID);

    @Query("SELECT * FROM user WHERE personal_profile LIKE 1")
    User getPersonalProfile();

    @Insert
    void insert(User... users);

    @Update
    void update(User... users);

    @Delete
    void delete(User... Users);

}
