package kr.co.negaboxdummy.seat;

import kr.co.negaboxdummy.config.FakerConfig;
import kr.co.negaboxdummy.seat.model.ScreenGetRes;
import kr.co.negaboxdummy.seat.model.SeatPostReq;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.Rollback;

import java.util.List;

public class SeatDummyInitializer extends FakerConfig {
    @Test
    @Rollback(false)
    void insertSeat() {
        final String SCREEN_RECLINER = "00103";

        // ExecutorType.BATCH
        // 쿼리를 실행할 때마다 DB 서버로 바로 보내지 않고, flushStatements()를 호출했을 때 한 번에 보냄
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);

        SeatMapper seatMapper = sqlSession.getMapper(SeatMapper.class);
        // 상영관 데이터를 조회하는 메소드를 호출한 뒤, 상영관 개수만큼 좌석 데이터를 넣는 for문을 실행
        List<ScreenGetRes> screens = seatMapper.findScreenIdAndScreenType();

        for (ScreenGetRes screen : screens) {
            int row, col;

            if (SCREEN_RECLINER.equals(screen.getScreenType())) {
                // 시작 행: A(65)
                // 리클라이너 최저 행: F(70)
                // 리클라이너 최대 행: H(72)

                // 리클라이너 최저 열: 10
                // 리클라이너 최대 열: 16
                row = (int)(Math.random() * 3) + 70;
                col = (int)(Math.random() * 7) + 10;
            } else {
                // 일반 최저 행: D(68)
                // 일반 최대 행: K(75)

                // 일반 최저 열: 7
                // 일반 최대 열: 20
                row = (int)(Math.random() * 8) + 68;
                col = (int)(Math.random() * 14) + 7;
            }

            for (int i = 65; i <= row; i++) {
                char rowLabel = (char) i;

                for (int j = 1; j <= col; j++) {
                    String colNo = String.format("%02d", j);

                    int type;
                    // 장애인석은 맨 앞 제일 왼쪽 2개, 맨 앞 제일 오른쪽 2개
                    if (i == 65 && (j <= 2 || j >= col - 1)) {
                        type = 1;
                    } else {
                        type = 0;
                    }

                    SeatPostReq req = SeatPostReq.builder()
                                                 .screenId(screen.getScreenId())
                                                 .rowLabel(rowLabel)
                                                 .colNo(colNo)
                                                 .type(type)
                                                 .build();

                    //System.out.println("req: " + req.toString());

                    seatMapper.save(req);
                }

                sqlSession.flushStatements();
            }
        }

        sqlSession.close();
    }
}
