package example.lotto.service;

import example.lotto.domain.DepositAmount;
import example.lotto.domain.Lotto;
import example.lotto.domain.Lottos;
import example.lotto.util.LottoNumberGenerator;
import example.lotto.util.RandomLottoNumberGenerator;
import myframework.annotation.Autowired;
import myframework.annotation.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 로또 구매 관련 로직을 담당하는 클래스
 */
@Component
public class PurchaseService {
    private static int LOTTO_PRICE = 1000;

    @Autowired
    private LottoNumberGenerator lottoNumberGenerator;

    public DepositAmount depositMoney(String amount) {
        return DepositAmount.from(amount);
    }

    public Lottos purchaseLottos(DepositAmount depositAmount) {
        int quantity = depositAmount.getNumberOfPurchasableLotto(LOTTO_PRICE);
        List<Lotto> lottos = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            List<Integer> lottoNumbers = lottoNumberGenerator.generateUniqueNumbersInRange();
            Lotto lotto = new Lotto(lottoNumbers);
            lottos.add(lotto);
        }
        return new Lottos(lottos);
    }
}
