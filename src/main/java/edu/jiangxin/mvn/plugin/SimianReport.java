package edu.jiangxin.mvn.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;

import com.harukizaemon.simian.Checker;
import com.harukizaemon.simian.FileLoader;
import com.harukizaemon.simian.Option;
import com.harukizaemon.simian.Options;
import com.harukizaemon.simian.StreamLoader;

@Mojo(name = "simian-report", defaultPhase = LifecyclePhase.SITE, threadSafe = true, requiresProject = true)
public class SimianReport extends AbstractMavenReport {

	private SimianReportRenderer simianReportRenderer;

	@Parameter(defaultValue = "${basedir}", property = "simian.projectDirectory", required = true)
	private File projectDirectory;

	@Parameter(defaultValue = "${project.build.sourceDirectory}", property = "simian.sourceDirectory", required = true)
	private File sourceDirectory;

	@Parameter(defaultValue = "${project.build.testSourceDirectory}", property = "simian.testSourceDirectory", required = true)
	private File testSourceDirectory;

	/**
	 * Link the violation line numbers to the source xref. Defaults to true and will link automatically if jxr plugin is
	 * being used.
	 */
	@Parameter(defaultValue = "true", property = "linkXRef")
	private boolean linkXRef;

	@Parameter(defaultValue = "${project.build.directory}/site/xref")
	private File xrefLocation;

	@Parameter(defaultValue = "${project.build.directory}/site/xref-test")
	private File xrefTestLocation;

	@Parameter(defaultValue = "6", property = "simian.threshold", required = true)
	private int threshold;

	@Override
	public void executeReport(Locale locale) throws MavenReportException {
		ArrayList<File> lists = new ArrayList<File>();
		getLog().info("sourceDirectory： " + sourceDirectory);
		ArrayList<File> sourceList = new FileFilterWrapper().list(sourceDirectory.getAbsolutePath(), ".java");
		getLog().info("testSourceDirectory： " + testSourceDirectory);
		ArrayList<File> testSourceList = new FileFilterWrapper().list(testSourceDirectory.getAbsolutePath(), ".java");
		lists.addAll(sourceList);
		lists.addAll(testSourceList);

		MyAuditListener auditListener = new MyAuditListener(getLog());

		Options options = new Options();
		options.setThreshold(threshold);
		options.setOption(Option.IGNORE_STRINGS, true);

		Checker checker = new Checker(auditListener, options);
		StreamLoader streamLoader = new StreamLoader(checker);

		FileLoader fileLoader = new FileLoader(streamLoader);

		for (int i = 0; i < lists.size(); ++i) {
			fileLoader.load(lists.get(i));
		}

		if (checker.check()) {
			getLog().info("Duplicate lines were found!");
		}

		getLog().info("process success");

		simianReportRenderer = new SimianReportRenderer(getSink(), getBundle(locale));
		simianReportRenderer.setOptions(options);
		simianReportRenderer.setCheckSummary(auditListener.getCheckSummary());
		simianReportRenderer.setBlockSets(auditListener.getBlockSets());
		simianReportRenderer.setSource(sourceDirectory, testSourceDirectory);
		simianReportRenderer.setLog(getLog());
		simianReportRenderer.setOutputDirectory(outputDirectory);
		simianReportRenderer.setLinkXRef(linkXRef);
		simianReportRenderer.setProject(project);
		simianReportRenderer.setXrefLocation(xrefLocation);
		simianReportRenderer.setXrefTestLocation(xrefTestLocation);
		simianReportRenderer.render();
	}

	@Override
	public String getOutputName() {
		return "simian-report";
	}

	@Override
	public String getName(Locale locale) {
		getLog().info(getBundle(locale).getString("report.dashboard.title.name"));
		return getBundle(locale).getString("report.dashboard.title.name");
	}

	@Override
	public String getDescription(Locale locale) {
		getLog().info(getBundle(locale).getString("report.dashboard.title.description"));
		return getBundle(locale).getString("report.dashboard.title.description");
	}

	private ResourceBundle getBundle(Locale locale) {
		return ResourceBundle.getBundle("simian-report", locale, this.getClass().getClassLoader());
	}

}