<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ page language="java"%>
<%@taglib uri="/WEB-INF/tld/c.tld" prefix="c" %>

<html>
    <head>
        <title>Buch bearbeiten</title>
    </head>
    <body>
        <h1>Buch bearbeiten:</h1>
        <small><c:out value="ID=${requestScope.buch.id}"></c:out></small>
        <br>
        <br>
        <form name="bearbeiten"
              action="buchBearbeiten"
              method="post" >
            <table border="1">
                    <tr>
                        <td>Autor:</td>
                        <td>
                            <input type="text" 
                                   name="autor"
                                   value="<c:out value="${requestScope.buch.autor}"/>" >
                        </td>
                    </tr>
                    <tr>
                        <td>Titel:</td>
                        <td>
                            <input type="text" 
                                   name="titel" 
                                   value="<c:out value="${requestScope.buch.titel}"/>" >
                        </td>
                    </tr>
                    <tr>
                        <td>Verfuegbar:</td>
                        <td>
                            <input type="checkbox" 
                                   name="verfuegbar" 
                                   value="true"
                                    <c:if test="${requestScope.buch.verfuegbar}">
                                        checked="checked"
                                    </c:if> >    
                        </td>
                    </tr>
                    <tr>
                        <td colspan="2">
                            <input type="submit" 
                                   name="btnSpeichern"
                                   value="Speichern" >
                        </td>
                    </tr>
            </table>
            <input type="hidden"
                   name="id"
                   value="<c:out value="${requestScope.buch.id}"/>" >
        </form>
    </body>
</html>
