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
import jakarta.persistence.OneToMany;

class EntityCommentTest {

    @Test
    @DisplayName("모든 엔티티 필드에 DB comment가 있는지 검증")
    void checkEntityColumnComments() {
        StringBuilder errorMessage = new StringBuilder("\n[DB 명세용 주석/어노테이션 누락 리스트]\n");
        boolean hasError = false;

        for (Class<?> entity : findEntityTypes()) {
            hasError |= validateEntityComments(entity, errorMessage);
        }

        if (hasError) {
            fail(errorMessage.toString() + "\n=> DB 명세 자동화를 위해 위 필드들에 어노테이션과 comment를 추가해주세요.");
        }
    }

    @Test
    @DisplayName("BaseEntity의 모든 필드에 @Column 및 comment가 있는지 검증")
    void checkBaseEntityComments() {
        StringBuilder errorMessage = new StringBuilder("\n[BaseEntity 주석 누락 발생]\n");
        boolean hasError = validateBaseEntityComments(errorMessage);

        if (hasError) {
            fail(errorMessage.toString() + "\n=> DB 명세 자동화를 위해 위 필드들에 어노테이션과 comment를 추가해주세요.");
        }
    }

    // ------------------ [HELPERS] ------------------
    private static Set<Class<?>> findEntityTypes() {
        Reflections reflections = new Reflections("com.dna.fooo_guard.domain");
        return reflections.getTypesAnnotatedWith(Entity.class);
    }

    private static boolean validateEntityComments(Class<?> entity, StringBuilder errorMessage) {
        boolean hasError = false;

        for (Field field : entity.getDeclaredFields()) {
            if (field.getAnnotation(OneToMany.class) != null) {
                continue;
            }

            Column column = field.getAnnotation(Column.class);
            JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);

            if (column == null && joinColumn == null) {
                hasError = true;
                appendError(errorMessage, entity.getSimpleName(), field.getName(),
                        "원인: @Column 또는 @JoinColumn 어노테이션 누락");
                continue;
            }

            String comment = (column != null) ? column.comment() : joinColumn.comment();
            if (comment.trim().isEmpty()) {
                hasError = true;
                appendError(errorMessage, entity.getSimpleName(), field.getName(), "원인: comment 속성 내용이 비어있음");
            }
        }

        return hasError;
    }

    private static boolean validateBaseEntityComments(StringBuilder errorMessage) {
        boolean hasError = false;

        for (Field field : BaseEntity.class.getDeclaredFields()) {
            Column column = field.getAnnotation(Column.class);

            if (column == null) {
                hasError = true;
                appendError(errorMessage, "BaseEntity", field.getName(), "원인: @Column 어노테이션 누락");
                continue;
            }

            if (column.comment().trim().isEmpty()) {
                hasError = true;
                appendError(errorMessage, "BaseEntity", field.getName(), "원인: comment 속성 내용이 비어있음");
            }
        }

        return hasError;
    }

    private static void appendError(StringBuilder errorMessage, String className, String fieldName, String reason) {
        errorMessage.append(String.format("- 클래스: %s | 필드: %s (%s)\n", className, fieldName, reason));
    }
}