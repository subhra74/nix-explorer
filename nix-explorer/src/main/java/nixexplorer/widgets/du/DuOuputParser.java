package nixexplorer.widgets.du;

import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nixexplorer.PathUtils;

public final class DuOuputParser {
	private static final Pattern duPattern = Pattern.compile("([\\d]+)\\s+(.+)");

	public static final long parse(List<String> inputList, List<DiskUsageEntry> outputList) {
		long total = -1;
		ListIterator<String> reverseIterator = inputList.listIterator(inputList.size());
		boolean first = true;
		while (reverseIterator.hasPrevious()) {
			String item = reverseIterator.previous();
			// System.out.println("Parsing item: " + item);
			Matcher matcher = duPattern.matcher(item);
			if (matcher.find()) {
				if (first) {
					total = Long.parseLong(matcher.group(1)) * 512;
					first = false;
				}

				long size = Long.parseLong(matcher.group(1)) * 512;
				String path = matcher.group(2);
				String name = PathUtils.getFileName(path);
				double usage = ((double) size * 100) / total;
				DiskUsageEntry ent = new DiskUsageEntry(name, path, size, usage < 0 ? 0 : usage);
				outputList.add(ent);
			}
		}
		return total;
	}
}
