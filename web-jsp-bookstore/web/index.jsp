
<%
try {
    //weiterleiten auf das buchListe servlet
    response.sendRedirect( "buchListe" );
} catch (Throwable t) {
    t.printStackTrace();
}
%>
