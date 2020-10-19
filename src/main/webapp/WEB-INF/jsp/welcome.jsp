<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>
 
<!-- %@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %-->  

<petclinic:layout pageName="home">
    <h2><fmt:message key="welcome"/></h2>
    <div class="row">
    <h2> Project ${title}</h2>
    <p><h2> Group ${group}</h2></p>
    <p><ul>
    <c:forEach items="${persons}" var="person">
    	<li>${person.firstName}${' '}${person.lastName}</li>
    </c:forEach>
    </ul></p>
        <div class="col-md-12">
            <spring:url value="/resources/images/pets.png" htmlEscape="true" var="petsImage"/>
            <spring:url value="/resources/images/usImage.png" htmlEscape="true" var="usImage"/>
            <spring:url value="/resources/images/usImage.png" htmlEscape="true" var="usImage"/>
            <img class="img-responsive" src="${petsImage}"/>
            <img class="img-responsive" src="${usImage}"/>
            <img class="img-responsive" src="${usImage}"/>
        </div>
    </div>
</petclinic:layout>
