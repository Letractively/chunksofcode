package com.myapp.jsf.bibliothek;

import java.util.ArrayList;
import java.util.Collection;

public class BookList {

    private Collection<Book> bookList = new ArrayList<Book>();

    public Collection<Book> getBooks() {
        DB db = DB.getInstance();
        bookList = db.getAllBooks();
        return bookList;
    }

    public void setBooks(Collection<Book> books) {
        bookList = books;
    }
}
