<%@ page contentType="text/html;charset=UTF-8" session="false" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="status" value="${requestScope['jakarta.servlet.error.status_code']}"/>
<c:set var="pageTitle" value="Error ${status}"/>
<%@ include file="header.jspf" %>
<h1>Oops!</h1>
<c:choose>
    <c:when test="${status == 404}">
        <p>Sorry, we couldn't find what you were looking for.
            <c:if test="${not empty requestScope['jakarta.servlet.error.message']}">
                (<c:out value="${requestScope['jakarta.servlet.error.message']}"/>)
            </c:if>
        </p>
    </c:when>
    <c:when test="${status >= 500}">
        <p>Our weather station is experiencing technical difficulties. Please try again later.</p>
    </c:when>
    <c:otherwise>
        <p>Sorry, we couldn't make sense of that request.</p>
    </c:otherwise>
</c:choose>
<p><a href="${pageContext.request.contextPath}/">Back to the home page</a></p>
<%@ include file="footer.jspf" %>
