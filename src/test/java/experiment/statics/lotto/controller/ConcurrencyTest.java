package experiment.statics.lotto.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

public class ConcurrencyTest {
    @Test
    @DisplayName("Static 방식: 100명이 동시에 로또 게임을 하면 100% 실행 오류가 발생한다 (데이터 무결성 검사까지 가기 전에 터진다)")
    void static_MultiThread_Explosion() {
        // given
        int threadCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<CompletableFuture<String>> futures = new ArrayList<>();

        List<String> inputs = new ArrayList<>();
        inputs.add("1000\n1,2,3,4,5,6\n7");
        inputs.add("2000\n1,2,3,4,5,6\n7");
        inputs.add("3000\n1,2,3,4,5,6\n7");
        inputs.add("4000\n1,2,3,4,5,6\n7");
        inputs.add("5000\n1,2,3,4,5,6\n7");
        inputs.add("6000\n1,2,3,4,5,6\n7");

        List<Integer> expected = List.of(1, 2, 3, 4, 5, 6);

        // when
        for (int i = 0; i < threadCount; i++) {
            int finalJ = i % 6;
            futures.add(CompletableFuture.supplyAsync(() -> {
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                System.setOut(new PrintStream(output));

                System.setIn(new ByteArrayInputStream(inputs.get(finalJ).getBytes()));

                try {
                    LottoController.run();

                    String result = output.toString();
                    if (!result.contains(String.format("%d개를 구매했습니다.", expected.get(finalJ)))) {
                        return "데이터 오염";
                    }
                    return "성공";
                } catch (Exception e) {
                    return "실행 오류";
                }
            }, executor));
        }
        System.setIn(System.in);
        System.setOut(System.out);

        // then
        List<String> results = futures.stream()
                .map(CompletableFuture::join)
                .toList();

        int successCount, runErrorCount, dataCorruptionCount;
        successCount = 0;
        runErrorCount = 0;
        dataCorruptionCount = 0;
        for (String result : results) {
            if (result.equals("성공")) {
                successCount++;
            }
            if (result.equals("실행 오류")) {
                runErrorCount++;
            }
            if (result.equals("데이터 오염")) {
                dataCorruptionCount++;
            }
        }

        assertThat(successCount).isEqualTo(0);
        assertThat(runErrorCount).isEqualTo(threadCount);
        assertThat(dataCorruptionCount).isEqualTo(0);
    }
}
