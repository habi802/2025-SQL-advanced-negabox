package kr.co.negaboxdummy.movie;

import kr.co.negaboxdummy.config.FakerConfig;
import kr.co.negaboxdummy.movie.model.ScreenSchedulePostReq;
import kr.co.negaboxdummy.movie.model.TheaterMovieGetRes;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ScreenScheduleDummyInitializer extends FakerConfig {
    private static final Random r = new Random();

    private static final String SCREEN_TIME_MORNING = "00301";
    private static final String SCREEN_TIME_NORMAL = "00302";
    private static final String SCREEN_TIME_NIGHT = "00303";

    // 상영 시작 시간 랜덤으로 뽑아내는 메소드
    private static LocalTime randomTimeInRange(int startHour, int endHour) {
        int hour = startHour + r.nextInt(endHour - startHour + 1); // 포함
        int fiveMinuteBlock = r.nextInt(12); // 0~55분 5분 단위
        int minute = fiveMinuteBlock * 5;
        return LocalTime.of(hour, minute, 0);
    }

    // 상영 종료 시간 계산하는 메소드 - 상영 시작 시간 + (상영 시간 + 10분), 분의 끝자리가 0이나 5가 아니면 0이나 5가 되게 만들어 줘야 함
    private static LocalTime calcEndTime(LocalTime start, int runtimeMinutes) {
        LocalTime end = start.plusMinutes(runtimeMinutes + 10);

        int remainder = end.getMinute() % 5;
        if (remainder != 0) {
            end = end.plusMinutes(5 - remainder);
        }

        return end.withSecond(0);
    }

    // 상영 시간 분류(조조, 일반, 심야) 계산하는 메소드
    private static String getScreenTime(LocalTime time) {
        LocalTime morningStart = LocalTime.of(6, 0);
        LocalTime morningEnd = LocalTime.of(11, 0);

        LocalTime normalStart = LocalTime.of(11, 0);
        LocalTime normalEnd = LocalTime.of(23, 0);

//        LocalTime nightStart = LocalTime.of(23, 0);
//        LocalTime nightEnd = LocalTime.of(3, 0);

        if (!time.isBefore(morningStart) && time.isBefore(morningEnd)) {
            return SCREEN_TIME_MORNING;
        } else if (!time.isBefore(normalStart) && time.isBefore(normalEnd)) {
            return SCREEN_TIME_NORMAL;
        } else {
            return SCREEN_TIME_NIGHT;
        }
    }

    @Test
    @Rollback(false)
    void insertSchedule() {
        final int MAX_SCHEDULE_COUNT = 4;
        final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);

        ScreenScheduleMapper screenScheduleMapper = sqlSession.getMapper(ScreenScheduleMapper.class);

        List<TheaterMovieGetRes> theaterMovieList = screenScheduleMapper.findInTheaterMovie();
        //System.out.println("list.size: " + theaterMovieList.size());
        for (TheaterMovieGetRes movie : theaterMovieList) {
            System.out.println("movie: " + movie);
            // 하루 상영 회차(1~4) 랜덤으로 뽑기
            int scheduleCnt = r.nextInt(MAX_SCHEDULE_COUNT) + 1;
            System.out.println("schedule: " + scheduleCnt);
            for (int i = 1; i <= scheduleCnt; i++) {
                // 상영 시작 기간부터 상영 종료 기간까지 하루에 1~4회씩 상영 일정 넣기
                LocalDate startDate = LocalDate.parse(movie.getStartDate(), DATE_FORMATTER);
                LocalDate endDate = LocalDate.parse(movie.getEndDate(), DATE_FORMATTER);
                for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                    // 회차에 따라 상영 시작 시간 들어가는 범위가 달라짐(1회차는 아무 시간대 가능)
                    List<int[][]> timeSlots = new ArrayList<>(MAX_SCHEDULE_COUNT);
                    timeSlots.add(new int[][]{{9, 11}, {18, 23}}); // 2회차
                    timeSlots.add(new int[][]{{9, 11}, {14, 16}, {20, 23}}); // 3회차
                    timeSlots.add(new int[][]{{9, 10}, {13, 14}, {17, 18}, {21, 22}}); // 4회차
                    //timeSlots.add(new int[][]{{9, 10}, {12, 13}, {15, 16}, {18, 19}, {21, 22}}); // 5회차
                    //timeSlots.add(new int[][]{{9, 10}, {11, 12}, {13, 14}, {15, 16}, {18, 19}, {20, 22}}); // 6회차

                    List<String> startTimes = new ArrayList<>(scheduleCnt);
                    List<String> endTimes = new ArrayList<>(scheduleCnt);
                    List<String> screenTimes = new ArrayList<>(scheduleCnt);

                    // 상영 시작 시간, 상영 종료 시간, 상영 시간 분류
                    if (scheduleCnt == 1) {
                        LocalTime startLocalTime = randomTimeInRange(9, 23);
                        LocalTime endLocalTime = calcEndTime(startLocalTime, movie.getRunningTime());

                        startTimes.add(startLocalTime.format(TIME_FORMATTER));
                        endTimes.add(endLocalTime.format(TIME_FORMATTER));
                        screenTimes.add(getScreenTime(startLocalTime));
                    } else {
                        int[][] slots = timeSlots.get(scheduleCnt - 2);
                        for(int[] slot : slots) {
                            LocalTime startLocalTime = randomTimeInRange(slot[0], slot[1]);
                            LocalTime endLocalTime = calcEndTime(startLocalTime, movie.getRunningTime());

                            startTimes.add(startLocalTime.format(TIME_FORMATTER));
                            endTimes.add(endLocalTime.format(TIME_FORMATTER));
                            screenTimes.add(getScreenTime(startLocalTime));
                        }
                    }

                    // 상영일
                    String runningDate = date.format(DATE_FORMATTER);

                    for (int j = 0; j < startTimes.size(); j++) {
                        ScreenSchedulePostReq req = ScreenSchedulePostReq.builder()
                                                                         .screenId(movie.getScreenId())
                                                                         .screenType(movie.getScreenType())
                                                                         .screenTime(screenTimes.get(j))
                                                                         .movieId(movie.getMovieId())
                                                                         .employeeId(movie.getEmployeeId())
                                                                         .runningDate(runningDate)
                                                                         .startTime(startTimes.get(j))
                                                                         .endTime(endTimes.get(j))
                                                                         .build();

                        screenScheduleMapper.save(req);
                    }
                }
            }

            sqlSession.flushStatements();
        }

        sqlSession.close();
    }
}
