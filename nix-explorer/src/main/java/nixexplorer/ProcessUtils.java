package nixexplorer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nixexplorer.app.AppContext;

public final class ProcessUtils {
	public static void openExternalApp(String file) {
		String os = System.getProperty("os.name").toLowerCase();
		System.out.println("Operating system: " + os);
		ProcessBuilder pb = new ProcessBuilder(AppContext.INSTANCE.getConfig().getFileBrowser().getExternalEditor(),
				file);
		try {
			pb.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void openDefaultApp(String file) {
		String os = System.getProperty("os.name").toLowerCase();
		System.out.println("Operating system: " + os);
		if (os.contains("linux")) {
			openDefaultAppLinux(file);
		} else if (os.contains("mac") || os.contains("darwin") || os.contains("os x")) {
			openDefaultAppOSX(file);
		} else if (os.contains("windows")) {
			openDefaultAppWin(file);
		}
	}

	public static void openDefaultAppLinux(String file) {
		try {
			System.out.println("Opening linux app");
			ProcessBuilder pb = new ProcessBuilder();
			pb.command("xdg-open", file);
			pb.start();// .waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void openDefaultAppWin(String file) {
		try {
			ProcessBuilder builder = new ProcessBuilder();
			List<String> lst = new ArrayList<String>();
			lst.add("rundll32");
			lst.add("url.dll,FileProtocolHandler");
			lst.add(file);
			builder.command(lst);
			System.out.println("Exit code: " + builder.start().waitFor());

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void openDefaultAppOSX(String file) {
		try {
			ProcessBuilder pb = new ProcessBuilder();
			pb.command("open", file);
			pb.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
