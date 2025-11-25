package kr.co.negaboxdummy.review;

import kr.co.negaboxdummy.config.FakerConfig;
import kr.co.negaboxdummy.review.model.ReviewInsertReq;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Review extends FakerConfig {

    @Autowired private ReviewMapper reviewMapper;
    @Autowired private JdbcTemplate jdbcTemplate;

    private static final int BATCH_SIZE = 1_000;

    private String pickRandom(String[] array) {
        int idx = faker.number().numberBetween(0, array.length);
        return array[idx];
    }

    private String generateKoreanReview() {
        String[] goodAdjectives = {"진짜", "완전", "정말", "너무", "엄청", "존나", "미쳤고", "개", "쩔고", "레전드급"};
        String[] subjects = {"영화", "스토리", "연기", "CG", "음향", "OST", "배우", "연출", "반전", "엔딩"};
        String[] goodVerbs = {"좋았어요", "미쳤어요", "감동적이었어요", "소름 돋았어요", "눈물 났어요", "웃겨 죽는 줄", "강추합니다", "인생 영화 등극", "다시 보고 싶어요", "최고예요"};
        String[] badVerbs = {"실망했어요", "기대 이하였어요", "졸았어요", "돈 아까워요", "시간 아까워요", "별로였어요", "스킵했어요"};
        String[] extras = {"또 볼 거예요", "친구한테 추천했어요", "가족이랑 또 갈 거예요", "IMAX로 다시 볼 예정", "3D로 보면 더 좋을 듯", "평점 왜 이렇게 높은지 모르겠네요", "광고만 화려했네요"};

        int caseNum = faker.number().numberBetween(1, 11);

        return switch(caseNum) {
            case 1 -> {
                String adj = pickRandom(goodAdjectives);
                String subj = pickRandom(subjects);
                String verb = pickRandom(goodVerbs);
                yield adj + " " + subj + "가 " + verb + "!";
            }
            case 2 -> {
                String subj = pickRandom(subjects);
                String verb = pickRandom(goodVerbs);
                yield subj + " 진짜 " + verb + " 우우";
            }
            case 3 -> {
                String adj = pickRandom(goodAdjectives);
                String verb = pickRandom(goodVerbs).split(" ")[0];
                yield "배우들 " + adj + " " + verb + "네요";
            }
            case 4 -> {
                String adj1 = pickRandom(goodAdjectives);
                String adj2 = pickRandom(goodAdjectives);
                String adj3 = pickRandom(goodAdjectives);
                yield adj1 + " 웃기고 " + adj2 + " 감동적이고 " + adj3 + " 재밌었어요";
            }
            case 5 -> {
                String subj = pickRandom(subjects);
                String verb = pickRandom(goodVerbs);
                yield "이 " + subj + " 때문에 " + verb + " 완전 강추!";
            }
            case 6 -> {
                String bad = pickRandom(badVerbs);
                String extra = pickRandom(extras);
                yield "솔직히 " + bad + "... " + extra;
            }
            case 7 -> {
                String adj = pickRandom(goodAdjectives);
                yield adj + " 재밌어서 시간 가는 줄 몰랐어요";
            }
            case 8 -> {
                String adj = pickRandom(goodAdjectives);
                yield "엔딩 크레딧까지 " + adj + " 집중하고 봤어요";
            }
            case 9 -> {
                String extra = pickRandom(extras);
                String verb = pickRandom(goodVerbs);
                yield extra + " " + verb;
            }
            case 10 -> {
                double score = faker.number().numberBetween(70, 99) / 10.0;
                String verb = pickRandom(goodVerbs);
                yield "평점 " + score + "점 줍니다. " + verb;
            }
            default -> "영화 잘 봤습니다~";
        } + faker.options().option("", ".", "!", "!!", "ㅋㅋ", "ㅠㅠ", "ㅎㅎ", "ㅋㅋㅋ", "ㅠㅠㅠ");
    }

    private String pickrandom(String[] array) {
        int idx = faker.number().numberBetween(0, array.length); // intValue 제거
        return array[idx];
    }

    @Test
    @Transactional
    void reviewInsert() {
        int LENGTH = 43529;
        List<ReviewInsertReq> batch = new ArrayList<>(BATCH_SIZE);

        for (int i = 0; i < LENGTH; i++) {
            ReviewInsertReq req = makeReview();
            batch.add(req);

            if (batch.size() == BATCH_SIZE || i == LENGTH - 1) {
                reviewMapper.reviewInsertBatch(batch);
                batch.clear();
                if ((i + 1) % 5000 == 0) {
                    System.out.println("진행 중... " + (i + 1) + "건 삽입됨");
                }
            }
        }
        System.out.println("영화 후기 더미 데이터 " + LENGTH + "건 삽입 완료!");
    }

    private ReviewInsertReq makeReview() {
        var row = jdbcTemplate.queryForMap(
                "SELECT user_id, movie_id, schedule_id " +
                        "FROM reservation " +
                        "WHERE status = 'COMPLETED' " +
                        "ORDER BY RAND() LIMIT 1"
        );

        Long userId = (Long) row.get("user_id");
        Long movieId = (Long) row.get("movie_id");
        Long scheduleId = (Long) row.get("schedule_id");

        int rating = faker.number().numberBetween(1, 11);
        String reviewText = generateKoreanReview();

        LocalDateTime createdAt = LocalDateTime.now()
                .minusDays(faker.number().numberBetween(1, 90))
                .withHour(faker.number().numberBetween(9, 23))
                .withMinute(faker.options().option(0, 15, 30, 45))
                .withSecond(0);

        return ReviewInsertReq.builder()
                .userId(userId)
                .movieId(movieId)
                .scheduleId(scheduleId)
                .reviewRating(rating)
                .reviewText(reviewText)
                .isDelete(0)
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .build();
    }
}