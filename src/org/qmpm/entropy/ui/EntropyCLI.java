package org.qmpm.entropy.ui;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.qmpm.logtrie.core.Framework;
import org.qmpm.logtrie.ui.CLI;
import org.qmpm.logtrie.ui.LambdaOption;
import org.qmpm.entropy.enums.EntropyMetricLabel;

public class EntropyCLI implements CLI {
	
	public static final String USAGE = "entropy [OPTION]... [FILE/PATH]...";
	
	public Options generateOptions() {

		final Options options = new Options();

		options.addOption( new LambdaOption("h", "help", false, "print this message", () -> printHelp(options)) );
		options.addOption( new LambdaOption("t", "time", false, "print time elapsed for computing metric", () -> Framework.showTime(true)) );
		options.addOption( new LambdaOption("s", "show-progress", false, "show progress in console (do not use if piping to output file)", () -> Framework.showProgress(true)) );

		final LambdaOption timeoutOption = new LambdaOption("T", "timeout", true, "set time limit for metric computation");
		timeoutOption.setArgs(1);
		timeoutOption.setArgName("SECONDS");
		timeoutOption.setOperation( () -> Framework.setTimeout(timeoutOption) );
		options.addOption(timeoutOption);

		final LambdaOption sigDigOption = new LambdaOption("D", "significant-digits", true, "set precision of output (no. of significant digits)");
		sigDigOption.setArgs(1);
		sigDigOption.setArgName("DIGITS");
		sigDigOption.setOperation( () -> Framework.setSigDigits(sigDigOption) );
		options.addOption(sigDigOption);
		
		options.addOption( new LambdaOption("F", "flatten", false, "ignore multiple occurrences of same sequence", () -> Framework.setFlatten(true)) );
		options.addOption( new LambdaOption("f", "frequency", false, "trace/sequence frequency based entropy", () -> Framework.addMetric(EntropyMetricLabel.Trace)) );
		options.addOption( new LambdaOption("p", "prefix", false, "prefix based entropy", () -> Framework.addMetric(EntropyMetricLabel.Prefix)) );
		options.addOption( new LambdaOption("B", "k-block-global", false, "global k-block entropy", () -> Framework.addMetric(EntropyMetricLabel.GlobalBlock)) );


		final LambdaOption kBlockOption = new LambdaOption("b", "k-block", true, "k-block entropy");
		kBlockOption.setArgs(1);
		kBlockOption.setArgName("SIZE");
		kBlockOption.setOperation( () -> Framework.addMetric(EntropyMetricLabel.KBlock, kBlockOption) );
		options.addOption(kBlockOption);

		final LambdaOption klOption = new LambdaOption("K", "kl", true, "Kozachenko-Leonenko (nearest neighbor) entropy");
		klOption.setArgs(1);
		klOption.setArgName("DIMENSION");
		klOption.setOperation( () -> Framework.addMetric(EntropyMetricLabel.KL, klOption) );
		options.addOption(klOption);

		final LambdaOption kNNOption = new LambdaOption("k", "knn", true, "entropy based on the kth nearest neighbor");
		kNNOption.setArgs(2);
		kNNOption.setArgName("K, DIMENSION");
		kNNOption.setOperation( () -> Framework.addMetric(EntropyMetricLabel.KNN, kNNOption) );
		options.addOption(kNNOption);

		options.addOption( new LambdaOption("z", "lempel-ziv", false, "Lempel-Ziv entropy rate", () -> Framework.addMetric(EntropyMetricLabel.LempelZiv)) );

		final LambdaOption kBlockRateDiffOption = new LambdaOption("d", "block-diff", true, "k-block entropy rate using difference-based estimate and cutoff constraint from 1-5");
		kBlockRateDiffOption.setArgs(1);
		kBlockRateDiffOption.setArgName("CONST");
		kBlockRateDiffOption.setOperation( () -> Framework.addMetric(EntropyMetricLabel.KBlockRateDiff, kBlockRateDiffOption) );
		options.addOption(kBlockRateDiffOption);

		final LambdaOption kBlockRateRatioOption = new LambdaOption("r", "block-ratio", true, "k-block entropy rate using ratio-based estimate and cutoff constraint from 1-5");
		kBlockRateRatioOption.setArgs(1);
		kBlockRateRatioOption.setArgName("CONST");
		kBlockRateRatioOption.setOperation( () -> Framework.addMetric(EntropyMetricLabel.KBlockRateRatio, kBlockRateRatioOption) );
		options.addOption(kBlockRateRatioOption);

		options.addOption( new LambdaOption("u", "unique", false, "ratio of unique traces to total traces (0.0 - 1.0)", () -> Framework.addMetric(EntropyMetricLabel.UniqueTraces)) );
		
		return options;
	}
	
	public CommandLine generateCommandLine(Options options, String[] commandLineArguments) {

		final CommandLineParser cmdLineParser = new DefaultParser();
		CommandLine commandLine = null;
		
		try {
			commandLine = cmdLineParser.parse(options, commandLineArguments);
		} catch (ParseException parseException) {
			Framework.permitOutput();
			System.out.println("ERROR: Unable to parse command-line arguments: " + parseException.getMessage());
			printHelp(options);
			System.exit(1);
		}
		
		return commandLine;
	}
	
	public void printHelp(Options options) {
		
		Framework.permitOutput();;
		HelpFormatter formatter = new HelpFormatter();
		formatter.setOptionComparator(null);
		formatter.printHelp(USAGE, options );
		Framework.resetQuiet();
	}
}