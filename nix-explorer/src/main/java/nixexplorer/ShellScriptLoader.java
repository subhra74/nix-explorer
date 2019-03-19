package nixexplorer;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ShellScriptLoader {
	public static synchronized String loadShellScript(String name,
			String path) {
		try {
			StringBuilder sb = new StringBuilder();
			try (BufferedReader r = new BufferedReader(new InputStreamReader(
					ShellScriptLoader.class.getResourceAsStream(
							"/scripts/" + path + "/" + name)))) {
				while (true) {
					String s = r.readLine();
					if (s == null) {
						break;
					}
					sb.append(s + "\n");
				}
			}
			return sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
