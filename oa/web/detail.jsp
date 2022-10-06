<%--<%@ page import="bean.Dept" %>--%>
<%@ page contentType="text/html;charset=UTF-8"%>
<html>
    <head>
        <title>部门详情</title>
    </head>
<body>
    <h3>欢迎${username}</h3>
    <h1>部门详情</h1>
    <%--<%
        //从Request域中取出数据
        Dept dept = (Dept) request.getAttribute("dept");//直接返回的是一个Object需要向下转型
    %>--%>
    <br>
    部门编号：${dept.deptno}<br>
    部门名称：${dept.dname}<br>
    部门位置：${dept.loc}<br>

    <input type="button" value="后退" onclick="window.history.back()" />
</body>
</html>
