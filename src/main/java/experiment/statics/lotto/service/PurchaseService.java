package experiment.statics.lotto.service;

import experiment.statics.lotto.domain.DepositAmount;
import experiment.statics.lotto.domain.Lotto;
import experiment.statics.lotto.domain.Lottos;
import experiment.statics.lotto.util.RandomLottoNumberGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * 로또 구매 관련 로직을 담당하는 클래스
 */
public class PurchaseService {
    private static final int LOTTO_PRICE = 1000;

    private PurchaseService() {}

    public static DepositAmount depositMoney(String amount) {
        return DepositAmount.from(amount);
    }

    public static Lottos purchaseLottos(DepositAmount depositAmount) {
        int quantity = depositAmount.getNumberOfPurchasableLotto(LOTTO_PRICE);
        List<Lotto> lottos = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            List<Integer> lottoNumbers = RandomLottoNumberGenerator.generateUniqueNumbersInRange();
            Lotto lotto = new Lotto(lottoNumbers);
            lottos.add(lotto);
        }
        return new Lottos(lottos);
    }
}
