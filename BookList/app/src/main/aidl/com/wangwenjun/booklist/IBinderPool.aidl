// IBinderPool.aidl
package com.wangwenjun.booklist;

// Declare any non-default types here with import statements

interface IBinderPool {
    IBinder queryBinder(int binderCode);
}
