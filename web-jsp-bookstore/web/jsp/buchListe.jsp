<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ page language="java" import="java.util.*"%>
<%@ taglib uri="/WEB-INF/tld/c.tld" prefix="c" %>

<html>
    <head>
        <title>Buch Liste</title>
    </head>
    <body>
    <big><big><b>Liste aller Buecher:</b></big></big>
    <table border="1">
        <tbody>
            <tr>
                <td>Autor</td>
                <td>Buchtitel</td>
                <td>Verfuegbar</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
            </tr>
            <c:forEach items="${requestScope.buecher}" var="buch">
                <tr>
                    <td>
                        <c:out value="${buch.autor}"/>
                    </td>
                    <td>
                        <c:out value="${buch.titel}"/>
                    </td>
                    <td>
                        <input
                                type="checkbox"  
                                disabled="disabled"
                                <c:if test="${buch.verfuegbar}" >
                                    checked="checked"
                                </c:if>
                        >
                    </td>
                    <td>
                        <a href="buchBearbeiten?do=bearbeiten&id=${buch.id}">bearbeiten</a>
                    </td>
                    <td>
                        <a href="buchBearbeiten?do=loeschen&id=${buch.id}">loeschen</a>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    <br/>
    <a href="buchBearbeiten?do=hinzu">Neues Buch einfuegen</a>
    <hr/>
    <small><a href="http://www.laliluna.de/first-java-servlets-jsp-tutorial_de.html">tutorial</a></small>
    </body>
</html>
