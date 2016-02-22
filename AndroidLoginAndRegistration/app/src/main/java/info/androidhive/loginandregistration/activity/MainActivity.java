package info.androidhive.loginandregistration.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import info.androidhive.loginandregistration.R;
import info.androidhive.loginandregistration.app.AppConfig;
import info.androidhive.loginandregistration.app.AppController;
import info.androidhive.loginandregistration.helper.SQLiteHandler;
import info.androidhive.loginandregistration.helper.SessionManager;

public class MainActivity extends Activity {

	private TextView txtid;
	private EditText txtName;
	private EditText txtEmail;
	private TextView txtapiKey;
	private TextView txtstatus;
	private TextView txtcreatedAt;
	private EditText txtPassword;
	private EditText txtrPassword;
	private Button btnUpdate;
	private Button btnLogout;

	private SQLiteHandler db;
	private SessionManager session;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		txtid = (TextView) findViewById(R.id.txtid);
		txtName = (EditText) findViewById(R.id.name);
		txtEmail = (EditText) findViewById(R.id.email);
		txtapiKey = (TextView) findViewById(R.id.txtapiKey);
		txtstatus = (TextView) findViewById(R.id.txtstatus);
		txtPassword = (EditText) findViewById(R.id.password);
		txtrPassword = (EditText) findViewById(R.id.rPassword);
		txtcreatedAt = (TextView) findViewById(R.id.txtcreatedAt);
		btnUpdate = (Button) findViewById(R.id.btnUpdate);
		btnLogout = (Button) findViewById(R.id.btnLogout);

		// SqLite database handler
		db = new SQLiteHandler(getApplicationContext());

		// session manager
		session = new SessionManager(getApplicationContext());

		if (!session.isLoggedIn()) {
			logoutUser();
		}

		// Fetching user details from SQLite
		HashMap<String, String> user = db.getUserDetails();

		String id = user.get("id");
		String name = user.get("name");
		String email = user.get("email");
		String apiKey = user.get("apiKey");
		String status = user.get("status");
		String createdAt = user.get("createdAt");

		/*String id = "Test_id";
		String name = "Test_name";
		String email = "Test_email";
		String apiKey = "test_apiKey";
		String status = "test_status";
		String createdAt = "testCreatedAt";*/

		// Displaying the user details on the screen
		txtid.setText(id);
		txtName.setText(name);
		txtEmail.setText(email);
		txtapiKey.setText(apiKey);
		txtstatus.setText(status);
		txtcreatedAt.setText(createdAt);

		// Logout button click event
		btnLogout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				logoutUser();
			}
		});

		btnUpdate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String userId = txtid.getText().toString();
				String newName = txtName.getText().toString().trim();
				String newEmail = txtEmail.getText().toString().trim();
				String password = txtPassword.getText().toString().trim();
				String rPassword = txtrPassword.getText().toString().trim();

				// Check for empty data in the form
				if (!newName.isEmpty() && !newEmail.isEmpty() && !password.isEmpty() && password.equals(rPassword.toString())) {
					// login user
					//updateUser(userId, newName, newEmail, password);
					updateUser(userId, newName, newEmail, password);

				} else {
					// Prompt user to enter credentials
					Toast.makeText(getApplicationContext(),
							"Input problem", Toast.LENGTH_LONG)
							.show();
				}
			}
		});
	}

	/**
	 * Logging out the user. Will set isLoggedIn flag to false in shared
	 * preferences Clears the user data from sqlite users table
	 * */
	private void logoutUser() {
		session.setLogin(false);

		db.deleteUsers();

		// Launching the login activity
		Intent intent = new Intent(MainActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();
	}


	public void updateUser(String userId, final String name, final String email, final String password){

		String updateUserURL = AppConfig.URL_UPDATE + "/" + userId;

		StringRequest putRequest = new StringRequest(Request.Method.PUT, updateUserURL,
				new Response.Listener<String>()
				{
					@Override
					public void onResponse(String response) {
						try {
							// Convert String to json object
							JSONObject jObj = new JSONObject(response);

							JSONObject user = jObj.getJSONObject("user");
							String error = user.getString("error");
							String message = user.getString("message");
							if(error.equals("false")){

								Toast.makeText(getApplicationContext(),
										"Successful ", Toast.LENGTH_LONG).show();
							}
							else{

								Toast.makeText(getApplicationContext(),
										"error!!!!!! : " + message, Toast.LENGTH_LONG).show();
							}



						} catch (JSONException e) {
							e.printStackTrace();
						}
						Log.d("Response", response);
					}
				},
				new Response.ErrorListener()
				{
					@Override
					public void onErrorResponse(VolleyError error) {

						Toast.makeText(getApplicationContext(),
								"Failed here "+ error.toString(), Toast.LENGTH_SHORT).show();
						Log.d("Error.Response", error.toString());
					}
				}
		) {


			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				Map<String,String> headers=new HashMap<String,String>();
				String credentials = txtapiKey.getText().toString();

				headers.put("Authorization",credentials);
				headers.put("Content-Type","application/x-www-form-urlencoded");
				return headers;
			}


			@Override
			protected Map<String, String> getParams()
			{
				Map<String, String>  params = new HashMap<String, String> ();
				params.put("name", name);
				params.put("email", email);
				params.put("password", password);
				Log.d("input : ", name + email + password);
				return params;

			}


		};
		AppController.getInstance().addToReqQueue(putRequest, "postReq");
	}



}
