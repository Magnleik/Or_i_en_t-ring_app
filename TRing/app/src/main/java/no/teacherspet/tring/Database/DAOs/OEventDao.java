package no.teacherspet.tring.Database.DAOs;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import io.reactivex.Maybe;
import no.teacherspet.tring.Database.Entities.OEvent;

/**
 * Created by Hermann on 13.02.2018.
 */
@Dao
public interface OEventDao {

    @Query("SELECT * FROM o_event")
    Maybe<List<OEvent>> getAll();

    @Query("SELECT * FROM o_event WHERE id LIKE :id")
    Maybe<OEvent> findById(int id);

    @Query("SELECT MAX(id) FROM o_event")
    Maybe<Integer> getMaxID();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insert(OEvent... oEvents);

    @Update
    void update(OEvent... oEvents);

    @Delete
    int delete(OEvent... oEvents);
}
