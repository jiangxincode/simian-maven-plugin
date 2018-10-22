package edu.jiangxin.mvn.plugin;

import java.util.List;
import java.util.ResourceBundle;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.reporting.AbstractMavenReportRenderer;

import com.harukizaemon.simian.Block;
import com.harukizaemon.simian.CheckSummary;
import com.harukizaemon.simian.Option;
import com.harukizaemon.simian.Options;

public class SimianReportRenderer extends AbstractMavenReportRenderer {

	private final ResourceBundle bundle;

	private Options options;

	private CheckSummary checkSummary;

	private List<BlockSet> blockSets;

	public SimianReportRenderer(Sink sink, ResourceBundle bundle) {
		super(sink);
		this.bundle = bundle;
	}

	@Override
	public String getTitle() {
		return bundle.getString("report.title");
	}

	@Override
	protected void renderBody() {
		sink.section1();

		sink.sectionTitle1();
		sink.text(bundle.getString("report.title"));
		sink.sectionTitle1_();

		sink.text(bundle.getString("report.introduction"));

		sink.sectionTitle1();
		sink.text(bundle.getString("report.summary.title"));
		sink.sectionTitle1_();

		doSummarySectionConfig();

		duSummarySectionResult();

		duDetailSection();

		sink.section1_();

	}

	public void setOptions(Options options) {
		this.options = options;
	}

	public void setCheckSummary(CheckSummary checkSummary) {
		this.checkSummary = checkSummary;
	}

	public void setBlockSets(List<BlockSet> blockSets) {
		this.blockSets = blockSets;
	}

	private void duDetailSection() {
		sink.sectionTitle1();
		sink.text(bundle.getString("report.detail.title"));
		sink.sectionTitle1_();

		for (BlockSet blockSet : blockSets) {
			duDetailSectionSingle(blockSet);
		}

	}

	private void duDetailSectionSingle(BlockSet blockSet) {
		sink.section2();

		sink.sectionTitle2();
		sink.text("LineCount: " + blockSet.getLineCount() + ", Fingerprint: " + blockSet.getFingerprint());
		sink.sectionTitle2_();

		sink.table();

		sink.tableRow();

		sink.tableHeaderCell();
		sink.text("FileName");
		sink.tableHeaderCell_();

		sink.tableHeaderCell();
		sink.text("StartLineNumber");
		sink.tableHeaderCell_();

		sink.tableHeaderCell();
		sink.text("EndLineNumber");
		sink.tableHeaderCell_();

		sink.tableRow_();

		for (Block block : blockSet.getBlocks()) {
			String path = block.getSourceFile().getFilename();
			String tmp = path.replace("src\\test\\java", "target/site/xref-test");
			tmp = tmp.replace(".java", ".html");
			tmp = tmp + "#L" + block.getStartLineNumber();
			sink.tableRow();
			sink.tableCell();
			sink.link(tmp);
			sink.text(String.valueOf(path));
			sink.link_();
			sink.tableCell_();

			sink.tableCell();
			sink.text(String.valueOf(block.getStartLineNumber()));
			sink.tableCell_();

			sink.tableCell();
			sink.text(String.valueOf(block.getEndLineNumber()));
			sink.tableCell_();

			sink.tableRow_();

		}

		sink.table_();

		sink.section2_();

	}

	private void doSummarySectionConfig() {
		sink.section2();

		sink.sectionTitle2();
		sink.text(bundle.getString("report.summary.configuration.title"));
		sink.sectionTitle2_();

		sink.table();

		sink.tableRow();

		sink.tableHeaderCell();
		sink.text("ignoreCharacterCase");
		sink.tableHeaderCell_();

		sink.tableHeaderCell();
		sink.text("ignoreCurlyBraces");
		sink.tableHeaderCell_();

		sink.tableHeaderCell();
		sink.text("ignoreIdentifierCase");
		sink.tableHeaderCell_();

		sink.tableHeaderCell();
		sink.text("ignoreModifiers");
		sink.tableHeaderCell_();

		sink.tableHeaderCell();
		sink.text("ignoreStringCase");
		sink.tableHeaderCell_();

		sink.tableHeaderCell();
		sink.text("ignoreStrings");
		sink.tableHeaderCell_();

		sink.tableHeaderCell();
		sink.text("threshold");
		sink.tableHeaderCell_();

		sink.tableRow_();

		sink.tableRow();

		sink.tableCell();
		sink.text(String.valueOf(options.getOption(Option.IGNORE_CHARACTER_CASE)));
		sink.tableCell_();

		sink.tableCell();
		sink.text(String.valueOf(options.getOption(Option.IGNORE_CURLY_BRACES)));
		sink.tableCell_();

		sink.tableCell();
		sink.text(String.valueOf(options.getOption(Option.IGNORE_IDENTIFIER_CASE)));
		sink.tableCell_();

		sink.tableCell();
		sink.text(String.valueOf(options.getOption(Option.IGNORE_MODIFIERS)));
		sink.tableCell_();

		sink.tableCell();
		sink.text(String.valueOf(options.getOption(Option.IGNORE_STRING_CASE)));
		sink.tableCell_();

		sink.tableCell();
		sink.text(String.valueOf(options.getOption(Option.IGNORE_STRINGS)));
		sink.tableCell_();

		sink.tableCell();
		sink.text(String.valueOf(options.getThreshold()));
		sink.tableCell_();

		sink.tableRow_();

		sink.table_();

		sink.section2_();

	}

	private void duSummarySectionResult() {
		sink.section2();

		sink.sectionTitle2();
		sink.text(bundle.getString("report.summary.result.title"));
		sink.sectionTitle2_();

		sink.table();

		sink.tableRow();

		sink.tableHeaderCell();
		sink.text("DuplicateBlockCount");
		sink.tableHeaderCell_();

		sink.tableHeaderCell();
		sink.text("DuplicateFileCount");
		sink.tableHeaderCell_();

		sink.tableHeaderCell();
		sink.text("DuplicateLineCount");
		sink.tableHeaderCell_();

		sink.tableHeaderCell();
		sink.text("DuplicateLinePercentage");
		sink.tableHeaderCell_();

		sink.tableHeaderCell();
		sink.text("ProcessingTime");
		sink.tableHeaderCell_();

		sink.tableHeaderCell();
		sink.text("TotalFileCount");
		sink.tableHeaderCell_();

		sink.tableHeaderCell();
		sink.text("TotalRawLineCount");
		sink.tableHeaderCell_();

		sink.tableHeaderCell();
		sink.text("TotalSignificantLineCount");
		sink.tableHeaderCell_();

		sink.tableRow_();

		sink.tableRow();

		sink.tableCell();
		sink.text(String.valueOf(checkSummary.getDuplicateBlockCount()));
		sink.tableCell_();

		sink.tableCell();
		sink.text(String.valueOf(checkSummary.getDuplicateFileCount()));
		sink.tableCell_();

		sink.tableCell();
		sink.text(String.valueOf(checkSummary.getDuplicateLineCount()));
		sink.tableCell_();

		sink.tableCell();
		sink.text(String.valueOf(checkSummary.getDuplicateLinePercentage()));
		sink.tableCell_();

		sink.tableCell();
		sink.text(String.valueOf(checkSummary.getProcessingTime()));
		sink.tableCell_();

		sink.tableCell();
		sink.text(String.valueOf(checkSummary.getTotalFileCount()));
		sink.tableCell_();

		sink.tableCell();
		sink.text(String.valueOf(checkSummary.getTotalRawLineCount()));
		sink.tableCell_();

		sink.tableCell();
		sink.text(String.valueOf(checkSummary.getTotalSignificantLineCount()));
		sink.tableCell_();

		sink.tableRow_();

		sink.table_();

		sink.section2_();

	}

}
