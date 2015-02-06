package net.pudovika.plugins;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.util.FileSetManager;

import java.io.*;

/**
 * UglifyJS uglify
 *
 * @phase compile
 * @goal uglify
 *
 */
public class UglifyMojo extends AbstractMojo {

	/**
	 * @parameter expression="${encoding}" default-value="UTF-8"
	 */
	private String encoding = "UTF-8";

	/**
	 * {@link org.apache.maven.shared.model.fileset.FileSet} containing JavaScript source files.
	 *
	 * @required
	 * @parameter expression="${sources}"
	 */
	protected FileSet sources;

    /**
     * {@link org.apache.maven.shared.model.fileset.FileSet} containing JavaScript source files.
     *
     * @parameter expression="${cssSources}"
     */

    protected FileSet cssSources;

	/**
	 * @required
	 * @parameter expression="${outputDirectory}"
	 */
	protected File outputDirectory;

	/**
	 * Skip UglifyJS execution.
	 *
	 * @parameter expression="${skip}" default-value="false"
	 */
	protected boolean skip = false;

	public void execute() throws MojoExecutionException, MojoFailureException {
		if (skip) {
			getLog().info( "Skipping" );
			return;
		}

		if (outputDirectory == null)
			throw new MojoExecutionException( "outputDirectory is not specified." );

		try {
			int count = uglify(getJsSourceFiles());
			getLog().info( "Uglified " + count + " file(s)." );
            if (cssSources != null) {
                count = cleanCss(getCssSourceFiles());
                getLog().info( "Celeaned " + count + " file(s)." );
            }
		} catch(IOException e) {
			throw new MojoExecutionException("Failure to precompile handlebars templates.", e);
		}
	}

	protected int uglify( File[] jsFiles ) throws IOException {
		int count = 0;
		for (File jsFile : jsFiles) {
			final String jsFilePath = jsFile.getPath();
			getLog().info( "Uglifying " + jsFilePath );
			try {
                Process p = Runtime.getRuntime().exec("uglifyjs " + jsFilePath + " -o " + getOutputFile(jsFile).getPath());
			} catch( IOException e ) {
				getLog().error( "Could not uglify " + jsFile.getPath() + ".", e );
				throw e;
			}
			count++;
		}
		return count;
	}

    protected int cleanCss( File[] cssFiles ) throws IOException {
        int count = 0;
        for (File cssFile : cssFiles) {
            final String cssFilePath = cssFile.getPath();
            getLog().info( "Cleaning Css " + cssFilePath );
            try {
                Process p = Runtime.getRuntime().exec("cleancss " + cssFilePath + " -o " + getOutputFile(cssFile).getPath());
            } catch( IOException e ) {
                getLog().error( "Could not clean css " + cssFile.getPath() + ".", e );
                throw e;
            }
            count++;
        }
        return count;
    }

	private final File getOutputFile( File inputFile ) throws IOException {
		final String relativePath = getSourceDir().toURI().relativize(inputFile.getParentFile().toURI()).getPath();
		final File outputBaseDir = new File(outputDirectory, relativePath);
		if (!outputBaseDir.exists())
			FileUtils.forceMkdir(outputBaseDir);
		return new File(outputBaseDir, inputFile.getName());
	}

	/**
	 * Returns {@link File directory} containing JavaScript source {@link File files}.
	 *
	 * @return {@link File Directory} containing JavaScript source {@link File files}
	 */
	private File getSourceDir() {
		return new File( sources.getDirectory() );
	}

	/**
	 * Returns JavaScript source {@link File files}.
	 *
	 * @return Array of JavaScript source {@link File files}
	 * @throws IOException
	 */
	private File[] getSourceFiles(FileSet src) throws IOException {
		final FileSetManager fileSetManager = new FileSetManager();
		final String[] includedFiles = fileSetManager.getIncludedFiles( src );
		final File sourceDir = getSourceDir();
		final File[] sourceFiles = new File[includedFiles.length];
		for (int i = 0; i < includedFiles.length; i++) {
			sourceFiles[i] = new File( sourceDir, includedFiles[i] );
		}
		return sourceFiles;
	}

    private File[] getCssSourceFiles() throws IOException {
        return getSourceFiles(cssSources);
    }

    private File[] getJsSourceFiles() throws IOException {
        return getSourceFiles(sources);
    }

}
