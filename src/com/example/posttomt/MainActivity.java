package com.example.posttomt;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import com.loopj.android.http.*;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.json.*;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity  {
	MainActivity act;
	String cgi_url = "http://your_domain/path_to_mt/mt-data-api.cgi";
	String blog_id = "1";
	String username = "foo";
	String password = "bar";
	String token;
	AsyncHttpClient client;
	Button btnSend;
	TextView txtTitle, txtBody;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		act = this;
		btnSend = (Button) findViewById(R.id.btnSend);
		txtTitle = (TextView) findViewById(R.id.txtTitle);
		txtBody = (TextView) findViewById(R.id.txtBody);

		// Login to Movable Type
		client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.put("username", username);
		params.put("password", password);
		params.put("clientId", "test");
		String url = cgi_url.concat("/v1/authentication");
		client.post(url, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject res) {
				try {
					token = res.getString("accessToken");
					btnSend.setEnabled(true);
    				Toast.makeText(act, "ログインしました", Toast.LENGTH_LONG).show();
				}
				catch (Exception e) {
    				Toast.makeText(act, "ログインエラー", Toast.LENGTH_LONG).show();
    			}
			}
			@Override
			public void onFailure(Throwable e, JSONObject res) {
				Toast.makeText(act, "ログインエラー", Toast.LENGTH_LONG).show();
			}
		});
		
		btnSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Post entry
				btnSend.setEnabled(false);
				Toast.makeText(act, "送信中です", Toast.LENGTH_LONG).show();
				CharSequence title = txtTitle.getText();
				CharSequence body = txtBody.getText();
				JSONObject entry = new JSONObject();
				RequestParams params = new RequestParams();
				try {
					entry.put("title", title);
					entry.put("body", body);
					entry.put("status", "Draft");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				params.put("entry", entry.toString());
				String url = cgi_url.concat("/v1/sites/").concat(blog_id).concat("/entries");
				Header[] headers = new Header[1];
				headers[0] = new BasicHeader("X-MT-Authorization", "MTAuth accessToken=".concat(token));
				client.post(getBaseContext(), url, headers, params, "application/x-www-form-urlencoded", new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(JSONObject res) {
						Toast.makeText(act, "送信完了", Toast.LENGTH_LONG).show();
						btnSend.setEnabled(true);
						txtTitle.setText("");
						txtBody.setText("");
					}
					public void onFailure(Throwable e, JSONObject res) {
						Toast.makeText(act, "送信エラー", Toast.LENGTH_LONG).show();
						btnSend.setEnabled(true);
					}
				});
			}
		});
	}
}
