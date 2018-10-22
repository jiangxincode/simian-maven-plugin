package edu.jiangxin.mvn.plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;

import com.harukizaemon.simian.Checker;
import com.harukizaemon.simian.FileLoader;
import com.harukizaemon.simian.Option;
import com.harukizaemon.simian.Options;
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

		simianReportRenderer = new SimianReportRenderer(getSink(), getBundle(locale));
		simianReportRenderer.setOptions(options);
		simianReportRenderer.setCheckSummary(auditListener.getCheckSummary());
		simianReportRenderer.setBlockSets(auditListener.getBlockSets());
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