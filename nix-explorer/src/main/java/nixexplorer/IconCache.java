/**
 * 
 */
package nixexplorer;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import nixexplorer.app.components.FileIcon;
import nixexplorer.drawables.icons.ScaledIcon;
import nixexplorer.widgets.util.Utility;

/**
 * @author subhro
 *
 */
public class IconCache {
	private static ConcurrentHashMap<String, FileIcon> largeIcons = new ConcurrentHashMap<>();
	private static ConcurrentHashMap<String, FileIcon> smallIcons = new ConcurrentHashMap<>();

	public static synchronized FileIcon getIconExt(String ext,
			boolean largeIcon) {
		Map<String, FileIcon> iconMap = largeIcon ? largeIcons : smallIcons;
		FileIcon icon = iconMap.get(ext);
		if (icon == null) {
			URL url = IconCache.class.getResource("/images/" + ext + ".png");
			if (url != null) {
				icon = new FileIcon(
						new ScaledIcon(
								IconCache.class
										.getResource("/images/" + ext + ".png"),
								Utility.toPixel(largeIcon ? 48 : 20),
								Utility.toPixel(largeIcon ? 48 : 20)),
						!largeIcon);
				iconMap.put(ext, icon);
			} else {
				return null;
			}
		}
		return iconMap.get(ext);
	}
}
