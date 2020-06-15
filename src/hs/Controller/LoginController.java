package hs.Controller;

import hs.Bean.BaseConf;
import hs.Bean.ModleConstainer;
import hs.Dao.Service.ModleServe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/4/30 12:49
 */
@Controller
@RequestMapping("/login")
public class LoginController {
    @Autowired
    private BaseConf baseConf;
    @Autowired
    private ModleServe modleServe;
    @Autowired
    private ModleConstainer modleConstainer;

    @RequestMapping("/login")
//    @ResponseBody
    public ModelAndView userlogin(){
        ModelAndView mv=new ModelAndView();
        mv.setViewName("login");
        mv.addObject("companyName",baseConf.getCommenName());
        return mv;
    }

    @RequestMapping("/index")
//    @ResponseBody
    public ModelAndView manager(){
        ModelAndView mv=new ModelAndView();
        mv.setViewName("index");
        mv.addObject("companyName",baseConf.getCommenName());
        mv.addObject("modles",modleConstainer.getModules().values());
        return mv;
    }


    @RequestMapping("/home")
    public ModelAndView modelStatus(){

        ModelAndView mv=new ModelAndView();
        mv.setViewName("home");
        mv.addObject("modles",modleConstainer.getModules().values());
        mv.addObject("basedata",baseConf);
        return mv;
    }




    @RequestMapping("/user")
//    @ResponseBody
    public ModelAndView manager_user(){
        ModelAndView mv=new ModelAndView();
        mv.setViewName("user");
        return mv;
    }

    @RequestMapping("/userinfo")
//    @ResponseBody
    public ModelAndView manager_userinfo(){
        ModelAndView mv=new ModelAndView();
        mv.setViewName("userinfo");
        return mv;
    }

    @RequestMapping("/set")
//    @ResponseBody
    public ModelAndView manager_set(){
        ModelAndView mv=new ModelAndView();
        mv.setViewName("set");
        return mv;
    }

    @RequestMapping("/register")
//    @ResponseBody
    public ModelAndView manager_register(){
        ModelAndView mv=new ModelAndView();
        mv.setViewName("register");
        return mv;
    }

    @RequestMapping("/changepassword")
//    @ResponseBody
    public ModelAndView manager_changepassword(){
        ModelAndView mv=new ModelAndView();
        mv.setViewName("changepassword");
        return mv;
    }

    @RequestMapping("/message")
//    @ResponseBody
    public ModelAndView manager_message(){
        ModelAndView mv=new ModelAndView();
        mv.setViewName("message");
        return mv;
    }
}
