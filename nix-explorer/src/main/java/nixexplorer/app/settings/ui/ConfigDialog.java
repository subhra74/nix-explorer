/**
 * 
 */
package nixexplorer.app.settings.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import nixexplorer.Constants;
import nixexplorer.TextHolder;
import nixexplorer.app.session.AppSession;
import nixexplorer.app.settings.AppConfig;
import nixexplorer.app.settings.AppConfig.FolderBrowser;
import nixexplorer.app.settings.AppConfig.LogViewer;
import nixexplorer.app.settings.AppConfig.Terminal;
import nixexplorer.widgets.folderview.ViewTogglePanel.ViewMode;
import nixexplorer.widgets.util.Utility;

/**
 * @author subhro
 *
 */
public class ConfigDialog extends JDialog {

	private CardLayout cardLayout;
	private JPanel cardPanel;
	private JList<String> listIndex;
	private JButton btnSave, btnCancel;
	private SnippetManagerPanel snippetManager;
	private LogHighlightConfigPanel logViewerPanel;

	// folderview ui elements
	private JCheckBox chkFolderCachingEnabled, chkReloadFolderAfterOperation, chkSidePanelVisible,
			chkPreferShellOverSftp, chkConfirmBeforeDelete;
	private JComboBox<String> cmbDblClickAction;
	private JComboBox<String> cmbMainViewMode;
	private JComboBox<String> cmbSidePanelViewMode;
	private JTextField txtExternalEditor;
	private JButton btnBrowseEditor;
	private AppConfig config;

	// terminal ui elements
	private JCheckBox chkX11CopyPaste;
	private JSpinner spFontSize;
	private JLabel lblTermBackGround, lblTermForeGround;
	private JButton btnChooseBgColor, btnChooseFgColor;

	// general panel
	private JCheckBox chkUseDarkThemes;

	/**
	 * 
	 */
	public ConfigDialog(Window window, AppConfig config) {
		super(window);
		createUI();
		setLocationRelativeTo(window);
		this.config = config;
		setConfig(config);
		listIndex.setSelectedIndex(0);
	}

	public void selectPage(int pageIndex) {
		listIndex.setSelectedIndex(pageIndex);
	}

	public void setConfig(AppConfig config) {
		setFolderViewConfig(config);
		setTerminalConfig(config);
		setLogViewerConfig(config);
		setGeneralConfig(config);
	}

	public void saveConfig(AppConfig config) {
		updateFolderViewConfig(config);
		updateTerminalConfig(config);
		updateLogViewerConfig(config);
		updateGeneralConfig(config);
	}

	private void setLogViewerConfig(AppConfig config) {
		logViewerPanel.setList(config.getLogViewer().getHighlightList());
	}

	private void setGeneralConfig(AppConfig config) {
		chkUseDarkThemes.setSelected(config.isUseDarkTheme());
	}

	private void setFolderViewConfig(AppConfig config) {
		FolderBrowser fb = config.getFileBrowser();
		chkFolderCachingEnabled.setSelected(fb.isFolderCachingEnabled());
		chkReloadFolderAfterOperation.setSelected(fb.isReloadFolderAfterOperation());
		chkSidePanelVisible.setSelected(fb.isSidePanelVisible());
		chkPreferShellOverSftp.setSelected(fb.isPreferShellOverSftp());
		chkConfirmBeforeDelete.setSelected(fb.isConfirmBeforeDelete());
		cmbDblClickAction.setSelectedIndex(fb.getDblClickAction());
		cmbSidePanelViewMode.setSelectedIndex(fb.getSidePanelViewMode());
		txtExternalEditor.setText(fb.getExternalEditor());
		cmbMainViewMode.setSelectedIndex(fb.getViewMode() == ViewMode.List ? 0 : 1);
	}

	private void updateFolderViewConfig(AppConfig config) {
		FolderBrowser fb = config.getFileBrowser();
		fb.setFolderCachingEnabled(chkFolderCachingEnabled.isSelected());
		fb.setReloadFolderAfterOperation(chkReloadFolderAfterOperation.isSelected());
		fb.setSidePanelVisible(chkSidePanelVisible.isSelected());
		fb.setPreferShellOverSftp(chkPreferShellOverSftp.isSelected());
		fb.setConfirmBeforeDelete(chkConfirmBeforeDelete.isSelected());

		fb.setDblClickAction(cmbDblClickAction.getSelectedIndex());
		fb.setSidePanelViewMode(cmbSidePanelViewMode.getSelectedIndex());
		fb.setExternalEditor(txtExternalEditor.getText());
		fb.setViewMode(cmbMainViewMode.getSelectedIndex() == 0 ? ViewMode.List : ViewMode.Details);
	}

	private void setTerminalConfig(AppConfig config) {
		Terminal terminal = config.getTerminal();
		chkX11CopyPaste.setSelected(terminal.isX11CopyPaste());
		spFontSize.setValue(Integer.valueOf(terminal.getFontSize()));
		System.out
				.println("C1: " + new Color(terminal.getBackGround()) + " C2: " + new Color(terminal.getForeGround()));
		lblTermBackGround.setBackground(new Color(terminal.getBackGround()));
		lblTermForeGround.setBackground(new Color(terminal.getForeGround()));
		snippetManager.setList(terminal.getSnippets());
	}

	private void updateLogViewerConfig(AppConfig config) {
		LogViewer logViewer = config.getLogViewer();
		logViewer.setHighlightList(logViewerPanel.getList());
	}

	private void updateGeneralConfig(AppConfig config) {
		config.setUseDarkTheme(chkUseDarkThemes.isSelected());
	}

	private void updateTerminalConfig(AppConfig config) {
		Terminal terminal = config.getTerminal();
		terminal.setX11CopyPaste(chkX11CopyPaste.isSelected());
		terminal.setFontSize((Integer) spFontSize.getValue());
		terminal.setForeGround(lblTermForeGround.getBackground().getRGB());
		terminal.setBackGround(lblTermBackGround.getBackground().getRGB());
	}

	private void createUI() {
		setSize(Utility.toPixel(640), Utility.toPixel(480));
		setModal(true);
		logViewerPanel = new LogHighlightConfigPanel();
		cardLayout = new CardLayout();
		cardPanel = new JPanel(cardLayout);
		cardPanel.add(createGeneralPanel(), TextHolder.getString("config.title.general"));
		cardPanel.add(createFolderViewPanel(), TextHolder.getString("config.title.folderView"));
		cardPanel.add(createTerminalPanel(), TextHolder.getString("config.title.terminal"));
		cardPanel.add(logViewerPanel, TextHolder.getString("logviewer.title"));

		listIndex = new JList<>(new String[] { TextHolder.getString("config.title.general"),
				TextHolder.getString("config.title.folderView"), TextHolder.getString("config.title.terminal"),
				TextHolder.getString("logviewer.title") });

		listIndex.setCellRenderer(new IndexCellRenderer());

		listIndex.setFont(Utility.getFont(Constants.NORMAL));

		listIndex.getSelectionModel().addListSelectionListener(e -> {
			System.out.println("Selected: " + listIndex.getSelectedValue());
			cardLayout.show(cardPanel, listIndex.getSelectedValue());
		});

		JScrollPane jsp = new JScrollPane(listIndex);
		jsp.setPreferredSize(new Dimension(Utility.toPixel(200), Utility.toPixel(400)));
		jsp.setBorder(new LineBorder(UIManager.getColor("DefaultBorder.color"), Utility.toPixel(1)));

		btnSave = new JButton(TextHolder.getString("config.button.save"));
		btnCancel = new JButton(TextHolder.getString("config.button.cancel"));

		btnSave.addActionListener(e -> {
			saveConfig(config);
			dispose();
		});

		btnCancel.addActionListener(e -> {
			dispose();
		});

		add(jsp, BorderLayout.WEST);

		Box b1 = Box.createHorizontalBox();
		b1.add(Box.createHorizontalGlue());
		b1.add(btnSave);
		b1.add(Box.createRigidArea(new Dimension(Utility.toPixel(10), Utility.toPixel(10))));
		b1.add(btnCancel);

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(cardPanel);
		panel.add(b1, BorderLayout.SOUTH);
		panel.setBorder(
				new EmptyBorder(Utility.toPixel(10), Utility.toPixel(10), Utility.toPixel(10), Utility.toPixel(10)));

		add(panel);
	}

	private JComponent createTerminalPanel() {
		Box box = Box.createVerticalBox();
		chkX11CopyPaste = new JCheckBox(TextHolder.getString("config.terminal.x11CopyPaste"));
		chkX11CopyPaste.setAlignmentX(Box.LEFT_ALIGNMENT);

		Box fontBox = Box.createHorizontalBox();
		fontBox.setAlignmentX(Box.LEFT_ALIGNMENT);

		JLabel lblFontSize = new JLabel(TextHolder.getString("config.terminal.fontSize"));

		spFontSize = new JSpinner(new SpinnerNumberModel());

		fontBox.add(lblFontSize);
		fontBox.add(Box.createHorizontalGlue());
		fontBox.add(spFontSize);

		JLabel lblTermBgText = new JLabel(TextHolder.getString("config.terminal.backgroundColor"));
		JLabel lblTermFgText = new JLabel(TextHolder.getString("config.terminal.foregroundColor"));

		lblTermBackGround = new JLabel();
		lblTermForeGround = new JLabel();

		lblTermBackGround.setOpaque(true);
		lblTermForeGround.setOpaque(true);

		lblTermBackGround.setPreferredSize(new Dimension(Utility.toPixel(50), Utility.toPixel(20)));
		lblTermForeGround.setPreferredSize(new Dimension(Utility.toPixel(50), Utility.toPixel(20)));

		lblTermForeGround.setMinimumSize(new Dimension(Utility.toPixel(50), Utility.toPixel(20)));
		lblTermBackGround.setMinimumSize(new Dimension(Utility.toPixel(50), Utility.toPixel(20)));

		lblTermForeGround.setMaximumSize(new Dimension(Utility.toPixel(50), Utility.toPixel(20)));
		lblTermBackGround.setMaximumSize(new Dimension(Utility.toPixel(50), Utility.toPixel(20)));

		lblTermBackGround.setBackground(Color.BLACK);
		lblTermForeGround.setBackground(Color.BLACK);

		btnChooseBgColor = new JButton(TextHolder.getString("config.terminal.select"));
		btnChooseFgColor = new JButton(TextHolder.getString("config.terminal.select"));

		btnChooseBgColor.addActionListener(e -> {
			Color c = JColorChooser.showDialog(null, "Select color", lblTermBackGround.getBackground());
			if (c != null) {
				lblTermBackGround.setBackground(c);
			}
		});

		btnChooseFgColor.addActionListener(e -> {
			Color c = JColorChooser.showDialog(null, "Select color", lblTermForeGround.getBackground());
			if (c != null) {
				lblTermForeGround.setBackground(c);
			}
		});

		Box bgBox = Box.createHorizontalBox();
		bgBox.add(lblTermBgText);
		bgBox.add(Box.createHorizontalGlue());
		bgBox.add(lblTermBackGround);
		bgBox.add(Box.createRigidArea(new Dimension(Utility.toPixel(10), Utility.toPixel(10))));
		bgBox.add(btnChooseBgColor);

		bgBox.setAlignmentX(Box.LEFT_ALIGNMENT);

		Box fgBox = Box.createHorizontalBox();
		fgBox.add(lblTermFgText);
		fgBox.add(Box.createHorizontalGlue());
		fgBox.add(lblTermForeGround);
		fgBox.add(Box.createRigidArea(new Dimension(Utility.toPixel(10), Utility.toPixel(10))));
		fgBox.add(btnChooseFgColor);

		fgBox.setAlignmentX(Box.LEFT_ALIGNMENT);

		spFontSize.setPreferredSize(
				new Dimension(btnChooseBgColor.getPreferredSize().width, spFontSize.getPreferredSize().height));
		spFontSize.setMaximumSize(
				new Dimension(btnChooseBgColor.getPreferredSize().width, spFontSize.getPreferredSize().height));

		box.add(chkX11CopyPaste);
		box.add(Box.createVerticalStrut(Utility.toPixel(10)));

		box.add(fontBox);
		box.add(Box.createVerticalStrut(Utility.toPixel(10)));

		box.add(fgBox);
		box.add(Box.createVerticalStrut(Utility.toPixel(10)));

		box.add(bgBox);
		box.add(Box.createVerticalStrut(Utility.toPixel(10)));

		snippetManager = new SnippetManagerPanel();
		snippetManager.setAlignmentX(Box.LEFT_ALIGNMENT);

		box.add(snippetManager);

		box.add(Box.createVerticalStrut(Utility.toPixel(10)));

		return box;
	}

	private JComponent createFolderViewPanel() {
		Box box = Box.createVerticalBox();
		chkFolderCachingEnabled = new JCheckBox(TextHolder.getString("config.folderview.caching"));
		chkFolderCachingEnabled.setAlignmentX(Box.LEFT_ALIGNMENT);

		chkReloadFolderAfterOperation = new JCheckBox(TextHolder.getString("config.folderview.autoReload"));
		chkReloadFolderAfterOperation.setAlignmentX(Box.LEFT_ALIGNMENT);

		chkSidePanelVisible = new JCheckBox(TextHolder.getString("config.folderview.sidePane"));
		chkSidePanelVisible.setAlignmentX(Box.LEFT_ALIGNMENT);

		chkPreferShellOverSftp = new JCheckBox(TextHolder.getString("config.folderview.preferShell"));
		chkPreferShellOverSftp.setAlignmentX(Box.LEFT_ALIGNMENT);

		chkConfirmBeforeDelete = new JCheckBox(TextHolder.getString("config.folderview.delete"));
		chkConfirmBeforeDelete.setAlignmentX(Box.LEFT_ALIGNMENT);

		JLabel lblDblClickAction = new JLabel(TextHolder.getString("config.folderview.dblClickText"));
		lblDblClickAction.setAlignmentX(Box.LEFT_ALIGNMENT);
		JLabel lblSidePanelViewMode = new JLabel(TextHolder.getString("config.folderview.viewMode"));
		lblSidePanelViewMode.setAlignmentX(Box.LEFT_ALIGNMENT);

		JLabel lblMainPanelViewMode = new JLabel(TextHolder.getString("config.folderview.view"));
		lblMainPanelViewMode.setAlignmentX(Box.LEFT_ALIGNMENT);

		cmbDblClickAction = new JComboBox<>(new String[] { TextHolder.getString("config.folderview.openWithTextEditor"),
				TextHolder.getString("config.folderview.openWithExternalEditor"),
				TextHolder.getString("config.folderview.openWithSystemDefaultApp") });
		cmbDblClickAction.setAlignmentX(Box.LEFT_ALIGNMENT);

		cmbMainViewMode = new JComboBox<>(new String[] { TextHolder.getString("config.folderview.ListView"),
				TextHolder.getString("config.folderview.DetailsView") });
		cmbMainViewMode.setAlignmentX(Box.LEFT_ALIGNMENT);

		cmbSidePanelViewMode = new JComboBox<>(new String[] { TextHolder.getString("config.folderview.treeView"),
				TextHolder.getString("config.folderview.listView") });
		cmbSidePanelViewMode.setAlignmentX(Box.LEFT_ALIGNMENT);

		adjustFieldForBox(cmbDblClickAction);
		adjustFieldForBox(cmbMainViewMode);
		adjustFieldForBox(cmbSidePanelViewMode);

		txtExternalEditor = new JTextField(30);
		adjustFieldForBox(txtExternalEditor);

		btnBrowseEditor = new JButton(TextHolder.getString("config.folderview.browse"));
		btnBrowseEditor.addActionListener(e -> {
			JFileChooser jfc = new JFileChooser();
			jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			jfc.setMultiSelectionEnabled(false);
			if (jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				File file = jfc.getSelectedFile();
				txtExternalEditor.setText(file.getAbsolutePath());
			}
		});

		JLabel lblExtEdit = new JLabel(TextHolder.getString("config.folderview.externalEditor"));
		lblExtEdit.setAlignmentX(Box.LEFT_ALIGNMENT);

		Box bx = Box.createHorizontalBox();
		bx.add(txtExternalEditor);
		bx.add(Box.createHorizontalGlue());
		bx.add(Box.createRigidArea(new Dimension(Utility.toPixel(10), Utility.toPixel(10))));
		bx.add(btnBrowseEditor);
		bx.setAlignmentX(Box.LEFT_ALIGNMENT);

		box.add(chkReloadFolderAfterOperation);
		box.add(Box.createVerticalStrut(Utility.toPixel(10)));

		box.add(chkSidePanelVisible);
		box.add(Box.createVerticalStrut(Utility.toPixel(10)));

		box.add(chkPreferShellOverSftp);
		box.add(Box.createVerticalStrut(Utility.toPixel(10)));

		box.add(chkConfirmBeforeDelete);
		box.add(Box.createVerticalStrut(Utility.toPixel(10)));

		box.add(lblSidePanelViewMode);
		box.add(cmbSidePanelViewMode);
		box.add(Box.createVerticalStrut(Utility.toPixel(10)));

		box.add(lblMainPanelViewMode);
		box.add(cmbMainViewMode);
		box.add(Box.createVerticalStrut(Utility.toPixel(10)));

		box.add(lblDblClickAction);
		box.add(cmbDblClickAction);
		box.add(Box.createVerticalStrut(Utility.toPixel(10)));

		box.add(lblExtEdit);
		box.add(bx);
		box.add(Box.createVerticalStrut(Utility.toPixel(10)));

		return box;
	}

	private void adjustFieldForBox(JComponent component) {
		component.setPreferredSize(
				new Dimension(component.getPreferredSize().width, component.getPreferredSize().height));
		component.setMaximumSize(new Dimension(Integer.MAX_VALUE, component.getPreferredSize().height));
	}

	private JComponent createGeneralPanel() {
		Box b1 = Box.createVerticalBox();
		chkUseDarkThemes = new JCheckBox(TextHolder.getString("config.general.useDarkTheme"));
		chkUseDarkThemes.setAlignmentX(Box.LEFT_ALIGNMENT);
		b1.add(chkUseDarkThemes);
		return b1;
	}
}
