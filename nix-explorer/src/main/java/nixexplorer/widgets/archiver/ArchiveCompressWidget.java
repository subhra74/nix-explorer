package nixexplorer.widgets.archiver;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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

import nixexplorer.PathUtils;
import nixexplorer.TextHolder;
import nixexplorer.app.session.AppSession;
import nixexplorer.core.FileSystemProvider;
import nixexplorer.app.session.SessionInfo;
import nixexplorer.core.ssh.SshUtility;
import nixexplorer.core.ssh.SshWrapper;
import nixexplorer.widgets.util.Utility;

public class ArchiveCompressWidget extends JDialog implements Runnable {
	private static final long serialVersionUID = -1887498535965638214L;
	private Map<String, String> compressCommands = new HashMap<>();
	private Thread t;
	private JList<String> logList;
	private DefaultListModel<String> logListModel;
	private JButton btnStop, btnOK, btnCancel;
	private List<String> files = new ArrayList<>();
	private String file;
	private JProgressBar prg;
	private String folder;
	private SshWrapper wrapper;
	private JPanel panel1;
	private JPanel panel2;
	private JTextField txtFile, txtFolder;
	private JComboBox<String> cmbFormats;
	private String compressCmd;
	private JCheckBox chkExt;
	private FileSystemProvider fs;
	private SessionInfo info;
	private AppSession appSession;
	private AtomicBoolean stopFlag = new AtomicBoolean(false);

	public ArchiveCompressWidget(SessionInfo info, String[] args,
			AppSession appSession, Window window) {
		super(window);
		this.info = info;
		this.appSession = appSession;
		setLayout(new BorderLayout());
		compressCommands.put("tar", "tar cvf - %s|cat>\"%s\"");
		compressCommands.put("tar.gz", "tar cvf - %s|gzip>\"%s\"");
		compressCommands.put("tar.bz2", "tar cvf - %s|bzip2 -z>\"%s\"");
		compressCommands.put("tar.xz", "tar cvf - %s|xz -z>\"%s\"");
		compressCommands.put("zip", "zip -r - %s|cat>\"%s\"");

		for (int i = 1; i < args.length; i++) {
			files.add(PathUtils.getFileName(args[i]));
		}
		this.folder = args[0];
		this.setSize(new Dimension(Utility.toPixel(400), Utility.toPixel(350)));
		this.panel1 = createCompressPanel();
		this.panel2 = createProgressPanel();
		setTitle(TextHolder.getString("archiver.compress"));
		add(panel1);
		setModal(true);
		setLocationRelativeTo(null);

		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				stop();
				dispose();
			}
		});
	}

	private JPanel createCompressPanel() {
		JPanel p2 = new JPanel(new BorderLayout());
		panel1 = new JPanel();
		BoxLayout bl = new BoxLayout(panel1, BoxLayout.Y_AXIS);
		panel1.setLayout(bl);
		p2.setBorder(new EmptyBorder(Utility.toPixel(10), Utility.toPixel(10),
				Utility.toPixel(10), Utility.toPixel(10)));

		JLabel lblName = new JLabel(TextHolder.getString("archiver.filename"));
		lblName.setAlignmentX(LEFT_ALIGNMENT);
		panel1.add(lblName);

		txtFile = new JTextField(30);
		txtFile.setAlignmentX(LEFT_ALIGNMENT);
		panel1.add(txtFile);

		panel1.add(Box.createRigidArea(new Dimension(0, Utility.toPixel(10))));

		txtFile.setMaximumSize(new Dimension(Integer.MAX_VALUE,
				txtFile.getPreferredSize().height));

		JLabel lblFolderName = new JLabel(
				TextHolder.getString("archiver.savein"));
		lblFolderName.setAlignmentX(LEFT_ALIGNMENT);
		panel1.add(lblFolderName);

		Box b2 = Box.createHorizontalBox();
		b2.setAlignmentX(LEFT_ALIGNMENT);
		txtFolder = new JTextField(30);
		txtFolder.setAlignmentX(LEFT_ALIGNMENT);
		b2.add(txtFolder);

		txtFolder.setMaximumSize(new Dimension(Integer.MAX_VALUE,
				txtFolder.getPreferredSize().height));
		txtFolder.setText(folder);

//		btnBrowse = new JButton(TextHolder.getString("archiver.browse"));
//		btnBrowse.setAlignmentX(LEFT_ALIGNMENT);
//		btnBrowse.addActionListener(e -> {
////			FolderBrowserDialog dlg = new FolderBrowserDialog(fs, null, Mode.FolderOpen);
////			dlg.setSelectionCallback(s -> {
////				if (s != null && s.length() > 0) {
////					txtFolder.setText(s);
////				}
////			});
////			AppSessionPanel.getsharedInstance().openWidget(dlg);
//		});
//		b2.add(btnBrowse);
		panel1.add(b2);

		panel1.add(Box.createRigidArea(new Dimension(0, Utility.toPixel(10))));

		String fmts[] = new String[compressCommands.size()];
		int i = 0;
		for (String s : compressCommands.keySet()) {
			fmts[i++] = s;
		}
		cmbFormats = new JComboBox<>(fmts);
		cmbFormats.setAlignmentX(LEFT_ALIGNMENT);

		JLabel lblFmt = new JLabel(TextHolder.getString("archiver.format"));
		lblFmt.setAlignmentX(LEFT_ALIGNMENT);
		panel1.add(lblFmt);
		panel1.add(cmbFormats);
		cmbFormats.setMaximumSize(new Dimension(Integer.MAX_VALUE,
				cmbFormats.getPreferredSize().height));
		cmbFormats.addItemListener(e -> {
			String value = e.getItem().toString();
			String txt = String.format(TextHolder.getString("archiver.addext"),
					"." + value);
			chkExt.setText(txt);
		});

		String name = files.size() == 1 ? files.get(0)
				: PathUtils.getFileName(folder);

		txtFile.setText(name);
		panel1.add(Box.createRigidArea(new Dimension(0, Utility.toPixel(10))));

		chkExt = new JCheckBox(
				String.format(TextHolder.getString("archiver.addext"),
						cmbFormats.getSelectedItem()));
		chkExt.setSelected(true);
		panel1.add(chkExt);

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
			this.file = txtFile.getText();
			if (this.file.length() < 1) {
				return;
			}
			this.compressCmd = compressCommands
					.get(cmbFormats.getSelectedItem().toString());
			this.remove(panel1);
			this.add(panel2);
			revalidate();
			repaint();
			compress();

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

	private JPanel createProgressPanel() {
		panel2 = new JPanel(
				new BorderLayout(Utility.toPixel(10), Utility.toPixel(10)));
		panel2.add(new JLabel(TextHolder.getString("archiver.compressing")),
				BorderLayout.NORTH);
		panel2.setBorder(new EmptyBorder(Utility.toPixel(10),
				Utility.toPixel(10), Utility.toPixel(10), Utility.toPixel(10)));

		logListModel = new DefaultListModel<>();
		logList = new JList<>(logListModel);

		JScrollPane jsp = new JScrollPane(logList);
		panel2.add(jsp);

		prg = new JProgressBar();
		prg.setIndeterminate(true);

		btnStop = new JButton(TextHolder.getString("archiver.stop"));
		btnStop.setPreferredSize(
				new Dimension(btnStop.getPreferredSize().width * 2,
						btnStop.getPreferredSize().height));
		btnStop.addActionListener(e -> {
			stop();
			dispose();
		});

		Box b2 = Box.createVerticalBox();
		b2.add(prg);
		b2.add(Box.createRigidArea(new Dimension(0, Utility.toPixel(10))));

		Box b1 = Box.createHorizontalBox();
		b1.add(Box.createHorizontalGlue());
		b1.add(btnStop);
		b2.add(b1);

		panel2.add(b2, BorderLayout.SOUTH);

		return panel2;
	}

	private void stop() {
		new Thread(() -> {
			stopFlag.set(true);
			try {
				System.out.println("Disconnecting compressor");
				wrapper.disconnect();
			} catch (Exception e) {
			}
		}).start();
	}

	@Override
	public void run() {
		compressAsync();
	}

	private void compressAsync() {
		System.out.println("Compressing.. " + file);
		SwingUtilities.invokeLater(() -> {
			setTitle(TextHolder.getString("archiver.compressing"));
		});
		try {
			wrapper = SshUtility.connectWrapper(info, stopFlag);
//			wrapper = new SshWrapper(info);
//			wrapper.connect();

			StringBuilder sb = new StringBuilder();
			for (String s : files) {
				sb.append(" \"" + s + "\"");
			}

			compressCmd = String.format(compressCmd, sb.toString(),
					PathUtils.combineUnix(txtFolder.getText(),
							file + (chkExt.isSelected()
									? "." + cmbFormats.getSelectedItem()
									: "")));
			String cd = String.format("cd \"%s\";", folder);
			System.out.println(cd + compressCmd);
			log(cd + compressCmd);
			ChannelExec exec = wrapper.getExecChannel();
			InputStream in = exec.getInputStream();
			exec.setCommand(cd + compressCmd);
			exec.connect();

			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in), 512);
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
				SwingUtilities.invokeLater(() -> {
					setTitle(TextHolder.getString("archiver.error"));
				});
			}
		} finally {
			wrapper.disconnect();
			stopProgress();
		}
	}

	private void stopProgress() {
		SwingUtilities.invokeLater(() -> {
			prg.setIndeterminate(false);
			prg.setValue(100);
			btnStop.setText(TextHolder.getString("archiver.close"));
		});
	}

	private void log(String text) {
		SwingUtilities.invokeLater(() -> {
			if (logListModel != null) {
				logListModel.addElement(text);
			}
		});
	}

	private void compress() {
		t = new Thread(this);
		t.start();
	}

}
