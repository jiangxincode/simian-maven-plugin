package edu.jiangxin.mvn.plugin;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.io.FilenameUtils;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.model.Model;
import org.apache.maven.model.ReportPlugin;
import org.apache.maven.model.Reporting;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReportRenderer;
import org.codehaus.plexus.util.PathTool;
import org.codehaus.plexus.util.StringUtils;

import com.harukizaemon.simian.Block;
import com.harukizaemon.simian.CheckSummary;
import com.harukizaemon.simian.Option;
import com.harukizaemon.simian.Options;

public class SimianReportRenderer extends AbstractMavenReportRenderer {

	private final ResourceBundle bundle;

	private Options options;

	private CheckSummary checkSummary;

	private List<BlockSet> blockSets;

	private File sourceDirectory;

	private File testSourceDirectory;

	private Log log;

	private String xrefRelativeLocation;

	private String xrefRelativeTestLocation;

	private boolean linkXRef;

	private File outputDirectory;

	private MavenProject project;

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
		
		sink.link("http://www.redhillconsulting.com.au/products/simian");
		sink.text(" simian");
		sink.link_();
		
		sink.lineBreak();
		sink.text(MessageFormat.format(bundle.getString("report.introduction.version"), Constant.SIMIAN_VERSION));

		sink.sectionTitle1();
		sink.text(bundle.getString("report.summary.title"));
		sink.sectionTitle1_();

		doSummarySectionConfig();

		duSummarySectionResult();

		duDetailSection();

		sink.section1_();

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
		sink.text("File");
		sink.tableHeaderCell_();

		sink.tableHeaderCell();
		sink.text("StartLineNumber");
		sink.tableHeaderCell_();

		sink.tableHeaderCell();
		sink.text("EndLineNumber");
		sink.tableHeaderCell_();

		sink.tableRow_();

		for (Block block : blockSet.getBlocks()) {
			sink.tableRow();

			sink.tableCell();
			processLinkPath(block);
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

	private void processLinkPath(Block block) {
		String path = block.getSourceFile().getFilename();
		log.debug("path: " + path);
		try {
			if (FilenameUtils.directoryContains(sourceDirectory.getAbsolutePath(), path)) {
				String relativePath = PathTool.getRelativeFilePath(sourceDirectory.getAbsolutePath(), path);
				log.debug("relativePath: " + relativePath);
				if (xrefRelativeLocation != null) {
					String linkUrl = xrefRelativeLocation + File.separator + relativePath;
					linkUrl = linkUrl.replace(FilenameUtils.getExtension(path), "html");
					linkUrl = linkUrl + "#L" + block.getStartLineNumber();
					sink.link(linkUrl);
					sink.text(relativePath);
					sink.link_();
				} else {
					sink.text(relativePath);
				}

			} else if (FilenameUtils.directoryContains(testSourceDirectory.getAbsolutePath(), path)) {
				String relativePath = PathTool.getRelativeFilePath(testSourceDirectory.getAbsolutePath(), path);
				log.debug("relativePath: " + relativePath);
				if (xrefRelativeTestLocation != null) {
					String linkUrl = xrefRelativeTestLocation + File.separator + relativePath;
					linkUrl = linkUrl.replace(FilenameUtils.getExtension(path), "html");
					linkUrl = linkUrl + "#L" + block.getStartLineNumber();
					sink.link(linkUrl);
					sink.text(relativePath);
					sink.link_();
				} else {
					sink.text(relativePath);
				}

			} else {
				log.error("file is invalid: " + path);
			}
		} catch (IOException e1) {
			log.error("replace failed: " + path, e1);
		}
	}

	private String constructRelativeXRefLocation(File xrefLocation) {
		if (!linkXRef) {
			log.error("linkXRef is false");
			return null;
		}
		String relativePath = PathTool.getRelativePath(outputDirectory.getAbsolutePath(),
				xrefLocation.getAbsolutePath());
		if (StringUtils.isEmpty(relativePath)) {
			relativePath = ".";
		}
		relativePath = relativePath + "/" + xrefLocation.getName();
		if (xrefLocation.exists()) {
			// XRef was already generated by manual execution of a lifecycle binding
			log.info("xrefLocation exists, relativePath: " + relativePath);
			return relativePath;
		}

		if (project == null) {
			log.error("project is null");
			return null;
		}
		Model model = project.getModel();
		if (model == null) {
			log.error("model is null");
			return null;
		}
		Reporting reporting = model.getReporting();
		if (reporting == null) {
			log.error("reporting is null");
			return null;
		}
		List<ReportPlugin> reportPlugins = reporting.getPlugins();
		if (reportPlugins == null) {
			log.error("reportPlugins is null");
			return null;
		}

		for (ReportPlugin plugin : reportPlugins) {
			String artifactId = plugin.getArtifactId();
			if ("maven-jxr-plugin".equals(artifactId) || "jxr-maven-plugin".equals(artifactId)) {
				log.info("plugin exists, relativePath: " + relativePath);
				return relativePath;
			}
		}

		log.warn("Unable to locate Source XRef to link to - DISABLED");
		return null;
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

	public void setSource(File sourceDirectory, File testSourceDirectory) {
		this.sourceDirectory = sourceDirectory;
		this.testSourceDirectory = testSourceDirectory;
	}

	public void setLog(Log log) {
		this.log = log;
	}

	public void setXrefLocation(File xrefLocation) {
		this.xrefRelativeLocation = constructRelativeXRefLocation(xrefLocation);
	}

	public void setXrefTestLocation(File xrefTestLocation) {
		this.xrefRelativeTestLocation = constructRelativeXRefLocation(xrefTestLocation);
	}

	public void setLinkXRef(boolean linkXRef) {
		this.linkXRef = linkXRef;
	}

	public void setOutputDirectory(File outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	public void setProject(MavenProject project) {
		this.project = project;
	}

}
