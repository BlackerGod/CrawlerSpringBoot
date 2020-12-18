package ZMQ.service;


import ZMQ.mapper.ProjectTableMapper;
import ZMQ.model.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    @Autowired
    private ProjectTableMapper projectTableMapper;

    public List<Project> queryProjects(String Date){
        return projectTableMapper.selectAll(Date);
    }


    public  int Insert(Project project){
        return projectTableMapper.insert(project);
    }

}
