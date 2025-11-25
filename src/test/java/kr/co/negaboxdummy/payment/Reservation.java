package kr.co.negaboxdummy.payment;

import kr.co.negaboxdummy.config.FakerConfig;
import kr.co.negaboxdummy.payment.model.ReservationReq;
import kr.co.negaboxdummy.payment.model.ReservationCountReq;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class Reservation extends FakerConfig {

    @Autowired
    private PaymentMapper paymentMapper;

    private final double[] priceList = {7000, 11000, 14000};

    private String age(double unitPrice) {
        return switch ((int) unitPrice) {
            case 14000 -> "00201"; // 성인
            case 11000 -> "00202"; // 청소년
            case 7000 -> "00203";  // 경로/우대
            default -> "00204";
        };
    }

    @Test
    @Transactional
    void insertReservationAndCount() {

        // 1) 11/21 ~ 12/1 일정에 해당하는 스케줄 ID만 조회
        LocalDate start = LocalDate.of(2025, 11, 21);
        LocalDate end   = LocalDate.of(2025, 12, 1);

        List<Long> scheduleIdList =
                paymentMapper.findScheduleIdsByDateRange(start, end);

        // 넣고 싶은 총 예매 개수 (너가 원하는 숫자로 조절)
        int TOTAL_RESERVATION = 50_000;

        for (int i = 0; i < TOTAL_RESERVATION; i++) {

            // 2) 기간 안의 상영 일정 중 하나 랜덤 선택
            Long scheduleId = scheduleIdList.get(
                    faker.random().nextInt(0, scheduleIdList.size() - 1)
            );

            int personCnt = faker.number().numberBetween(1, 3);

            // 가격(단가)별 인원 수를 합치기 위한 map
            // value: 해당 가격의 인원 수
            Map<Double, Integer> priceToCount = new HashMap<>();

            for (int p = 0; p < personCnt; p++) {
                // 1인당 단가 랜덤 선택
                double unitPrice = priceList[(int) (Math.random() * priceList.length)];
                priceToCount.merge(unitPrice, 1, Integer::sum);
            }

            // 4) 연령대별 인원/단가를 가지고 총 금액 계산 + reservation_count용 리스트 만들기
            double totalPrice = 0;
            List<ReservationCountReq> countReqList = new ArrayList<>();

            for (Map.Entry<Double, Integer> entry : priceToCount.entrySet()) {
                double unitPrice = entry.getKey();
                int count = entry.getValue();

                String ageType = age(unitPrice); // 가격 -> 연령 코드

                totalPrice += unitPrice * count;

                countReqList.add(ReservationCountReq.builder()
                        .reservationId(null) // 나중에 세팅
                        .ageType(ageType)
                        .count(count)
                        .price(unitPrice)    // 1인당 가격
                        .build());
            }

            Long userId = faker.random().nextLong(1, 163975);
            Long nonUserId = faker.random().nextLong(1, 1869);
            int status = faker.random().nextInt(0, 2);

            LocalDateTime date = LocalDateTime.now()
                    .minusDays(faker.number().numberBetween(0, 4))
                    .minusHours(faker.number().numberBetween(0, 23))
                    .minusMinutes(faker.number().numberBetween(0, 59));

            Long[] userList = {userId, nonUserId};
            Long user = userList[(int) (Math.random() * userList.length)];

            if (user.equals(userId)) {
                nonUserId = null;
            } else {
                userId = null;
            }

            ReservationReq reservation = ReservationReq.builder()
                    .scheduleId(scheduleId)
                    .userId(userId)
                    .nonUserId(nonUserId)
                    .price(totalPrice)   // ★ 여기!
                    .status(status)
                    .createdAt(date)
                    .updatedAt(date)
                    .build();

            paymentMapper.reservationInsert(reservation);
            long reservationId = reservation.getReservationId();

            for (ReservationCountReq c : countReqList) {
                c.setReservationId(reservationId);
                paymentMapper.reservationCountInsert(c);
            }
        }
    }
}
