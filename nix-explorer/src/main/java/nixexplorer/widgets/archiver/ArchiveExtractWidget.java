package nixexplorer.widgets.archiver;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.jcraft.jsch.ChannelExec;

import nixexplorer.TextHolder;
import nixexplorer.app.session.AppSession;
import nixexplorer.app.session.SessionInfo;
import nixexplorer.core.ssh.SshUtility;
import nixexplorer.core.ssh.SshWrapper;
import nixexplorer.widgets.util.Utility;

public class ArchiveExtractWidget extends JDialog implements Runnable {
	private static final long serialVersionUID = -1887498535965638214L;
	private static Map<String, String> extractCommands;
	private Thread t;
	private JList<String> logList;
	private DefaultListModel<String> logListModel;
	private JButton btnStop;
	private String path;
	private JProgressBar prg;
	private String folder;
	private SshWrapper wrapper;
	private String files;
	private SessionInfo info;
	private AppSession appSession;
	private AtomicBoolean stopFlag = new AtomicBoolean(false);

	public ArchiveExtractWidget(SessionInfo info, String[] args,
			AppSession appSession, Window window) {
		super(window);
		this.info = info;
		this.appSession = appSession;
		setModal(true);
		setTitle(TextHolder.getString("archiver.extract"));
		setSize(Utility.toPixel(450), Utility.toPixel(350));
		setLocationRelativeTo(null);

		this.path = args[1];
		this.folder = args[0];

		if (args.length == 3) {
			this.files = args[2];
		}

		createExtractPanel();

		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				stop();
				dispose();
			}
		});

		this.extract();
	}

//	public ArchiverWidget(SessionInfo info, List<String> files, String folder) {
//		this.info = info;
//		compressCommands.put("tar", "tar cvf %s %s");
//		compressCommands.put("tar.gz", "tar cvf - %s|gzip>%s");
//		compressCommands.put("tar.bz2", "tar cvf - %s|bzip2 -z>%s");
//		putClientProperty("location.center", "true");
//		putClientProperty("frame.dialog", "true");
//
//		this.path = file;
//		this.folder = folder;
//
//		createExtractPanel();
//
//		this.extract();
//	}

	public static synchronized String getExtractCmd(String file) {
		if (extractCommands == null) {
			extractCommands = new TreeMap<>(new Comparator<String>() {
				public int compare(String o1, String o2) {
					if (o1.length() == o2.length()) {
						return o1.compareTo(o2);
					}
					return o1.length() - o2.length();
				};
			});
			extractCommands.put(".tar", "cat \"%s\"|tar -C \"%s\" -xvf -");
			extractCommands.put(".tar.gz",
					"gunzip -c <\"%s\"|tar -C \"%s\" -xvf -");
			extractCommands.put(".tgz",
					"gunzip -c <\"%s\"|tar -C \"%s\" -xvf -");
			extractCommands.put(".tar.bz2",
					"bzip2 -d -c <\"%s\"|tar -C \"%s\" -xvf -");
			extractCommands.put(".tbz2",
					"bzip2 -d -c <\"%s\"|tar -C \"%s\" -xvf -");
			extractCommands.put(".tbz",
					"bzip2 -d -c <\"%s\"|tar -C \"%s\" -xvf -");
			extractCommands.put(".tar.xz",
					"xz -d -c <\"%s\"|tar -C \"%s\" -xvf -");
			extractCommands.put(".txz",
					"xz -d -c <\"%s\"|tar -C \"%s\" -xvf -");
			extractCommands.put(".zip", "unzip -o \"%s\" -d \"%s\" ");
		}
		for (String key : extractCommands.keySet()) {
			System.out.println(file + " " + key + " " + (file.endsWith(key)));
			if (file.endsWith(key)) {
				return extractCommands.get(key);
			}
		}
		return null;
	}

	private void extract() {
		t = new Thread(this);
		t.start();
	}

	private void createExtractPanel() {
		setLayout(new BorderLayout());

		JPanel panel = new JPanel(
				new BorderLayout(Utility.toPixel(10), Utility.toPixel(10)));
		panel.setBorder(new EmptyBorder(Utility.toPixel(10),
				Utility.toPixel(10), Utility.toPixel(10), Utility.toPixel(10)));

		logListModel = new DefaultListModel<>();
		logList = new JList<>(logListModel);
		logList.setFont(
				new Font(Font.MONOSPACED, Font.PLAIN, Utility.toPixel(12)));
		panel.add(new JScrollPane(logList));

		Box b2 = Box.createVerticalBox();

		prg = new JProgressBar();

		prg.setIndeterminate(true);
		b2.add(prg);
		b2.add(Box.createVerticalStrut(Utility.toPixel(10)));

		btnStop = new JButton(TextHolder.getString("archiver.stop"));
		btnStop.setPreferredSize(
				new Dimension(btnStop.getPreferredSize().width * 2,
						btnStop.getPreferredSize().height));
		btnStop.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				stop();
				dispose();
			}
		});
		Box b1 = Box.createHorizontalBox();
//		b1.setBorder(new EmptyBorder(Utility.toPixel(10), Utility.toPixel(10),
//				Utility.toPixel(10), Utility.toPixel(10)));
		b1.add(Box.createHorizontalGlue());
		b1.add(btnStop);
		b2.add(b1);
		panel.add(b2, BorderLayout.SOUTH);

		add(panel);
	}

	private void stop() {
		new Thread(() -> {
			stopFlag.set(true);
			try {
				if (wrapper != null) {
					System.out.println("Disconnecting decompressor");
					wrapper.disconnect();
				}
			} catch (Exception e) {
			}
		}).start();
	}

	private void log(String text) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (logListModel != null) {
					logListModel.addElement(text);
					if (logListModel.getSize() > 0) {
						logList.ensureIndexIsVisible(
								logListModel.getSize() - 1);
					}
				}
			}
		});
	}

	private void stopProgress() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				prg.setIndeterminate(false);
				prg.setValue(100);
				btnStop.setText(TextHolder.getString("archiver.close"));
			}
		});
	}

	private void extractAsync(String path) {
		System.out.println("Extracting.. " + path);
		try {
			wrapper = SshUtility.connect(info, stopFlag);// new
															// SshWrapper(info);
			// wrapper.connect();
			String extractCmd = ArchiveExtractWidget
					.getExtractCmd(path.toLowerCase());
			if (extractCmd == null) {
				log(TextHolder.getString("archiver.unknownformat"));
				return;
			}
			extractCmd = String.format(extractCmd, path, folder);

			if (files != null && files.length() > 0) {
				extractCmd += files;
			}

			System.out.println(extractCmd);
			log(extractCmd);
			ChannelExec exec = wrapper.getExecChannel();
			InputStream in = exec.getInputStream();
			exec.setCommand(extractCmd);
			exec.connect();

			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			while (true) {
				String line = reader.readLine();
				if (line != null && line.length() > 0) {
					log(line);
					System.err.println(line);
				}
				if (!wrapper.isConnected() || exec.getExitStatus() != -1) {
					break;
				}
			}

			System.err.flush();

			reader.close();
			exec.disconnect();
			int ret = exec.getExitStatus();
			System.err.println("Exit code: " + ret);
			log(TextHolder.getString("archiver.exitcode") + ret);

			if (ret == 0) {
				SwingUtilities.invokeLater(() -> {
					dispose();
				});
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (!stopFlag.get()) {
				log(TextHolder.getString("archiver.error"));
			}
		} finally {
			wrapper.disconnect();
			stopProgress();
		}
	}

	@Override
	public void run() {
		extractAsync(this.path);
	}
}
