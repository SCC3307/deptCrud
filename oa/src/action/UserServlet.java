package action;

import bean.User;
import utils.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet({"/user/login","/user/exit"})
public class UserServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String servletPath = request.getServletPath();
        if ("/user/login".equals(servletPath)){
            doLogin(request,response);
        } else if ("/user/exit".equals(servletPath)){
            doExit(request,response);
        }

    }

    private void doExit(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{

        //获取session对象，销毁session
        HttpSession session = request.getSession(false);
        if (session != null) {

            //从session域中删除user对象
            session.removeAttribute("user");

            //手动销毁session对象
            session.invalidate();

            //跳转到登录页面
            response.sendRedirect(request.getContextPath() + "/index.jsp");
        }

        //获取cookie对象，销毁cookies
        Cookie[] cookies = request.getCookies();
        if (cookies != null){
            //销毁存储登录信息的cookie
            for (Cookie cookie : cookies) {
                if ("username".equals(cookie.getName()) || "password".equals(cookie.getName())){
                    cookie.setMaxAge(0);//设置的是在浏览器中的cookie的存在时间
                    cookie.setPath(request.getContextPath());  //路径一定要写上，不然可能销毁不了 原先创建的路径是什么，就还写什么，用于替换
                    response.addCookie(cookie);//一定要再发送给浏览器，让浏览器更改cookie值
                    System.out.println(cookie.getName() + "=" + cookie.getValue());
                    //此时服务器端并未销毁cookie，服务器端依旧可以打印出cookies的值
                }
                /*底部代码优化为上面的代码，合并
                else if ("password".equals(cookie.getName())){
                    cookie.setMaxAge(0);//设置的是在浏览器中的cookie的存在时间
                    cookie.setPath(request.getContextPath());  //路径一定要写上，不然可能销毁不了
                    response.addCookie(cookie);//一定要再发送给浏览器，让浏览器更改cookie值
                    System.out.println(cookie.getName() + "=" + cookie.getValue());
                    //此时服务器端并未销毁cookie，服务器端依旧可以打印出cookies的值
                }*/
            }
        }



    }

    private void doLogin(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException{
        //也可以设置一下布尔值
        boolean success = false;

        //你要做什么事？验证用户名和密码是否正确
        //获取用户名和密码
        String username = request.getParameter("username");//尽量复制
        String password = request.getParameter("password");
        //前端也买你是这样提交数据的：username=admin&password=123456
        //连接数据库验证用户名和密码
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "select * from t_user where username = ? and password = ?";
            //String sql = "select username,password from t_user where username = ? and password = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1,username);
            ps.setString(2,password);
            rs = ps.executeQuery();
            if (rs.next()){
                success = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DBUtil.close(conn,ps,rs);
        }
        //登录成功/失败
        if (success){
            //获取session对象(这里的要求是，必须获取到session，没有session要创建一个session对象)
            HttpSession session = request.getSession();

            User user = new User(username, password);
            session.setAttribute("user",user);
            //session.setAttribute("username",username);

            //登录成功了，并且用户选择了“10天内免登录”功能
            String f = request.getParameter("f");
            if ("1".equals(f)){
                //创建cookie对象存储登录名
                Cookie cookie1 = new Cookie("username",username);//真实情况下是要加密的
                //创建cookie对象存储登录密码
                Cookie cookie2 = new Cookie("password",password);
                //设置cookie的有效期为10天
                cookie1.setMaxAge(60 * 60 * 24 * 10);
                cookie2.setMaxAge(60 * 60 * 24 * 10);
                //设置cookie的path(只要访问这个应用，就一定要携带这个cookie)
                cookie1.setPath(request.getContextPath());
                cookie2.setPath(request.getContextPath());

                //把cookie 发送给浏览器，让浏览器存储起来
                response.addCookie(cookie1);
                response.addCookie(cookie2);

            }


            response.sendRedirect(request.getContextPath() + "/dept/list");
        }else {
            response.sendRedirect( request.getContextPath() + "/error.jsp");
        }
    }

}
