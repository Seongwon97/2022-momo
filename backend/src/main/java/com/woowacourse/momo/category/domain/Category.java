package com.woowacourse.momo.category.domain;

import java.util.Arrays;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.woowacourse.momo.globalException.exception.ErrorCode;
import com.woowacourse.momo.globalException.exception.MomoException;

@Getter
@RequiredArgsConstructor
public enum Category {

    STUDY(1, "스터디"),
    MOCO(2, "모각코"),
    EAT(3, "식사"),
    CAFE(4, "카페"),
    DRINK(5, "술"),
    HEALTH(6, "운동"),
    GAME(7, "게임"),
    TRAVEL(8, "여행"),
    CULTURE(9, "문화생활"),
    ETC(10, "기타");

    private final long id;
    private final String name;

    public static Category from(long id) {
        return Arrays.stream(values())
                .filter(category -> category.id == id)
                .findFirst()
                .orElseThrow(() -> new MomoException(ErrorCode.CATEGORY_NOT_EXIST));
    }

    public static Category from(String name) {
        return Arrays.stream(values())
                .filter(category -> category.name.equals(name))
                .findFirst()
                .orElseThrow(() -> new MomoException(ErrorCode.CATEGORY_NOT_EXIST));
    }
}
