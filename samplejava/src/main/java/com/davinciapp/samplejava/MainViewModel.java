package com.davinciapp.samplejava;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

import io.axept.android.library.AxeptioService;
import io.axept.android.model.AxeptioCookies;

public class MainViewModel extends ViewModel {

    private SharedPreferencesRepository prefRepo;

    MainViewModel(SharedPreferencesRepository prefRepo) {
        this.prefRepo = prefRepo;
    }

    private final MutableLiveData<Boolean> _loadingAd = new MutableLiveData<Boolean>(false);
    LiveData<Boolean> loadingAd = _loadingAd;

    void setAdLoading(Boolean loading) {
        _loadingAd.setValue(loading);
    }


    ArrayList<PrefencesItemUI> getSharedPreferences() {
        ArrayList<PrefencesItemUI> items = new ArrayList<>();

        if (BuildConfig.AXEPTIO_TARGET_SERVICE == "publishers") {
            for (TCFFields field : TCFFields.values()) {
                String value = prefRepo.get(field.key, field.type);
                if (value.length() > 100) {
                    value = value.subSequence(0, 100) + "...";
                }

                items.add(
                        new PrefencesItemUI(field.key, value)
                );
            }
        } else if (BuildConfig.AXEPTIO_TARGET_SERVICE == "brands") {
            for (COOKIE_FIELDS field : COOKIE_FIELDS.values()) {
                String value = prefRepo.get(field.getKey(), String.class);
                items.add(new PrefencesItemUI(field.getKey(), value));
            }
        }

        return items;
    }

    static class Factory implements ViewModelProvider.Factory {

        private Application app;

        Factory(Application app) {
            this.app = app;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new MainViewModel(new SharedPreferencesRepositoryImpl(app));
        }
    }
}
