package no.teacherspet.tring.Database.ViewModels;

import android.arch.lifecycle.ViewModel;

import no.teacherspet.tring.Database.DAOs.PointDao;

/**
 * Created by Hermann on 05.03.2018.
 */

public class PointViewModel extends ViewModel {

    private PointDao pointDao;

    public PointViewModel(PointDao pointDao) {
        this.pointDao = pointDao;
    }

}
