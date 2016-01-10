package com.inekon.app;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.inekon.app.helper.DeleteAll;

public class ShoppingCartBean implements Comparable<ShoppingCartBean> {

	public static final Charset JSON_CHARTSET = Charset.forName("UTF-8");
	
	public class VersionBean implements Comparable<VersionBean> {

		private File excelFile;

		private final File folder;

		private File resultFile;

		private VersionBean(final File aFolder) {
			folder = aFolder;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		public int compareTo(VersionBean o) {
			// compare the filenames
			return o.folder.getName().compareTo(folder.getName());
		}

		public final File getExcelFile() {
			// logging support
			final String LOG_METHOD = "getExcelFile()";
			// lazily construct the name
			if (excelFile == null) {
				excelFile = new File(folder, "driver.xls");
				// log this
				if (bIsLogging) {
					LOGGER.logp(LOG_LEVEL, LOG_CLASS, LOG_METHOD, "Excel file [{0}].", excelFile);
				}
			}
			// ok
			return excelFile;
		}

		public final String getId() {
			return folder.getName();
		}

		public final File getResultFile() {
			// logging support
			final String LOG_METHOD = "getResultFile()";
			// lazily construct the name
			if (resultFile == null) {
				resultFile = new File(folder, "result.json");
				// log this
				if (bIsLogging) {
					LOGGER.logp(LOG_LEVEL, LOG_CLASS, LOG_METHOD, "Result file [{0}].", resultFile);
				}
			}
			// ok
			return resultFile;
		}

		public final String getTitle() {
			return getId();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			// debugging only
			return folder.getName();
		}
	}

	private static final String KEY_TITLE = "title";

	/** class name for the logger */
	private static final String LOG_CLASS = ShoppingCartBean.class.getName();

	/** logging level */
	private static final Level LOG_LEVEL = Level.FINER;

	/** class logger */
	private static final Logger LOGGER = Logger.getLogger(LOG_CLASS);

	private final boolean bIsLogging = LOGGER.isLoggable(LOG_LEVEL);

	/**
	 * Properties
	 */
	private Properties props;

	/**
	 * File that holds the properties
	 */
	private File propsFile;

	/**
	 * Root folder for our file
	 */
	private final File rootFolder;

	private File templateFile;

	public ShoppingCartBean(final File aRootFolder) {
		rootFolder = aRootFolder;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(ShoppingCartBean o) {
		// order by names
		return o.rootFolder.getName().compareTo(rootFolder.getName());
	}

	public VersionBean createNewVersion() throws IOException {
		// logging support
		final String LOG_METHOD = "createNewVersion()";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD);
		}
		// use the current timestamp
		final long time = System.currentTimeMillis();
		final String folderName = "v" + time;
		// new directory
		final File newFolder = new File(rootFolder, folderName);
		newFolder.mkdirs();
		// the version object
		final VersionBean version = new VersionBean(newFolder);
		// copy the template
		final File srcFile = getTemplateFile(), dstFile = version.getExcelFile();
		// copy
		Files.copy(srcFile.toPath(), dstFile.toPath(), REPLACE_EXISTING);
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD, version);
		}
		// ok
		return version;
	}

	public boolean deleteVersion(final VersionBean aVersion) {
		// logging support
		final String LOG_METHOD = "deleteVersion(aVersion)";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD, aVersion);
		}
		// the result
		boolean bResult;
		// root
		final File rootFolder = aVersion.folder;
		if (rootFolder.exists()) {
			try {
				// delete
				Files.walkFileTree(rootFolder.toPath(), DeleteAll.SINGLETON);
				bResult = true;
			} catch (IOException ex) {
				// bad luck
				ex.printStackTrace();
				bResult = false;
			}
		} else {
			bResult = false;
		}
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD, bResult);
		}
		// ok
		return bResult;
	}

	public String getId() {
		return rootFolder.getName();
	}

	public Properties getProperties() {
		// logging support
		final String LOG_METHOD = "getProperties()";
		// lazily access the properties
		if (props == null) {
			// default
			props = new Properties();
			try {
				// load the properties
				final File propsFile = getPropsFile();
				if (propsFile.exists()) {
					// load
					final InputStream is = new FileInputStream(propsFile);
					try {
						props.load(is);
					} finally {
						// done
						is.close();
					}
				}
			} catch (IOException ex) {
				// bad luck, ignore and go with the defaults
				ex.printStackTrace();
			}
			// log this
			if (bIsLogging) {
				LOGGER.logp(LOG_LEVEL, LOG_CLASS, LOG_METHOD, "Properties [{0}].", props);
			}

		}

		return props;
	}

	private final File getPropsFile() {
		// logging support
		final String LOG_METHOD = "getPropsFile()";
		// locate the properties file
		if (propsFile == null) {
			propsFile = new File(rootFolder, "cart.properties");
			// log this
			if (bIsLogging) {
				LOGGER.logp(LOG_LEVEL, LOG_CLASS, LOG_METHOD, "Properties stored in [{0}].", propsFile);
			}
		}
		// returns the files
		return propsFile;
	}

	public final File getTemplateFile() {
		// lazily get the template file
		if (templateFile == null) {
			templateFile = new File(rootFolder, "template.xls");
		}
		// the template file
		return templateFile;
	}

	public final String getTitle() {
		// logging support
		final String LOG_METHOD = "getTitle()";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD);
		}
		// the properties
		final Properties props = getProperties();
		assert props != null;
		// check for a title
		final String title = props.getProperty(KEY_TITLE, getId());
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD, title);
		}
		// ok
		return title;
	}

	public final List<VersionBean> getVersions() {
		// logging support
		final String LOG_METHOD = "getVersions()";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD);
		}
		// the versions
		final List<VersionBean> versions = new ArrayList<VersionBean>();
		// list the versions
		rootFolder.mkdirs();
		for (File file : rootFolder.listFiles()) {
			// check the name
			if (file.isDirectory()) {
				versions.add(new VersionBean(file));
			}
		}
		// order
		Collections.sort(versions);
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD, versions);
		}
		// ok
		return versions;
	}

	public final boolean saveProperties() {
		// logging support
		final String LOG_METHOD = "saveProperties()";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD);
		}
		// the result
		boolean bResult;
		// check if we have props to save
		if (props != null) {
			try {
				// write
				final File propsFile = getPropsFile();
				// make sure the parent directories exist
				propsFile.getParentFile().mkdirs();
				// write
				final FileOutputStream fos = new FileOutputStream(propsFile);
				try {
					// persist
					props.store(fos, null);
					// success
					bResult = true;
				} finally {
					fos.close();
				}
			} catch (IOException ex) {
				// bad luck
				ex.printStackTrace();
				// not saved
				bResult = false;
			}
		} else {
			// not saved
			bResult = false;
		}
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD, bResult);
		}
		// ok
		return bResult;

	}

	public final void setTitle(final String aTitle) {
		// logging support
		final String LOG_METHOD = "setTitle(aTitle)";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD, aTitle);
		}
		// the title
		getProperties().setProperty(KEY_TITLE, aTitle);
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public final String toString() {
		// just debugging
		return rootFolder.getName();
	}
}
