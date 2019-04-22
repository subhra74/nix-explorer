package nixexplorer.widgets.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import org.fife.rsta.ui.GoToDialog;
import org.fife.rsta.ui.search.FindDialog;
import org.fife.rsta.ui.search.ReplaceDialog;
import org.fife.rsta.ui.search.SearchEvent;
import org.fife.rsta.ui.search.SearchListener;
//import org.fife.rsta.ui.CollapsibleSectionPanel;
//import org.fife.rsta.ui.search.FindDialog;
//import org.fife.rsta.ui.search.FindToolBar;
//import org.fife.rsta.ui.search.ReplaceDialog;
//import org.fife.rsta.ui.search.ReplaceToolBar;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;
import org.fife.ui.rtextarea.Gutter;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.fife.ui.rtextarea.SearchResult;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpATTRS;

import nixexplorer.PathUtils;
import nixexplorer.TextHolder;
import nixexplorer.app.AppContext;
import nixexplorer.app.session.AppSession;
import nixexplorer.app.session.SessionInfo;
import nixexplorer.core.ssh.SshUtility;
import nixexplorer.core.ssh.SshWrapper;
import nixexplorer.widgets.Widget;
import nixexplorer.widgets.folderview.ContentChangeListener;
import nixexplorer.widgets.util.Utility;

public class FormattedEditorWidget extends Widget implements SearchListener {
	private ContentChangeListener listener;
	private static final long serialVersionUID = -3968450910174508931L;
	private String file;
	private RSyntaxTextArea textArea;
	private RTextScrollPane sp;
	private JComboBox<String> cmbSyntax;
	private Box toolbar;
	private JButton btnSave, btnFind, btnReplace, btnGotoLine, btnReload,
			btnCut, btnCopy, btnPaste;
	private SshWrapper wrapper;
	private ChannelSftp sftp;
	private InputStream in;
	private OutputStream out;
	private int length;
	private JProgressBar prgLoad;
	private String tmpFile;
	private Box statusBox;
	private boolean changed = false;
	private FindDialog findDialog;
	private ReplaceDialog replaceDialog;
	private GoToDialog dialog;
	private AtomicBoolean closing = new AtomicBoolean(false);
	private AtomicBoolean saving = new AtomicBoolean(false);
	private AtomicBoolean cancelled = new AtomicBoolean(false);
	private SaveProgressDialog saveProgressDialog;
	private String fileName;
	private Cursor curBusy, curDef;
	private JCheckBox chkWrapText;
	private JSpinner spFont;

	private JComboBox<String> cmbTheme;

	public FormattedEditorWidget(SessionInfo info, String[] args,
			AppSession appSession, Window window) {
		super(info, args, appSession, window);
		if (args.length > 0) {
			this.file = args[0];
			this.fileName = PathUtils.getFileName(this.file);
		}

		this.curBusy = new Cursor(Cursor.WAIT_CURSOR);
		this.curDef = new Cursor(Cursor.DEFAULT_CURSOR);

		this.setLayout(new BorderLayout());
		JPanel top = new JPanel(new BorderLayout());

		chkWrapText = new JCheckBox(TextHolder.getString("editor.wrapText"));
		chkWrapText.setSelected(
				AppContext.INSTANCE.getConfig().getEditor().isWrap());
		chkWrapText.addActionListener(e -> {
			this.textArea.setLineWrap(chkWrapText.isSelected());
			this.textArea.setWrapStyleWord(chkWrapText.isSelected());
			AppContext.INSTANCE.getConfig().getEditor()
					.setWrap(chkWrapText.isSelected());
			AppContext.INSTANCE.getConfig().save();
		});
		spFont = new JSpinner(new SpinnerNumberModel(
				AppContext.INSTANCE.getConfig().getEditor().getFontSize(), 1,
				100, 1));
		spFont.addChangeListener(e -> {
			System.out.println("Setting font: " + (Integer) spFont.getValue());
			Font font = this.textArea.getFont().deriveFont(
					(float) Utility.toPixel((Integer) spFont.getValue()));
			this.textArea.setFont(font);
			this.sp.getGutter().setLineNumberFont(font);
			AppContext.INSTANCE.getConfig().getEditor()
					.setFontSize(font.getSize());
			AppContext.INSTANCE.getConfig().save();
		});

		JTextField txtFilePath = new JTextField(30);
		txtFilePath.setFont(
				new Font(Font.DIALOG, Font.PLAIN, Utility.toPixel(14)));
		txtFilePath.setEditable(false);
		txtFilePath.setText(this.file);
		txtFilePath.setBorder(new EmptyBorder(Utility.toPixel(5),
				Utility.toPixel(5), Utility.toPixel(5), Utility.toPixel(5)));
		top.add(txtFilePath, BorderLayout.NORTH);
		// top.add(createMenu(), BorderLayout.NORTH);
		this.toolbar = Box.createHorizontalBox();
		this.toolbar.add(Box.createRigidArea(
				new Dimension(Utility.toPixel(0), Utility.toPixel(30))));
		this.toolbar.setBorder(
				new MatteBorder(Utility.toPixel(1), 0, Utility.toPixel(1), 0,
						UIManager.getColor("DefaultBorder.color")));

		this.btnSave = new JButton(UIManager.getIcon("TextEditor.saveIcon"));
		this.btnSave.setBorderPainted(false);
		this.btnSave.setToolTipText(TextHolder.getString("editor.save"));

		this.btnFind = new JButton(UIManager.getIcon("TextEditor.findIcon"));
		this.btnFind.setBorderPainted(false);
		this.btnFind.setToolTipText(TextHolder.getString("editor.find"));

		this.btnReplace = new JButton(
				UIManager.getIcon("TextEditor.replaceIcon"));
		this.btnReplace.setBorderPainted(false);
		this.btnReplace.setToolTipText(TextHolder.getString("editor.replace"));

		this.btnGotoLine = new JButton(
				UIManager.getIcon("TextEditor.gotoLineIcon"));
		this.btnGotoLine.setBorderPainted(false);
		this.btnGotoLine
				.setToolTipText(TextHolder.getString("editor.gotoline"));

		this.btnReload = new JButton(
				UIManager.getIcon("TextEditor.reloadIcon"));
		this.btnReload.setBorderPainted(false);
		this.btnReload.setToolTipText(TextHolder.getString("editor.reload"));

		this.btnCut = new JButton(UIManager.getIcon("TextEditor.cutTextIcon"));
		this.btnCut.setBorderPainted(false);
		this.btnCut.setToolTipText(TextHolder.getString("editor.cutText"));

		this.btnPaste = new JButton(
				UIManager.getIcon("TextEditor.pasteTextIcon"));
		this.btnPaste.setBorderPainted(false);
		this.btnPaste.setToolTipText(TextHolder.getString("editor.pasteText"));

		this.btnCopy = new JButton(
				UIManager.getIcon("TextEditor.copyTextIcon"));
		this.btnCopy.setBorderPainted(false);
		this.btnCopy.setToolTipText(TextHolder.getString("editor.copyText"));

		toolbar.add(Box.createHorizontalStrut(Utility.toPixel(15)));
		// toolbar.add(btnNew);
		// toolbar.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		// toolbar.add(btnOpen);
		// toolbar.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		toolbar.add(btnSave);
		toolbar.add(btnFind);
		toolbar.add(btnReplace);
		toolbar.add(btnGotoLine);
		toolbar.add(btnReload);
		toolbar.add(btnCut);
		toolbar.add(btnCopy);
		toolbar.add(btnPaste);
		toolbar.add(Box.createHorizontalStrut(Utility.toPixel(5)));

		btnSave.addActionListener(e -> {
			saveFile();
		});

		btnFind.addActionListener(e -> {
			findText();
		});

		btnReplace.addActionListener(e -> {
			replaceText();
		});

		btnGotoLine.addActionListener(e -> {
			gotoLine();
		});

		btnReload.addActionListener(e -> {
			reloadFile();
		});

		btnCut.addActionListener(e -> {
			textArea.cut();
		});

		btnCopy.addActionListener(e -> {
			textArea.copy();
		});

		btnPaste.addActionListener(e -> {
			textArea.paste();
		});

		this.setPreferredSize(
				new Dimension(Utility.toPixel(640), Utility.toPixel(480)));

		TokenMakerFactory factory = TokenMakerFactory.getDefaultInstance();

		Set<String> styles = factory.keySet();

		String stylesArr[] = new String[styles.size()];

		stylesArr = styles.toArray(stylesArr);

		cmbSyntax = new JComboBox<>(stylesArr);

		cmbSyntax.addItemListener(e -> {
			textArea.setSyntaxEditingStyle(e.getItem() + "");
		});

		Dimension d = new Dimension(Utility.toPixel(120),
				cmbSyntax.getPreferredSize().height);
		cmbSyntax.setPreferredSize(d);
		cmbSyntax.setMaximumSize(d);

		spFont.setMaximumSize(new Dimension(
				spFont.getPreferredSize().width + Utility.toPixel(30),
				spFont.getPreferredSize().height));

		cmbTheme = new JComboBox<>(new String[] { "idea", "dark", "default",
				"default-alt", "default-alt", "monokai", "eclipse", "vs" });

		cmbTheme.addItemListener(e -> {
			applyTheme(e.getItem() + "");
		});

		Dimension d1 = new Dimension(Utility.toPixel(120),
				cmbTheme.getPreferredSize().height);
		cmbTheme.setPreferredSize(d1);
		cmbTheme.setMaximumSize(d1);

		toolbar.add(Box.createHorizontalGlue());
		toolbar.add(new JLabel(TextHolder.getString("editor.syntax")));
		toolbar.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		toolbar.add(cmbSyntax);
		toolbar.add(Box.createHorizontalStrut(Utility.toPixel(15)));
		toolbar.add(new JLabel(TextHolder.getString("editor.fontSize")));
		toolbar.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		toolbar.add(spFont);
		toolbar.add(Box.createHorizontalStrut(Utility.toPixel(15)));
		toolbar.add(chkWrapText);
		toolbar.add(Box.createHorizontalStrut(Utility.toPixel(15)));
		toolbar.add(new JLabel(TextHolder.getString("editor.theme")));
		toolbar.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		toolbar.add(cmbTheme);
		toolbar.add(Box.createHorizontalStrut(Utility.toPixel(15)));
		top.add(toolbar);
		this.add(top, BorderLayout.NORTH);

		this.textArea = new RSyntaxTextArea();
		this.textArea.setFont(new Font(Font.DIALOG, Font.PLAIN, Utility.toPixel(
				AppContext.INSTANCE.getConfig().getEditor().getFontSize())));
		this.textArea.setBackground(UIManager.getColor("TextArea.background"));
		this.textArea.setForeground(UIManager.getColor("TextArea.foreground"));
		this.textArea.setCurrentLineHighlightColor(
				UIManager.getColor("RTextArea.highlight"));
		this.textArea.setBracketMatchingEnabled(true);
		this.textArea.setMarkAllOnOccurrenceSearches(false);
		this.textArea.setSelectedTextColor(Color.WHITE);
		this.textArea
				.setSelectionColor(UIManager.getColor("RTextArea.highlight"));
		this.textArea.setUseSelectedTextColor(true);
//		this.textArea.setUseSelectedTextColor(true);
//		this.textArea.setMarkOccurrences(true);
//		this.textArea.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mouseClicked(MouseEvent e) {
//				textArea.re
//			}
//		});
		// RTextAreaUI

		// this.textArea.setHighlightCurrentLine(false);
//		final Highlighter highlighter = textArea.getHighlighter ();
//        if ( highlighter instanceof RSyntaxTextAreaHighlighter )
//        {
//            ( ( RSyntaxTextAreaHighlighter ) highlighter ).setDrawsLayeredHighlights ( false );
//        }

		// this.textArea.set
		this.sp = new RTextScrollPane(textArea);
		Gutter gutter = this.sp.getGutter();
		gutter.setLineNumberColor(UIManager.getColor("Gutter.foreground"));
		gutter.setBorderColor(UIManager.getColor("DefaultBorder.color"));
		gutter.setLineNumberFont(this.textArea.getFont());
		// gutter.setBackground(Color.BLACK);

		add(sp);

		statusBox = Box.createVerticalBox();
		statusBox.setOpaque(true);
		statusBox.setBorder(new MatteBorder(Utility.toPixel(1), 0, 0, 0,
				UIManager.getColor("DefaultBorder.color")));
		// statusBox.setBackground(UIManager.getColor("DefaultBorder.color"));

		Box b1 = Box.createHorizontalBox();
		b1.add(Box.createRigidArea(
				new Dimension(Utility.toPixel(20), Utility.toPixel(20))));
		b1.add(Box.createHorizontalGlue());
		prgLoad = new JProgressBar();
		b1.add(prgLoad);
		b1.setAlignmentX(Box.LEFT_ALIGNMENT);
		statusBox.add(b1);
		this.add(statusBox, BorderLayout.SOUTH);
		prgLoad.setVisible(false);
		textArea.setSyntaxEditingStyle(
				SyntaxConstants.SYNTAX_STYLE_UNIX_SHELL + "");

		findDialog = new FindDialog((Frame) getWindow(), this);
		replaceDialog = new ReplaceDialog((Frame) getWindow(), this);
		dialog = new GoToDialog((Frame) getWindow());

		this.textArea.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				System.out.println("document change event");
				changed = true;
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				System.out.println("document change event");
				changed = true;
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				System.out.println("document change event");
				changed = true;
			}
		});

		saveProgressDialog = new SaveProgressDialog(getWindow(), e -> {
			cancelled.set(true);
			new Thread(() -> {
				try {
					wrapper.disconnect();
				} catch (Exception e2) {
				}
				saveProgressDialog.setVisible(false);
			}).start();
		});

		this.changed = false;

		for (int i = 0; i < stylesArr.length; i++) {
			String str = stylesArr[i];
			if (str.equals("text/unix")) {
				cmbSyntax.setSelectedIndex(i);
			}
		}

		InputMap inpMap = getInputMap(
				JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		ActionMap actMap = getActionMap();

		KeyStroke ksSave = KeyStroke.getKeyStroke(KeyEvent.VK_S,
				InputEvent.CTRL_DOWN_MASK);

		inpMap.put(ksSave, "saveKey");
		actMap.put("saveKey", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				saveFile();
			}
		});

		KeyStroke ksFind = KeyStroke.getKeyStroke(KeyEvent.VK_F,
				InputEvent.CTRL_DOWN_MASK);

		inpMap.put(ksFind, "findKey");
		actMap.put("findKey", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				findText();
			}
		});

		KeyStroke ksReplace = KeyStroke.getKeyStroke(KeyEvent.VK_H,
				InputEvent.CTRL_DOWN_MASK);

		inpMap.put(ksReplace, "ksReplace");
		actMap.put("ksReplace", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				replaceText();
			}
		});

		KeyStroke ksReload = KeyStroke.getKeyStroke(KeyEvent.VK_R,
				InputEvent.CTRL_DOWN_MASK);

		inpMap.put(ksReload, "reloadKey");
		actMap.put("reloadKey", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				reloadFile();
			}
		});

		KeyStroke ksGotoLine = KeyStroke.getKeyStroke(KeyEvent.VK_G,
				InputEvent.CTRL_DOWN_MASK);

		inpMap.put(ksGotoLine, "gotoKey");
		actMap.put("gotoKey", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				gotoLine();
			}
		});

		cmbTheme.setSelectedItem(UIManager.getString("Editor.theme"));

		loadFile();
	}

	/**
	 * @param string
	 */
	private void applyTheme(String string) {
		try {
			Theme theme = Theme.load(getClass().getResourceAsStream(
					"/org/fife/ui/rsyntaxtextarea/themes/" + string + ".xml"),
					textArea.getFont());
			theme.apply(textArea);
			textArea.validate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	private void reloadFile() {
		if (changed) {
			if (JOptionPane.showConfirmDialog(getWindow(),
					"Changes will be lost. Reload now?") == JOptionPane.YES_OPTION) {
				loadFile();
			}
		}
	}

	/**
	 * 
	 */
	protected void gotoLine() {
		if (findDialog.isVisible()) {
			findDialog.setVisible(false);
		}
		if (replaceDialog.isVisible()) {
			replaceDialog.setVisible(false);
		}

		dialog.setMaxLineNumberAllowed(textArea.getLineCount());
		dialog.setVisible(true);
		int line = dialog.getLineNumber();
		if (line > 0) {
			try {
				textArea.setCaretPosition(
						textArea.getLineStartOffset(line - 1));
			} catch (BadLocationException ble) { // Never happens
				UIManager.getLookAndFeel().provideErrorFeedback(textArea);
				ble.printStackTrace();
			}
		}
	}

	/**
	 * 
	 */
	protected void replaceText() {
		if (findDialog.isVisible()) {
			findDialog.setVisible(false);
		}
		replaceDialog.setVisible(true);
	}

	/**
	 * 
	 */
	protected void findText() {
		if (replaceDialog.isVisible()) {
			replaceDialog.setVisible(false);
		}
		findDialog.setVisible(true);
	}

	/**
	 * 
	 */
	protected void saveFile() {
		System.out.println("Saving file");
		try {
			new Thread(() -> {
				save();
			}).start();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	private void loadFile() {
		if (this.file != null && this.file.length() > 0) {
			this.changed = false;
			try {
				new Thread(() -> {
					load();
				}).start();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	@Override
	public void reconnect() {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() {
		cleanup();
		try {
			if (this.wrapper != null) {
				this.wrapper.disconnect();
				System.out.println("Text editors connection is closed");
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 
	 */
	private void saveChanges() {
		saving.set(true);

		try {
			new Thread(() -> {
				save();
			}).start();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if (saving.get()) {
			saveProgressDialog.setLocationRelativeTo(getWindow());
			saveProgressDialog.setVisible(true);
		}
	}

	private void save() {
		while (true) {
			try {
				try {
					open(true);
					write();
					cleanup();
					changed = false;
					return;
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (cancelled.get()) {
					break;
				}
				System.out.println("Error encountered");
				if (JOptionPane.showConfirmDialog(saveProgressDialog,
						"Error saving file, retry?") != JOptionPane.YES_OPTION) {
					break;
				}
			} finally {
				saving.set(false);
				saveProgressDialog.setVisible(false);
			}
		}
	}

	private void load() {
		SwingUtilities.invokeLater(() -> {
			textArea.setEditable(false);
			prgLoad.setValue(0);
			prgLoad.setVisible(true);
			this.setCursor(curBusy);
		});
		while (true) {
			try {
				open(false);
			} catch (Exception e) {
				changed = false;
				e.printStackTrace();
				this.close();
				SwingUtilities.invokeLater(() -> {
					closeView();
				});
				break;
			}
			try {
				byte[] data = read();
				cleanup();
				SwingUtilities.invokeLater(() -> {
					this.setCursor(curDef);
					textArea.setText(new String(data));
					textArea.setEditable(true);
					prgLoad.setVisible(false);
					textArea.setCaretPosition(0);
					super.updateTabTitle(fileName);
//					if (lblTitleTab != null) {
//						String fileName = PathUtils.getFileName(this.file);
//						lblTitleTab.setText(fileName);
//					}
					changed = false;
					textArea.requestFocusInWindow();
				});
				return;
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (cancelled.get()) {
				this.close();
				break;
			}
			if (JOptionPane.showConfirmDialog(this,
					"Error loading file, retry?") != JOptionPane.YES_OPTION) {
				this.close();
				SwingUtilities.invokeLater(() -> {
					closeView();
				});
				break;
			}
		}
	}

	private void open(boolean write) throws Exception {
		if (info != null) {
			if (wrapper == null || !wrapper.isConnected()) {
				while (true) {
					wrapper = SshUtility.connectWrapper(info, widgetClosed);
					try {
						sftp = wrapper.getSftpChannel();
						break;
					} catch (Exception e) {
						try {
							wrapper.disconnect();
						} catch (Exception e1) {
						}
					}
					if (JOptionPane.showConfirmDialog(null,
							"Unable to connect to server. Retry?") != JOptionPane.YES_OPTION) {
						throw new Exception("User cancelled the operation");
					}
				}
			}

			if (!write) {
				SftpATTRS attrs = sftp.stat(file);
				long sz = attrs.getSize();
				if (sz > Integer.MAX_VALUE) {
					throw new Exception("File larger than 2GB");
				}
				this.length = (int) sz;
				this.in = sftp.get(file);
			} else {
				String tmpFile = UUID.randomUUID().toString();
				String folder = PathUtils.getParent(file);
				String tmpFullPath = PathUtils.combineUnix(folder, tmpFile);
				this.tmpFile = tmpFullPath;
				this.out = sftp.put(tmpFullPath);
			}
		} else {
			if (!write) {
				this.in = new FileInputStream(file);
			} else {
				String tmpFile = UUID.randomUUID().toString();
				String folder = new File(file).getParent();
				String tmpFullPath = new File(folder, tmpFile)
						.getAbsolutePath();
				this.tmpFile = tmpFullPath;
				this.out = new FileOutputStream(tmpFullPath);
			}
		}
	}

	private void write() throws Exception {
		byte[] data = textArea.getText().getBytes();
		this.length = data.length;
		ByteArrayInputStream bin = new ByteArrayInputStream(data);
		byte[] b = new byte[8192];
		int write = 0;
		saveProgressDialog.setValue(0);
		while (true) {
			int x = bin.read(b);
			if (x == -1)
				break;
			this.out.write(b, 0, x);
			write += x;
			int w = write;
			saveProgressDialog.setValue((w * 100) / length);
		}
		this.out.close();
		SftpATTRS attrs1 = sftp.stat(file);
		sftp.rm(file);
		sftp.rename(tmpFile, file);
		SftpATTRS attrs2 = sftp.stat(file);
		attrs2.setPERMISSIONS(attrs1.getPermissions());
		sftp.setStat(file, attrs2);
	}

	private byte[] read() throws Exception {
		SwingUtilities.invokeLater(() -> {
			prgLoad.setValue(0);
			textArea.setText("");
		});

		ByteArrayOutputStream out = new ByteArrayOutputStream(length);
		byte[] b = new byte[8192];
		int read = 0;
		while (true) {
			int x = in.read(b);
			if (x == -1)
				break;
			out.write(b, 0, x);
			read += x;
			int r = read;
			SwingUtilities.invokeLater(() -> {
				prgLoad.setValue((r * 100) / length);
			});
		}
		return out.toByteArray();
	}

	private void cleanup() {
		try {
			if (this.in != null) {
				this.in.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		try {
			if (this.out != null) {
				this.out.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

//		try {
//			if (this.sftp != null) {
//				this.sftp.disconnect();
//			}
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.app.components.TabbedChild#tabClosing()
	 */
	@Override
	public boolean viewClosing() {
		try {
			if (closing.get()) {
				return true;
			}
			if (saving.get()) {
				return true;
			}

			if (changed) {
				if (JOptionPane.showConfirmDialog(getWindow(),
						"Save changes?") != JOptionPane.YES_OPTION) {
					return true;
				} else {
					saveChanges();
					return true;
				}
			}
			return true;
		} finally {
			widgetClosed.set(true);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.app.components.TabbedChild#tabClosed()
	 */
	@Override
	public void viewClosed() {
		super.viewClosed();
		if (closing.get()) {
			return;
		}
		new Thread(() -> {
			closing.set(true);
			try {
				close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.app.components.TabbedChild#tabSelected()
	 */
	@Override
	public void tabSelected() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.app.components.TabbedChild#getIcon()
	 */
	@Override
	public Icon getIcon() {
		return UIManager.getIcon("ServerTools.editorIcon16");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.app.components.TabbedChild#getTitle()
	 */
	@Override
	public String getTitle() {
		return "Text editor";
	}

	private void closeDocument() {
		if (changed) {

		}
	}

	/**
	 * Listens for events from our search dialogs and actually does the dirty
	 * work.
	 */
	@Override
	public void searchEvent(SearchEvent e) {

		SearchEvent.Type type = e.getType();
		SearchContext context = e.getSearchContext();
		SearchResult result = null;

		switch (type) {
		default: // Prevent FindBugs warning later
		case MARK_ALL:
			// result = SearchEngine.markAll(textArea, context);
			break;
		case FIND:
			result = SearchEngine.find(textArea, context);
			if (!result.wasFound()) {
				UIManager.getLookAndFeel().provideErrorFeedback(textArea);
			}
			break;
		case REPLACE:
			result = SearchEngine.replace(textArea, context);
			if (!result.wasFound()) {
				UIManager.getLookAndFeel().provideErrorFeedback(textArea);
			}
			break;
		case REPLACE_ALL:
			result = SearchEngine.replaceAll(textArea, context);
			JOptionPane.showMessageDialog(null,
					result.getCount() + " occurrences replaced.");
			break;
		}

		String text = null;
		if (result != null && result.wasFound()) {
			text = "Text found; occurrences marked: " + result.getMarkedCount();
		} else if (type == SearchEvent.Type.MARK_ALL) {
			if (result.getMarkedCount() > 0) {
				text = "Occurrences marked: " + result.getMarkedCount();
			} else {
				text = "";
			}
		} else {
			text = "Text not found";
		}
		// statusBar.setLabel(text);

	}

	@Override
	public String getSelectedText() {
		return textArea.getSelectedText();
	}

}
