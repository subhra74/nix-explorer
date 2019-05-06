package nixexplorer.widgets.component;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import nixexplorer.widgets.util.Utility;

public class AddressBarPanel extends JPanel {
	private AddressBar addressBar;
	private JComboBox<String> txtAddressBar;
	private DefaultComboBoxModel<String> model;
	private JButton btnEdit;
	private JPanel addrPanel;
	private boolean updating = false;
	private ActionListener a;

	public AddressBarPanel(char separator, ActionListener popupTriggeredListener) {
		setLayout(new BorderLayout());
		addrPanel = new JPanel(new BorderLayout());
		addrPanel.setBorder(
				new EmptyBorder(Utility.toPixel(3), Utility.toPixel(3), Utility.toPixel(3), Utility.toPixel(3)));
		model = new DefaultComboBoxModel<>();
		txtAddressBar = new JComboBox<>(model);
		txtAddressBar.addActionListener(e -> {
			if (updating) {
				return;
			}
			System.out.println("calling action listener");
			String item = (String) txtAddressBar.getSelectedItem();
			if (e.getActionCommand().equals("comboBoxEdited")) {
				System.out.println("Editted");
				ComboBoxModel<String> model = txtAddressBar.getModel();
				boolean found = false;
				for (int i = 0; i < model.getSize(); i++) {
					if (model.getElementAt(i).equals(item)) {
						found = true;
						break;
					}
				}
				if (!found) {
					txtAddressBar.addItem(item);
				}
				if (a != null) {
					a.actionPerformed(new ActionEvent(this, 0, null));
				}
			}
		});
		txtAddressBar.setEditable(true);
		addressBar = new AddressBar(separator, popupTriggeredListener);
		addressBar.addActionListener(e -> {
			if (a != null) {
				a.actionPerformed(new ActionEvent(this, 0, null));
			}
		});
		btnEdit = new JButton(UIManager.getIcon("AddressBar.edit"));
		// btnEdit.setMargin(new Insets(0, 0, 0, 0));
		btnEdit.setBorderPainted(false);
		// btnEdit.setContentAreaFilled(false);
		// btnEdit.setFocusPainted(false);
		btnEdit.addActionListener(e -> {
			if (!isSelected()) {
				addrPanel.remove(addressBar);
				addrPanel.add(txtAddressBar);
				btnEdit.setIcon(UIManager.getIcon("AddressBar.toggle"));
				btnEdit.putClientProperty("toggle.selected", Boolean.TRUE);
				txtAddressBar.getEditor().selectAll();
			} else {
				addrPanel.remove(txtAddressBar);
				addrPanel.add(addressBar);
				btnEdit.setIcon(UIManager.getIcon("AddressBar.edit"));
				btnEdit.putClientProperty("toggle.selected", Boolean.FALSE);
			}
			revalidate();
			repaint();
		});
		addrPanel.add(addressBar);
		add(addrPanel);
		add(btnEdit, BorderLayout.EAST);
		btnEdit.putClientProperty("toggle.selected", Boolean.FALSE);
	}

	public void setText(String text) {
		System.out.println("Setting text: " + text);
		updating = true;
		txtAddressBar.setSelectedItem(text);
		addressBar.setText(text);
		updating = false;
		System.out.println("Setting text done: " + text);
	}

	public String getText() {
		return isSelected() ? (String) txtAddressBar.getSelectedItem() : addressBar.getSelectedText();
	}

	public void addActionListener(ActionListener e) {
		this.a = e;
	}

	private boolean isSelected() {
		return btnEdit.getClientProperty("toggle.selected") == Boolean.TRUE;
	}
}
