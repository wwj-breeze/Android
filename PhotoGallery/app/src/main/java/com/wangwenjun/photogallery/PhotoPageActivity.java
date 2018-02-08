package com.wangwenjun.photogallery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;

/**
 * Created by Administrator on 2018/2/6.
 */

public class PhotoPageActivity extends SingleFragmentActivity {

    public static Intent newIntent(Context context, Uri uri){
        Intent i = new Intent(context, PhotoPageActivity.class);
        i.setData(uri);
        return i;
    }

    @Override
    protected Fragment createFragment() {
        return PhotoPageFragment.newInstance(getIntent().getData());

    }

    @Override
    public void onBackPressed() {
        if (!PhotoPageFragment.webCanGoBack()) {
            super.onBackPressed();
        }
    }
}
