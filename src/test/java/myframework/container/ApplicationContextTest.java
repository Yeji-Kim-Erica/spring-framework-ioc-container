package myframework.container;

import myframework.exception.BeanCreationException;
import myframework.exception.NoSuchBeanException;
import myframework.test.fakepackage.FakeComponent;
import myframework.test.fakepackage.fakesubpackage.FakeSubComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class ApplicationContextTest {
    private static final String BASE_PACKAGE = "myframework.test.fakepackage";
    private static final String EXCEPTION_TEST_BASE_PACKAGE = "myframework.test.failpackage";

    private ApplicationContext context;

    @BeforeEach
    void setUp() {
        context = new ApplicationContext(BASE_PACKAGE);
    }

    @Nested
    class SuccessTest {
        @DisplayName("ApplicationContext 컨테이너가 정상적으로 생성된다")
        @Test
        void returnApplicationContext() {
            // then
            assertThat(context).isNotNull();
        }

        @DisplayName("패키지를 스캔해서 Component 어노테이션이 달린 클래스들을 Bean으로 등록한다")
        @Test
        void scanBasePackageAndRegisterComponentBeans() {
            // given
            String componentClassName = BASE_PACKAGE + ".FakeComponent";
            String subComponentClassName = BASE_PACKAGE + ".fakesubpackage.FakeSubComponent";

            // when & then
            assertThat(context.getBean(componentClassName))
                    .isNotNull()
                    .isInstanceOf(FakeComponent.class);
            assertThat(context.getBean(subComponentClassName))
                    .isNotNull()
                    .isInstanceOf(FakeSubComponent.class);
        }
    }

    @Nested
    class ExceptionTest {
        @DisplayName("등록되지 않은 클래스의 Bean을 얻으려고 할 경우 예외가 발생한다")
        @Test
        void throwException_WhenBeanNotFound() {
            // given
            String nonComponentClassName = BASE_PACKAGE + ".FakeNonComponent";
            String subNonComponentClassName = BASE_PACKAGE + ".fakesubpackage.FakeSubNonComponent";

            // when & then
            assertThatThrownBy(() -> context.getBean(nonComponentClassName))
                    .isInstanceOf(NoSuchBeanException.class);
            assertThatThrownBy(() -> context.getBean(subNonComponentClassName))
                    .isInstanceOf(NoSuchBeanException.class);
        }

        @DisplayName("기본 생성자가 없는 Component를 스캔할 경우 예외가 발생한다")
        @Test
        void throwException_WhenNoDefaultConstructor() {
            // given
            String errorPackage = EXCEPTION_TEST_BASE_PACKAGE + ".nodefaultconstructorcomponent";

            // when & then
            assertThatThrownBy(() -> new ApplicationContext(errorPackage))
                    .isInstanceOf(BeanCreationException.class)
                    .hasCauseInstanceOf(NoSuchMethodException.class);
        }

        @DisplayName("추상 클래스 Component를 스캔할 경우 예외가 발생한다")
        @Test
        void throwException_WhenAbstractClass() {
            // given
            String errorPackage = EXCEPTION_TEST_BASE_PACKAGE + ".abstractcomponent";

            // when & then
            assertThatThrownBy(() -> new ApplicationContext(errorPackage))
                    .isInstanceOf(BeanCreationException.class)
                    .hasCauseInstanceOf(InstantiationException.class);
        }
    }
}
