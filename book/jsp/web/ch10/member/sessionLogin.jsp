<%--
  Created by IntelliJ IDEA.
  User: heojeonghwa
  Date: 2023/02/07
  Time: 12:13 오전
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8"%>
<%
    String id = request.getParameter("id");
    String password = request.getParameter("password");
    if(id.equals(password)){
        session.setAttribute("MEMBERID", id);
%>
<html>
<head>
    <title>로그인성공</title>
</head>
<body>
로그인에 성공했습니다.
</body>
</html>
<%
    } else{
%>
<script>
    alert("로그인에 실패하였습니다.");
    history.go(-1);
</script>
<%
    }
%>
