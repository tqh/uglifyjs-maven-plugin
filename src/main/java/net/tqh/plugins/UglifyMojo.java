package net.tqh.plugins;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;

/**
 * UglifyJS uglify
 *
 * @phase compile
 * @goal uglify
 *
 */
public class UglifyMojo extends AbstractMojo {
	private static final String[] JS_EXTENSIONS = {"js"};

	/**
	 * @parameter expression="${encoding}" default-value="UTF-8"
	 */
	private String encoding = "UTF-8";

	/**
	 * @required
	 * @parameter expression="${sourceDirectory}"
	 */
	protected File sourceDirectory;

	/**
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
			int count = uglify( sourceDirectory );
			getLog().info( "Uglified " + count + " file(s)." );
		} catch(IOException e) {
			throw new MojoExecutionException("Failure to precompile handlebars templates.", e);
		}
	}

	protected int uglify( File directory ) throws IOException {
		Collection<File> jsFiles = FileUtils.listFiles(directory, JS_EXTENSIONS, true);
		int count = 0;

		OutputStreamWriter out = null;
		for (File jsFile : jsFiles) {
			try {
				String output = new JavascriptContext("uglifyjs.js", "uglifyJavascript.js").executeCmdOnFile( "uglifyJavascript", jsFile );
				out = new OutputStreamWriter( new FileOutputStream(getOutputFile(jsFile), false), encoding);
				out.write(output);
			} catch( IOException e ) {
				getLog().error( "Could not uglify '" + jsFile.getPath() + "'.", e );
				throw e;
			} finally {
				Context.exit();
				IOUtils.closeQuietly(out);
			}
			count+=1;
		}
		return count;
	}

	private final File getOutputFile(File inputFile) throws IOException {
		String relativePath = sourceDirectory.toURI().relativize(inputFile.getParentFile().toURI()).getPath();
		File outputBaseDir = new File(outputDirectory, relativePath);
		if (!outputBaseDir.exists())
			FileUtils.forceMkdir(outputBaseDir);
		return new File(outputBaseDir, inputFile.getName());
	}
}
