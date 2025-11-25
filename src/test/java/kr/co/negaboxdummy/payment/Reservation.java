package kr.co.negaboxdummy.payment;


import kr.co.negaboxdummy.config.FakerConfig;
import kr.co.negaboxdummy.payment.model.ReservationReq;
import kr.co.negaboxdummy.user.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

public class Reservation extends FakerConfig {

    @Autowired private PaymentMapper paymentMapper;

    @Test
    void insertReservation() {
        int LENGTH = 8953;
        for (int i = 0; i < LENGTH; i++) {
            ReservationReq req = makeReservation();
            paymentMapper.ReservationInsert(req);
        }
    }

    private double[] priceList = {28000, 14000, 22000, 11000, 14000, 7000};
    private ReservationReq makeReservation() {

        int scheduleId = faker.random().nextInt(1, 3097672);
        Long userId = faker.random().nextLong(1, 163975);
        double price = priceList[(int)(Math.random() * priceList.length)];
        int status = faker.random().nextInt(0, 2);
        LocalDateTime date = LocalDateTime.now()
                .minusDays(faker.number().numberBetween(0, 10))
                .minusHours(faker.number().numberBetween(0, 23))
                .minusMinutes(faker.number().numberBetween(0, 59));

        return ReservationReq.builder()
                .scheduleId(scheduleId)
                .userId(userId)
                .price(price)
                .status(status)
                .createdAt(date)
                .updatedAt(date)
                .build();
    }
}
