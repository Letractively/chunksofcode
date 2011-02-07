package com.myapp.bookstore.jsp;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

public class DatenBank {

    private static List<Buch> liste;
    private static int i = 0;

    static {
        liste = new ArrayList<Buch>();
        liste.add(new Buch(i++, "A.R.", "Servlets und JSP Technologie", true));
        liste.add(new Buch(i++, "S.E.", "Warum ich so haesslich bin", false));
        liste.add(new Buch(i++, "D.L.", "Kosovoalbanische Kultur", true));
        liste.add(new Buch(i++, "C.L.", "Schwangerschaftsgymnastik", false));
        liste.add(new Buch(i++, "S.H.", "PL/SQL for masters", false));
    }

    /**
     * speichert ein buch in der db und
     * 
     * @param buch
     * @return
     */
    public static long speichereInListe(Buch buch) {

        Buch zuEntfernen = null;

        for (Buch b : liste)
            if (b.getId() == buch.getId())
                zuEntfernen = b;

        if (zuEntfernen == null)
            buch.setId(i++);
        else {
            long id = zuEntfernen.getId();
            liste.remove(zuEntfernen);
            buch.setId(id);
        }

        liste.add(buch);
        return buch.getId();
    }

    public static Buch ladeBuchMitId(long id) {
        for (Buch b : liste)
            if (b.getId() == id)
                return b;

        return null;
    }

    public static void loescheBuchMitId(long id) {
        Buch zuEntfernen = null;

        for (Buch b : liste)
            if (b.getId() == id)
                zuEntfernen = b;

        liste.remove(zuEntfernen);
    }

    @SuppressWarnings("unused")
    public static List<Buch> getListe(HttpSession session) {
        return liste;
    }
}
