package video.streaming.platform.streamly.webView;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class webViewController {

    @GetMapping("/register")
    public String registerPage(){
        return "register";
    }

    @GetMapping("/login")
    public String loginPage(){
        return "login";
    }

    @GetMapping("/home")
    public String homePage() {return "home";}

    @GetMapping("/upload")
    public String uploadPage() {return "upload";}

    @GetMapping("/watch/{id}")
    public String watchPage(@PathVariable String id) {return "watch";}
}
