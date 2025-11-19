package example.lotto.config;

import example.lotto.controller.LottoController;
import example.lotto.service.DrawService;
import example.lotto.service.PurchaseService;

/**
 * 애플리케이션의 실행에 필요한 모든 객체를 생성하고 서로 연결하는 설정 클래스
 */
public class AppConfig {
    private static class LazyHolder {
        public static final AppConfig INSTANCE = new AppConfig();

        public static final PurchaseService PURCHASE_SERVICE = new PurchaseService();
        public static final DrawService DRAW_SERVICE = new DrawService();
        public static final LottoController CONTROLLER = new LottoController(PURCHASE_SERVICE, DRAW_SERVICE);
    }

    private AppConfig() {}

    public static AppConfig getInstance() {
        return LazyHolder.INSTANCE;
    }

    public LottoController lottoController() {
        return LazyHolder.CONTROLLER;
    }
}
