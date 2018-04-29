package no.teacherspet.tring.Database.ViewModels;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import no.teacherspet.tring.Database.DAOs.ResultDAO;
import no.teacherspet.tring.Database.Entities.EventResult;

/**
 * Created by Hermann on 29.04.2018.
 */

public class ResultViewModel {
    private ResultDAO resultDAO;

    public ResultViewModel(ResultDAO resultDAO){
        this.resultDAO = resultDAO;
    }

    public Maybe<List<EventResult>> getAllResults(){
        return resultDAO.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    public Maybe<List<EventResult>> getResult(int resultID){
        return resultDAO.getResult(resultID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    public Single<long[]> addResults(EventResult... eventResults){
        return Single.fromCallable(()->resultDAO.insert(eventResults))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    public Single<Integer> deleteResult(int resultID){
        return Single.fromCallable(()->resultDAO.deleteResult(resultID))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }




}
