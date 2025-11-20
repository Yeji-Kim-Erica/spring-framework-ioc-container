package myframework.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import myframework.exception.TestFieldInjectionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class TestUtilsTest {
    static class TestTarget {
        private String field;
    }

    static class TestInheritedTarget extends TestTarget {
    }

    @Nested
    class SuccessTest {
        @DisplayName("private 필드에 값을 강제로 주입한다")
        @Test
        void shouldSetPrivateField() {
            // given
            TestTarget target = new TestTarget();
            String value = "dependency";

            // when
            TestUtils.inject(target, "field", value);

            // then
            assertThat(target).extracting("field").isEqualTo(value);
        }

        @DisplayName("부모에게 물려받은 필드에 값을 강제로 주입한다")
        @Test
        void shouldSetInheritedField() {
            // given
            TestInheritedTarget target = new TestInheritedTarget();
            String value = "dependency";

            // when
            TestUtils.inject(target, "field", value);

            // then
            assertThat(target).extracting("field").isEqualTo(value);
        }
    }

    @Nested
    class ExceptionTest {
        @DisplayName("존재하지 않는 필드에 주입하려고 하면 예외가 발생한다")
        @Test
        void throwException_WhenFieldnotExists() {
            // given
            TestTarget target = new TestTarget();

            // when & then
            assertThatThrownBy(() -> TestUtils.inject(target, "wrongField", "value"))
                    .isInstanceOf(TestFieldInjectionException.class);
        }
    }
}
