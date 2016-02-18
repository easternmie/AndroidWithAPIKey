package info.androidhive.loginandregistration.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

import info.androidhive.loginandregistration.R;
import info.androidhive.loginandregistration.helper.SQLiteHandler;
import info.androidhive.loginandregistration.helper.SessionManager;

public class MainActivity extends Activity {

	private TextView txtid;
	private TextView txtName;
	private TextView txtEmail;
	private TextView txtapiKey;
	private TextView txtstatus;
	private TextView txtcreatedAt;
	private Button btnLogout;

	private SQLiteHandler db;
	private SessionManager session;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		txtid = (TextView) findViewById(R.id.txtid);
		txtName = (TextView) findViewById(R.id.name);
		txtEmail = (TextView) findViewById(R.id.email);
		txtapiKey = (TextView) findViewById(R.id.txtapiKey);
		txtstatus = (TextView) findViewById(R.id.txtstatus);
		txtcreatedAt = (TextView) findViewById(R.id.txtcreatedAt);
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
}
