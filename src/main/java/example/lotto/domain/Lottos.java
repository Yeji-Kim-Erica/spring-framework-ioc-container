package example.lotto.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 로또 목록을 관리하는 일급 컬렉션 클래스
 */
public class Lottos {
    private final List<Lotto> lottos;

    public Lottos(List<Lotto> lottos) {
        this.lottos = lottos;
    }

    public List<Lotto> getLottos() {
        return Collections.unmodifiableList(lottos);
    }

    public int size() {
        return lottos.size();
    }

    public List<Prize> getPrizeResults(WinningNumbers winningNumbers, BonusNumber bonusNumber) {
        List<Prize> prizes = new ArrayList<>();
        for (Lotto lotto : lottos) {
            int winningNumbersMatchCount = lotto.countMatchingWinningNumbers(winningNumbers);
            boolean hasMatchingBonusNumber = lotto.hasMatchingBonusNumber(bonusNumber);
            Prize prize = Prize.of(winningNumbersMatchCount, hasMatchingBonusNumber);
            prizes.add(prize);
        }
        return prizes;
    }
}
