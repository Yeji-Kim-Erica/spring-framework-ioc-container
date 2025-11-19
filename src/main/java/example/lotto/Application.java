package example.lotto;

import example.lotto.config.AppConfig;
import example.lotto.controller.LottoController;

public class Application {
    public static void main(String[] args) {
        AppConfig appConfig = AppConfig.getInstance();

        LottoController controller = appConfig.lottoController();
        controller.run();
    }
}
