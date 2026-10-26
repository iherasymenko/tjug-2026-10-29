<%@ page contentType="text/html;charset=UTF-8" session="false" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Guestbook"/>
<%@ include file="header.jspf" %>
<h1>Guestbook</h1>
<p>Thanks for visiting the TJUG Weather Network! Please leave a message in our guestbook.</p>

<form class="guestbook-form" action="${pageContext.request.contextPath}/guestbook" method="post">
    <c:if test="${not empty error}">
        <p class="error"><c:out value="${error}"/></p>
    </c:if>
    <label for="name">Your name:</label>
    <input type="text" id="name" name="name" size="40" maxlength="50" value="<c:out value='${name}'/>">
    <label for="message">Message:</label>
    <textarea id="message" name="message" rows="5" cols="60"><c:out value="${message}"/></textarea>
    <button type="submit">Sign the guestbook</button>
</form>

<h2>Messages</h2>
<c:choose>
    <c:when test="${empty entries}">
        <p>Nobody has signed the guestbook yet. Be the first!</p>
    </c:when>
    <c:otherwise>
        <c:forEach var="entry" items="${entries}">
            <div class="guestbook-entry">
                <div class="guestbook-entry-header">
                    <b><c:out value="${entry.name()}"/></b> wrote on <c:out value="${entry.signedAt().format(dateTimeFormat)}"/>:
                </div>
                <div class="guestbook-entry-message"><c:out value="${entry.message()}"/></div>
            </div>
        </c:forEach>
    </c:otherwise>
</c:choose>
<%@ include file="footer.jspf" %>
