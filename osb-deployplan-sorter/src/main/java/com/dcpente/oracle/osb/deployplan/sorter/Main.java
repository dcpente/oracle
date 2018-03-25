package com.dcpente.oracle.osb.deployplan.sorter;

import java.io.FileNotFoundException;
import java.nio.file.FileAlreadyExistsException;
import java.util.Arrays;

import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {

	private final static Logger log = LogManager.getLogger(Main.class);

	public static void main(String[] args) {

		MainArguments.configureConsoleLog(args);

		if (log.isDebugEnabled()) {
			log.debug("Main start");

			if (log.isTraceEnabled()) {
				log.trace(String.format("String[] args: %s", Arrays.toString(args)));
			}
		}

		MainArguments mArgs = new MainArguments(args);
		try {
			mArgs.compile();
		} catch (ParseException exp) {
			log.error("Parsing failed.  Reason: " + exp.getMessage());
			return;
		} catch (FileAlreadyExistsException e) {
			log.error("File already exist: " + e.getMessage());
			return;
		} catch (FileNotFoundException e) {
			log.error("File not found exist: " + e.getMessage());
			return;
		} catch (IllegalArgumentException e) {
			log.error("Invalid argument: " + e.getMessage());
			return;
		} catch (SecurityException e) {
			log.error("Security lock: " + e.getMessage());
			return;
		}


		log.error("test error");
		log.info("Test info");
		log.debug("Test debug");
		log.trace("Test trace");

		if (mArgs.isHelp()) {
			mArgs.printHelp();
			return;
		}
		
		if (mArgs.isVersion()) {
			Package objPackage = Main.class.getPackage();
			System.out.println("Package name: " + objPackage.getImplementationTitle());
			System.out.println("Package version: " + objPackage.getImplementationVersion());
			return;
		}

	}

}
