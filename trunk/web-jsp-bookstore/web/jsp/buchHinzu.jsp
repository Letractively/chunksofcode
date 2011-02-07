<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ page language="java"%>

<html>
    <head>
        <title>Book erstellen</title>
    </head>
    <body>
        <h1>Ein neues Buch einfuegen:</h1>
        <form  name="bearbeiten"
                action="buchBearbeiten"
                method="post" >
            <table>
                <tbody>
                    <tr>
                        <td>Autor:</td>
                        <td><input
                            type="text"
                            name="autor"
                            value=""
                        ></td>
                    </tr>
                    <tr>
                        <td>Titel:</td>
                        <td><input
                            type="text"
                            name="titel"
                            value=""
                        ></td>
                    </tr>
                    <tr>
                        <td>Verfuegbar:</td>
                        <td><input
                            type="checkbox"
                            name="verfuegbar"
                            value="true"
                        ></td>
                    </tr>
                    <tr>
                        <td colspan="2"><input
                            type="submit"
                            name="buttonSpeichern"
                            value="Speichern"
                        ></td>
                    </tr>
                </tbody>
            </table>
        </form>
    </body>
</html>
