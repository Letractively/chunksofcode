package com.myapp.bookstore.jsp;

public class Buch {

    private long id = 0;
    private String autor = "";
    private String titel = "";
    private boolean verfuegbar = false;

    public Buch(long id, String autor, String titel, boolean verfuegbar) {
        super();
        this.id = id;
        this.autor = autor;
        this.titel = titel;
        this.verfuegbar = verfuegbar;
    }

    public Buch() {
    }

    public long getId() {
        return id;
    }

    public void setId(long isbn) {
        id = isbn;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public boolean isVerfuegbar() {
        return verfuegbar;
    }

    public void setVerfuegbar(boolean verfuegbar) {
        this.verfuegbar = verfuegbar;
    }

    @Override
    public String toString() {
        return "ID=\""
                + id
                + "\"<br>Autor=\""
                + autor
                + "\"<br>Titel=\""
                + titel
                + "\"<br>Verfuegbar=\""
                + verfuegbar
                + "\"";
    }

}
