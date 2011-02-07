<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html" pageEncoding="UTF-8"%>

<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tld/struts-logic.tld" prefix="logic" %>

<html:html xhtml="true">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>/pages/multibox/multibox.jsp</title>
    </head>
    <body>
        <h1>&lt;html:multibox&gt; test page - choose</h1>
        <p>Select members:</p>
        <html:form action="/processMultibox">
            <table style="background-color:silver">
                <tr>
                    <th colspan="2">Person / Membership</th>
                </tr><%--an array of strings was set as attribute to the
                         request under "allPersons"--%>
                <logic:iterate name="allPersons" id="p">
                    <tr>
                        <td>
                            <bean:write name="p" />
                        </td>
                        <td><%--the persons property means the
                                set/getPersons methods of the form--%>
                            <html:multibox property="persons">
                                <bean:write name="p" />
                            </html:multibox>
                        </td>
                    </tr>
                </logic:iterate>
            </table>
            <html:submit/><html:cancel/><html:reset/>
        </html:form>
    </body>
</html:html>
