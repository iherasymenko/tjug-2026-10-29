<%@ page contentType="text/html;charset=UTF-8" session="false" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Home"/>
<%@ include file="header.jspf" %>
<h1>Welcome to the TJUG Weather Network!</h1>
<p>Your source for current conditions, 7-day forecasts and air quality reports from around the world.
    Type a city name into the box above to get started.</p>

<h2>My Cities</h2>
<c:choose>
    <c:when test="${empty myCities}">
        <p>You haven't saved any cities yet. Find a city and click <i>Add to My Cities</i>.</p>
    </c:when>
    <c:otherwise>
        <div class="sunken">
            <table>
                <thead>
                <tr>
                    <th>City</th>
                    <th>Conditions</th>
                    <th>Temperature</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="saved" items="${myCities}">
                    <tr>
                        <td><a href="<c:url value='/city'><c:param name='id' value='${saved.city().id()}'/></c:url>"><c:out value="${saved.city().displayName()}"/></a></td>
                        <td>
                            <img src="${pageContext.request.contextPath}/images/weather/${saved.current().condition().icon()}" width="32" height="32" alt="">
                            <c:out value="${saved.current().condition().description()}"/>
                        </td>
                        <td><fmt:formatNumber value="${saved.current().temperature()}" maxFractionDigits="0"/>${units.temperatureSymbol()}</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </c:otherwise>
</c:choose>

<h2>Popular Cities</h2>
<p class="popular-cities">
    <a href="<c:url value='/city?id=6167865'/>">Toronto</a> |
    <a href="<c:url value='/city?id=2950159'/>">Berlin</a> |
    <a href="<c:url value='/city?id=2643743'/>">London</a> |
    <a href="<c:url value='/city?id=5128581'/>">New York</a> |
    <a href="<c:url value='/city?id=1850147'/>">Tokyo</a> |
    <a href="<c:url value='/city?id=703448'/>">Kyiv</a>
</p>

<p>Tell us what you think! <a href="${pageContext.request.contextPath}/guestbook">Sign our guestbook</a> <span class="new">NEW!</span></p>

<p class="hit-counter-line">You are visitor number <span class="hit-counter"><c:out value="${hits}"/></span></p>
<%@ include file="footer.jspf" %>
