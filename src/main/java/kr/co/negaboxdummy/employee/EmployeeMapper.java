package kr.co.negaboxdummy.employee;

import kr.co.negaboxdummy.employee.model.EmployeePostReq;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EmployeeMapper {
    List<Long> findTheaterId();
    int save(EmployeePostReq req);
}
