package no.teacherspet.tring.Database.ViewModels;

import android.arch.lifecycle.ViewModel;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import no.teacherspet.tring.Database.DAOs.PointOEventJoinDao;
import no.teacherspet.tring.Database.Entities.OEvent;
import no.teacherspet.tring.Database.Entities.Point;
import no.teacherspet.tring.Database.Entities.PointOEventJoin;

/**
 * Created by Hermann on 05.03.2018.
 */

public class PointOEventJoinViewModel extends ViewModel {

    private PointOEventJoinDao pointOEventJoinDao;

    public PointOEventJoinViewModel(PointOEventJoinDao pointOEventJoinDao) {
        this.pointOEventJoinDao = pointOEventJoinDao;
    }

    public Flowable<List<Point>> getPointsForOEvent(int oEventID){
        return pointOEventJoinDao.getPointsForOEvent(oEventID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    public Flowable<Point> getStartPoint(int oEventID){
        return pointOEventJoinDao.getStartPoint(oEventID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    public Flowable<List<Point>> getPointsNotStart(int oEventID){
        return pointOEventJoinDao.getPointsNotStart(oEventID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    public Flowable<List<OEvent>> getOEventsForPoint(int pointID){
        return pointOEventJoinDao.getOEventsForPoint(pointID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Flowable<Integer> deleteJoin(PointOEventJoin... pointOEventJoins){
        return Flowable.fromCallable(()->pointOEventJoinDao.delete(pointOEventJoins))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Flowable<long[]> addJoin(PointOEventJoin... pointOEventJoins){
        return Flowable.fromCallable(()->pointOEventJoinDao.insert(pointOEventJoins))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
