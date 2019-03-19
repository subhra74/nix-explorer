/**
 * 
 */
package nixexplorer.app.settings.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import nixexplorer.TextHolder;
import nixexplorer.widgets.logviewer.LogHighlightEntry;
import nixexplorer.widgets.util.Utility;

/**
 * @author subhro
 *
 */
public class LogHighlightConfigPanel extends JPanel {
	private LogHighlightConfigTableModel model;
	private JTable table;
	private JButton btnAdd, btnEdit, btnDel;
	private JTextField txtDesc, txtPattern;
	private JLabel lblColor;

	/**
	 * 
	 */
	public LogHighlightConfigPanel() {
		setLayout(new BorderLayout(Utility.toPixel(10), Utility.toPixel(10)));
		model = new LogHighlightConfigTableModel();
		table = new JTable(model);
		table.setFillsViewportHeight(true);
		table.setShowGrid(false);
		table.setIntercellSpacing(new Dimension(0, 0));
		table.setDefaultRenderer(Object.class,
				new LogHighlightRenderer(table, model));
		JScrollPane jsp = new JScrollPane(table);
		add(jsp);

		btnAdd = new JButton(TextHolder.getString("logview.highlight.add"));
		btnEdit = new JButton(TextHolder.getString("logview.highlight.edit"));
		btnDel = new JButton(TextHolder.getString("logview.highlight.del"));

		btnAdd.addActionListener(e -> {
			addItem(false, -1);
		});

		btnEdit.addActionListener(e -> {
			int r = table.getSelectedRow();
			if (r == -1) {
				return;
			}
			addItem(true, r);
		});

		btnDel.addActionListener(e -> {
			int r = table.getSelectedRow();
			if (r == -1) {
				return;
			}
			model.remove(r);
		});

		Box b1 = Box.createHorizontalBox();
		b1.add(Box.createHorizontalGlue());
		b1.add(btnAdd);
		b1.add(Box.createHorizontalStrut(Utility.toPixel(10)));
		b1.add(btnEdit);
		b1.add(Box.createHorizontalStrut(Utility.toPixel(10)));
		b1.add(btnDel);

		add(b1, BorderLayout.SOUTH);
		add(new JLabel(TextHolder.getString("logview.highlight.title")),
				BorderLayout.NORTH);
		setBorder(new EmptyBorder(Utility.toPixel(0), Utility.toPixel(0),
				Utility.toPixel(10), Utility.toPixel(0)));

		txtDesc = new JTextField(30);
		txtPattern = new JTextField(30);
		lblColor = new JLabel();
		lblColor.setPreferredSize(
				new Dimension(Utility.toPixel(100), Utility.toPixel(30)));
		lblColor.setOpaque(true);
		lblColor.addMouseListener(new MouseAdapter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.
			 * MouseEvent)
			 */
			@Override
			public void mouseClicked(MouseEvent e) {
				Color c = JColorChooser.showDialog(null, "Select color",
						lblColor.getBackground());
				if (c != null) {
					lblColor.setBackground(c);
				}
			}
		});
	}

	public void setList(List<LogHighlightEntry> list) {
		this.model.setList(list);
	}

	public List<LogHighlightEntry> getList() {
		return new ArrayList<>(model.getList());
	}

	private void addItem(boolean edit, int r) {
		if (edit) {
			LogHighlightEntry ent = model.getItem(r);
			txtPattern.setText(ent.getPattern());
			txtDesc.setText(ent.getDescription());
			lblColor.setBackground(new Color(ent.getColor()));
		} else {
			txtPattern.setText("");
			txtDesc.setText("");
			lblColor.setBackground(UIManager.getColor("Label.foreground"));
		}
		while (JOptionPane.showOptionDialog(this, new Object[] {
				TextHolder.getString("logview.highlight.description"), txtDesc,
				TextHolder.getString("logview.highlight.pattern"), txtPattern,
				TextHolder.getString("logview.highlight.color"), lblColor },
				TextHolder.getString("logview.highlight.newHighlight"),
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null,
				null, null) == JOptionPane.OK_OPTION) {
			if (txtDesc.getText().isEmpty() || txtPattern.getText().isEmpty()) {
				JOptionPane.showMessageDialog(this,
						TextHolder.getString("logview.highlight.blankField"));
				continue;
			} else {
				LogHighlightEntry ent = new LogHighlightEntry();
				ent.setDescription(txtDesc.getText());
				ent.setColor(lblColor.getBackground().getRGB());
				ent.setPattern(txtPattern.getText());
				if (edit) {
					model.setItem(r, ent);
				} else {
					model.addItem(ent);
				}
				break;
			}
		}
	}
}
