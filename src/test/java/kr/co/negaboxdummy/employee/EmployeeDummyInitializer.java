package kr.co.negaboxdummy.employee;

import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import kr.co.negaboxdummy.config.FakerConfig;
import kr.co.negaboxdummy.employee.model.EmployeePostReq;
import kr.co.negaboxdummy.employee.model.KobisActorListRes;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.annotation.Rollback;

import java.util.ArrayList;
import java.util.List;

public class EmployeeDummyInitializer extends FakerConfig {
    @Value("${constants.kobis-movie.key}")
    String key;

    @Test
    @Rollback(false)
    void insertEmployee() {
        final int MAX_QUITTER_COUNT = 3;
        final int MAX_ACTOR_COUNT = 2_000;

        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);

        EmployeeMapper employeeMapper = sqlSession.getMapper(EmployeeMapper.class);

        // 배우 이름 넣고 싶어서 영화인 정보 가져오는 API 호출했음..
        KobisActorListFeignClient actorListClient = Feign.builder()
                                                         .encoder(new JacksonEncoder())
                                                         .decoder(new JacksonDecoder())
                                                         .target(KobisActorListFeignClient.class, "https://kobis.or.kr/kobisopenapi/webservice/rest/people");

        // 영화인 중에 배우 아닌 사람도 있어서 걸러야 됨
        List<String> actorNameList = new ArrayList<>(MAX_ACTOR_COUNT);
        for (int page = 1; page <= 100; page++) {
            KobisActorListRes moviePeopleListResult = actorListClient.getActorList(key, String.valueOf(page));

            List<KobisActorListRes.PeopleListResult.PeopleList> moviePeople = moviePeopleListResult.getPeopleListResult().getPeopleList();
            for (KobisActorListRes.PeopleListResult.PeopleList people : moviePeople) {
                if ("배우".equals(people.getRepRoleNm()) && people.getPeopleNm().length() <= 4) {
                    actorNameList.add(people.getPeopleNm());
                }
            }
        }

        int actorNameCnt = 0;
        List<Long> theaterIds = employeeMapper.findTheaterId();
        for (Long theaterId : theaterIds) {
            int adminId = (int) (Math.random() * 4) + 1;
            int employeeCnt = (int) (Math.random() * 8) + 8;

            int existManager = 0;
            int quitterCnt = 0;

            for (int j = 1; j <= employeeCnt; j++) {
                int type = 0;
                int isActive = 0;
                String name = actorNameList.get(actorNameCnt++);
                String phone = "010-" + faker.number().digits(4) + "-" + faker.number().digits(4);

                if (existManager < 1) {
                    type = 1;
                    existManager++;
                }

                if (quitterCnt < MAX_QUITTER_COUNT) {
                    isActive = (int) (Math.random() * 2);
                    if (isActive == 1) {
                        quitterCnt++;
                        if (type == 1) {
                            existManager--;
                        }
                    }
                }

                EmployeePostReq req = EmployeePostReq.builder()
                        .theaterId(theaterId)
                        .adminId(adminId)
                        .name(name)
                        .phone(phone)
                        .type(type)
                        .isActive(isActive)
                        .build();

                employeeMapper.save(req);
            }

            sqlSession.flushStatements();
        }
    }
}
