<%@ page contentType="text/html;charset=UTF-8" session="false" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="${city.name()}"/>
<%@ include file="header.jspf" %>
<c:url var="rssUrl" value="/rss/forecast.xml"><c:param name="id" value="${city.id()}"/></c:url>
<h1><c:out value="${city.displayName()}"/></h1>
<p class="coordinates">
    Latitude <fmt:formatNumber value="${city.latitude()}" maxFractionDigits="2"/>,
    longitude <fmt:formatNumber value="${city.longitude()}" maxFractionDigits="2"/>
</p>

<div class="toolbar">
    <c:choose>
        <c:when test="${inMyCities}">
            <form action="${pageContext.request.contextPath}/mycities/remove" method="post">
                <input type="hidden" name="id" value="${city.id()}">
                <button type="submit">Remove from My Cities</button>
            </form>
        </c:when>
        <c:otherwise>
            <form action="${pageContext.request.contextPath}/mycities/add" method="post">
                <input type="hidden" name="id" value="${city.id()}">
                <button type="submit">Add to My Cities</button>
            </form>
        </c:otherwise>
    </c:choose>
    <a href="${rssUrl}"><span class="xml-badge">XML</span> Subscribe to this forecast (RSS)</a>
</div>

<h2>Current Conditions</h2>
<div class="current-conditions">
    <img src="${pageContext.request.contextPath}/images/weather/${forecast.current().condition().icon()}" width="64" height="64" alt="">
    <div>
        <span class="temperature"><fmt:formatNumber value="${forecast.current().temperature()}" maxFractionDigits="0"/>${units.temperatureSymbol()}</span>
        <c:out value="${forecast.current().condition().description()}"/><br>
        Humidity: ${forecast.current().humidity()}%<br>
        Wind: <fmt:formatNumber value="${forecast.current().windSpeed()}" maxFractionDigits="0"/> ${units.windSpeedSymbol()}<br>
        <span class="local-time">Local time: <c:out value="${forecast.current().time().format(dateTimeFormat)}"/></span>
    </div>
</div>

<h2>7-Day Forecast</h2>
<div class="sunken">
    <table class="forecast">
        <thead>
        <tr>
            <th>Day</th>
            <th>Conditions</th>
            <th>Low</th>
            <th>High</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="day" items="${forecast.days()}">
            <tr>
                <td><c:out value="${day.date().format(dateFormat)}"/></td>
                <td>
                    <img src="${pageContext.request.contextPath}/images/weather/${day.condition().icon()}" width="32" height="32" alt="">
                    <c:out value="${day.condition().description()}"/>
                </td>
                <td><fmt:formatNumber value="${day.minTemperature()}" maxFractionDigits="0"/>${units.temperatureSymbol()}</td>
                <td><fmt:formatNumber value="${day.maxTemperature()}" maxFractionDigits="0"/>${units.temperatureSymbol()}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>

<h2>Air Quality</h2>
<c:choose>
    <c:when test="${empty airQuality}">
        <p class="unavailable">Air quality data is currently unavailable.</p>
    </c:when>
    <c:otherwise>
        <p class="air-quality">
            US AQI: <b>${airQuality.usAqi()}</b> (<c:out value="${airQuality.category()}"/>)<br>
            PM2.5: <fmt:formatNumber value="${airQuality.pm25()}" maxFractionDigits="1"/> &micro;g/m&sup3;,
            PM10: <fmt:formatNumber value="${airQuality.pm10()}" maxFractionDigits="1"/> &micro;g/m&sup3;
        </p>
    </c:otherwise>
</c:choose>

<h2>Country Profile</h2>
<c:choose>
    <c:when test="${empty country}">
        <p class="unavailable">Country information is currently unavailable.</p>
    </c:when>
    <c:otherwise>
        <div class="country-profile">
            <img src="<c:out value='${country.flagUrl()}'/>" width="64" alt="Flag of <c:out value='${country.name()}'/>">
            <div>
                <b>About <c:out value="${country.name()}"/></b><br>
                Capital: <c:out value="${country.capital()}"/><br>
                Currency: <c:out value="${country.currencyCode()}"/><br>
                Languages: <c:forEach var="language" items="${country.languages()}" varStatus="status"><c:out value="${language}"/><c:if test="${not status.last}">, </c:if></c:forEach><br>
                Dialing code: +<c:out value="${country.phoneCode()}"/>
            </div>
        </div>
    </c:otherwise>
</c:choose>

<h2>This Day Last Year</h2>
<c:choose>
    <c:when test="${empty lastYear}">
        <p class="unavailable">Historical data is currently unavailable.</p>
    </c:when>
    <c:otherwise>
        <p class="last-year">
            <img src="${pageContext.request.contextPath}/images/weather/${lastYear.condition().icon()}" width="32" height="32" alt="">
            On <c:out value="${lastYear.date().format(fullDateFormat)}"/>:
            <c:out value="${lastYear.condition().description()}"/>,
            low <fmt:formatNumber value="${lastYear.minTemperature()}" maxFractionDigits="0"/>${units.temperatureSymbol()},
            high <fmt:formatNumber value="${lastYear.maxTemperature()}" maxFractionDigits="0"/>${units.temperatureSymbol()}
        </p>
    </c:otherwise>
</c:choose>
<%@ include file="footer.jspf" %>
