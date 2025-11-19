package example.lotto;

import example.lotto.controller.LottoController;
import myframework.container.ApplicationContext;

public class Application {
    public static void main(String[] args) {
        ApplicationContext context = new ApplicationContext("example.lotto");

        LottoController controller =
                context.getBean("example.lotto.controller.LottoController", LottoController.class);
        controller.run();
    }
}
