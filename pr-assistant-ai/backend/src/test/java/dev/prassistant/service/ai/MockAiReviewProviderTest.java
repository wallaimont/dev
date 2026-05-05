package dev.prassistant.service.ai;

import dev.prassistant.domain.entity.PullRequest;
import dev.prassistant.domain.entity.PullRequestFile;
import dev.prassistant.domain.enums.FileStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MockAiReviewProviderTest {

    private MockAiReviewProvider provider;

    @BeforeEach
    void setUp() {
        provider = new MockAiReviewProvider();
    }

    @Test
    @DisplayName("name returns 'mock'")
    void name_returnsMock() {
        assertThat(provider.name()).isEqualTo("mock");
    }

    @Test
    @DisplayName("review - small PR gets HIGH quality score and LOW risk")
    void review_smallPr() {
        PullRequest pr = buildPrWithFiles(3);

        AiReviewProvider.AiReviewResult result = provider.review(pr);

        assertThat(result.qualityScore()).isEqualTo(91); // max(40, 100 - 3*3) = 91
        assertThat(result.riskLevel()).isEqualTo("LOW");
        assertThat(result.executiveSummary()).isNotBlank();
        assertThat(result.technicalSummary()).isNotBlank();
    }

    @Test
    @DisplayName("review - medium PR (8 files) gets MEDIUM risk")
    void review_mediumPr() {
        PullRequest pr = buildPrWithFiles(8);

        AiReviewProvider.AiReviewResult result = provider.review(pr);

        assertThat(result.qualityScore()).isEqualTo(76); // 100 - 8*3 = 76
        assertThat(result.riskLevel()).isEqualTo("MEDIUM");
    }

    @Test
    @DisplayName("review - large PR (20 files) gets HIGH risk")
    void review_largePr() {
        PullRequest pr = buildPrWithFiles(20);

        AiReviewProvider.AiReviewResult result = provider.review(pr);

        assertThat(result.qualityScore()).isEqualTo(40); // max(40, 100 - 20*3) = 40
        assertThat(result.riskLevel()).isEqualTo("HIGH");
    }

    @Test
    @DisplayName("review - very large PR (30 files) score floors at 40")
    void review_veryLargePr_scoreFloor() {
        PullRequest pr = buildPrWithFiles(30);

        AiReviewProvider.AiReviewResult result = provider.review(pr);

        assertThat(result.qualityScore()).isEqualTo(40); // max(40, 100 - 90) = 40
    }

    @Test
    @DisplayName("review - empty files list gets LOW risk and max score")
    void review_emptyFiles() {
        PullRequest pr = PullRequest.builder().build();
        pr.setFiles(List.of());

        AiReviewProvider.AiReviewResult result = provider.review(pr);

        assertThat(result.qualityScore()).isEqualTo(100);
        assertThat(result.riskLevel()).isEqualTo("LOW");
    }

    private PullRequest buildPrWithFiles(int count) {
        PullRequest pr = PullRequest.builder().title("Test PR").build();
        List<PullRequestFile> files = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            files.add(PullRequestFile.builder()
                    .filePath("file" + i + ".java")
                    .fileStatus(FileStatus.MODIFIED)
                    .additions(5)
                    .deletions(2)
                    .build());
        }
        pr.setFiles(files);
        return pr;
    }
}
