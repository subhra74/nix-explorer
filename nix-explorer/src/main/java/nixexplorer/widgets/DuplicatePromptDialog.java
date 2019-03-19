/**
 * 
 */
package nixexplorer.widgets;

import java.awt.BorderLayout;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import nixexplorer.TextHolder;

/**
 * @author subhro
 *
 */
public class DuplicatePromptDialog {
//	private JTextArea txtFileList;
//	private JComboBox<String> cmbAction;
//	private JButton btnOk, btnCancel;
//	private JLabel lblTitle;
//	private boolean approved = false;

	public static int selectDuplicateAction(String fileText) {
		int action = -1;
//		JLabel lblTitle = new JLabel(TextHolder.getString("duplicate.prompt"));
		JComboBox<String> cmbAction = new JComboBox<String>(
				new String[] { TextHolder.getString("duplicate.autorename"),
						TextHolder.getString("duplicate.overwrite"),
						TextHolder.getString("duplicate.skip") });
		JTextArea txtFileList = new JTextArea(fileText);
		txtFileList.setEditable(false);
		if (JOptionPane.showOptionDialog(null,
				new Object[] { TextHolder.getString("duplicate.prompt"),
						cmbAction, txtFileList },
				TextHolder.getString("duplicate.prompt"),
				JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE,
				null, null, null) == JOptionPane.YES_OPTION) {
			action = cmbAction.getSelectedIndex();
		}
		return action;
	}

	/**
	 * 
	 */
//	public DuplicatePromptDialog(String fileText) {
//		lblTitle = new JLabel(TextHolder.getString("duplicate.prompt"));
//		txtFileList = new JTextArea(fileText);
//		txtFileList.setEditable(false);
//		cmbAction = new JComboBox<String>(
//				new String[] { TextHolder.getString("duplicate.autorename"),
//						TextHolder.getString("duplicate.overwrite"),
//						TextHolder.getString("duplicate.skip") });
//		btnOk = new JButton(TextHolder.getString("duplicate.ok"));
//		btnCancel = new JButton(TextHolder.getString("duplicate.cancel"));
//		btnOk.addActionListener(e -> {
//			approved = true;
//			dispose();
//		});
//		btnCancel.addActionListener(e -> {
//			dispose();
//		});
//		add(lblTitle, BorderLayout.NORTH);
//		add(txtFileList);
//		Box b1 = Box.createVerticalBox();
//		cmbAction.setAlignmentX(Box.LEFT_ALIGNMENT);
//		b1.add(cmbAction);
//		Box b = Box.createHorizontalBox();
//		b.add(Box.createHorizontalGlue());
//		b.add(btnOk);
//		b.add(btnCancel);
//		b.setAlignmentX(Box.LEFT_ALIGNMENT);
//		b1.add(b);
//		add(b1, BorderLayout.SOUTH);
//	}

//	public int getAction() {
//		return cmbAction.getSelectedIndex();
//	}
//
//	/**
//	 * @return the approved
//	 */
//	public boolean isApproved() {
//		return approved;
//	}

}
