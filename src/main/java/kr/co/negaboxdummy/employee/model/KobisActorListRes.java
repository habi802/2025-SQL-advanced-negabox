package kr.co.negaboxdummy.employee.model;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
public class KobisActorListRes {
    private PeopleListResult peopleListResult;

    @Getter
    public static class PeopleListResult {
        private List<PeopleList> peopleList;

        @Getter
        @ToString
        public static class PeopleList {
            private String peopleNm;
            private String repRoleNm;
        }
    }
}
