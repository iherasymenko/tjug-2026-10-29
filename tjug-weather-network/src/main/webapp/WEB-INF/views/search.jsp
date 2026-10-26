<%@ page contentType="text/html;charset=UTF-8" session="false" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Search"/>
<%@ include file="header.jspf" %>
<h1>Search results for &quot;<c:out value="${query}"/>&quot;</h1>
<c:choose>
    <c:when test="${empty cities}">
        <p>Sorry, we couldn't find any cities matching &quot;<c:out value="${query}"/>&quot;. Please check the spelling and try again.</p>
    </c:when>
    <c:otherwise>
        <ul class="search-results">
            <c:forEach var="city" items="${cities}">
                <li><a href="<c:url value='/city'><c:param name='id' value='${city.id()}'/></c:url>"><c:out value="${city.displayName()}"/></a></li>
            </c:forEach>
        </ul>
    </c:otherwise>
</c:choose>
<%@ include file="footer.jspf" %>
