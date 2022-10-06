<%--
    <%@ page import="java.util.List,bean.Dept" %> 也可以写成下面的形式
--%>
<%--<%@ page import="bean.Dept" %>
<%@ page import="java.util.List" %>--%>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--毙掉session对象，写上这个，内置对象就不能用了--%>
<%--<%@page session="false" %>--%>
<html>
<head>
    <title>部门列表页面</title>
    <%--设置整个网页的基础路径是：http://localhost:8080/oa3/--%>
    <%--<base href="http://localhost:8080/oa3/">--%>
    <base href="${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}/">
    <%--        http                         ://localhost                        :8080                             /oa3                              /--%>
</head>

<body>
    <%--显示一个登录名--%>
    <h3>欢迎${user.username},在线人数${onlinecount}人</h3>
    <a href="user/exit">[退出系统]</a>

    <script type="text/javascript">
        function del(dno){
            var ok = window.confirm("亲，删了不可恢复哦！");
            if (ok){
                /*主义HTML的base标签可能对js中的代码不起作用，所以js代码最好前面写上‘/oa3’*/
                document.location.href = "${pageContext.request.contextPath}/dept/delete?deptno=" + dno;
            }
        }
    </script>

    <h1 align="center">部门列表</h1>
    <hr>
    <table border="1px" align="center" width="50%">
        <tr>
            <th>序号</th>
            <th>部门编号</th>
            <th>部门名称</th>
            <th>操作</th>
        </tr>

        <%--<%
            List<Dept> deptList = (List<Dept>)request.getAttribute("deptList");
            //循环遍历
            int count = 0;
            for(Dept dept : deptList){
                //在后台输出
                //System.out.println(dept.getDeptno());

                //把部门名输出到浏览器
                //out.write(dept.getDname());
                //不能直接嵌套java语句块，只能截断
        %>--%>

        <c:forEach items="${deptList}" varStatus="deptStatus" var="dept">
            <tr>
                <td>${deptStatus.count}</td>
                <td>${dept.deptno}</td>
                <td>${dept.dname}</td>
                <td>
                    <a href="javascript:void(0)" onclick="del(${dept.deptno})">删除</a>
                    <a href="dept/detail?f=edit&dno=${dept.deptno}">修改</a>
                    <a href="dept/detail?f=detail&dno=${dept.deptno}">详情</a>
                </td>
            </tr>
        </c:forEach>

        <%--<%
            }
        %>--%>
    </table>

    <hr>
    <a href="add.jsp">新增部门</a>
</body>
</html>
