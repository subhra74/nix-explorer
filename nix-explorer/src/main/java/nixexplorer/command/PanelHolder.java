package nixexplorer.command;

import java.awt.Component;

public class PanelHolder {
	private Component panel;
	private String name;

	public PanelHolder(Component panel, String name) {
		super();
		this.panel = panel;
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	public Component getPanel() {
		return panel;
	}

	public void setPanel(Component panel) {
		this.panel = panel;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
