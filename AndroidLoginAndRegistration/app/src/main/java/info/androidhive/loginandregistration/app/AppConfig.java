package info.androidhive.loginandregistration.app;

public class AppConfig {
	//public static final String  HOST = "http://10.9.169.165/";
	public static final String  HOST = "http://192.168.0.102/";


	// Server user login url
	//public static String URL_LOGIN = "http://192.168.0.102:8888/android_login_api/login.php";
	public static String URL_LOGIN = HOST +  "task_manager/v1/login";
    //public static String URL_LOGIN = "http://192.168.0.103/task_manager/v1/login";
    //public static String URL_LOGIN = "http://192.168.15.72/task_manager/v1/login";

	// Server user register url
	public static String URL_REGISTER = HOST +  "task_manager/v1/register";

    // Server user update url
    public static String URL_UPDATE = HOST + "task_manager/v1/user";
}
