package no.teacherspet.tring.Database.ViewModels;

import android.arch.lifecycle.ViewModel;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import no.teacherspet.tring.Database.DAOs.OEventDao;
import no.teacherspet.tring.Database.Entities.OEvent;

/**
 * Created by Hermann on 05.03.2018.
 */

public class OEventViewModel extends ViewModel {

    private OEventDao oEventDao;

    public OEventViewModel(OEventDao oEventDao) {
        this.oEventDao = oEventDao;
    }

    public Flowable<List<OEvent>> getAllOEvents(){
        return oEventDao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Flowable<OEvent> getOEventByID(int id){
        return oEventDao.findById(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Flowable<Integer> deleteOEvents(OEvent... oEvents){
        return Flowable.fromCallable(()->oEventDao.delete(oEvents))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Flowable<long[]> addOEvents(OEvent... oEvents){
        return Flowable.fromCallable(()->oEventDao.insert(oEvents))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
