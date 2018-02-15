package no.teacherspet.tring.Database.DAOs;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import no.teacherspet.tring.Database.Entities.OEvent;

/**
 * Created by Hermann on 13.02.2018.
 */
@Dao
public interface OEventDAO {

    @Query("SELECT * FROM OEvent")
    List<OEvent> getAll();

    @Query("SELECT * FROM point WHERE o_event_id LIKE :oEventID")
    OEvent findById(int oEventID);

    @Insert
    void insert(OEvent... oEvents);

    @Update
    void update(OEvent... oEvents);

    @Delete
    void delete(OEvent... oEvents);
}
