// IBookManager.aidl
package com.wangwenjun.booklist;

import com.wangwenjun.booklist.Book;

// Declare any non-default types here with import statements

interface IBookManager {
    List<Book> getBookList();
    void addBook(in Book book);
}
