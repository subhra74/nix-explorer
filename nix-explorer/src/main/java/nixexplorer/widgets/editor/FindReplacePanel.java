package nixexplorer.widgets.editor;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.util.Locale;
import java.util.regex.Pattern;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import nixexplorer.TextHolder;

public class FindReplacePanel extends JPanel {
	private JCheckBox chkWholeWord, chkIgnoreCase, chkReverse;
	private JButton btnSearch, btnReplace, btnReplaceAll;
	private JTextField txtFind, txtReplace;
	private JTextArea textArea;
	private JButton btnClose;
	private Container cont;
	private int pos;
	private Pattern pattern;
	private boolean finding = false;
	private boolean selectionValid = false;

	public FindReplacePanel(JTextArea textArea, Container cont) {
		this.textArea = textArea;
		this.cont = cont;

		setLayout(new BorderLayout());

		btnSearch = new JButton(TextHolder.getString("searchbox.search"));
		btnReplace = new JButton(TextHolder.getString("searchbox.replace"));

		btnReplaceAll = new JButton(
				TextHolder.getString("searchbox.replaceAll"));

		Box b1 = Box.createHorizontalBox();
		b1.add(Box.createHorizontalGlue());
		b1.add(btnSearch);
		b1.add(btnReplace);
		b1.add(btnReplaceAll);
		b1.setAlignmentX(Box.LEFT_ALIGNMENT);

		JLabel lbl1 = new JLabel(TextHolder.getString("searchbox.search"));
		JLabel lbl2 = new JLabel(TextHolder.getString("searchbox.replace"));

		int maxWidth = Math.max(lbl1.getPreferredSize().width,
				lbl2.getPreferredSize().width);

		Dimension d2 = new Dimension(maxWidth, lbl1.getPreferredSize().height);
		lbl1.setPreferredSize(d2);
		lbl2.setPreferredSize(d2);

		Box b2 = Box.createHorizontalBox();
		txtFind = new JTextField(30);

		b2.add(lbl1);
		b2.add(txtFind);
		b2.setAlignmentX(Box.LEFT_ALIGNMENT);

		Box b3 = Box.createHorizontalBox();
		txtReplace = new JTextField(30);
		b3.add(lbl2);
		b3.add(txtReplace);
		b3.setAlignmentX(Box.LEFT_ALIGNMENT);

		Box b4 = Box.createHorizontalBox();
		chkIgnoreCase = new JCheckBox(
				TextHolder.getString("searchbox.ignoreCase"));
		chkReverse = new JCheckBox(TextHolder.getString("searchbox.reverse"));
		chkWholeWord = new JCheckBox(
				TextHolder.getString("searchbox.wholeWord"));
		b4.add(chkIgnoreCase);
		b4.add(chkReverse);
		b4.add(chkWholeWord);
		b4.setAlignmentX(Box.LEFT_ALIGNMENT);

		Box vbox = Box.createVerticalBox();
		vbox.add(b2);
		vbox.add(b3);
		vbox.add(b4);
		vbox.add(b1);

		btnClose = new JButton(TextHolder.getString("searchbox.close"));
		add(btnClose, BorderLayout.EAST);
		btnClose.addActionListener(e -> {
			getParent().remove(FindReplacePanel.this);
			cont.revalidate();
			cont.repaint();
		});

		add(vbox);

		btnSearch.addActionListener(e -> {

			pos = textArea.getCaretPosition();
			String pattern = txtFind.getText();
			try {
				String content = textArea.getText();

				if (chkIgnoreCase.isSelected()) {
					content = content.toLowerCase(Locale.ENGLISH);
					pattern = pattern.toLowerCase(Locale.ENGLISH);
				}

				int start = -1;

				int index = pos;

				if (chkReverse.isSelected()) {
					if (selectionValid) {
						if (index > 0) {
							index -= 2 * pattern.length();
							System.out.println("Index: " + index
									+ " selectionValid: " + selectionValid);

						}
					}
				}

				if (index < 1) {
					return;
				}

				while (true) {
					if (chkReverse.isSelected()) {
						index = content.lastIndexOf(pattern, index);
					} else {
						index = content.indexOf(pattern, index);
					}
					if (index >= 0) {
						if (chkWholeWord.isSelected()) {
							boolean startOk = false, endOk = false;
							if (index > 0) {
								char ch1 = content.charAt(index - 1);
								startOk = !(Character.isDigit(ch1)
										|| Character.isAlphabetic(ch1));
							} else {
								startOk = true;
							}

							if (index + pattern.length() + 1 >= content
									.length()) {
								endOk = true;
							} else {
								char ch2 = content
										.charAt(index + pattern.length());
								endOk = !(Character.isDigit(ch2)
										|| Character.isAlphabetic(ch2));
							}
							if (startOk && endOk) {
								start = index;
								break;
							}
						} else {
							start = index;
							break;
						}

					} else {
						break;
					}

					if (chkReverse.isSelected()) {
						index -= 1;
					} else {
						index += 1;
					}
				}

				if (start != -1) {
					finding = true;
					textArea.setCaretPosition(start);
					textArea.select(start, start + pattern.length());
					finding = false;
					selectionValid = true;
				}

			} catch (Exception e1) {
				e1.printStackTrace();
			}

		});

		btnReplace.addActionListener(e -> {
			if (!selectionValid) {
				btnSearch.doClick();
			} else {
				int pos1 = textArea.getSelectionStart();
				int pos2 = textArea.getSelectionEnd();
				try {
					textArea.getDocument().remove(pos1, pos2 - pos1);
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}
				textArea.insert(txtReplace.getText(), pos1);
			}
		});

		this.textArea.addCaretListener(new CaretListener() {

			@Override
			public void caretUpdate(CaretEvent e) {
				if (!finding) {
					selectionValid = false;
				}
			}
		});

		this.txtFind.getDocument().addDocumentListener(new DocumentListener() {

			private void changed() {
				pattern = null;
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				changed();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				changed();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				changed();
			}
		});

	}

}
