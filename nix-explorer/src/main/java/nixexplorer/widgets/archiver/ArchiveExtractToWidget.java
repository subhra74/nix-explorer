/**
 * 
 */
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
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.jcraft.jsch.ChannelExec;

import nixexplorer.TextHolder;
import nixexplorer.app.session.AppSession;
import nixexplorer.app.session.SessionInfo;
import nixexplorer.core.ssh.SshUtility;
import nixexplorer.core.ssh.SshWrapper;
import nixexplorer.widgets.util.Utility;

/**
 * @author subhro
 *
 */
public class ArchiveExtractToWidget extends JDialog implements Runnable {
	private static Map<String, String> extractCommands;
	private Thread t;
	private JList<String> logList;
	private DefaultListModel<String> logListModel;
	private JButton btnStop;
	private String file;
	private JProgressBar prg;
	private String folder;
	private SshWrapper wrapper;
	private String files;
	private SessionInfo info;
	private AppSession appSession;
	private AtomicBoolean stopFlag = new AtomicBoolean(false);
	private JTextField txtTargetDir;
	private JPanel panel1, panel2;
	private JTextField txtFile;
	private JButton btnOK, btnCancel;

	public ArchiveExtractToWidget(SessionInfo info, String[] args,
			AppSession appSession, Window window) {
		super(window);
		this.info = info;
		this.appSession = appSession;
		setLayout(new BorderLayout());
		setModal(true);
		setTitle(TextHolder.getString("archiver.extract"));
		setSize(Utility.toPixel(450), Utility.toPixel(350));
		setLocationRelativeTo(null);

		if (args.length != 2) {
			return;
		}

		this.file = args[1];
		this.folder = args[0];

		System.out.println("Extract params: file: " + this.file + " folder: "
				+ this.folder);

		panel1 = createExtractToPanel();
		panel2 = createExtractPanel();
		add(panel1);

		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				stop();
				dispose();
			}
		});

		// this.extract();
	}

	private JPanel createExtractToPanel() {
		JPanel p2 = new JPanel(new BorderLayout());
		panel1 = new JPanel();
		BoxLayout bl = new BoxLayout(panel1, BoxLayout.Y_AXIS);
		panel1.setLayout(bl);
		p2.setBorder(new EmptyBorder(Utility.toPixel(10), Utility.toPixel(10),
				Utility.toPixel(10), Utility.toPixel(10)));

		JLabel lblName = new JLabel(TextHolder.getString("archiver.filename"));
		lblName.setAlignmentX(LEFT_ALIGNMENT);
		panel1.add(lblName);

		panel1.add(Box.createRigidArea(new Dimension(0, Utility.toPixel(5))));

		txtFile = new JTextField(30);
		txtFile.setAlignmentX(LEFT_ALIGNMENT);
		txtFile.setMaximumSize(new Dimension(Integer.MAX_VALUE,
				txtFile.getPreferredSize().height));
		txtFile.setEditable(false);
		txtFile.setText(this.file);
		panel1.add(txtFile);

		panel1.add(Box.createRigidArea(new Dimension(0, Utility.toPixel(10))));

		JLabel lblTarget = new JLabel(
				TextHolder.getString("archiver.extractto"));
		lblTarget.setAlignmentX(LEFT_ALIGNMENT);
		panel1.add(lblTarget);

		txtTargetDir = new JTextField();
		txtTargetDir.setText(this.folder);
		txtTargetDir.setAlignmentX(LEFT_ALIGNMENT);
		panel1.add(txtTargetDir);

		panel1.add(Box.createRigidArea(new Dimension(0, Utility.toPixel(5))));

		txtTargetDir.setMaximumSize(new Dimension(Integer.MAX_VALUE,
				txtTargetDir.getPreferredSize().height));

		panel1.add(txtTargetDir);

		panel1.add(Box.createRigidArea(new Dimension(0, Utility.toPixel(10))));

		panel1.add(Box.createVerticalGlue());

		Box b1 = Box.createHorizontalBox();
		b1.setAlignmentX(LEFT_ALIGNMENT);
		b1.add(Box.createHorizontalGlue());
		btnOK = new JButton(TextHolder.getString("archiver.ok"));
		btnCancel = new JButton(TextHolder.getString("archiver.cancel"));
		btnCancel.addActionListener(e -> {
			dispose();
		});

		btnOK.addActionListener(e -> {
			this.folder = txtTargetDir.getText();
			if (this.folder.length() < 1) {
				return;
			}

			this.remove(panel1);
			this.add(panel2);
			revalidate();
			repaint();
			this.extract();

		});
		btnOK.setPreferredSize(btnCancel.getPreferredSize());
		b1.add(btnOK);
		b1.add(Box.createHorizontalStrut(Utility.toPixel(10)));
		b1.add(btnCancel);
		p2.add(panel1);
		p2.add(b1, BorderLayout.SOUTH);
		// panel1.add(b1);
		return p2;
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

	private JPanel createExtractPanel() {
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

		return panel;
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

	private void extractAsync() {
		System.out.println("Extracting.. " + file);
		try {
			wrapper = SshUtility.connect(info, stopFlag);// new
															// SshWrapper(info);
//			wrapper.connect();
			String extractCmd = ArchiveExtractToWidget
					.getExtractCmd(file.toLowerCase());
			if (extractCmd == null) {
				log(TextHolder.getString("archiver.unknownformat"));
				return;
			}
			extractCmd = String.format(extractCmd, file, folder);

			System.out.println("Extract command: " + extractCmd);

//			if (files != null && files.length() > 0) {
//				extractCmd += files;
//			}

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
		extractAsync();
	}
}
