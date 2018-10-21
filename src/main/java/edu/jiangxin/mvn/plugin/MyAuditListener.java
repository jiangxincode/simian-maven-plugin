package edu.jiangxin.mvn.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.logging.Log;

import com.harukizaemon.simian.AuditListener;
import com.harukizaemon.simian.Block;
import com.harukizaemon.simian.CheckSummary;
import com.harukizaemon.simian.Options;
import com.harukizaemon.simian.SourceFile;

public class MyAuditListener implements AuditListener {

	private Log log;
	private CheckSummary checkSummary;
	private List<BlockSet> blockSets;
	private BlockSet currentBlockSet;

	public MyAuditListener(Log log) {
		super();
		this.log = log;
		blockSets = new ArrayList<BlockSet>();
	}

	@Override
	public void fileProcessed(SourceFile sourceFile) {
		log.info("fileProcessed is invoked: " + sourceFile.getFilename());

	}

	@Override
	public void startCheck(Options options) {
		log.info("startCheck is invoked: " + options.toString());

	}

	@Override
	public void endCheck(CheckSummary checkSummary) {
		StringBuilder sb = new StringBuilder();
		sb.append("DuplicateBlockCount: ").append(checkSummary.getDuplicateBlockCount())
		.append("DuplicateFileCount").append(checkSummary.getDuplicateFileCount())
		.append("DuplicateLineCount").append(checkSummary.getDuplicateLineCount())
		.append("DuplicateLinePercentage").append(checkSummary.getDuplicateLinePercentage())
		.append("ProcessingTime").append(checkSummary.getProcessingTime())
		.append("TotalFileCount").append(checkSummary.getTotalFileCount())
		.append("TotalRawLineCount").append(checkSummary.getTotalRawLineCount())
		.append("TotalSignificantLineCount").append(checkSummary.getTotalSignificantLineCount());
		log.info("endCheck is invoked: [" + sb.toString() + "]");
		this.checkSummary = checkSummary;
	}

	@Override
	public void block(Block block) {
		StringBuilder sb = new StringBuilder();
		sb.append("EndLineNumber: ").append(block.getEndLineNumber())
		.append("SourceFile: ").append(block.getSourceFile())
		.append("StartLineNumber: ").append(block.getStartLineNumber());
		log.info("block is invoked: [" + sb.toString() + "]");
		currentBlockSet.getBlocks().add(block);
	}

	@Override
	public void startSet(int lineCount, String fingerprint) {
		log.info("startSet is invoked: [lineCount: " + lineCount + ", fingerprint: " + fingerprint + "]");
		BlockSet blockSet = new BlockSet();
		blockSet.setLineCount(lineCount);
		blockSet.setFingerprint(fingerprint);
		blockSet.setBlocks(new ArrayList<Block>());
		currentBlockSet = blockSet;
		blockSets.add(blockSet);
	}

	@Override
	public void endSet(String text) {
		log.info("endSet is invoked: " + text);

	}

	@Override
	public void error(File file, Throwable e) {
		log.info("error is invoked");

	}

	/**
	 * @return the checkSummary
	 */
	public CheckSummary getCheckSummary() {
		return checkSummary;
	}
	
	/**
	 * @return the blockSets
	 */
	public List<BlockSet> getBlockSets() {
		return blockSets;
	}

}
