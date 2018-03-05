package no.teacherspet.tring.Database.DAOs;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;


/**
 * Created by Hermann on 20.02.2018.
 */
@Dao
public interface DeleteDao {

    @Query("DELETE FROM point")
    void deleteAllPoints();

    @Query("DELETE FROM user")
    void deleteAllUsers();

    @Query("DELETE FROM o_event")
    void deleteAllOEvents();

    @Query("DELETE FROM point_oevent_join")
    void deleteAllJoins();

}
