package com.wangwenjun.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.nio.BufferUnderflowException;

/**
 * Created by Administrator on 2018/1/4.
 */

public class CrimeListActivity extends SingleFragmentActivity {

    private TextView mBlankTextView;
    private Button mNewCrimeButton;

    @Override
    protected void onResume() {
        super.onResume();
        if(CrimeLab.get(this).getCrimes().size() == 0){
            mBlankTextView.setVisibility(View.VISIBLE);
            mBlankTextView.setText(R.string.blank_text);

        }else {
            mBlankTextView.setVisibility(View.INVISIBLE);
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
}
