package nixexplorer.widgets.sysmon;

import java.awt.BorderLayout;
import java.util.Map;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;

import nixexplorer.Constants;
import nixexplorer.widgets.util.Utility;

public class SystemLoadPanel extends JPanel {
	private static final long serialVersionUID = -7520660063724214639L;
	private JLabel lblLoadAvg, lblCpuPct, lblMemPct, lblSwapPct;
	private Box b1;
	private LineGraph cpuGraph, memGraph, swpGraph;
	private float cpuStats[] = new float[60];
	private float memStats[] = new float[60];
	private float swpStats[] = new float[60];
	private float loavAvgs[] = new float[60];

	public SystemLoadPanel() {
		setLayout(new BorderLayout());
		b1 = Box.createVerticalBox();
		lblLoadAvg = new JLabel("Uptime: ");
		lblLoadAvg.setFont(Utility.getFont(Constants.NORMAL));

		b1.add(lblLoadAvg);
		b1.add(Box.createVerticalStrut(Utility.toPixel(5)));

		lblCpuPct = new JLabel("Cpu: ");
		lblMemPct = new JLabel("Memory: ");
		lblSwapPct = new JLabel("Swap: ");

		lblCpuPct.setFont(Utility.getFont(Constants.NORMAL));
		lblMemPct.setFont(Utility.getFont(Constants.NORMAL));
		lblSwapPct.setFont(Utility.getFont(Constants.NORMAL));

		b1.add(lblCpuPct);

		cpuGraph = new LineGraph();
		cpuGraph.setValues(cpuStats);
		b1.add(cpuGraph);
		b1.add(Box.createVerticalStrut(Utility.toPixel(10)));
		b1.add(lblMemPct);

		memGraph = new LineGraph();
		memGraph.setValues(memStats);
		b1.add(memGraph);
		b1.add(Box.createVerticalStrut(Utility.toPixel(10)));
		b1.add(lblSwapPct);

		swpGraph = new LineGraph();
		swpGraph.setValues(swpStats);
		b1.add(swpGraph);

		add(b1, BorderLayout.CENTER);
	}

	public void updateValues(Map<String, String> map) {

		String uptime = map.get("UPTIME");
		StringBuilder distro = new StringBuilder();

		if (map.get("PRETTY_NAME") != null) {
			distro.append(map.get("PRETTY_NAME"));
		} else if (map.get("DISTRIB_DESCRIPTION") != null) {
			distro.append(map.get("DISTRIB_DESCRIPTION"));
		} else if (map.get("DISTRIB_ID") != null) {
			distro.append(map.get("DISTRIB_ID"));
		}

		if (uptime != null) {
			lblLoadAvg.setText("Uptime: " + uptime);
		}

		String cpuValue = map.get("CPU_USAGE");
		String memPct = map.get("MEMORY_USAGE");
		String swapPct = map.get("SWAP_USAGE");

		if (cpuValue != null && cpuValue.length() > 0) {
			try {
				lblCpuPct.setText("Cpu: " + cpuValue + " %");
				System.arraycopy(cpuStats, 1, cpuStats, 0, cpuStats.length - 1);
				cpuStats[cpuStats.length - 1] = Integer.parseInt(cpuValue);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (memPct != null && memPct.length() > 0) {
			try {
				lblMemPct.setText("Memory: " + memPct + " %");
				System.arraycopy(memStats, 1, memStats, 0, memStats.length - 1);
				memStats[memStats.length - 1] = Integer.parseInt(memPct);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (swapPct != null && swapPct.length() > 0) {
			try {
				lblSwapPct.setText("Swap: " + swapPct + " %");
				System.arraycopy(swpStats, 1, swpStats, 0, swpStats.length - 1);
				swpStats[swpStats.length - 1] = Integer.parseInt(swapPct);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		cpuGraph.setValues(cpuStats);
		memGraph.setValues(memStats);
		swpGraph.setValues(swpStats);
//		System.arraycopy(cpuStats, 1, cpuStats, 0, cpuStats.length - 1);
//		cpuStats[cpuStats.length - 1] = Integer.parseInt(cpuValue);
	}

}
