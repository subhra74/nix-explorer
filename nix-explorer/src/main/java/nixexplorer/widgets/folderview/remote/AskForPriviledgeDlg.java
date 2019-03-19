package nixexplorer.widgets.folderview.remote;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import nixexplorer.TextHolder;

public class AskForPriviledgeDlg {
//	private JComboBox<String> cmbCommand;
//	private JButton btnOK, btnCancel;
//	private boolean approved = false;

	public static String askForPriviledge() {
		JComboBox<String> cmbCommand = new JComboBox<String>(
				new String[] { "sudo", "su -m root -c" });
		cmbCommand.setEditable(true);
		if (JOptionPane.showOptionDialog(null,
				new Object[] { TextHolder.getString("elevated.details"),
						new JLabel(TextHolder.getString("elevated.prompt")),
						cmbCommand },
				TextHolder.getString("elevated.title"),
				JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE,
				null, null, null) == JOptionPane.OK_OPTION) {
			return (String) cmbCommand.getSelectedItem();
		}
		return null;
	}

//	public AskForPriviledgeDlg() {
//		cmbCommand = new JComboBox<String>(
//				new String[] { "sudo", "su -m root -c" });
//		JLabel lblText = new JLabel(TextHolder.getString("elevated.details"));
//		setTitle(TextHolder.getString("elevated.title"));
//		btnOK = new JButton(TextHolder.getString("elevated.ok"));
//		btnCancel = new JButton(TextHolder.getString("elevated.cancel"));
//
//		btnOK.addActionListener(e -> {
//			approved = true;
//			dispose();
//		});
//		btnCancel.addActionListener(e -> {
//			dispose();
//		});
//
//		Box b1 = Box.createHorizontalBox();
//		b1.add(Box.createHorizontalGlue());
//		b1.add(btnOK);
//		b1.add(Box.createHorizontalStrut(Utility.toPixel(5)));
//		b1.add(btnCancel);
//
//		JLabel lblPrompt = new JLabel(TextHolder.getString("elevated.prompt"));
//
//		Box vbox = Box.createVerticalBox();
//		vbox.setBorder(new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5),
//				Utility.toPixel(5), Utility.toPixel(5)));
//		lblPrompt.setAlignmentX(Box.LEFT_ALIGNMENT);
//		lblText.setAlignmentX(Box.LEFT_ALIGNMENT);
//		vbox.add(Box.createVerticalStrut(Utility.toPixel(10)));
//		vbox.add(lblText);
//		vbox.add(Box.createVerticalStrut(Utility.toPixel(10)));
//		cmbCommand.setAlignmentX(Box.LEFT_ALIGNMENT);
//		b1.setAlignmentX(Box.LEFT_ALIGNMENT);
//		vbox.add(lblPrompt);
//		vbox.add(Box.createVerticalStrut(Utility.toPixel(5)));
//		vbox.add(cmbCommand);
//		vbox.add(Box.createVerticalStrut(Utility.toPixel(10)));
//		vbox.add(b1);
//
//		add(vbox);
//	}
//
//	public String getCommand() {
//		return (String) cmbCommand.getSelectedItem();
//	}
//
//	public boolean isApproved() {
//		return approved;
//	}
//
//	public void setApproved(boolean approved) {
//		this.approved = approved;
//	}
}
