package control.web;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class RestClient {

	public String createJSONRequest(String url){
		CloseableHttpClient client = HttpClients.createDefault();
		String response = "";
		try{
			HttpGet httpGet = new HttpGet(url);

			// Create a custom response handler
			ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
				@Override
				public String handleResponse(final HttpResponse response)  {
					try{
						int status = response.getStatusLine().getStatusCode();
						if (status >= 200 && status < 300) {
							HttpEntity entity = response.getEntity();
							return entity != null ? EntityUtils.toString(entity) : null;
						} else {
							throw new ClientProtocolException("Unexpected response status: " + status);
						}
					} catch (Exception e){
						return "";
					}
				}
			};

			response = client.execute(httpGet, responseHandler);
			return response;

		} catch (Exception e){ //Catch everything to prevent potential crash
		} finally {
			try {
				client.close();
			} catch (IOException e) {
				//Bad luck
			}
		}

		return response;
	}
}
