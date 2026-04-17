package com.dna.fooo_guard.global.util;

public class CommonUtil {
    // 값이 있으면 새 값, 없으면 기존 값 반환
    static public <T> T updateIfPresent(T current, T newValue) {
        return newValue != null ? newValue : current;
    }
}
