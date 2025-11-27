// src/test/java/kr/co/negaboxdummy/payment/ReservationSeat.java
package kr.co.negaboxdummy.payment;

import kr.co.negaboxdummy.config.FakerConfig;
import kr.co.negaboxdummy.payment.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class ReservationSeat extends FakerConfig {

    @Autowired
    private PaymentMapper paymentMapper;

    /**
     * 이미 넣어둔 reservation / reservation_count 더미 데이터를 기준으로
     *  - 각 예매별 인원수만큼
     *  - 같은 행(row_label)에서 붙어 있는 좌석을 골라서
     *  - reservation_seat, reservation_seat_list 더미를 생성한다.
     *
     *  각 상영일정별로 상영관 좌석의 70~80%까지만 채운다.
     */
    @Test
    @Transactional
    void insertReservationSeatDummy() {

        LocalDate start = LocalDate.of(2025, 11, 21);
        LocalDate end   = LocalDate.of(2025, 12, 1);

        List<ScheduleInfo> schedules =
                paymentMapper.findSchedulesWithScreenByDateRange(start, end);

        Map<Long, List<SeatInfo>> seatCache = new HashMap<>();

        for (ScheduleInfo schedule : schedules) {
            long scheduleId = schedule.getScheduleId();
            Long screenId   = schedule.getScreenId();

            List<SeatInfo> seats = seatCache.computeIfAbsent(
                    screenId,
                    id -> paymentMapper.findSeatsByScreenId(id)
            );

            if (seats == null || seats.isEmpty()) continue;

            int totalSeats = seats.size();

            List<ReservationPerson> reservations =
                    paymentMapper.findReservationsWithPersonCountBySchedule(scheduleId);

            if (reservations.isEmpty()) continue;

            double ratio = faker.random().nextDouble(0.7, 0.8);
            int targetBooked = (int) Math.round(totalSeats * ratio);

            boolean[] occupied = new boolean[totalSeats];
            int usedSeats = 0;

            Collections.shuffle(reservations, new Random());

            for (ReservationPerson rp : reservations) {

                int groupSize = rp.getPersonCount();
                if (groupSize <= 0) continue;
                if (usedSeats + groupSize > targetBooked) continue;

                // 예매 생성 시간 조회 (reservation.created_at)
                LocalDateTime reservationCreatedAt =
                        paymentMapper.findReservationCreatedAt(rp.getReservationId());

                List<Integer> seatIndexes =
                        findAdjacentSeatsByRow(occupied, seats, groupSize);

                if (seatIndexes.isEmpty()) continue;

                for (int idx : seatIndexes) {
                    occupied[idx] = true;

                    SeatInfo seat = seats.get(idx);

                    // (1) reservation_seat insert
                    ReservationSeatReq rs = ReservationSeatReq.builder()
                            .scheduleId(scheduleId)
                            .seatId(seat.getSeatId())
                            .createdAt(reservationCreatedAt)   // 수정: 예매 생성시간으로
                            .build();

                    paymentMapper.insertReservationSeat(rs);
                    long reservationSeatId = rs.getReservationSeatId();

                    // (2) reservation_seat_list insert
                    ReservationSeatListReq rsl = ReservationSeatListReq.builder()
                            .reservationId(rp.getReservationId())
                            .reservationSeatId(reservationSeatId)
                            .build();

                    paymentMapper.insertReservationSeatList(rsl);
                }

                usedSeats += groupSize;
            }
        }
    }

    private List<Integer> findAdjacentSeatsByRow(boolean[] occupied,
                                                 List<SeatInfo> seats,
                                                 int groupSize) {

        Map<String, List<Integer>> rowMap = new LinkedHashMap<>();
        for (int i = 0; i < seats.size(); i++) {
            String row = seats.get(i).getRowLabel();
            rowMap.computeIfAbsent(row, k -> new ArrayList<>()).add(i);
        }

        List<String> rows = new ArrayList<>(rowMap.keySet());
        Collections.shuffle(rows, new Random());

        for (String row : rows) {
            List<Integer> idxList = rowMap.get(row);
            if (idxList == null || idxList.size() < groupSize) continue;

            List<Integer> result = findAdjacentInOneRow(idxList, occupied, groupSize);
            if (!result.isEmpty()) return result;
        }

        return Collections.emptyList();
    }

    private List<Integer> findAdjacentInOneRow(List<Integer> idxList,
                                               boolean[] occupied,
                                               int groupSize) {

        int n = idxList.size();

        for (int start = 0; start <= n - groupSize; start++) {
            boolean ok = true;
            List<Integer> temp = new ArrayList<>();

            for (int k = 0; k < groupSize; k++) {
                int seatIdx = idxList.get(start + k);
                if (occupied[seatIdx]) {
                    ok = false;
                    break;
                }
                temp.add(seatIdx);
            }

            if (ok) return temp;
        }

        return Collections.emptyList();
    }
}
