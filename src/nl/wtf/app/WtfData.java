package nl.wtf.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

@SuppressLint("UseSparseArrays")
public class WtfData {

	private static final String API_URL = "http://www.wtf.nl/api/v1/news/section/home?limit=10&offset=0";

	// List of post IDs
	private static ArrayList<Integer> wtfPostList;

	// HashMap with the contents of each post ina bundle
	private static HashMap<Integer, Bundle> wtfPostHash;

	private static final WtfData instance = new WtfData();

	protected static final String ID = "id";
	protected static final String TITLE = "title";
	protected static final String SUBJECT = "subject";
	protected static final String SUBTITLE = "subtitle";
	protected static final String LEAD = "lead";
	protected static final String BODY = "body";
	protected static final String MEDIA_ID = "media_id";

	private WtfData() {
		wtfPostHash = new HashMap<Integer, Bundle>(30);
		wtfPostList = new ArrayList<Integer>(30);
	}

	public static WtfData getInstance() {
		return instance;
	}

	private static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	public Bundle getItemAt(int position) {
		Bundle b = wtfPostHash.get(wtfPostList.get(position));
		return b;
	}

	public static void requestNews(final Handler handler) {

		new Thread(new Runnable() {
			public void run() {
				HttpClient httpclient = new DefaultHttpClient();
				HttpGet httpget = new HttpGet(API_URL);

				HttpResponse response;
				try {
					response = httpclient.execute(httpget);
					// Examine the response status
					Log.i("WTF Response", response.getStatusLine().toString());

					// Get hold of the response entity
					HttpEntity entity = response.getEntity();
					// If the response does not enclose an entity, there is no
					// need
					// to worry about connection release

					if (entity != null) {

						// A Simple JSON Response Read
						InputStream instream = entity.getContent();
						String result = convertStreamToString(instream);

						Bundle bPost = null;
						JSONObject jPost = null;

						JSONArray jPostArray = new JSONArray(result);
						int len = jPostArray.length();
						for (int i = 0; i < len; i++) {
							jPost = jPostArray.getJSONObject(i);
							bPost = new Bundle();
							int id = jPost.getInt(ID);
							bPost.putInt(ID, id);
							bPost.putString(TITLE, jPost.getString(TITLE));
							bPost.putString(SUBJECT, jPost.getString(SUBJECT));
							bPost.putString(LEAD, jPost.getString(LEAD));
							bPost.putString(BODY, jPost.getString(BODY));
							bPost.putString(SUBTITLE, jPost.getString(SUBTITLE));
							bPost.putString(MEDIA_ID, jPost.getString(MEDIA_ID));
							wtfPostList.add(id);
							wtfPostHash.put(id, bPost);
						}
						// Closing the input stream will trigger connection
						instream.close();

						Message msg = new Message();
						msg.what = 0; // ok
						handler.handleMessage(msg);
					}

				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					Message msg = new Message();
					msg.what = 1;
					handler.handleMessage(msg);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Message msg = new Message();
					msg.what = 2;
					handler.handleMessage(msg);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Message msg = new Message();
					msg.what = 3;
					handler.handleMessage(msg);
				}

			}
		}).start();

	}

	public int getCount() {
		return wtfPostList.size();
	}

	public int getPositionOf(int id) {
		return wtfPostList.indexOf(id);
	}
}