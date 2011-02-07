package com.myapp.jsf.bibliothek;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class DB {

    private static DB instance;
    private static Random random = new Random();

    public static DB getInstance() {
        if (instance == null)
            instance = new DB();
        return instance;
    }

    private Collection<Book> books;

    private DB() {
        books = new ArrayList<Book>();
        books.add(new Book(random.nextLong(), "Micheal Jackson", "J2EE for professionals", true));
        books.add(new Book(random.nextLong(), "Bruce Lee", "JSF for beginners", false));
        books.add(new Book(random.nextLong(), "Tom Jones", "EJB book", true));
        books.add(new Book(random.nextLong(), "Mc Donald", "Jboss for beginners", false));
        books.add(new Book(random.nextLong(), "Mary Jane", "EJB or spending your weekends", true));
    }

    public long saveBook(Book book) {
        boolean bookexists = false;

        for (Book b : books)
            if (b.getId() == book.getId()) {
                b.setBook(book); // mach ein update...
                bookexists = true;
                break;
            }

        if ( ! bookexists) {
            book.setId(random.nextLong());
            books.add(book);
        }

        return book.getId();
    }

    public Book getBookById(long id) {

        for (Book b : books)
            if (b.getId() == id)
                return b;

        return null;
    }

    public void deleteBookById(long id) {

        for (Book b : books)
            if (b.getId() == id) {
                books.remove(b);
                break;
            }
    }

    public Collection<Book> getAllBooks() {
        return books;
    }
}
