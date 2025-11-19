package example.lotto.domain;

import example.lotto.util.LottoNumberGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 로또 목록을 관리하는 일급 컬렉션 클래스
 */
public class Lottos {
    private final List<Lotto> lottos;

    private Lottos(List<Lotto> lottos) {
        this.lottos = lottos;
    }

    public static Lottos issue(int quantity, LottoNumberGenerator lottoNumberGenerator) {
        List<Lotto> lottos = new ArrayList<>(quantity);
        for (int i = 1; i <= quantity; i++) {
            Lotto lotto = generateLotto(lottoNumberGenerator);
            lottos.add(lotto);
        }
        return new Lottos(lottos);
    }

    public List<Lotto> getLottos() {
        return Collections.unmodifiableList(lottos);
    }

    public int size() {
        return lottos.size();
    }

    private static Lotto generateLotto(LottoNumberGenerator lottoNumberGenerator) {
        List<Integer> lottoNumbers = lottoNumberGenerator.generateUniqueNumbersInRange();
        return new Lotto(lottoNumbers);
    }
}
