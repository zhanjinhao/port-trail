package cn.addenda.porttrail.server.biz.analyze;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * 一次 explain 的结论，在 {@code AnalyzeDedupHandler} 内部使用，不再写入 Redis。
 *
 * <p>去重表只在命中 full scan 时把全量行序列化进 {@code full_scan_explain_json}，
 * 不再从中挑代表行。
 *
 * @author addenda
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class ExplainConclusion {

  /**
   * explain 结果中是否存在 type = ALL 的行
   */
  private boolean ifFullScan;

  /**
   * explain 的全部行
   */
  private List<ExplainRow> explainRowList = new ArrayList<>();

}
