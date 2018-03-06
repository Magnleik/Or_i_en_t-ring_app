package no.teacherspet.tring.Database.ViewModels;

import android.arch.lifecycle.ViewModel;

import no.teacherspet.tring.Database.DAOs.PointOEventJoinDao;

/**
 * Created by Hermann on 05.03.2018.
 */

public class PointOEventJoinViewModel extends ViewModel {

    private PointOEventJoinDao pointOEventJoinDao;

    public PointOEventJoinViewModel(PointOEventJoinDao pointOEventJoinDao) {
        this.pointOEventJoinDao = pointOEventJoinDao;
    }

}
