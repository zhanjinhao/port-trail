package cn.addenda.porttrail.server.test.biz.analyze;

import cn.addenda.porttrail.server.biz.analyze.AnalyzeDedupHandler;
import cn.addenda.porttrail.server.biz.analyze.ExplainConclusion;
import cn.addenda.porttrail.server.biz.analyze.ExplainRow;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

/**
 * {@link AnalyzeDedupHandler#buildConclusion(List)} 的单元测试。
 *
 * <p>完整流程见 {@link AnalyzeDedupHandlerFlowTest}（连真实 Redis / MySQL）。
 */
class AnalyzeDedupHandlerTest {

  private static ExplainRow row(String type, Long id) {
    ExplainRow explainRow = new ExplainRow();
    explainRow.setExplainType(type);
    explainRow.setExplainId(id);
    return explainRow;
  }

  @Test
  void testEmptyExplainResult() {
    ExplainConclusion conclusion = AnalyzeDedupHandler.buildConclusion(Collections.emptyList());

    Assertions.assertFalse(conclusion.isIfFullScan());
    Assertions.assertTrue(conclusion.getExplainRowList().isEmpty());
  }

  @Test
  void testHasAllRow() {
    ExplainRow indexRow = row("ref", 1L);
    ExplainRow allRow = row("ALL", 2L);
    ExplainRow rangeRow = row("range", 3L);

    ExplainConclusion conclusion = AnalyzeDedupHandler.buildConclusion(
            Arrays.asList(indexRow, allRow, rangeRow));

    Assertions.assertTrue(conclusion.isIfFullScan());
    Assertions.assertEquals(3, conclusion.getExplainRowList().size());
    Assertions.assertEquals(Arrays.asList(1L, 2L, 3L), Arrays.asList(
            conclusion.getExplainRowList().get(0).getExplainId(),
            conclusion.getExplainRowList().get(1).getExplainId(),
            conclusion.getExplainRowList().get(2).getExplainId()));
  }

  @Test
  void testNoAllRow() {
    ExplainConclusion conclusion = AnalyzeDedupHandler.buildConclusion(
            Arrays.asList(row("ref", 1L), row("range", 2L)));

    Assertions.assertFalse(conclusion.isIfFullScan());
    Assertions.assertEquals(2, conclusion.getExplainRowList().size());
  }

  @Test
  void testAllTypeIsCaseInsensitive() {
    Assertions.assertTrue(AnalyzeDedupHandler.buildConclusion(
            Collections.singletonList(row("all", 1L))).isIfFullScan());
  }

  @Test
  void testNullTypeIsNotAll() {
    Assertions.assertFalse(AnalyzeDedupHandler.buildConclusion(
            Arrays.asList(row(null, 1L), row("ref", 2L))).isIfFullScan());
  }

}
