/**
 * 
 */
package nixexplorer.app.settings.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import nixexplorer.TextHolder;
import nixexplorer.app.settings.snippet.SnippetItem;
import nixexplorer.widgets.util.Utility;

/**
 * @author subhro
 *
 */
public class SnippetManagerPanel extends JPanel {
	private SnippetTableModel model;
	private JTable table;
	private JComboBox<String> cmbChars;
	private JCheckBox chkAlt, chkShift, chkCtrl;

	private JTextField txtName, txtCommand;

	/**
	 * 
	 */
	public SnippetManagerPanel() {
		createUI();
	}

	public void setList(List<SnippetItem> list) {
		model.setList(list);
	}

	private void createUI() {
		setLayout(new BorderLayout());

		model = new SnippetTableModel();
		table = new JTable(model);
		table.setFillsViewportHeight(true);
		table.setShowGrid(false);
		table.setIntercellSpacing(new Dimension(0, 0));

		txtName = new JTextField(30);
		txtCommand = new JTextField(30);

		JLabel titleLabel = new JLabel(TextHolder.getString("snippet.title"));
		titleLabel.setBorder(new EmptyBorder(Utility.toPixel(5),
				Utility.toPixel(5), Utility.toPixel(5), Utility.toPixel(5)));

		add(titleLabel, BorderLayout.NORTH);
		JScrollPane jsp = new JScrollPane(table);
		add(jsp);

		setBorder(new LineBorder(UIManager.getColor("DefaultBorder.color"),
				Utility.toPixel(1)));

		JButton btnAdd = new JButton(TextHolder.getString("snippet.add"));
		btnAdd.addActionListener(e -> {
			txtName.setText("");
			txtCommand.setText("");
			cmbChars.setSelectedItem("");
			chkAlt.setSelected(false);
			chkCtrl.setSelected(false);
			chkShift.setSelected(false);
			showNewSnippetDialog();
		});
		JButton btnDelete = new JButton(TextHolder.getString("snippet.delete"));
		btnDelete.addActionListener(e -> {
			int r = table.getSelectedRow();
			if (r != -1) {
				model.removeSnippetAt(r);
			}
		});
		JButton btnEdit = new JButton(TextHolder.getString("snippet.edit"));
		btnEdit.addActionListener(e -> {
			int r = table.getSelectedRow();
			if (r != -1) {
				SnippetItem item = model.getItemAt(r);
				txtName.setText(item.getName());
				txtCommand.setText(item.getCommand());
				cmbChars.setSelectedItem(item.getKeyChar() + "");
				chkAlt.setSelected(item.isAltDown());
				chkCtrl.setSelected(item.isCtrlDown());
				chkShift.setSelected(item.isShiftDown());
				showNewSnippetDialog();
			}
		});

		Box vbox = Box.createHorizontalBox();
		vbox.add(Box.createHorizontalGlue());
		vbox.add(btnAdd);
		vbox.add(Box.createHorizontalStrut(Utility.toPixel(10)));
		vbox.add(btnEdit);
		vbox.add(Box.createHorizontalStrut(Utility.toPixel(10)));
		vbox.add(btnDelete);
		vbox.setBorder(new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5),
				Utility.toPixel(5), Utility.toPixel(5)));

		add(vbox, BorderLayout.SOUTH);

		String arr[] = new String[26];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = ((char) (i + 'A') + "");
		}

		cmbChars = new JComboBox<>(arr);

		chkAlt = new JCheckBox(TextHolder.getString("snippet.alt"));
		chkShift = new JCheckBox(TextHolder.getString("snippet.shift"));
		chkCtrl = new JCheckBox(TextHolder.getString("snippet.ctrl"));
	}

	private void showNewSnippetDialog() {
		Object arr[] = { TextHolder.getString("snippet.new"),
				TextHolder.getString("snippet.name"), txtName,
				TextHolder.getString("snippet.command"), txtCommand,
				TextHolder.getString("snippet.chars"), cmbChars, chkAlt,
				chkShift, chkCtrl };
		while (JOptionPane.showOptionDialog(null, arr,
				TextHolder.getString("snippet.new"),
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null,
				null, null) == JOptionPane.OK_OPTION) {
			String name = txtName.getText();
			if (name.trim().length() < 1) {
				JOptionPane.showMessageDialog(null,
						TextHolder.getString("snippet.missingName"));
				continue;
			}
			String command = txtCommand.getText();
			if (command.trim().length() < 1) {
				JOptionPane.showMessageDialog(null,
						TextHolder.getString("snippet.missingCommand"));
				continue;
			}

			if (cmbChars.getSelectedIndex() < 0) {
				JOptionPane.showMessageDialog(null,
						TextHolder.getString("snippet.noCharSelected"));
				continue;
			}

			char ch = cmbChars.getSelectedItem().toString().charAt(0);

			if (!(chkAlt.isSelected() || chkCtrl.isSelected()
					|| chkShift.isSelected())) {
				JOptionPane.showMessageDialog(null,
						TextHolder.getString("snippet.noCharSelected"));
				continue;
			}

			SnippetItem item = new SnippetItem(name, command, ch,
					chkAlt.isSelected(), chkCtrl.isSelected(),
					chkShift.isSelected());

			model.addSnippet(item);
			break;
		}
	}

}
