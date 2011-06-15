package info.yalamanchili.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

public class SyncHttp {
	public static HttpResponse response;

	public static String httpGet(String url) {
		try {
			response = HttpHelper.getHttpClient().execute(new HttpGet(url));
		} catch (Exception e) {
			throw new RuntimeException("Http Get called failed for uri:" + url
					+ e);
		}
		if (response != null)
			return HttpHelper.request(response);
		else
			return null;
	}

	public static String httpPut(String... params) {
		try {
			HttpPut put = new HttpPut(params[0]);
			StringEntity entity = new StringEntity(params[1]);
			put.setEntity(entity);
			put.setHeader("Content-Type", "application/xml");
			response = HttpHelper.getHttpClient().execute(put);
		} catch (ClientProtocolException e) {
			throw new RuntimeException("Http Put called failed for uri:"
					+ params[0] + e);
		} catch (IOException e) {
			throw new RuntimeException("Http Put called failed for uri:"
					+ params[0] + e);
		}
		if (response != null) {
			return HttpHelper.request(response);
		} else
			return null;
	}

	public static String httpPost(String uri, String contentType,
			Map<String, String> params) {
		HttpPost post = new HttpPost(uri);
		post.setHeader("Content-Type", contentType);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		for (String key : params.keySet()) {
			nvps.add(new BasicNameValuePair(key, params.get(key)));
		}
		try {
			post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			response = HttpHelper.getHttpClient().execute(post);
		} catch (Exception e) {
			throw new RuntimeException("Http Post called failed for uri:" + uri
					+ e);
		}
		if (response != null) {
			return HttpHelper.request(response);
		} else
			return null;
	}
}
