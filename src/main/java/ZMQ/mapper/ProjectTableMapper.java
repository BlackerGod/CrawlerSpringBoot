package ZMQ.mapper;

import ZMQ.model.Project;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProjectTableMapper {
    int insert(Project record);

    List<Project> selectAll(String date);
}