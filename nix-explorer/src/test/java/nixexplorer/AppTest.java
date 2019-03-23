package nixexplorer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.jcraft.jsch.ChannelExec;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import nixexplorer.app.session.SessionInfo;
import nixexplorer.core.ssh.SshWrapper;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public AppTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(AppTest.class);
	}

	/**
	 * Rigourous Test :-)
	 */
	public void testApp() {
		assertTrue(true);
	}

	public void testPaths() {
		System.out.println("Called");
		assertEquals(PathUtils.combineUnix("/", "initrd.img"), "/initrd.img");
		assertEquals(PathUtils.combineUnix("", "initrd.img"), "/initrd.img");
	}

	/**
	 * @throws Exception
	 * 
	 */
	public void testExt() throws Exception {
		String file = "/home/subhro/x.zip";
		assertEquals(FilenameUtils.getExtension(file), "zip");
	}
}
