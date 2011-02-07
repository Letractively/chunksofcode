package com.myapp.bookstore.jsp.servlets;

import java.io.IOException;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.myapp.bookstore.jsp.Buch;
import com.myapp.bookstore.jsp.DatenBank;



// import bibliothek.PseudoDB;

@SuppressWarnings("serial")
public class BuchListeServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
            IOException {

        HttpSession session = req.getSession();
        List<Buch> liste = DatenBank.getListe(session);

        // setze die collection in den request...
        req.setAttribute("buecher", liste);

        // hole mir den request dispatcher...
        RequestDispatcher dis;
        dis = getServletContext().getRequestDispatcher("/jsp/buchListe.jsp");

        // und weiter gehts auf der seite buchListe.jsp...
        dis.forward(req, resp);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
            IOException {
        doGet(req, resp);
    }
}
