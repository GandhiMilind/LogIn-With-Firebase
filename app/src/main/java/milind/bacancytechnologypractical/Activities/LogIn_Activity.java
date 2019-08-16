package milind.bacancytechnologypractical.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;

import milind.bacancytechnologypractical.ConnectionDetector;
import milind.bacancytechnologypractical.R;

import static milind.bacancytechnologypractical.Utils.log;


public class LogIn_Activity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = "Login_Activity";
    private Context context;
    private final String passwordvalidation = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[#$^+=!*()@%&]).{8,10}";

    //View variables
    private EditText email, passowrd;
    private Button login;
    private TextView signuptext;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authenticateStateListener;
    private ProgressDialog progressDialog;
    private ConnectionDetector cd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_);
        context = this;
        init();

    }

    private void init() {

        email = findViewById(R.id.email);
        passowrd = findViewById(R.id.password);
        login = findViewById(R.id.login);
        signuptext = findViewById(R.id.signuptext);

        login.setOnClickListener(this);
        signuptext.setOnClickListener(this);
        firebaseAuth = FirebaseAuth.getInstance();

        cd = new ConnectionDetector(getApplicationContext());
        authenticateStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser firebaseuser = firebaseAuth.getCurrentUser();
                if (firebaseuser != null) {
                    Toast.makeText(context, "You are already login", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(context, MainActivity.class));
                }

            }
        };


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:

                if(cd.hasInternetConnection()) {

                    progressDialog = new ProgressDialog(context);
                    progressDialog.setMessage("Log you in, please wait.");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    String emaildata = "", passworddata = "";

                    emaildata = email.getText().toString().trim();
                    passworddata = passowrd.getText().toString().trim();

//                if (emaildata.isEmpty()) {
//                    Toast.makeText(context, "Please enter email.", Toast.LENGTH_LONG).show();
//                }


                    if (!emailvalidation(emaildata)) {
                        email.setError(context.getResources().getString(R.string.emailerror));
                        progressDialog.dismiss();
                        break;
                    }

//                    if (!validation(passworddata, passwordvalidation)) {
////                        Snackbar snackbar1 = Snackbar.make(passowrd, "Password need to contain at list one Number,Special Symbol,Capital and Small letter.", Snackbar.LENGTH_SHORT);
////                        passowrd.setError(context.getResources().getString(R.string.passworderror));
////                        snackbar1.show();
////                        progressDialog.dismiss();
////                        break;
////                    }

//                else if (passworddata.isEmpty()) {
//                    Toast.makeText(context, "Please enter password", Toast.LENGTH_LONG).show();
//                    progressDialog.dismiss();
//                }

                    try {

                        firebaseAuth.signInWithEmailAndPassword(emaildata, passworddata)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        if (task.isSuccessful()) {
                                            progressDialog.dismiss();
                                            startActivity(new Intent(context, MainActivity.class));

                                        } else {
                                            progressDialog.dismiss();
                                            Snackbar snackbar1 = Snackbar.make(passowrd, "Email or Password is incorrect.", Snackbar.LENGTH_SHORT);
                                            snackbar1.show();
                                        }
                                    }
                                });

                    } catch (Exception e) {
                        Snackbar snackbar1 = Snackbar.make(passowrd, "Something Went wrong try after some time.", Snackbar.LENGTH_SHORT);
                        snackbar1.show();
                    }
                }else{
                    Snackbar snackbar1 = Snackbar.make(passowrd, "Turn on your internet connection.", Snackbar.LENGTH_SHORT);
                    snackbar1.show();
                }
                break;

            case R.id.signuptext:

                Intent intent = new Intent(context, SignUp_Activity.class);
                startActivity(intent);
                break;

        }
    }


    private boolean validation(String data, String regularexpration) {
        return (!TextUtils.isEmpty(data) && data.matches(regularexpration));
    }

    private boolean emailvalidation(String data) {
        return (!TextUtils.isEmpty(data) && Patterns.EMAIL_ADDRESS.matcher(data).matches());
    }

    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            log(TAG,"Net connection:"+e.getMessage());
            return false;
        }
    }

    public boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            try {
                URL url = new URL("http://www.google.com/");
                HttpURLConnection urlc = (HttpURLConnection)url.openConnection();
                urlc.setRequestProperty("User-Agent", "test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1000); // mTimeout is in seconds
                urlc.connect();
                if (urlc.getResponseCode() == 200) {
                    return true;
                } else {
                    return false;
                }
            } catch (IOException e) {
                log(TAG, "Error checking internet connection"+e);
                return false;
            }
        }

        return false;

    }
}
