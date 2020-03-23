package hs.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/3/19 14:11
 */

@Controller("testcontrl")
@RequestMapping("/test")
public class testController {
    @RequestMapping("/index")
    public ModelAndView index(){
        ModelAndView mv=new ModelAndView();
        mv.setViewName("index");
        return mv;
    }
}
