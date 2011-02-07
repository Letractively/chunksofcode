<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/tld/html_basic.tld" prefix="h" %>
<%@ taglib uri="/WEB-INF/tld/jsf_core.tld" prefix="core" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
    <head>
        <title>Buchliste</title>
    </head>
    <body>
        <core:view>
            <h:form id="bookList">
                <h:dataTable
                        id="books"
                        value="#{bookListBean.books}"
                        var="book"
                        border="1">

            <%-- DIE ID: --%>   
                    <h:column>
                        <core:facet name="header">
                            <h:outputText value="ID" />
                        </core:facet>
                        <h:outputText value="#{book.id}" />
                    </h:column>


            <%-- DER AUTOR: --%>
                    <h:column>
                        <core:facet name="header">
                            <h:outputText value="Autor" />
                        </core:facet>
                        <h:outputText value="#{book.author}" />
                    </h:column>

            <%-- DER TITEL: --%>
                    <h:column>
                        <core:facet name="header">
                            <h:outputText value="Titel" />
                        </core:facet>
                        <h:outputText value="#{book.title}" />
                    </h:column>

            <%-- DIE VERF�GBARKEIT: --%>
                    <h:column>
                        <core:facet name="header">
                            <h:outputText value="Verfuegbar" />
                        </core:facet>
                        <h:selectBooleanCheckbox value="#{book.available}" disabled="true" />
                    </h:column>

            <%-- DER BEARBEITENLINK: --%>
                    <h:column>
                        <core:facet name="header">
                            <h:outputText value="Bearbeiten" />
                        </core:facet>
                        <h:commandLink 
                                id="bearbeiten" 
                                action="book_edit"
                                actionListener="#{bookBean.selectBook}">
                            <h:outputText value="bearbeiten..." />
                            <core:param 
                                    id="editId" 
                                    name="id"
                                    value="#{book.id}" />
                        </h:commandLink>
                    </h:column>

            <%-- DER LOESCHLINK: --%>
                    <h:column>
                        <core:facet name="header">
                            <h:outputText value="Loeschen" />
                        </core:facet>
                        <h:commandLink
                                id="Delete"
                                action="book_listall"
                                actionListener="#{bookBean.deleteBook}">
                            <h:outputText value="loeschen..." />
                            <core:param
                                    id="deleteId" 
                                    name="id" 
                                    value="#{book.id}" />
                        </h:commandLink>
                    </h:column>
                </h:dataTable>

        <%-- EIN NEUES BUCH ERSTELLEN: --%>
                <h:commandLink id="Hinzufuegen" action="book_edit" actionListener="#{bookBean.initBook}">
                    <h:outputText value="Ein neues Buch einfuegen..." />
                </h:commandLink>    

            </h:form>
        </core:view>
    </body>
</html>
