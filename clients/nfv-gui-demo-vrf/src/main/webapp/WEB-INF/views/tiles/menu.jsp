<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<ul id="navbar">
    <li>
        <a href="<c:url value="${url}/secure/noc/nfvRouting/getRouteTable?type=IPv4" />">
            <spring:message code="menu.RouteTable" />
        </a>
        <ul>
            <li>
                <a href="<c:url value="${url}/secure/noc/nfvRouting/getRouteTable?type=IPv4" />">
                    IPv4 Table
                </a>
            </li>
            <li>
                <a href="<c:url value="${url}/secure/noc/nfvRouting/getRouteTable?type=IPv6" />">
                    IPv6 Table
                </a>
            </li>
        </ul>
    </li>
    <li>
        <a href="<c:url value="${url}/secure/noc/nfvRouting/insertRoute" />">
            <spring:message code="menu.insertRoute" />
        </a>
    </li>
    <li>
        <a href="<c:url value="${url}/secure/noc/nfvRouting/insertCtrlInfo" />">
            <spring:message code="menu.insertController" />
        </a>
    </li>
</ul>
