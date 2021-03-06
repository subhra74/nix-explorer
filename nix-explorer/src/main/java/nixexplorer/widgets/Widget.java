package nixexplorer.widgets;

import java.awt.BorderLayout;
import java.awt.Window;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import nixexplorer.PathUtils;
import nixexplorer.app.components.TabbedChild;
import nixexplorer.app.session.AppSession;
import nixexplorer.app.session.SessionInfo;

public abstract class Widget extends JPanel implements TabbedChild {
	private static final long serialVersionUID = -2994169561586076844L;

	protected SessionInfo info;
	protected String[] args;
	protected JComponent glassPane;
	protected boolean closeInitiated = false;
	protected boolean closePending = false;
	protected AppSession appSession;
	protected JLabel lblTitleTab;
	private Window window;
	protected AtomicBoolean widgetClosed = new AtomicBoolean(Boolean.FALSE);
	protected AtomicBoolean stopRequested = new AtomicBoolean(Boolean.FALSE);

	public Widget(SessionInfo info, String args[], AppSession appSession,
			Window window) {
		this.info = info;
		this.args = args;
		this.appSession = appSession;
		this.window = window;
		setLayout(new BorderLayout());
	}

	@Override
	public void viewClosed() {
		stopRequested.set(true);
	}

	public abstract void reconnect();

	public abstract void close();

	public SessionInfo getInfo() {
		return info;
	}

	public void setInfo(SessionInfo info) {
		this.info = info;
	}

	public AppSession getAppSession() {
		return appSession;
	}

	public void setAppSession(AppSession appSession) {
		this.appSession = appSession;
	}

	public Window getWindow() {
		return window;
	}

	public void setWindow(Window window) {
		this.window = window;
	}

//	protected SshWrapper connect() throws Exception {
//		return SshUtility.connect(info, stopRequested);
//	}

//	public synchronized void stopModal() {
//		BlockedWidgets.remove(this);
//		notifyAll();
//	}
//
//	public synchronized void startModal(Component comp) {
//		BlockedWidgets.add(this);
//		try {
//			if (SwingUtilities.isEventDispatchThread()) {
//				EventQueue theQueue = Toolkit.getDefaultToolkit()
//						.getSystemEventQueue();
//				while (comp.isVisible()) {
//					AWTEvent event = theQueue.getNextEvent();
//					Object source = event.getSource();
//
//					if (event instanceof MouseEvent) {
//						MouseEvent me = (MouseEvent) event;
//						Component c = SwingUtilities.getDeepestComponentAt(
//								me.getComponent(), me.getX(), me.getY());
//						// System.out.println("Component: " + c);
//						boolean found = false;
//						if (c != null) {
//							for (Widget w : BlockedWidgets
//									.getBlockedWidgets()) {
//								if (SwingUtilities.isDescendingFrom(c, w)) {
//									System.err.println(
//											"Unable to dispatch: " + event);
//									found = true;
//									break;
//								}
//							}
//						}
//						if (found) {
//							continue;
//						}
//					}
//
//					if (event instanceof ActiveEvent) {
//						((ActiveEvent) event).dispatch();
//					} else if (source instanceof Component) {
//						((Component) source).dispatchEvent(event);
//					} else if (source instanceof MenuComponent) {
//						((MenuComponent) source).dispatchEvent(event);
//					}
//				}
//			} else {
//				while (comp.isVisible()) {
//					wait();
//				}
//			}
//		} catch (InterruptedException ignored) {
//		}
//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.app.components.TabbedChild#setLabel(javax.swing.JLabel)
	 */
	@Override
	public void setLabel(JLabel labelRef) {
		this.lblTitleTab = labelRef;
	}

	protected String getTabTitle(String title) {
		if (title.length() > 20) {
			return title.substring(0, 20) + "...";
		}
		return title;
	}

	protected void updateTabTitle(String title) {
		if (title != null) {
			if (lblTitleTab != null) {
				String fileName = getTabTitle(title);
				lblTitleTab.setText(fileName);
			}
		}
	}

	@Override
	public boolean getWidgetClosed() {
		return widgetClosed.get();
	}

	@Override
	public void setWidgetClosed(boolean widgetClosed) {
		this.widgetClosed.set(widgetClosed);
	}

	@Override
	public boolean closeView() {
		System.out.println("widget - Closing view: " + this);
		return appSession.closeTab(this);
	}
}
