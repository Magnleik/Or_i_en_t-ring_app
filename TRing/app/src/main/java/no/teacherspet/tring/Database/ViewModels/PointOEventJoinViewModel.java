package no.teacherspet.tring.Database.ViewModels;

import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import no.teacherspet.tring.Database.DAOs.PointOEventJoinDao;
import no.teacherspet.tring.Database.Entities.RoomOEvent;
import no.teacherspet.tring.Database.Entities.RoomPoint;
import no.teacherspet.tring.Database.Entities.PointOEventJoin;

/**
 * Created by Hermann on 05.03.2018.
 */

public class PointOEventJoinViewModel extends ViewModel {

    private PointOEventJoinDao pointOEventJoinDao;

    public PointOEventJoinViewModel(PointOEventJoinDao pointOEventJoinDao) {
        this.pointOEventJoinDao = pointOEventJoinDao;
    }

    public Maybe<List<RoomPoint>> getPointsForOEvent(int oEventID){
        return pointOEventJoinDao.getPointsForOEvent(oEventID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    public Maybe<RoomPoint> getStartPoint(int oEventID){
        return pointOEventJoinDao.getStartPoint(oEventID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    public Maybe<List<RoomPoint>> getPointsNotStart(int oEventID){
        return pointOEventJoinDao.getPointsNotStart(oEventID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    public Maybe<List<RoomOEvent>> getOEventsForPoint(int pointID){
        return pointOEventJoinDao.getOEventsForPoint(pointID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<long[]> addJoin(PointOEventJoin... pointOEventJoins){
        return Single.fromCallable(()->pointOEventJoinDao.insert(pointOEventJoins))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Integer> deleteJoin(RoomPoint roomPoint, RoomOEvent roomOEvent){
        return Single.fromCallable(()->pointOEventJoinDao.delete(roomPoint.getId(), roomOEvent.getId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Integer> deleteJoin(PointOEventJoin... pointOEventJoins){
        return Single.fromCallable(()->pointOEventJoinDao.delete(pointOEventJoins))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
