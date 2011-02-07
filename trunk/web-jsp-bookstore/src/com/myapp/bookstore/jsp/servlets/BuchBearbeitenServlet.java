package com.myapp.bookstore.jsp.servlets;


import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.myapp.bookstore.jsp.Buch;
import com.myapp.bookstore.jsp.DatenBank;


@SuppressWarnings( "serial" )
public class BuchBearbeitenServlet extends HttpServlet {

    @Override
    public void doGet( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {

        // hole mir den do-parameter...
        String aktion = req.getParameter( "do" );

        // hole die id des requests...
        Long id = null;
        if ( req.getParameter( "id" ) != null )
            id = Long.valueOf( req.getParameter( "id" ) );

        // buch hinzu...
        // ##############################################
        if ( aktion.equals( "hinzu" ) ) {
            RequestDispatcher dis;
            dis = getServletContext().getRequestDispatcher( "/jsp/buchHinzu.jsp" );
            dis.forward( req, resp );
        }

        // buch bearbeiten...
        // ##############################################
        else if ( aktion.equals( "bearbeiten" ) ) {
            Buch buch = new Buch();
            if ( id != null )
                // hole das buch aus der pseudodatenbank...
                buch = DatenBank.ladeBuchMitId( id.longValue() );

            // setze das buch in den request...
            req.setAttribute( "buch", buch );

            // hole mir den dispatcher...
            RequestDispatcher dis;
            dis = getServletContext().getRequestDispatcher( "/jsp/buchBearbeiten.jsp" );

            // weiter gehts auf der seite buchListe.jsp...
            dis.forward( req, resp );
        }

        // buch loeschen...
        // ##############################################
        else if ( aktion.equals( "loeschen" ) ) {
            // loesche das buch mit der id...
            DatenBank.loescheBuchMitId( id.longValue() );

            // und weiter gehts auf der seite buchListe.jsp...
            resp.sendRedirect( req.getContextPath() + "/buchListe" );
        }

    }

    @Override
    public void doPost( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {

        // hole mir die bucheigenschaften vom request...
        long id = 0;
        try {
            id = Long.parseLong( req.getParameter( "id" ) );
        }
        catch ( NumberFormatException e ) {
        }

        String autor = req.getParameter( "autor" );
        String titel = req.getParameter( "titel" );
        boolean verfuegbar = Boolean.valueOf( req.getParameter( "verfuegbar" ) );

        // erzeuge ein neues buch anhand der gewonnenen parameter...
        Buch buch = new Buch( id, autor, titel, verfuegbar );
        DatenBank.speichereInListe( buch );

        // und weiter gehts auf der seite buchListe.jsp...
        resp.sendRedirect( req.getContextPath() + "/buchListe" );
    }
}
