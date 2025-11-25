package kr.co.negaboxdummy.event;

import kr.co.negaboxdummy.config.FakerConfig;
import kr.co.negaboxdummy.event.model.EventInsertReq;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

public class Event extends FakerConfig {

    @Autowired private EventMapper eventMapper;

    @Test
    void eventInsert() {
        int LENGTH = 16279;
        for (int i = 0; i < LENGTH; i++) {
            EventInsertReq req = makeEvent();
            eventMapper.eventInsert(req);
        }
    }

    private EventInsertReq makeEvent() {

        int[] eventList = {1,3,4,5,6,7,8,9,10,11,12};

        int eventId = eventList[(int) (Math.random() * eventList.length)];
        int userId = faker.random().nextInt(1, 163975);

        LocalDateTime partDate = LocalDateTime.now()
                .minusDays(faker.number().numberBetween(0, 7))
                .plusHours(faker.number().numberBetween(0, 23))
                .minusMinutes(faker.number().numberBetween(0, 59));

        int status = faker.number().numberBetween(0, 2);

        LocalDateTime updateDate;

        if (status == 1 || status == 2) {
            updateDate = LocalDateTime.now();
        } else {
            updateDate = partDate;
        }

        return EventInsertReq.builder()
                .eventId(eventId)
                .userId(userId)
                .partDate(partDate)
                .status(status)
                .createdAt(partDate)
                .updatedAt(updateDate).build();
    }
}
