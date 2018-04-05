package no.teacherspet.tring.Database.ViewModels;

import android.arch.lifecycle.ViewModel;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import no.teacherspet.tring.Database.DAOs.PointDao;
import no.teacherspet.tring.Database.Entities.Point;

/**
 * Created by Hermann on 05.03.2018.
 */

public class PointViewModel extends ViewModel {

    private PointDao pointDao;

    public PointViewModel(PointDao pointDao) {
        this.pointDao = pointDao;
    }

    public Maybe<List<Point>> getAllPoints(){
        return pointDao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Maybe<Point> getPointByID(int pointID){
        return pointDao.findById(pointID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Maybe<Integer> getMaxID(){
        return pointDao.getMaxID().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).defaultIfEmpty(-1);
    }

    public Single<Integer> deletePoints(Point... points){
        return Single.fromCallable(()->pointDao.delete(points))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<long[]> addPoints(Point... points){
        return Single.fromCallable(()->pointDao.insert(points))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
