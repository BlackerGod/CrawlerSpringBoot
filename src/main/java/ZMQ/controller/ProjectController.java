package ZMQ.controller;

import ZMQ.crawlerUtils.DateUtil;
import ZMQ.model.Project;
import ZMQ.service.ProjectService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;



import java.util.List;

@Controller
public class ProjectController {

    @Autowired
    private ProjectService projectService;


    @RequestMapping("/")
    public String index(Model model){
        String date = DateUtil.toDay();
        List<Project> projects = projectService.queryProjects(date);
        Gson gson = new GsonBuilder().create();
        String respString = gson.toJson(projects);
        model.addAttribute("projectList", respString);
        return "index";
    }

}
