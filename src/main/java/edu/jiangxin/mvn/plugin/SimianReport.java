package edu.jiangxin.mvn.plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.jelly.JellyException;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;

import com.harukizaemon.simian.Block;
import com.harukizaemon.simian.CheckSummary;
import com.harukizaemon.simian.Checker;
import com.harukizaemon.simian.FileLoader;
import com.harukizaemon.simian.Option;
import com.harukizaemon.simian.Options;
import com.harukizaemon.simian.SourceFile;
import com.harukizaemon.simian.StreamLoader;

@Mojo(name = "my-report", defaultPhase = LifecyclePhase.SITE, threadSafe = true, requiresProject = true)
public class SimianReport extends AbstractMavenReport {

	private SimianReportRenderer simianReportRenderer;

	@Override
	public Renderer getSiteRenderer() {
		return siteRenderer;
	}

	@Parameter(defaultValue = "${project.reporting.outputDirectory}", property = "simian.srcDirectory", required = true)
	private String outputDirectory;

	@Override
	public String getOutputDirectory() {
		return outputDirectory;
	}

	@Parameter(defaultValue = "${project.build.sourceDirectory}", property = "simian.srcDirectory", required = true)
	private String sourceDirectory;

	@Parameter(defaultValue = "${project.build.testSourceDirectory}", property = "simian.testSourceDirectory", required = true)
	private String testSourceDirectory;

	@Parameter(defaultValue = "${project}", readonly = true, required = true)
	private MavenProject project;

	@Override
	public MavenProject getProject() {
		return project;
	}

	@Override
	public void executeReport(Locale locale) throws MavenReportException {
		getLog().info("testSourceDirectory： " + testSourceDirectory);
		ArrayList<File> lists = new FileFilterWrapper().list(testSourceDirectory, ".java");
		assert lists != null;
		String[] params = new String[lists.size()];
		for (int i = 0; i < lists.size(); i++) {
			try {
				params[i] = lists.get(i).getCanonicalPath();
			} catch (IOException e) {
				getLog().error(e);
			}
		}

		MyAuditListener auditListener = new MyAuditListener(getLog());

		Options options = new Options();
		options.setThreshold(6);
		options.setOption(Option.IGNORE_STRINGS, true);

		Checker checker = new Checker(auditListener, options);
		StreamLoader streamLoader = new StreamLoader(checker);

		FileLoader fileLoader = new FileLoader(streamLoader);

		for (int i = 0; i < lists.size(); ++i) {
			fileLoader.load(lists.get(i));
		}

		if (checker.check()) {
			System.out.println("Duplicate lines were found!");
		}

		getLog().info("process success");

		Sink sink = getSink();
		sink.head();
		sink.title();
		sink.text(getBundle(locale).getString("report.title"));
		sink.title_();
		sink.head_();
		sink.body();
		sink.section1();

		sink.sectionTitle1();
		sink.text(getBundle(locale).getString("report.title"));
		sink.sectionTitle1_();

		sink.text(getBundle(locale).getString("report.introduction"));

		sink.sectionTitle1();
		sink.text(getBundle(locale).getString("report.summary.title"));
		sink.sectionTitle1_();

		doSummarySectionConfig(getBundle(locale), sink, options);

		duSummarySectionResult(getBundle(locale), sink, auditListener.getCheckSummary());

		duDetailSection(getBundle(locale), sink, auditListener.getBlockSets());

		sink.section1_();
		sink.body_();
		sink.flush();
		sink.close();

		/*simianReportRenderer = new SimianReportRenderer(sink);
		simianReportRenderer.render();*/
	}

	private void duDetailSection(ResourceBundle bundle, Sink sink, List<BlockSet> blockSets) {
		sink.sectionTitle1();
		sink.text(bundle.getString("report.detail.title"));
		sink.sectionTitle1_();

		for (BlockSet blockSet : blockSets) {
			duDetailSectionSingle(bundle, sink, blockSet);
		}

	}

	private void duDetailSectionSingle(ResourceBundle bundle, Sink sink, BlockSet blockSet) {
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

	private void doSummarySectionConfig(ResourceBundle bundle, Sink sink, Options options) {
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

	private void duSummarySectionResult(ResourceBundle bundle, Sink sink, CheckSummary summary) {
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
		sink.text(String.valueOf(summary.getDuplicateBlockCount()));
		sink.tableCell_();

		sink.tableCell();
		sink.text(String.valueOf(summary.getDuplicateFileCount()));
		sink.tableCell_();

		sink.tableCell();
		sink.text(String.valueOf(summary.getDuplicateLineCount()));
		sink.tableCell_();

		sink.tableCell();
		sink.text(String.valueOf(summary.getDuplicateLinePercentage()));
		sink.tableCell_();

		sink.tableCell();
		sink.text(String.valueOf(summary.getProcessingTime()));
		sink.tableCell_();

		sink.tableCell();
		sink.text(String.valueOf(summary.getTotalFileCount()));
		sink.tableCell_();

		sink.tableCell();
		sink.text(String.valueOf(summary.getTotalRawLineCount()));
		sink.tableCell_();

		sink.tableCell();
		sink.text(String.valueOf(summary.getTotalSignificantLineCount()));
		sink.tableCell_();

		sink.tableRow_();

		sink.table_();

		sink.section2_();

	}

	public String getOutputName() {
		return "simian-report";
	}

	public String getName(Locale locale) {
		getLog().info(getBundle(locale).getString("report.dashboard.title.name"));
		return getBundle(locale).getString("report.dashboard.title.name");
	}

	public String getDescription(Locale locale) {
		getLog().info(getBundle(locale).getString("report.dashboard.title.description"));
		return getBundle(locale).getString("report.dashboard.title.description");
	}

	private ResourceBundle getBundle(Locale locale) {
		return ResourceBundle.getBundle("simian-report", locale, this.getClass().getClassLoader());
	}

	public static void main(String[] args) throws JellyException, IOException {
		/*String result = SimianReport.class.getResource("/").getPath() + "jiangxin.html";
		System.out.println(result);
		String template = SimianReport.class.getResource("/simian.jelly").getPath();
		System.out.println(template);
		OutputStream output = new FileOutputStream(result);
		XMLOutput xmlOutput = XMLOutput.createXMLOutput(output);
		JellyContext context = new JellyContext();
		context.setVariable("pom.build.sourceDirectory", "D:/temp/Java/ApkToolBoxGUI/src/main/java");
		context.setVariable("pom.build.unitTestSourceDirectory", "D:/temp/Java/ApkToolBoxGUI/src/test/java");
		context.setAllowDtdToCallExternalEntities(true);
		context.runScript(new File(template), xmlOutput);
		xmlOutput.flush();*/
	}

}