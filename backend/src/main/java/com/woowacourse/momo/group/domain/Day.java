package com.woowacourse.momo.group.domain;

import java.util.Arrays;

import com.woowacourse.momo.group.exception.InvalidDayException;
import lombok.Getter;

@Getter
public enum Day {

    MONDAY("월"),
    TUESDAY("화"),
    WEDNESDAY("수"),
    THURSDAY("목"),
    FRIDAY("금"),
    SATURDAY("토"),
    SUNDAY("일");

    private final String value;

    Day(String value) {
        this.value = value;
    }

    public static Day from(String value) {
        return Arrays.stream(values())
                .filter(day -> day.value.equals(value))
                .findAny()
                .orElseThrow(InvalidDayException::new);
    }
}
