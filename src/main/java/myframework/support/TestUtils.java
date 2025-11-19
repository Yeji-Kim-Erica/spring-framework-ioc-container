package myframework.support;

import myframework.exception.ErrorMessage;
import myframework.exception.TestFieldInjectionException;

import java.lang.reflect.Field;

/**
 * 테스트 편의를 위한 유틸리티 클래스
 */
public final class TestUtils {
    private TestUtils() { }

    public static void inject(Object target, String fieldName, Object dependency) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, dependency);
        } catch (Exception e) {
            throw new TestFieldInjectionException(ErrorMessage.TEST_FIELD_INJECTION_FAILED.getMessage(fieldName), e);
        }
    }
}
