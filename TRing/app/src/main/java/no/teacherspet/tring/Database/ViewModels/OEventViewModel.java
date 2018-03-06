package no.teacherspet.tring.Database.ViewModels;

import android.arch.lifecycle.ViewModel;

import no.teacherspet.tring.Database.DAOs.OEventDao;

/**
 * Created by Hermann on 05.03.2018.
 */

public class OEventViewModel extends ViewModel {

    private OEventDao oEventDao;

    public OEventViewModel(OEventDao oEventDao) {
        this.oEventDao = oEventDao;
    }

}
