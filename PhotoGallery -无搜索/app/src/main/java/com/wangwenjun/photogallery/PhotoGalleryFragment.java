package com.wangwenjun.photogallery;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/19.
 */

public class PhotoGalleryFragment extends Fragment {

    private static final int MAXCACHE = 10*4*1024*1024;
    private static final String TAG = "PhotoGalleryFragment";

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
        mFetchItemTask = new FetchItemTask();
        mFetchItemTask.execute();

        Handler responseHandle = new Handler();
        mThumbnailDownloader = new ThumbnailDownloader<>(responseHandle);
        mThumbnailDownloader.setThumbnailDownloadListener(new ThumbnailDownloader.ThumbnailDownloadListener<PhotoHolder>() {
            @Override
            public void onThumbnailDownloaded(PhotoHolder target, Bitmap thumbnail) {
                Drawable drawable;
                if (mLruCache.get(target) == null) {
                    drawable = new BitmapDrawable(getResources(), thumbnail);
                    mLruCache.put(target, drawable);
                }else{
                    drawable = mLruCache.get(target);
                }
                //Log.i(TAG, "drawable width is" + thumbnail.getWidth() + ", height" + thumbnail.getHeight());
                target.bindGalleryItem(drawable);
            }
        });
        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();
        Log.i(TAG, "Background thread started");
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

    private void setupAdapter() {

        /*配置adapter前，应检查isAdded()的返回值是否为true。该检查确认fragment已与目
            标activity相关联，进而保证getActivity()方法返回结果不为空。
            既然在用AsyncTask，我们就正在从后台进程触发回调指令。因而不能确定fragment是否关
            联着activity。必须检查确认fragment是否仍与activity关联。*/
        if(isAdded()){
            mPhotoRecycleView.setAdapter(new PhotoAdapter(mItems));
        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder{
        private ImageView mItemImageView;

        public PhotoHolder(View itemView) {
            super(itemView);
            mItemImageView = (ImageView) itemView.findViewById(R.id.fragment_photo_gallery_image_view);
        }

        public void bindGalleryItem(Drawable drawable){
            mItemImageView.setImageDrawable(drawable);
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
            Drawable placeholder = getResources().getDrawable(R.drawable.ic_launcher);
            holder.bindGalleryItem(placeholder);
            mThumbnailDownloader.queueThumbnail(holder, galleryItem.getUrl());
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }

    //开启线程
    private class FetchItemTask extends AsyncTask<Void, Void,List<GalleryItem>>{

        @Override
        protected List<GalleryItem> doInBackground(Void... params) {
            return new FickrFetchr().fetchItems();
        }
        //为避免安全隐患，不推荐也不允许从后台线程更新UI。

        //此接口可以做到在在doInBackground(...)方法执行完毕后才会运行。更为重要的
        //是，它是在主线程而非后台线程上运行的。
        @Override
        protected void onPostExecute(List<GalleryItem> galleryItems) {
            mItems =galleryItems;
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
