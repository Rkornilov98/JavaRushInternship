<%--
  Created by IntelliJ IDEA.
  User: rkorn
  Date: 11.01.2022
  Time: 11:15
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Players</title>
</head>
<body>

<h2>Players</h2>
<table>
    <tr>
        <th>id</th>
        <th>name</th>
        <th>title</th>
        <th>race</th>
        <th>profession</th>
        <th>birthday</th>
        <th>banned</th>
        <th>experience</th>
    </tr>
    <c:forEach var="player" items="${playerList}">
        <tr>
            <td>${player.id}</td>
            <td>${player.name}</td>
            <td>${player.title}</td>
            <td>${player.race}</td>
            <td>${player.profession}</td>
            <td>${birthday}</td>
            <td>${banned}</td>
            <td>${experience}</td>
            <td>
                <a href="/edit/${player.id}">edit</a>
                <a href="/delete/${player.id}">delete</a>
            </td>
        </tr>
    </c:forEach>
</table>
</body>
</html>
