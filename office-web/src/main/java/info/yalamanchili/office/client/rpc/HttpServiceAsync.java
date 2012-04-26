package info.yalamanchili.office.client.rpc;

import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface HttpServiceAsync {
	public void doPut(String url, String body, Map<String, String> headers,
			boolean newClient, AsyncCallback<String> response);

	public void doGet(String url, Map<String, String> headers,
			boolean newClient, AsyncCallback<String> response);
}
