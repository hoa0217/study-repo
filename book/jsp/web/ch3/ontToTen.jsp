<%--
  Created by IntelliJ IDEA.
  User: heojeonghwa
  Date: 2023/01/31
  Time: 3:36 오전
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8"%>
<html>
<head>
    <title>1-10까지의 합</title>
</head>
<body>
<%
    int sum = 0;
    for (int i = 1; i <= 10; i++) {
        sum = sum + i;
    }
%>
1부터 10까지의 합은 <%= sum%> 입니다.

</body>
</html>
