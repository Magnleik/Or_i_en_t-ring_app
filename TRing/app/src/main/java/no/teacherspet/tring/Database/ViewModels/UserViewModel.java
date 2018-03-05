package no.teacherspet.tring.Database.ViewModels;

import android.arch.lifecycle.ViewModel;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
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

    public Flowable<List<User>> getAllUsers(){
        return userDao.getAll();
    }

    public Flowable<User> getUserbyID(int userID){
        return userDao.findById(userID);
    }

    public Flowable<List<User>> getOtherUsers(){
        return userDao.getOtherUsers();
    }

    public Flowable<User> getPersonalUser(){
        return userDao.getPersonalUser();
    }

    public Flowable<Integer> deleteUsers(User... users){
        return Flowable.fromCallable(()->userDao.delete(users));

        /*
        return Flowable.fromCallable(()->userDao.delete(users))
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
         */

    }
    public Flowable<long[]> addUsers(User... users){
        return Flowable.fromCallable(()->userDao.insert(users));
    /*
    return Flowable.fromCallable(()->userDao.insert(users))
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
     */
    }


}