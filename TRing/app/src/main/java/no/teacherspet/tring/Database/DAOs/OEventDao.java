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
import no.teacherspet.tring.Database.Entities.OEvent;

/**
 * Created by Hermann on 13.02.2018.
 */
@Dao
public interface OEventDao {

    @Query("SELECT * FROM o_event")
    Flowable<List<OEvent>> getAll();

    @Query("SELECT * FROM o_event WHERE id LIKE :id")
    Flowable<OEvent> findById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insert(OEvent... oEvents);

    @Update
    void update(OEvent... oEvents);

    @Delete
    void delete(OEvent... oEvents);
}
