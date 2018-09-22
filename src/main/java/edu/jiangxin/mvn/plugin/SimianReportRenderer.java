package edu.jiangxin.mvn.plugin;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.reporting.AbstractMavenReportRenderer;

public class SimianReportRenderer extends AbstractMavenReportRenderer {

	public SimianReportRenderer(Sink sink) {
		super(sink);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return "Simian Report Title";
	}

	@Override
	protected void renderBody() {
		// TODO Auto-generated method stub

	}

}
