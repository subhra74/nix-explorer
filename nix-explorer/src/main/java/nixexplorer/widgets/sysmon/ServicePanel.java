/**
 * 
 */
package nixexplorer.widgets.sysmon;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import nixexplorer.TextHolder;
import nixexplorer.widgets.util.Utility;

/**
 * @author subhro
 *
 */
public class ServicePanel extends JPanel {
	private ServiceTableModel model = new ServiceTableModel();
	private JTable table;
	private static final Pattern SERVICE_PATTERN = Pattern
			.compile("(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+([\\S]+.*)");
	private static final Pattern UNIT_PATTERN = Pattern
			.compile("(\\S+)\\s+([\\S]+.*)");
	private JButton btnStart, btnStop, btnRestart, btnReload, btnEnable,
			btnDisable, btnRefresh;

	private JCheckBox chkRunAsSuperUser;

	private static final String SEP = UUID.randomUUID().toString();

	private static final String SYSTEMD_COMMAND = "systemctl list-unit-files -t service -a --plain --no-pager --no-legend --full; echo "
			+ SEP
			+ "; systemctl list-units -t service -a --plain --no-pager --no-legend --full";

	/**
	 * 
	 */
	public ServicePanel() {
		super(new BorderLayout(Utility.toPixel(5), Utility.toPixel(5)));
		table = new JTable(model);
		table.setFillsViewportHeight(true);
		table.setAutoCreateRowSorter(true);
		add(new JScrollPane(table));

		Box box = Box.createHorizontalBox();

		btnStart = new JButton(TextHolder.getString("sysmon.service.start"));
		btnStop = new JButton(TextHolder.getString("sysmon.service.stop"));
		btnRestart = new JButton(
				TextHolder.getString("sysmon.service.restart"));
		btnReload = new JButton(TextHolder.getString("sysmon.service.reload"));
		btnEnable = new JButton(TextHolder.getString("sysmon.service.enable"));
		btnDisable = new JButton(
				TextHolder.getString("sysmon.service.disable"));

		chkRunAsSuperUser = new JCheckBox(
				TextHolder.getString("sysmon.superuser"));
		box.add(chkRunAsSuperUser);

		box.add(Box.createHorizontalGlue());
		box.add(btnStart);
		box.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		box.add(btnStop);
		box.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		box.add(btnRestart);
		box.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		box.add(btnReload);
		box.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		box.add(btnEnable);
		box.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		box.add(btnDisable);
		box.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		btnRefresh = new JButton(TextHolder.getString("sysmon.refresh"));
		box.add(btnRefresh);

		add(box, BorderLayout.SOUTH);
	}

	public void setElevationActionListener(ActionListener a) {
		chkRunAsSuperUser.addActionListener(a);
	}

	public void setRefreshActionListener(ActionListener a) {
		btnRefresh.addActionListener(a);
	}

	public void setStartServiceActionListener(ActionListener a) {
		btnStart.addActionListener(a);
	}

	public void setStopServiceActionListener(ActionListener a) {
		btnStop.addActionListener(a);
	}

	public void setRestartServiceActionListener(ActionListener a) {
		btnRestart.addActionListener(a);
	}

	public void setReloadServiceActionListener(ActionListener a) {
		btnReload.addActionListener(a);
	}

	public void setEnableServiceActionListener(ActionListener a) {
		btnEnable.addActionListener(a);
	}

	public void setDisableServiceActionListener(ActionListener a) {
		btnDisable.addActionListener(a);
	}

	private String getSelectedService() {
		int r = table.getSelectedRow();
		if (r < 0) {
			return null;
		}
		return (String) model.getValueAt(table.convertRowIndexToModel(r), 0);
	}

	public String getStartServiceCommand() {
		String cmd = getSelectedService();
		if (cmd == null) {
			return null;
		}
		return "systemctl start " + cmd;
	}

	public String getStopServiceCommand() {
		String cmd = getSelectedService();
		if (cmd == null) {
			return null;
		}
		return "systemctl stop " + cmd;
	}

	public String getRestartServiceCommand() {
		String cmd = getSelectedService();
		if (cmd == null) {
			return null;
		}
		return "systemctl restart " + cmd;
	}

	public String getReloadServiceCommand() {
		String cmd = getSelectedService();
		if (cmd == null) {
			return null;
		}
		return "systemctl reload " + cmd;
	}

	public String getEnableServiceCommand() {
		String cmd = getSelectedService();
		if (cmd == null) {
			return null;
		}
		return "systemctl enable " + cmd;
	}

	public String getDisableServiceCommand() {
		String cmd = getSelectedService();
		if (cmd == null) {
			return null;
		}
		return "systemctl disable " + cmd;
	}

	public void setUseSuperUser(boolean select) {
		chkRunAsSuperUser.setSelected(select);
	}

	public boolean getUseSuperUser() {
		return chkRunAsSuperUser.isSelected();
	}

	public void setServiceData(List<String> data) {
		Map<String, ServiceEntry> unitMap = new HashMap<>();
		boolean parsingUnit = true;
		for (String s : data) {
			if (parsingUnit && s.equals(SEP)) {
				parsingUnit = false;
				continue;
			}

			if (parsingUnit) {
				ServiceEntry e = parseUnitFile(s);
				if (e != null) {
					unitMap.put(e.getName(), e);
				}
			} else {
				parseUnit(s, unitMap);
			}

		}

		model.clear();
		model.addEntries(unitMap.entrySet().stream().map(e -> e.getValue())
				.collect(Collectors.toList()));
	}

	private ServiceEntry parseUnitFile(String data) {
		Matcher m = UNIT_PATTERN.matcher(data);
		if (m.find() && m.groupCount() == 2) {
			ServiceEntry e = new ServiceEntry();
			e.setName(m.group(1));
			e.setUnitStatus("");
			e.setDesc("");
			e.setUnitFileStatus(m.group(2));
			return e;
		}
		return null;
	}

	private void parseUnit(String data, Map<String, ServiceEntry> unitMap) {
		Matcher m = SERVICE_PATTERN.matcher(data);
		if (m.find() && m.groupCount() == 5) {
			ServiceEntry e = unitMap.get(m.group(1));
			if (e != null) {
				e.setDesc(m.group(5));
				e.setUnitStatus(m.group(3) + "(" + m.group(4) + ")");
			}
		}
	}

	public String getServiceListCommand() {
		return SYSTEMD_COMMAND;
	}
}
