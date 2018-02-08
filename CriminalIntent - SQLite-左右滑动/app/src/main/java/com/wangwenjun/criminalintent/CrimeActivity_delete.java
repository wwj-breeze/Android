package com.wangwenjun.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.UUID;

public class CrimeActivity_delete extends SingleFragmentActivity {

    private   static final String EXTRA_CRIME_ID =
            "com.bignerdranch.android.criminalintent.crime_id";

    public static Intent newIntent(Context packageContext, UUID crimedId) {
        Intent intent = new Intent(packageContext, CrimeActivity_delete.class);
        intent.putExtra(EXTRA_CRIME_ID, crimedId);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        UUID crimeId  = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        return CrimeFragment.newInstance(crimeId);
    }
}
