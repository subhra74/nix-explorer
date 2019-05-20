package nixexplorer.widgets.runas;

import java.awt.BorderLayout;
import java.awt.Window;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import nixexplorer.App;
import nixexplorer.PathUtils;
import nixexplorer.TextHolder;
import nixexplorer.app.components.DisposableView;
import nixexplorer.app.session.AppSession;
import nixexplorer.app.session.SessionInfo;
import nixexplorer.drawables.icons.ScaledIcon;
import nixexplorer.widgets.util.Utility;

public class RunAsWidget extends JDialog implements DisposableView {
	// private JComboBox<String> cmb;
	private JCheckBox chkOptions1, chkOptions2;
	private JTextField txt1;
	private JTextField txt2;
	private AppSession appSession;
	protected AtomicBoolean widgetClosed = new AtomicBoolean(Boolean.FALSE);

	public RunAsWidget(SessionInfo info, String[] args, AppSession appSession,
			Window window) {
		super(window);
		this.appSession = appSession;
		setIconImage(App.getAppIcon());
		setTitle(TextHolder.getString("runas.title"));

		int border = Utility.toPixel(5);

		try {
			setIconImage(new ScaledIcon(
					App.class.getResource("/images/Run-icon.png"),
					Utility.toPixel(24), Utility.toPixel(24)).getImg());
		} catch (Exception e) {
			e.printStackTrace();
		}

		txt1 = new JTextField(30);
		txt1.setAlignmentX(LEFT_ALIGNMENT);
		txt1.setText(args[0]);

		txt2 = new JTextField(30);
		txt2.setAlignmentX(LEFT_ALIGNMENT);

		chkOptions1 = new JCheckBox(TextHolder.getString("folderview.nohup"));
		chkOptions2 = new JCheckBox(
				TextHolder.getString("folderview.background"));

		chkOptions1.setAlignmentX(LEFT_ALIGNMENT);
		chkOptions2.setAlignmentX(LEFT_ALIGNMENT);

//		cmb = new JComboBox<>(
//				new String[] { TextHolder.getString("folderview.normal"),
//						TextHolder.getString("folderview.nohup"),
//						TextHolder.getString("folderview.background") });
//		cmb.setAlignmentX(LEFT_ALIGNMENT);
//		cmb.setPreferredSize(txt1.getPreferredSize());
//
//		cmb.setMaximumSize(cmb.getPreferredSize());

		JLabel lblCommand = new JLabel(TextHolder.getString("runas.cmd"));
		lblCommand.setAlignmentX(LEFT_ALIGNMENT);
		JLabel lblArgs = new JLabel(TextHolder.getString("runas.args"));
		lblArgs.setAlignmentX(LEFT_ALIGNMENT);
		JLabel lblRunoption = new JLabel(
				TextHolder.getString("folderview.runoption"));
		lblRunoption.setAlignmentX(LEFT_ALIGNMENT);

		setLayout(new BorderLayout());

		Box b1 = Box.createVerticalBox();
		b1.setBorder(new EmptyBorder(border, border, border, border));

		b1.add(lblCommand);
		b1.add(txt1);
		b1.add(lblArgs);
		b1.add(txt2);
		b1.add(lblRunoption);
		b1.add(chkOptions1);
		b1.add(chkOptions2);
		b1.add(Box.createVerticalStrut(Utility.toPixel(10)));

		Box b2 = Box.createHorizontalBox();
		b2.setAlignmentX(LEFT_ALIGNMENT);
		JButton btnOk = new JButton(TextHolder.getString("runas.run"));
		JButton btnCancel = new JButton(TextHolder.getString("runas.cancel"));
		b2.add(Box.createHorizontalGlue());
		b2.add(btnOk);
		b2.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		b2.add(btnCancel);

		btnOk.addActionListener(e -> {
			String parent = PathUtils.getParent(txt1.getText());
			String cmd = "cd \"" + parent + "\"; "
					+ (chkOptions1.isSelected() ? "nohup \"" : "\"")
					+ txt1.getText() + "\" " + txt2.getText()
					+ (chkOptions2.isSelected() ? " &" : "");
			String a[] = new String[2];
			a[0] = "-c";
			a[1] = cmd;
			dispose();
			appSession.createWidget(
					"nixexplorer.widgets.console.TabbedConsoleWidget", a);
		});

		btnCancel.addActionListener(e -> {
			dispose();
		});

		b1.add(b2);

		add(b1);

		this.pack();
		setLocationRelativeTo(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.app.components.DisposableView#viewClosing()
	 */
	@Override
	public boolean viewClosing() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.app.components.DisposableView#viewClosed()
	 */
	@Override
	public void viewClosed() {

	}

	@Override
	public boolean getWidgetClosed() {
		return widgetClosed.get();
	}

	@Override
	public void setWidgetClosed(boolean widgetClosed) {
		this.widgetClosed.set(widgetClosed);
	}

	@Override
	public boolean closeView() {
		dispose();
		return true;
	}

}
