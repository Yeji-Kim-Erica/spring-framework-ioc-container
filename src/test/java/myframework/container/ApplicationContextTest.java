package myframework.container;

import myframework.exception.BeanCreationException;
import myframework.exception.DependencyInjectionException;
import myframework.exception.NoSuchBeanException;
import myframework.test.fakepackage.FakeAutowiredComponent;
import myframework.test.fakepackage.FakeComponent;
import myframework.test.fakepackage.FakeComponentWithInheritance;
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

        @DisplayName("빈 이름과 타입을 함께 제공하면 캐스팅된 빈을 반환한다")
        @Test
        void returnCastedBeanForBeanWithTypeName() {
            // given
            String componentName = BASE_PACKAGE + ".FakeComponent";

            // when & then
            assertThat(context.getBean(componentName, FakeComponent.class)).isNotNull()
                    .isInstanceOf(FakeComponent.class);
        }

        @DisplayName("Autowired 어노테이션으로 빈 저장소의 해당 클래스 빈을 주입받는다.")
        @Test
        void injectBeanFromBeanRegistry_WhenAnnotatedAsAutowired() {
            // given
            String componentName = BASE_PACKAGE + ".FakeComponent";
            String autowiredComponentName = BASE_PACKAGE + ".FakeAutowiredComponent";

            // when
            FakeComponent component = context.getBean(componentName, FakeComponent.class);

            // then
            assertThat(component).extracting("autowiredComponent").isNotNull()
                    .isEqualTo(context.getBean(autowiredComponentName, FakeAutowiredComponent.class));
        }

        @DisplayName("상속받은 필드에도 빈을 주입받는다.")
        @Test
        void injectBeanForInheritedField() {
            // given
            String componentName = BASE_PACKAGE + ".FakeComponentWithInheritance";
            String autowiredComponentName = BASE_PACKAGE + ".FakeAutowiredComponent";

            // when
            FakeComponentWithInheritance component = context.getBean(componentName, FakeComponentWithInheritance.class);

            // then
            assertThat(component).extracting("autowiredComponent").isNotNull()
                    .isEqualTo(context.getBean(autowiredComponentName, FakeAutowiredComponent.class));
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

        @DisplayName("빈의 이름은 존재하지만 요청한 타입과 다를 경우 예외가 발생한다")
        @Test
        void throwException_WhenTypeMismatch() {
            // given
            String componentName = BASE_PACKAGE + ".FakeComponent";

            // when & then
            assertThatThrownBy(() -> context.getBean(componentName, String.class))
                    .isInstanceOf(NoSuchBeanException.class)
                    .hasMessageContaining("FakeComponent");
        }

        @DisplayName("Autowired 대상인 빈이 존재하지 않으면(Component 누락) 예외가 발생한다")
        @Test
        void throwException_WhenAutowiredBeanMissing() {
            // given
            String errorPackage = "myframework.test.failpackage.missingdependency";

            // when & then
            assertThatThrownBy(() -> new ApplicationContext(errorPackage))
                    .isInstanceOf(NoSuchBeanException.class);
        }
    }
}
