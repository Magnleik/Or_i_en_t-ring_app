package no.teacherspet.tring.Database.ViewModels;

import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;

import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import no.teacherspet.tring.Database.DAOs.OEventDao;
import no.teacherspet.tring.Database.Entities.RoomOEvent;

/**
 * Created by Hermann on 05.03.2018.
 */

public class OEventViewModel extends ViewModel {

    private OEventDao oEventDao;

    public OEventViewModel(OEventDao oEventDao) {
        this.oEventDao = oEventDao;
    }

    public Maybe<ArrayList<RoomOEvent>> getAllOEvents(){
        return oEventDao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Maybe<RoomOEvent> getOEventByID(int id){
        return oEventDao.findById(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Maybe<Integer> getMaxID(){
        return oEventDao.getMaxID().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).defaultIfEmpty(-1);
    }

    public Single<Integer> deleteOEvents(RoomOEvent... roomOEvents){
        return Single.fromCallable(()->oEventDao.delete(roomOEvents))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<long[]> addOEvents(RoomOEvent... roomOEvents){
        return Single.fromCallable(()->oEventDao.insert(roomOEvents))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
