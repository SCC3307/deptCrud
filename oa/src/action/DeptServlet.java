package action;

import bean.Dept;
import utils.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


@WebServlet({"/dept/list","/dept/detail","/dept/delete","/dept/save","/dept/edit","/dept/modify"})
public class DeptServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //获取session（这个session不需要新建）
        //只是或许当前session，获取不到则返回null
        /*HttpSession session = request.getSession(false);//有过滤器不用写了

        if (session != null && session.getAttribute("username") != null){//session不是null,因为在jsp页面里，session是九大内置对象，提前创建了，但是获取不到username的信息。
            //获取servlet path
            String servletPath = request.getServletPath();//模糊匹配获取路径方式不是这个
            if ("/dept/list".equals(servletPath)){
                doList(request,response);
            } else if ("/dept/detail".equals(servletPath)){
                doDetail(request,response);
            } else if ("/dept/delete".equals(servletPath)){
                doDel(request,response);
            } else if ("/dept/save".equals(servletPath)){
                doSave(request,response);
            } else if ("/dept/modify".equals(servletPath)){
                doModify(request,response);
            }
        }else {
            //跳转到登录页面
            //response.sendRedirect("/oa3/index.jsp");
            //response.sendRedirect("/oa3");
            response.sendRedirect(request.getContextPath() + "/index.jsp");//访问WEB站点的根

        }*/

        //获取servlet path
        String servletPath = request.getServletPath();//模糊匹配获取路径方式不是这个
        if ("/dept/list".equals(servletPath)){
            doList(request,response);
        } else if ("/dept/detail".equals(servletPath)){
            doDetail(request,response);
        } else if ("/dept/delete".equals(servletPath)){
            doDel(request,response);
        } else if ("/dept/save".equals(servletPath)){
            doSave(request,response);
        } else if ("/dept/modify".equals(servletPath)){
            doModify(request,response);
        }


    }

    private void doModify(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
        request.setCharacterEncoding("UTF-8");
        //获取前端传来的部门编号，部门名称，部门位置信息，更新数据库
        int deptno = Integer.parseInt(request.getParameter("deptno"));
        String dname = request.getParameter("dname");
        String loc = request.getParameter("loc");

        Connection conn = null;
        PreparedStatement ps = null;
        int count = 0;

        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);
            String sql = "update dept set dname = ?,loc = ? where deptno = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1,dname);
            ps.setString(2,loc);
            ps.setInt(3,deptno);
            count = ps.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            if (conn != null){
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        }finally {
            DBUtil.close(conn,ps,null);
        }

        if (count == 1){
            response.sendRedirect(request.getContextPath() + "/dept/list");
        }
    }

    private void doSave(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
        //获取部门信息
        //注意乱码问题（Tomcat10不会有这个问题）
        request.setCharacterEncoding("UTF-8");
        int deptno = Integer.parseInt(request.getParameter("deptno"));
        String dname = request.getParameter("dname");
        String loc = request.getParameter("loc");

        //连接数据库，执行insert 语句
        Connection conn = null;
        PreparedStatement ps = null;
        int count = 0;

        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);
            String sql = "insert into dept(deptno,dname,loc) values(?,?,?)";
            ps = conn.prepareStatement(sql);
            ps.setInt(1,deptno);
            ps.setString(2,dname);
            ps.setString(3,loc);
            count = ps.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null){
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }finally {
            DBUtil.close(conn,ps,null);
        }
        if (count == 1){
            response.sendRedirect(request.getContextPath() + "/dept/list");
        }
    }

    /**
     * 根据部门编号删除部门
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void doDel(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
        //获取部门编号
        int deptno = Integer.parseInt(request.getParameter("deptno"));
        //连接数据库，删除部门
        Connection conn = null;
        PreparedStatement ps = null;
        int count = 0;
        try {
            conn = DBUtil.getConnection();
            String sql = "delete from dept where deptno = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1,deptno);
            count = ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DBUtil.close(conn,ps,null);
        }

        if (count == 1){
            response.sendRedirect(request.getContextPath() + "/dept/list");
        }
    }

    /**
     * 根据编号获取部门信息
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void doDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
        //获取部门编号
        int dno = Integer.parseInt(request.getParameter("dno"));
        //根据部门编号获取部门信息，将部门信息封装成咖啡豆
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        //这个豆子只有一个，不需要袋子，只需要将咖啡豆放到request域中即可
        //创建部门对象
        Dept dept = new Dept();

        try {
            conn = DBUtil.getConnection();
            String sql = "select dname,loc from dept where deptno = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1,dno);
            rs = ps.executeQuery();

            if (rs.next()){//一定要有if语句去执行rs.next，哪怕只有一条数据
                /*String dname = rs.getString("dname");
                String loc = rs.getString("loc");*/
                //rs.next();//这条语句不仅判断rs中是否有数据，而且移动了指针,因此是必须要执行的
                dept.setDeptno(dno);
                dept.setDname(rs.getString("dname"));
                dept.setLoc(rs.getString("loc"));

            }


        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DBUtil.close(conn,ps,rs);
        }

        request.setAttribute("dept",dept);
        /*request.getRequestDispatcher("/detail.jsp").forward(request,response);*/

        String f = request.getParameter("f");//共享代码，因为这个方法的代码都是根据部门编号查找数据库中的对应数据，
        // 仅仅获取数据，代码可以共享，然后跳转到不同的显示页面，一个仅供显示，一个部门名和位置可以修改
        //这个方法中的代码仅是用于根据部门编号查询数据
        /*if ("m".equals(f)){
            //转发到修改页面
            request.getRequestDispatcher("/edit.jsp").forward(request,response);
        }else if ("d".equals(f)){
            //跳转到修改也买你
            request.getRequestDispatcher("/detail.jsp").forward(request,response);
        }*/
        String forward = "/" + request.getParameter("f") + ".jsp";
        request.getRequestDispatcher(forward).forward(request,response);
    }

    /**
     * 连接数据库，查询所有的部门信息，将部门信息收集好，然后跳转到JSP做页面展示
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void doList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{

        /**
         * 箱子，需要装车方便运走，因为会有很多箱苹果，一箱一箱运不方便，一次运一车显然更方便
         * new一个集合对象，就是叫了一辆卡车。
         */
        //准备一个容器，用来专门存储部门
        List<Dept> depts = new ArrayList<>();

        //连接数据库查询所有的数据库信息
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            //获取连接
            conn = DBUtil.getConnection();
            //执行查询语句
            String sql = "select deptno,dname,loc from dept";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            //遍历结果集
            while (rs.next()){
                //从结果集中取出
                int deptno = rs.getInt("deptno");
                String dname = rs.getString("dname");
                String loc = rs.getString("loc");

                //将以上零散的数据封装成对象。
                Dept dept = new Dept();
                dept.setDeptno(deptno);
                dept.setDname(dname);
                dept.setLoc(loc);

                //将部门对象放到list集合当中
                depts.add(dept);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            //释放资源
            DBUtil.close(conn,ps,rs);
        }

        //将一个集合放到请求域当中
        request.setAttribute("deptList",depts);

        //转发（不要重定向）
        request.getRequestDispatcher("/list.jsp").forward(request,response);

    }


}





















