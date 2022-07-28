package com.woowacourse.momo.group.domain.group;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static com.woowacourse.momo.fixture.DateTimeFixture._6월_30일_23시_59분;
import static com.woowacourse.momo.fixture.DurationFixture._7월_1일부터_2일까지;
import static com.woowacourse.momo.fixture.ScheduleFixture._7월_1일_10시부터_12시까지;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.woowacourse.momo.category.domain.Category;
import com.woowacourse.momo.group.domain.schedule.Schedule;
import com.woowacourse.momo.member.domain.Member;

class GroupTest {

    private Member host = new Member("주최자", "password", "momo");

    @DisplayName("회원이 모임의 생성자면 True를 반환한다.")
    @Test
    void isSameHost() {
        Group group = constructGroup();
        boolean actual = group.isSameHost(host);

        assertThat(actual).isTrue();
    }

    @DisplayName("회원이 모임의 생성자가 아니면 false를 반환한다.")
    @Test
    void isNotSameHost() {
        Group group = constructGroup();
        Member member = new Member("주최자 아님", "password", "momo");
        boolean actual = group.isSameHost(member);

        assertThat(actual).isFalse();
    }

    @DisplayName("모임에 참여한다")
    @Test
    void participate() {
        Group group = constructGroup();
        Member member = new Member("momo@woowa.com", "qwer123!@#", "모모");
        group.participate(member);

        assertThat(group.getParticipants()).hasSize(2);
    }

    @DisplayName("이미 참여한 모임에 참가할 경우 예외가 발생한다")
    @Test
    void validateParticipant() {
        Group group = constructGroup();
        Member member = new Member("momo@woowa.com", "qwer123!@#", "모모");
        group.participate(member);
        assertThatThrownBy(() -> group.participate(member))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 참여한 모임입니다.");
    }

    @DisplayName("정원이 가득찬 모임에 참가할 경우 예외가 발생한다")
    @Test
    void validateOvercapacity() {
        int capacity = 2;
        Group group = constructGroupWithSetcapacity(capacity);
        Member member1 = new Member("momo@woowa.com", "qwer123!@#", "모모");
        group.participate(member1);

        Member member2 = new Member("dudu@woowa.com", "qwer123!@#", "두두");
        assertThatThrownBy(() -> group.participate(member2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("정원이 가득 찼습니다.");
    }

    @DisplayName("모임에 참여한 회원을 반환한다")
    @Test
    void getParticipants() {
        Group group = constructGroup();
        Member member = new Member("momo@woowa.com", "qwer123!@#", "모모");
        group.participate(member);

        List<Member> participants = group.getParticipants();
        assertThat(participants).contains(host, member);
    }

    private Group constructGroup() {
        return constructGroupWithSetcapacity(10);
    }

    private Group constructGroupWithSetcapacity(int capacity) {
        List<Schedule> schedules = List.of(_7월_1일_10시부터_12시까지.newInstance());
        return new Group("momo 회의", host, Category.STUDY, capacity, _7월_1일부터_2일까지.getInstance(),
                _6월_30일_23시_59분.getInstance(),
                schedules, "", "");
    }
}
