<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html" pageEncoding="UTF-8"%>

<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tld/struts-logic.tld" prefix="logic" %>

<html:html xhtml="true">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>/pages/multibox/multiboxResults.jsp</title>
    </head>
    <body>
        <h1>&lt;html:multibox&gt; test page - results</h1>
        <h3>you selected following car one page ago:</h3>

            <div style="background-color:silver">
                <bean:write name="comboboxForm" property="car"/>
            </div>
            it was also written to server logfile during ComboboxProcessAction
        <h3>you selected following girls from the list:</h3>
            <div style="background-color:silver">
                <logic:iterate name="comboboxForm" property="girls" id="g">
                    <bean:write name="g"/><br/>
                </logic:iterate>
            </div>
            it was also written to server logfile during ComboboxProcessAction
        <p>
            <html:link forward="welcome" >home</html:link><br/>
            <html:link action="/prepareCombobox">again</html:link>
        </p>
    </body>
</html:html>
