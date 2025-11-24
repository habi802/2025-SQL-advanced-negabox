package kr.co.negaboxdummy.user;

import kr.co.negaboxdummy.config.FakerConfig;
import kr.co.negaboxdummy.user.model.NonUserJoinReq;
import kr.co.negaboxdummy.user.model.UserJoinReq;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class UserDummyInsertTest extends FakerConfig {

    @Autowired
    private UserMapper userMapper;

    private String randomUserId() {
        // 1) 영문 길이 랜덤 (3~8자)
        int letterCount = faker.number().numberBetween(3, 8);

        // 2) 숫자 길이 랜덤 (2~4자)
        int numberCount = faker.number().numberBetween(2, 4);

        // 3) 영문 생성
        String letters = faker.letterify("?".repeat(letterCount)).toLowerCase();

        // 4) 숫자 생성
        String numbers = faker.number().digits(numberCount);

        // 5) 영문 + 숫자 이어붙임
        return letters + numbers;
    }

    @Transactional
    @Test
    void insertDummyUsers() {
        int LENGTH = 163975;
        for (int i = 0; i < LENGTH; i++) {
            UserJoinReq req = makeUser();
            userMapper.userJoin(req);
        }
    }

    private UserJoinReq makeUser() {

        LocalDate birth = LocalDate.of(
                faker.number().numberBetween(1970, 2011),
                faker.number().numberBetween(1, 12),
                faker.number().numberBetween(1, 28)
        );

        LocalDateTime createdAt = LocalDateTime.now()
                .minusDays(faker.number().numberBetween(0, 365))
                .minusHours(faker.number().numberBetween(0, 23))
                .minusMinutes(faker.number().numberBetween(0, 59));

        LocalDateTime updatedAt = createdAt.plusDays(
                faker.number().numberBetween(0, 30)
        );
        // 랜덤 로그인 아이디 (영문+숫자)
        String userIdStr = randomUserId();

        // 이메일 = 아이디 + 도메인
        String email = userIdStr + faker.internet().emailAddress();

        // 이름의 공백 제거 (성/이름 붙이기)
        String name = faker.name().fullName().replaceAll("\\s+", "");

        // Faker 비밀번호 + BCrypt 암호화했다고 가정
        String rawPassword = "$2a$12$m7qvhiSgQeqtxq.FiT.Wbe0BuMKfWgoLzemM82RIP2KaOE7fFzZSS";
//        String encodedPassword = passwordEncoder.encode(rawPassword);

        return UserJoinReq.builder()
                .membershipId(faker.number().numberBetween(1, 4))
                .name(name)
                .email(email)
                .password(rawPassword)
                .birth(birth)
                .carrierCode(randomCarrier())
                .phone(randomPhone())
                .point(faker.number().randomDouble(2, 0, 30000))
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .cardNum(makeCardNumber())
                .gradeUpdatedAt(createdAt)
                .build();
    }

    private String randomCarrier() {
        String[] list = {"00901", "00902", "00903"};
        return list[faker.number().numberBetween(0, list.length)];
    }

    private String randomPhone() {
        return "010-" + faker.number().digits(4) + "-" + faker.number().digits(4);
    }

    private String makeCardNumber() {
        return faker.number().digits(4) + "-" +
                faker.number().digits(4) + "-" +
                faker.number().digits(4) + "-" +
                faker.number().digits(4);
    }

    @Transactional
    @Test
    void insertDummyNonUsers() {
        int LENGTH = 1869;
        for (int i = 0; i < LENGTH; i++) {
            NonUserJoinReq req = makeNonUser();
            userMapper.nonUserJoin(req);
        }
    }

    private NonUserJoinReq makeNonUser() {

        String name = faker.name().fullName().replaceAll("\\s+", "");

        String rawPassword = "$2a$12$m7qvhiSgQeqtxq.FiT.Wbe0BuMKfWgoLzemM82RIP2KaOE7fFzZSS";

        LocalDateTime createdAt = LocalDateTime.now()
                .plusDays(faker.number().numberBetween(0, 7))
                .plusHours(faker.number().numberBetween(0, 23))
                .plusMinutes(faker.number().numberBetween(0, 59));

        LocalDateTime updatedAt = createdAt.plusDays(7);

        return NonUserJoinReq.builder()
                .name(name)
                .phone(randomPhone())
                .birth(randomBrith())
                .password(rawPassword)
                .createdAt(createdAt)
                .expireAt(updatedAt)
                .build();
    }

    private String randomBrith() {
        int year = faker.number().numberBetween(1970, 2011);
        int month = faker.number().numberBetween(1, 12);
        int day = faker.number().numberBetween(1, 28);

        // YYMMDD 포맷
        String yy = String.format("%02d", year % 100);
        String mm = String.format("%02d", month);
        String dd = String.format("%02d", day);

        return yy + mm + dd;
    }
}
