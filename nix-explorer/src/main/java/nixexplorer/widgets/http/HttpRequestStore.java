/**
 * 
 */
package nixexplorer.widgets.http;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import nixexplorer.app.session.AppSession;

/**
 * @author subhro
 *
 */
public class HttpRequestStore {

	private static HttpRequestStore me;

	/**
	 * @return
	 */
	public static HttpRequestStore getSharedInstance(AppSession appSession) {
		if (me == null) {
			me = new HttpRequestStore(
					new File(appSession.getDirectory(), "curl.json"));
		}
		return me;
	}

	/**
	 * 
	 */
	public HttpRequestStore(File file) {
		this.file = file;
	}

	/**
	 * 
	 */
	private ObjectMapper objectMapper = new ObjectMapper();
	/**
	 * 
	 */
	private File file;

	/**
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public List<HttpRequest> load()
			throws JsonParseException, JsonMappingException, IOException {
		return objectMapper.readValue(file,
				new TypeReference<List<HttpRequest>>() {
				});
	}

	/**
	 * @param list
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public void store(List<HttpRequest> list)
			throws JsonGenerationException, JsonMappingException, IOException {
		objectMapper.writeValue(file, list);
	}
}
