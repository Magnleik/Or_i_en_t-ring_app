package no.teacherspet.tring.Database.ViewModels;

import android.arch.lifecycle.ViewModel;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;
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

    public Maybe<List<Point>> getPointsForOEvent(int oEventID){
        return pointOEventJoinDao.getPointsForOEvent(oEventID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    public Maybe<Point> getStartPoint(int oEventID){
        return pointOEventJoinDao.getStartPoint(oEventID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    public Maybe<List<Point>> getPointsNotStart(int oEventID){
        return pointOEventJoinDao.getPointsNotStart(oEventID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    public Maybe<List<OEvent>> getOEventsForPoint(int pointID){
        return pointOEventJoinDao.getOEventsForPoint(pointID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<long[]> addJoin(PointOEventJoin... pointOEventJoins){
        return Single.fromCallable(()->pointOEventJoinDao.insert(pointOEventJoins))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Integer> deleteJoin(Point point, OEvent oEvent){
        return Single.fromCallable(()->pointOEventJoinDao.delete(point.getId(), oEvent.getId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Integer> deleteJoin(PointOEventJoin... pointOEventJoins){
        return Single.fromCallable(()->pointOEventJoinDao.delete(pointOEventJoins))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
