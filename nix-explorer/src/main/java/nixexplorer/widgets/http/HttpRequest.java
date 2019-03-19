/**
 * 
 */
package nixexplorer.widgets.http;

import java.util.ArrayList;
import java.util.List;

/**
 * @author subhro
 *
 */
public class HttpRequest {

	/**
	 * 
	 */
	private String method = "GET", url = "http://", id, contentType;
	/**
	 * 
	 */
	private List<KeyValuePair> queryParams = new ArrayList<>();
	/**
	 * 
	 */
	private List<KeyValuePair> headres = new ArrayList<>();

	/**
	 * 
	 */
	private int payloadType;

	/**
	 * 
	 */
	private String payloadFilePath, payloadData;

	/**
	 * 
	 */
	public HttpRequest() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param id
	 * @param method
	 * @param url
	 * @param queryParams
	 * @param headres
	 * @param payloadType
	 * @param payloadFilePath
	 * @param payloadData
	 */
	public HttpRequest(String id, String method, String url,
			List<KeyValuePair> queryParams, List<KeyValuePair> headres,
			int payloadType, String contentType, String payloadFilePath,
			String payloadData) {
		super();
		this.id = id;
		this.method = method;
		this.url = url;
		this.queryParams = queryParams;
		this.headres = headres;
		this.payloadType = payloadType;
		this.contentType = contentType;
		this.payloadFilePath = payloadFilePath;
		this.payloadData = payloadData;
	}

	/**
	 * @return the method
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * @param method the method to set
	 */
	public void setMethod(String method) {
		this.method = method;
	}

	/**
	 * @return the url
	 */

	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */

	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the queryParams
	 */

	public List<KeyValuePair> getQueryParams() {
		return queryParams;
	}

	/**
	 * @param queryParams the queryParams to set
	 */

	public void setQueryParams(List<KeyValuePair> queryParams) {
		this.queryParams = queryParams;
	}

	/**
	 * @return the headres
	 */

	public List<KeyValuePair> getHeadres() {
		return headres;
	}

	/**
	 * @param headres the headres to set
	 */

	public void setHeadres(List<KeyValuePair> headres) {
		this.headres = headres;
	}

	/**
	 * @return the payloadType
	 */

	public int getPayloadType() {
		return payloadType;
	}

	/**
	 * @param payloadType the payloadType to set
	 */

	public void setPayloadType(int payloadType) {
		this.payloadType = payloadType;
	}

	/**
	 * @return the payloadFilePath
	 */

	public String getPayloadFilePath() {
		return payloadFilePath;
	}

	/**
	 * @param payloadFilePath the payloadFilePath to set
	 */

	public void setPayloadFilePath(String payloadFilePath) {
		this.payloadFilePath = payloadFilePath;
	}

	/**
	 * @return the payloadData
	 */

	public String getPayloadData() {
		return payloadData;
	}

	/**
	 * @param payloadData the payloadData to set
	 */

	public void setPayloadData(String payloadData) {
		this.payloadData = payloadData;
	}

	/**
	 * @return the id
	 */

	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */

	public void setId(String id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getUrl();
	}

	/**
	 * @return the contentType
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * @param contextType the contentType to set
	 */
	public void setContentType(String contextType) {
		this.contentType = contextType;
	}
}
