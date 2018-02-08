package com.wangwenjun.booklist;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Administrator on 2018/2/2.
 */

public class BookManagerService extends Service {
    private final String TAG = this.getClass().getSimpleName();

    //CopyOnWriteArrayList支持并发读写
    public static CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<Book>();
    private  IBinder mBinder = new IBookManager.Stub(){

        @Override
        public List<Book> getBookList() throws RemoteException {
            if(mBookList != null) {
                return mBookList;
            }
            return new CopyOnWriteArrayList<Book>();
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            mBookList.add(book);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mBookList.add(new Book(1, "Android"));
        mBookList.add(new Book(2, "IOS"));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;
    }
}
