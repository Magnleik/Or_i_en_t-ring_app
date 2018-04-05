package no.teacherspet.tring.Database.ViewModels;

import android.arch.lifecycle.ViewModel;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import no.teacherspet.tring.Database.DAOs.UserDao;
import no.teacherspet.tring.Database.Entities.User;

/**
 * Created by Hermann on 23.02.2018.
 */

public class UserViewModel extends ViewModel {

    private UserDao userDao;

    public UserViewModel(UserDao userDao) {
        this.userDao = userDao;
    }

    public Maybe<List<User>> getAllUsers(){
        return userDao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Maybe<User> getUserByID(int userID){
        return userDao.findById(userID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Maybe<List<User>> getOtherUsers(){
        return userDao.getOtherUsers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Maybe<User> getPersonalUser(){
        return userDao.getPersonalUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Maybe<Integer> getMaxID(){
        return userDao.getMaxID().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).defaultIfEmpty(-1);
    }

    public Single<Integer> deleteUsers(User... users){
        return Single.fromCallable(()->userDao.delete(users))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<long[]> addUsers(User... users){
        return Single.fromCallable(()->userDao.insert(users))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
