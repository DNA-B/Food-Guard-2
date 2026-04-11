package com.dna.fooo_guard.global;

import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Field;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;

class EntityCommentTest {

    @Test
    @DisplayName("모든 엔티티 필드에 DB comment가 있는지 검증")
    void checkEntityColumnComments() {
        // 1. 도메인 패키지 내의 클래스 스캔
        Reflections reflections = new Reflections("com.dna.fooo_guard.domain");

        // 2. @Entity 어노테이션이 붙은 모든 클래스 추출
        Set<Class<?>> entities = reflections.getTypesAnnotatedWith(Entity.class);

        StringBuilder errorMessage = new StringBuilder("\n[DB 명세용 주석/어노테이션 누락 리스트]\n");
        boolean hasError = false;

        for (Class<?> entity : entities) {
            for (Field field : entity.getDeclaredFields()) {
                // 3. 어노테이션 정보 가져오기
                Column column = field.getAnnotation(Column.class);
                JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);

                // [검증 1] 어노테이션 자체가 누락된 경우
                if (column == null && joinColumn == null) {
                    hasError = true;
                    errorMessage.append(String.format("- 클래스: %s | 필드: %s (원인: @Column 또는 @JoinColumn 어노테이션 누락)\n",
                            entity.getSimpleName(), field.getName()));
                    continue;
                }

                // [검증 2] 어노테이션은 있으나 comment 속성이 비어있는 경우
                String comment = (column != null) ? column.comment() : joinColumn.comment();
                if (comment.trim().isEmpty()) {
                    hasError = true;
                    errorMessage.append(String.format("- 클래스: %s | 필드: %s (원인: comment 속성 내용이 비어있음)\n",
                            entity.getSimpleName(), field.getName()));
                }
            }
        }

        // 4. 하나라도 누락되었다면 테스트 실패 처리 및 리스트 출력
        if (hasError) {
            fail(errorMessage.toString() + "\n=> DB 명세 자동화를 위해 위 필드들에 어노테이션과 comment를 추가해주세요.");
        }
    }

    @Test
    @DisplayName("BaseEntity의 모든 필드에 @Column 및 comment가 있는지 검증")
    void checkBaseEntityComments() {
        // 1. 검증할 클래스 지정 (BaseEntity 위치에 맞게 import 확인)
        Class<BaseEntity> baseEntity = BaseEntity.class;

        StringBuilder errorMessage = new StringBuilder("\n[BaseEntity 주석 누락 발생]\n");
        boolean hasError = false;

        // 2. BaseEntity에 선언된 모든 필드 확인
        for (Field field : baseEntity.getDeclaredFields()) {
            Column column = field.getAnnotation(Column.class);

            // [검증 1] @Column 누락
            if (column == null) {
                hasError = true;
                errorMessage.append(String.format("- 필드: %s (원인: @Column 어노테이션 누락)\n", field.getName()));
                continue;
            }

            // [검증 2] comment 속성 비어있음
            if (column.comment().trim().isEmpty()) {
                hasError = true;
                errorMessage.append(String.format("- 필드: %s (원인: comment 속성 내용이 비어있음)\n", field.getName()));
            }
        }

        if (hasError) {
            fail(errorMessage.toString() + "\n=> DB 명세 자동화를 위해 위 필드들에 어노테이션과 comment를 추가해주세요.");
        }
    }
}