package no.teacherspet.tring.Database.ViewModels;

import android.arch.lifecycle.ViewModel;

import java.util.List;

import io.reactivex.Flowable;
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

    public Flowable<List<Point>> getAllPoints(){
        return pointDao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Flowable<Point> getPointByID(int pointID){
        return pointDao.findById(pointID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Flowable<Integer> deletePoints(Point... points){
        return Flowable.fromCallable(()->pointDao.delete(points))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Flowable<long[]> addPoints(Point... points){
        return Flowable.fromCallable(()->pointDao.insert(points))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
