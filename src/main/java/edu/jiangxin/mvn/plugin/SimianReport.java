package edu.jiangxin.mvn.plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;

import au.com.redhillconsulting.simian.SimianMain;

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

	@Parameter(defaultValue = "${project}", readonly = true, required = true)
	private MavenProject project;

	@Override
	public MavenProject getProject() {
		return project;
	}

	@Override
	public void executeReport(Locale locale) throws MavenReportException {
		
		ArrayList<File> lists =  new FileFilterWrapper().list(sourceDirectory, ".java");
		String[] params = new String[lists.size() + 1];
		for (int i=0; i<lists.size();i++) {
			try {
				params[i] = lists.get(i).getCanonicalPath();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		params[lists.size()] = "-formatter=xml";
		SimianMain.main(params);

		Sink sink = getSink();
		sink.paragraph();
		sink.text("testjiangxin");
		sink.paragraph();

		simianReportRenderer = new SimianReportRenderer(getSink());
		simianReportRenderer.render();
	}

	public String getOutputName() {
		return "simian-report";
	}

	public String getName(Locale locale) {
		return getBundle(locale).getString("report.dashboard.title.name");
	}

	public String getDescription(Locale locale) {
		return getBundle(locale).getString("report.dashboard.title.description");
	}

	private ResourceBundle getBundle(Locale locale) {
		return ResourceBundle.getBundle("simian-report", locale, this.getClass().getClassLoader());
	}
	
	public static void main(String[] args) throws JellyException, IOException {
		String result = SimianReport.class.getResource("/").getPath() + "jiangxin.html";
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
	        xmlOutput.flush();
	}

}