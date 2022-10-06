package filter;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LoginCheckFilter implements Filter {
    /*@Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }*/



    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        /**
         * 什么情况下不能拦截？
         *      目前写的路径是：/* 表示所有请求均拦截。
         *
         *      用户访问 index.jsp的时候不能拦截
         *      用户真正的登录了，这个需要放行，不能拦截。
         *      用户要去登录，这个也不能拦截。
         *      WelcomeServlet也不能拦截。
         *      选择10天免登录时不能拦截
         */

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        //获取请求路径
        String servletPath = request.getServletPath();

        HttpSession session = request.getSession(false);

        /*使在使用过滤器时十天免登录可以实现*/
        boolean cookieFlag = false;
        Cookie[] cookies = request.getCookies();
        if (cookies != null){
            for (Cookie cookie : cookies) {
                if ("username".equals(cookie.getName()) || "password".equals(cookie.getName())){//使用equals不要用双等号
                    //判断只要带有含username和password的cookie,就可以放行
                    cookieFlag = true;
                    break;
                }
            }
        }

        /*if ("/index.jsp".equals(servletPath) || "/welcome".equals(servletPath) ||
                "/user/login".equals(servletPath) || "/user/exit".equals(servletPath) ||
                cookieFlag || (session != null && session.getAttribute("username") != null)){*/
        if ("/index.jsp".equals(servletPath) || "/welcome".equals(servletPath) ||
                "/user/login".equals(servletPath) || "/user/exit".equals(servletPath) ||
                cookieFlag || (session != null && session.getAttribute("user") != null)){
            //继续往下走
            chain.doFilter(request,response);
        }else {
            //跳转到登录页面
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            //访问WEB站点的根
        }
    }

    /*@Override
    public void destroy() {

    }*/

}
