package myframework.container;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

public class ApplicationContextTest {
    @DisplayName("ApplicationContext_컨테이너가_정상적으로_생성된다")
    @Test
    void returnApplicationContext() {
        // when
        ApplicationContext context = new ApplicationContext("myframework.fakepackage");

        // then
        assertThat(context).isNotNull();
    }
}
