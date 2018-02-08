package com.wangwenjun.photogallery;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.LruCache;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/19.
 */

public class PhotoGalleryFragment extends VisibleFragment {

    private static final int MAXCACHE = 10*4*1024*1024;
    private static final String TAG = "PhotoGalleryFragment";

    private InputMethodManager mInputMethodManager;
    private SearchView mSearchView;
    public ProgressDialog mProgressDialog;

    private RecyclerView mPhotoRecycleView;
    private static FetchItemTask mFetchItemTask;
    private static LruCache<PhotoHolder, Drawable> mLruCache = new LruCache<>(MAXCACHE);
    private List<GalleryItem> mItems = new ArrayList<>();
    private ThumbnailDownloader<PhotoHolder> mThumbnailDownloader;

    public static Fragment newInstance(){
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        mFetchItemTask = updateItems();

        Handler responseHandle = new Handler();
        mThumbnailDownloader = new ThumbnailDownloader<>(responseHandle);
        mThumbnailDownloader.setThumbnailDownloadListener(new ThumbnailDownloader.ThumbnailDownloadListener<PhotoHolder>() {
            @Override
            public void onThumbnailDownloaded(PhotoHolder target, Bitmap thumbnail) {
                Drawable drawable;
                //mLruCache是缓存list
                if (mLruCache.get(target) == null) {
                    drawable = new BitmapDrawable(getResources(), thumbnail);
                    mLruCache.put(target, drawable);
                }else{
                    drawable = mLruCache.get(target);
                }
                target.bindDrawable(drawable);
            }
        });
        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();
        Log.i(TAG, "Background thread started");

        mInputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);



    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        mPhotoRecycleView = (RecyclerView) view.findViewById(R.id.fragment_photo_gallery_recycle_view);

        mPhotoRecycleView.setLayoutManager(new GridLayoutManager(getActivity(),3 ));
        
        setupAdapter();

        return view;
    }

    //搜索菜单
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_photo_gallery, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        mSearchView = (SearchView) searchItem.getActionView();

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "QueryTextSubmit: " + query);
                QueryPreferences.setStoredQuery(getActivity(), query);
                updateItems();
                hintSoftInputAndSearchField();

                //显示状态界面
                mProgressDialog = new ProgressDialog(getActivity());
                mProgressDialog.setMessage("loading...");
                mProgressDialog.show();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Log.d(TAG, "QueryTextChange: " + newText);
                return false;
            }
        });

        //搜索栏缓存显示
        mSearchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = QueryPreferences.getStoreQuery(getActivity());
                mSearchView.setQuery(query, false);
            }
        });

        MenuItem toggleItem = menu.findItem(R.id.menu_item_toggle_polling);
        if (PollService.isServiceAlarmOn(getActivity())){
            toggleItem.setTitle(R.string.stop_polling);
        }else {
            toggleItem.setTitle(R.string.start_polling);
        }
    }

    private void hintSoftInputAndSearchField(){
        View v = getActivity().getCurrentFocus();
        if(v == null || mSearchView == null){
            return;
        }
        mInputMethodManager.hideSoftInputFromWindow(v.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS); //折叠软键盘
        mSearchView.clearFocus();
        mSearchView.onActionViewCollapsed();  //收起SearchView的方法。
    }

    //溢出菜单
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_clear:
                QueryPreferences.setStoredQuery(getActivity(), null);
                updateItems();
                return true;
            case R.id.menu_item_toggle_polling:
                boolean shouldStartAlarm = !PollService.isServiceAlarmOn(getActivity());
                PollService.setServiceAlarm(getActivity(), shouldStartAlarm);
                getActivity().invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private FetchItemTask updateItems() {
        String query = QueryPreferences.getStoreQuery(getActivity());
        FetchItemTask fetchItemTask = new FetchItemTask(query);
        fetchItemTask.execute();
        return fetchItemTask;
    }

    private void setupAdapter() {

        /*配置adapter前，应检查isAdded()的返回值是否为true。该检查确认fragment已与目
            标activity相关联，进而保证getActivity()方法返回结果不为空。
            既然在用AsyncTask，我们就正在从后台进程触发回调指令。因而不能确定fragment是否关
            联着activity。必须检查确认fragment是否仍与activity关联。*/
        if(isAdded()){
            mPhotoRecycleView.setAdapter(new PhotoAdapter(mItems));
        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder
                implements View.OnClickListener{
        private ImageView mItemImageView;
        private GalleryItem mGalleryItem;

        public PhotoHolder(View itemView) {
            super(itemView);
            mItemImageView = (ImageView) itemView.findViewById(R.id.fragment_photo_gallery_image_view);
            itemView.setOnClickListener(this);
        }

        public void bindDrawable(Drawable drawable){
            mItemImageView.setImageDrawable(drawable);
        }

        public void bindGalleryItem(GalleryItem galleryItem){
            mGalleryItem = galleryItem;
        }

        @Override
        public void onClick(View v) {

            Intent i = PhotoPageActivity.newIntent(getActivity(), mGalleryItem.getPhotoPageUri());
            //弃用隐式开启自带浏览器
            // Intent i = new Intent(Intent.ACTION_VIEW, mGalleryItem.getPhotoPageUri());
            startActivity(i);
        }
    }

    private  class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder>{
        private List<GalleryItem> mGalleryItems;

        public PhotoAdapter(List<GalleryItem> galleryItems) {
            mGalleryItems = galleryItems;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.gallery_item, parent, false);
            return new PhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {
            GalleryItem galleryItem = mGalleryItems.get(position);
            holder.bindGalleryItem(galleryItem);
            Drawable placeholder = getResources().getDrawable(R.drawable.ic_menu_gallery);
            holder.bindDrawable(placeholder);
            mThumbnailDownloader.queueThumbnail(holder, galleryItem.getUrl());
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }

    //开启线程
    private class FetchItemTask extends AsyncTask<Void, Void,List<GalleryItem>>{
        private String mQuery;
        private FetchItemTask(String query){
            mQuery =query;
        }
        @Override
        protected List<GalleryItem> doInBackground(Void... params) {

            if (mQuery == null){
                return new FickrFetchr().fetchRecentPhotos();
            }else {
                return new FickrFetchr().searchPhotos(mQuery);
            }
        }
        //为避免安全隐患，不推荐也不允许从后台线程更新UI。

        //此接口可以做到在在doInBackground(...)方法执行完毕后才会运行。更为重要的
        //是，它是在主线程而非后台线程上运行的。
        @Override
        protected void onPostExecute(List<GalleryItem> galleryItems) {
            mItems =galleryItems;
            if(mItems.size() > 0 && mProgressDialog != null){
                mProgressDialog.dismiss();
            }
            setupAdapter();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mThumbnailDownloader.quit();
        Log.i(TAG, "Background thread destroyed");

        //不停止线程的话会引发潜在的内存泄漏，也可能会出现UI更新问题（因为UI已失效）
        mFetchItemTask.cancel(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mThumbnailDownloader.clearQueue();
    }
}
