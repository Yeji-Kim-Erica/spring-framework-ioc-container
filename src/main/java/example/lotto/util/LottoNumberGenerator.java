package example.lotto.util;

import java.util.List;

/**
 * 숫자 목록 생성 인터페이스
 */
public interface LottoNumberGenerator {
    public List<Integer> generateUniqueNumbersInRange();
}
