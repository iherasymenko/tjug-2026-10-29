<%@ page contentType="application/rss+xml;charset=UTF-8" session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="en_US"/>
<c:set var="baseUrl" value="${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}"/>
<c:set var="cityUrl" value="${baseUrl}/city?id=${city.id()}"/>
<?xml version="1.0" encoding="UTF-8"?>
<rss version="2.0">
    <channel>
        <title>TJUG Weather Network: <c:out value="${city.displayName()}"/></title>
        <link><c:out value="${cityUrl}"/></link>
        <description>7-day forecast for <c:out value="${city.displayName()}"/></description>
        <language>en-us</language>
        <generator>TJUG Weather Network</generator>
        <c:forEach var="day" items="${forecast.days()}">
            <item>
                <title><c:out value="${day.date().format(dateFormat)}"/>: <c:out value="${day.condition().description()}"/>, <fmt:formatNumber value="${day.minTemperature()}" maxFractionDigits="0"/>${units.temperatureSymbol()} to <fmt:formatNumber value="${day.maxTemperature()}" maxFractionDigits="0"/>${units.temperatureSymbol()}</title>
                <link><c:out value="${cityUrl}"/></link>
                <guid isPermaLink="false">city-${city.id()}-${day.date()}</guid>
                <description><c:out value="${day.condition().description()}"/> with a low of <fmt:formatNumber value="${day.minTemperature()}" maxFractionDigits="0"/>${units.temperatureSymbol()} and a high of <fmt:formatNumber value="${day.maxTemperature()}" maxFractionDigits="0"/>${units.temperatureSymbol()}.</description>
            </item>
        </c:forEach>
    </channel>
</rss>
