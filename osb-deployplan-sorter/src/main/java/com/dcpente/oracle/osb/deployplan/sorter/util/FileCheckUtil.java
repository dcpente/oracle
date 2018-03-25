package com.dcpente.oracle.osb.deployplan.sorter.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.FileAlreadyExistsException;

public class FileCheckUtil {

	public static void mandatoryFileExists(String file, String argumentDescription) 
			throws FileNotFoundException, IllegalArgumentException {
		
		if (file == null)
			throw new IllegalArgumentException(argumentDescription);
		
		File fileObj = new File(file);
		if (!fileObj.exists() || fileObj.isDirectory())
			throw new FileNotFoundException(argumentDescription);
	}
	
	 public static void mandatoryFileCanCreate(String file, String argumentDescription) 
			throws FileAlreadyExistsException, IllegalArgumentException, SecurityException {
		 
		if (file == null)
			throw new IllegalArgumentException(argumentDescription);

		File fileObj = new File(file);
		if (fileObj.exists())
			throw new FileAlreadyExistsException(argumentDescription);
		
		if (!fileObj.canWrite())
			throw new SecurityException(argumentDescription);		
     }
	 
	 public static void mandatoryFileCanCreateOrOverwrite(String file, String argumentDescription) 
			 throws FileAlreadyExistsException, IllegalArgumentException, SecurityException {
		 
		if (file == null)
			throw new IllegalArgumentException(argumentDescription);

		File fileObj = new File(file);
		if (fileObj.exists() && fileObj.isDirectory())
			throw new FileAlreadyExistsException(argumentDescription);
		
		if (!fileObj.canWrite())
			throw new SecurityException(argumentDescription);		
     }
}
