<%@ page import="com.google.gson.Gson"%>
<%@ page import="ru.trukhmanov.dto.response.ErrorMessageDto"%>
<%@ page contentType="application/json; charset=UTF-8"  %>

<% Gson gson = (Gson) pageContext.getAttribute("gson"); %>
<% out.println(gson.toJson(new ErrorMessageDto("Page not found")));%>