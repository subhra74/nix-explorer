package nixexplorer.test;

import java.awt.Component;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class GroupLayoutTest {

	public static void main(String[] args) {
		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		Component b[] = new Component[4];
		for (int i = 0; i < 3; i++) {
			b[i] = new JButton("Button " + (i + 1));
		}
		
		b[3]=new JTextField();

		layout.setHorizontalGroup(layout.createSequentialGroup().addComponent(b[0]).addComponent(b[1]).addGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(b[2]).addComponent(b[3])));
		layout.setVerticalGroup(
				layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(b[0]).addComponent(b[1]).addComponent(b[2])).addComponent(b[3]));
		
		JFrame f=new JFrame();
		f.setSize(400, 300);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setLocationRelativeTo(null);
		f.add(panel);
		f.setVisible(true);
	}

}
