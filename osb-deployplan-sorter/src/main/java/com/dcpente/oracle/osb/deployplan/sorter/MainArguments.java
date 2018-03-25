package com.dcpente.oracle.osb.deployplan.sorter;

import java.io.FileNotFoundException;
import java.nio.file.FileAlreadyExistsException;
import java.security.CodeSource;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import com.dcpente.oracle.osb.deployplan.sorter.util.FileCheckUtil;

public class MainArguments {

	private final static Logger log = LogManager.getLogger(Main.class);

	private static final String HELP_HEADER = "Sort XML in deploy plan of Oracle Service Bus por easy compare with others.\n\n";
	private static final String HELP_FOOTER = "\nPlease, report issues at https://www.github.com/dcpente";

	// Source option
	private final static String OP_SRC = "src";
	/// Destination output option
	private final static String OP_DST = "dst";
	/// Source for compare option
	private final static String OP_CMP = "cmp";

	/// Destination will be overwrite if exist
	private final static String OP_OVERWRITE = "o";

	// Help option
	private final static String[] OP_HELP = { "h", "help" };
	// JAR version option
	private final static String OP_VERSION = "version";

	// Log option: no write console log
	private final static String[] OP_QUIET = { "q", "quiet" };
	// Log option: debug console log
	private final static String[] OP_DEBUG = { "d", "debug" };
	// Log option: verbose console log
	private final static String[] OP_VERBOSE = { "v", "verbose" };
	// Log option: default console log
	private final static String OP_DEFAULT = "default";

	// Options defined for this JAR
	private Options options = null;

	// JAR arguments
	private String[] args = null;

	private String sourceFile = null;
	private String destinationFile = null;
	private String compareFile = null;
	private boolean overwrite = false;
	private boolean help = false;
	private boolean version = false;

	public MainArguments(String[] args) {
		setArgs(args);
	}

	private Options getOptions() {
		return options;
	}

	private void setOptions(Options options) {
		this.options = options;
	}

	private String[] getArgs() {
		return args;
	}

	private void setArgs(String[] args) {
		this.args = args;
	}

	public String getSourceFile() {
		return sourceFile;
	}

	private void setSourceFile(String sourceFile) {
		this.sourceFile = sourceFile;
	}

	public String getDestinationFile() {
		return destinationFile;
	}

	private void setDestinationFile(String destinationFile) {
		this.destinationFile = destinationFile;
	}

	public String getCompareFile() {
		return compareFile;
	}

	private void setCompareFile(String compareFile) {
		this.compareFile = compareFile;
	}

	public boolean isOverwrite() {
		return overwrite;
	}

	private void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}

	public boolean isHelp() {
		return help;
	}

	private void setHelp(boolean help) {
		this.help = help;
	}

	public boolean isVersion() {
		return version;
	}

	private void setVersion(boolean version) {
		this.version = version;
	}

	public void compile() throws ParseException, FileNotFoundException, FileAlreadyExistsException,
			IllegalArgumentException, SecurityException {

		if (log.isDebugEnabled())
			log.debug("MainArguments.compile()");

		setOptions(createOptions());

		CommandLineParser parser = new DefaultParser();
		CommandLine line = parser.parse(getOptions(), getArgs());

		setHelp(line.hasOption(OP_HELP[0]));
		setVersion(line.hasOption(OP_VERSION));

		if (isHelp() || isVersion())
			return;

		setSourceFile(line.getOptionValue(OP_SRC));
		setDestinationFile(line.getOptionValue(OP_DST));
		setCompareFile(line.getOptionValue(OP_CMP));
		setOverwrite(line.hasOption(OP_OVERWRITE));
		
		if (getSourceFile() == null && getDestinationFile() == null) {
			setHelp(true);
			return;
		}
		
		FileCheckUtil.mandatoryFileExists(getSourceFile(), "source_file");
		if (getCompareFile() != null)
			FileCheckUtil.mandatoryFileExists(getCompareFile(), "comparer_file");
		if (isOverwrite()) {
			FileCheckUtil.mandatoryFileCanCreateOrOverwrite(getDestinationFile(), "destination_file");
		} else {
			FileCheckUtil.mandatoryFileCanCreate(getDestinationFile(), "destination_file");
		}
	}

	public void printHelp() {
		printHelp(getOptions());
	}

	private static void printHelp(Options options) {
		new HelpFormatter().printHelp(getJarName(), HELP_HEADER, options, HELP_FOOTER, true);
	}

	private static String getJarName() {
		CodeSource cs = Main.class.getProtectionDomain().getCodeSource();
		return cs == null ? "<jar_name>.jar" : new java.io.File(cs.getLocation().getPath()).getName();
	}

	private static Options createOptions() {
		
		OptionGroup input = new OptionGroup();
		
		Option src = new Option(OP_SRC, true, "TODO, input XML file, deploy plan to sort");
		input.addOption(src);
		Option dst = new Option(OP_DST, true, "TODO, output XML file, deploy plan after sort");
		input.addOption(dst);
		Option cmp = new Option(OP_CMP, true, "TODO, XML deploy plan file, thats your target to compare");
		input.addOption(cmp);

		Option ovewrite = new Option(OP_OVERWRITE, false, "TODO, flag to overwrite file if exist");
		input.addOption(ovewrite);
		
		OptionGroup log = new OptionGroup();

		Option quiet = new Option(OP_QUIET[0], OP_QUIET[1], false, "disable output console");
		log.addOption(quiet);
//		Option defaultLog = new Option(OP_DEFAULT, "print default information to console");
//		log.addOption(defaultLog);
		Option debug = new Option(OP_DEBUG[0], OP_DEBUG[1], false, "print debugging information");
		log.addOption(debug);
		Option verbose = new Option(OP_VERBOSE[0], OP_VERBOSE[1], false, "be extra verbose");
		log.addOption(verbose);

		OptionGroup helpGroup = new OptionGroup();
		
		Option help = new Option(OP_HELP[0], OP_HELP[1], false, "print this message");
		helpGroup.addOption(help);
		Option version = new Option(OP_VERSION, "print the version information and exit");
		helpGroup.addOption(version);

		Options options = new Options();
		options.addOptionGroup(log);
		options.addOptionGroup(helpGroup);
		options.addOptionGroup(input);
		
		
		return options;
	}

	public static void configureConsoleLog(String[] args) {
		try {
			if (args == null || args.length == 0)
				return;
			Set<String> hsArgs = new HashSet<String>(Arrays.asList(args));

			boolean quiet = hsArgs.contains("-" + OP_QUIET);
			if (quiet) {
				Configurator.setRootLevel(Level.OFF);
				Configurator.setAllLevels("com.dcpente.oracle.osb.deployplan.sorter", Level.OFF);
				return;
			}

			boolean verbose = hsArgs.contains("-" + OP_VERBOSE);
			if (verbose) {
				Configurator.setAllLevels("com.dcpente.oracle.osb.deployplan.sorter", Level.ALL);
				return;
			}

			boolean debug = hsArgs.contains("-" + OP_DEBUG);
			if (debug) {
				Configurator.setAllLevels("com.dcpente.oracle.osb.deployplan.sorter", Level.DEBUG);
				return;
			}
		} finally {
			log.debug("Console Log setteaded");
		}
	}

}
