package net.tqh.plugins;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.util.FileSetManager;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

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

	class JavascriptContext {
		final Context cx = Context.enter();
		final ScriptableObject global = cx.initStandardObjects();

		JavascriptContext( String... scripts ) throws IOException {
			ClassLoader cl = getClass().getClassLoader();
			for( String script : scripts ) {
				InputStreamReader in = new InputStreamReader(cl.getResourceAsStream("script/" + script));
				cx.evaluateReader( global, in, script, 1, null);
				IOUtils.closeQuietly( in );
			}
		}

		String executeCmdOnFile( String cmd, File file ) throws IOException {
			String data = FileUtils.readFileToString( file, "UTF-8" );
			ScriptableObject.putProperty( global, "data", data);
			return cx.evaluateString( global, cmd + "(String(data));", "<cmd>", 1, null).toString();
		}
	}

	public void execute() throws MojoExecutionException, MojoFailureException {
		if (skip) {
			getLog().info( "Skipping" );
			return;
		}

		if (outputDirectory == null)
			throw new MojoExecutionException( "outputDirectory is not specified." );

		try {
			int count = uglify(getSourceFiles());
			getLog().info( "Uglified " + count + " file(s)." );
		} catch(IOException e) {
			throw new MojoExecutionException("Failure to precompile handlebars templates.", e);
		}
	}

	protected int uglify( File[] jsFiles ) throws IOException {
		int count = 0;
		OutputStreamWriter out = null;
		for (File jsFile : jsFiles) {
			final String jsFilePath = jsFile.getPath();
			getLog().info( "Uglifying " + jsFilePath );
			try {
				String output = new JavascriptContext("uglifyjs.js", "uglifyJavascript.js").executeCmdOnFile( "uglifyJavascript", jsFile );
				out = new OutputStreamWriter( new FileOutputStream(getOutputFile(jsFile), false), encoding);
				out.write(output);
			} catch( IOException e ) {
				getLog().error( "Could not uglify " + jsFile.getPath() + ".", e );
				throw e;
			} finally {
				Context.exit();
				IOUtils.closeQuietly(out);
			}
			count+=1;
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
	private File[] getSourceFiles() throws IOException {
		final FileSetManager fileSetManager = new FileSetManager();
		final String[] includedFiles = fileSetManager.getIncludedFiles( sources );
		final File sourceDir = getSourceDir();
		final File[] sourceFiles = new File[includedFiles.length];
		for (int i = 0; i < includedFiles.length; i++) {
			sourceFiles[i] = new File( sourceDir, includedFiles[i] );
		}
		return sourceFiles;
	}

}
