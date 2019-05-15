package link.bosswang.nettywebsocket.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 路由，返回视图
 */
@Controller
@RequestMapping("/")
public class RouterController {
    @RequestMapping(value = "/chat")
    public String chat() {
        return "chat";
    }
}
