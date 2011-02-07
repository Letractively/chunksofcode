<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/tld/html_basic.tld" prefix="h" %>
<%@ taglib uri="/WEB-INF/tld/jsf_core.tld" prefix="core" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
    <head>
        <title>Buch bearbeiten</title>
    </head>
    <body>
        <core:view>
            <h:form>
                <h:inputHidden 
                        id="id" 
                        value="#{bookBean.id}" />
                <h:panelGrid columns="2" border="1">

                    <h:outputText value="ID:" />
                    <h:outputText value="#{bookBean.id}" />

                    <h:outputText value="Autor:" />
                    <h:inputText 
                            id="author" 
                            value="#{bookBean.author}" />

                    <h:outputText value="Titel:" />
                    <h:inputText
                            id="title"
                            value="#{bookBean.title}" />

                    <h:outputText value="Verfuegbar:" />
                    <h:selectBooleanCheckbox 
                            id="available" 
                            value="#{bookBean.available}" />

                </h:panelGrid>
                <h:commandButton 
                        value="Speichern" 
                        action="book_listall"
                        actionListener="#{bookBean.saveBook}" />
            </h:form>
        </core:view>
    </body>
</html>
