package bean;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

public class User implements HttpSessionBindingListener {

    @Override
    public void valueBound(HttpSessionBindingEvent event) {
        //用户登录了
        //User类型的对象向Session域中存放了
        //获取ServletContext对象，即项目对象
        ServletContext application = event.getSession().getServletContext();

        Object onlinecount = application.getAttribute("onlinecount");
        if (onlinecount == null){
            application.setAttribute("onlinecount",1);
        }else {
            int count = (Integer)onlinecount;
            count ++ ;
            application.setAttribute("onlinecount",count);
        }
    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        //用户退出了
        //User类型的对象从Session域中删除了
        ServletContext application = event.getSession().getServletContext();

        Integer onlinecount = (Integer) application.getAttribute("onlinecount");

        onlinecount --;

        application.setAttribute("onlinecount",onlinecount);

    }

    private String username;
    private String password;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
