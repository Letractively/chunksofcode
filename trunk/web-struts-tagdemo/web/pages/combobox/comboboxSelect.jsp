<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" %>

<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tld/struts-logic.tld" prefix="logic" %>

<html:html xhtml="true">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <title>/pages/combobox/comboboxSelect.jsp</title>
        <html:base />
    </head>
    <body>
        <h1>&lt;html:selec&gt; test page - choose</h1>
        <p>View the HTML source to see the generated option values.</p>
        <html:form action="/processCombobox">
            <h2>select one car from the car map:</h2>
            <p>
                <html:select name="comboboxForm"
                             property="car"
                             size="1"> 
                    <html:options collection="allCars"
                                  property="key"
                                  labelProperty="value"/>
                </html:select>
            </p>
            <h2>select some girls from the list:</h2>
            <p>
                <html:select property="girls"
                             multiple="multiple"
                             size="5">
                    <html:options name="allGirls" />
                </html:select>
            </p>
            <p>
                <html:submit /><html:cancel /><html:reset />
            </p>
        </html:form>
    </body>
</html:html>
