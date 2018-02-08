package com.wangwenjun.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Administrator on 2018/1/4.
 */

public class CrimeListActivity extends SingleFragmentActivity
                                    implements CrimeListFragment.Callbacks, CrimeFragment.Callbacks{

    private TextView mBlankTextView;
    private Button mNewCrimeButton;

    //分为左右两边
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(CrimeLab.get(this).getCrimes().size() == 0){
            //mBlankTextView.setVisibility(View.VISIBLE);
            //mBlankTextView.setText(R.string.blank_text);

        }else {
            //mBlankTextView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBlankTextView = (TextView) findViewById(R.id.blank_text);

    }

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    //回调实现分屏
    @Override
    public void onCrimeSelected(Crime crime) {
        if(findViewById(R.id.detail_fragment_container) == null){
            Intent intent = CrimePagerActivity.newIntent(this, crime.getId());
            startActivity(intent);
        }else {
            Fragment newDetail = CrimeFragment.newInstance(crime.getId());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }
    }

    @Override
    public void onCrimeUpdated(Crime crime) {
        CrimeListFragment listFragment = (CrimeListFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }
}
