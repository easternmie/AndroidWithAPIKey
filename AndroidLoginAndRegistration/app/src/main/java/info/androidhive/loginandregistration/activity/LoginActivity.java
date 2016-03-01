/**
 * Author: Ravi Tamada
 * URL: www.androidhive.info
 * twitter: http://twitter.com/ravitamada
 */
package info.androidhive.loginandregistration.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class LoginActivity extends Activity  implements LoaderCallbacks<Cursor> {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnLogin;
    private Button btnLinkToRegister;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    //private Dialog wcDialog;
    private SessionManager session;
    private SQLiteHandler db;


    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                // Check for empty data in the form
                if (!email.isEmpty() && !password.isEmpty()) {
                    // login user
                    checkLogin(email, password);

                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Please enter the credentials!", Toast.LENGTH_LONG)
                            .show();
                }
            }

        });

        // Link to Register Screen
        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        inputEmail.setError(null);
        inputPassword.setError(null);

        // Store values at the time of the login attempt.
        String noMyKad = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(noMyKad, password);

            //mAuthTask.execute((Void) null);
            mAuthTask.execute();
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.

    }


    /**
     * function to verify login details in mysql db
     * */
    private void checkLogin(final String email, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Logging in ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    //boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    //if (!error) {
                    //if (true) {
                        // user successfully logged in
                        // Create login session


                        // Now store the user in SQLite
                        //String uid = jObj.getString("uid");
                        //String uid = "1";

                        JSONObject user = jObj.getJSONObject("user");
                        //String error = user.getString("createdAt");


                        String error = user.getString("error");


                        // Launch main activity
                        if (error.equals("false")) {

                            String id = user.getString("id");
                            String name = user.getString("name");
                            String email = user.getString("email");
                            String apiKey = user.getString("apiKey");
                            String status = user.getString("status");
                            String createdAt = user.getString("createdAt");

                            // Inserting row in users table
                            db.addUser(id, name, email, apiKey, status, createdAt);

                            session.setLogin(true);

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {

                            String errorMsg = user.getString("message");
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                            alertDialogBuilder.setMessage(errorMsg + " Login again?");

                            alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    //Toast.makeText(LoginActivity.this,"You clicked yes button",Toast.LENGTH_LONG).show();
                                }
                            });

                            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            });

                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();

                            //wcDialog = new Dialog(LoginActivity.this);
                            //wcDialog.setCancelable(true);


                            //wcDialog.setTitle(CharSequence errorMsg);

                            //showwcDialog();
                            //Toast.makeText(getApplicationContext(),errorMsg, Toast.LENGTH_LONG).show();
                        }


                        //Toast.makeText(getApplicationContext(), "Berjaya login " , Toast.LENGTH_LONG).show();
                    /*} else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }*/
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);

                return params;
            }

        };

        // Adding request to request queue
        //AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        AppController.getInstance().addToReqQueue(strReq, "loginReq");
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        public JSONObject jObj;
        public JSONObject user;
        private String error;
        private String message;

        //String id;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        //@Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                StringRequest strReq = new StringRequest(Request.Method.POST,
                        AppConfig.URL_LOGIN, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Login Response: " + response.toString());
                        showProgress(true);

                        try {
                            pDialog.show();
                            jObj = new JSONObject(response);

                            user = jObj.getJSONObject("user");
                            error = user.getString("error");


                            // Launch main activity
                            if (error.equals("false")) {
                                message = "Tiada error!!!!";

                                pDialog.hide();

                            } else {
                                message = user.getString("message");

                                String errorMsg = user.getString("message");
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                                alertDialogBuilder.setMessage(errorMsg + " Login again?");

                                alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        Toast.makeText(LoginActivity.this,"You clicked yes button",Toast.LENGTH_LONG).show();
                                        attemptLogin();

                                    }

                                });

                                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });

                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();

                                //wcDialog = new Dialog(LoginActivity.this);
                                //wcDialog.setCancelable(true);


                                //wcDialog.setTitle(CharSequence errorMsg);

                                //showwcDialog();
                                //Toast.makeText(getApplicationContext(),errorMsg, Toast.LENGTH_LONG).show();

                                pDialog.hide();
                            }


                            //Toast.makeText(getApplicationContext(), "Berjaya login " , Toast.LENGTH_LONG).show();
                    /*} else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }*/
                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Login Error: " + error.getMessage());
                        Toast.makeText(getApplicationContext(),
                                error.getMessage(), Toast.LENGTH_LONG).show();
                        showProgress(false);
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        // Posting parameters to login url
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("email", mEmail);
                        params.put("password", mPassword);

                        return params;
                    }

                };

                // Adding request to request queue
                //AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
                AppController.getInstance().addToReqQueue(strReq, "loginReq");
                // Simulate network access.
                Thread.sleep(2000);

            } catch (InterruptedException e) {
                Toast.makeText(getApplicationContext(), "Interrupted Exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
                return false;
            }


            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {


                try {
                    String id = user.getString("id");

                    String name = user.getString("name");
                    String email = user.getString("email");
                    String apiKey = user.getString("apiKey");
                    String status = user.getString("status");
                    String createdAt = user.getString("createdAt");

                    // Inserting row in users table
                    db.addUser(id, name, email, apiKey, status, createdAt);
                    session.setLogin(true);


                    Toast.makeText(getApplicationContext(), "Successful login..." + id + name + email + apiKey + status + createdAt, Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    StringRequest strReq = new StringRequest(Request.Method.POST,
                            AppConfig.URL_LOGIN, new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            Log.d(TAG, "Login Response: " + response.toString());
                            showProgress(false);

                            try {
                                JSONObject jObj = new JSONObject(response);
                                //boolean error = jObj.getBoolean("error");

                                // Check for error node in json
                                //if (!error) {
                                //if (true) {
                                // user successfully logged in
                                // Create login session


                                // Now store the user in SQLite
                                //String uid = jObj.getString("uid");
                                //String uid = "1";

                                JSONObject user = jObj.getJSONObject("user");
                                //String error = user.getString("createdAt");


                                String error = user.getString("error");


                                // Launch main activity
                                if (error.equals("false")) {

                                } else {

                                    Log.d("TEST", "error=true");


                                    //wcDialog = new Dialog(LoginActivity.this);
                                    //wcDialog.setCancelable(true);


                                    //wcDialog.setTitle(CharSequence errorMsg);

                                    //showwcDialog();
                                    //Toast.makeText(getApplicationContext(),errorMsg, Toast.LENGTH_LONG).show();
                                }


                                //Toast.makeText(getApplicationContext(), "Berjaya login " , Toast.LENGTH_LONG).show();
                    /*} else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }*/
                            } catch (JSONException e) {
                                // JSON error
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }

                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, "Login Error: " + error.getMessage());
                            Toast.makeText(getApplicationContext(),
                                    error.getMessage(), Toast.LENGTH_LONG).show();
                            showProgress(false);
                        }
                    }) {

                        @Override
                        protected Map<String, String> getParams() {
                            // Posting parameters to login url
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("email", mEmail);
                            params.put("password", mPassword);

                            return params;
                        }

                    };

                    // Adding request to request queue
                    //AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
                    AppController.getInstance().addToReqQueue(strReq, "loginReq");
                    // Simulate network access.
                    Thread.sleep(2000);

                } catch (InterruptedException e) {
                    Toast.makeText(getApplicationContext(), "Interrupted Exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
                    //return false;
                }

                //Toast.makeText(getApplicationContext(),"Success onPostExecute!!!", Toast.LENGTH_LONG).show();
                //finish();
            } else {

                //String errorMsg = user.getString("message");
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                //alertDialogBuilder.setMessage(errorMsg + " Login again?");
                alertDialogBuilder.setMessage("Error login" + " Login again?");

                alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Toast.makeText(LoginActivity.this,"You clicked yes button",Toast.LENGTH_LONG).show();

                        //mAuthTask = new UserLoginTask(noMyKad, password);

                        //mAuthTask.execute((Void) null);
                        mAuthTask.execute();

                    }

                });

                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}
