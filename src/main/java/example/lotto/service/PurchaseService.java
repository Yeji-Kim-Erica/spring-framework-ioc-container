package example.lotto.service;

import example.lotto.domain.DepositAmount;
import example.lotto.domain.Lottos;
import example.lotto.util.RandomLottoNumberGenerator;

/**
 * 로또 구매 관련 로직을 담당하는 클래스
 */
public class PurchaseService {
    public DepositAmount depositMoney(String amount) {
        return DepositAmount.from(amount);
    }

    public Lottos purchaseLottos(DepositAmount depositAmount) {
        int quantity = depositAmount.getNumberOfPurchasableLotto();
        return Lottos.issue(quantity, new RandomLottoNumberGenerator());
    }
}
