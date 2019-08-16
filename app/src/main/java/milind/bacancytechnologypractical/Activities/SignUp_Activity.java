package milind.bacancytechnologypractical.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import milind.bacancytechnologypractical.ModelClasses.User;
import milind.bacancytechnologypractical.R;

import static milind.bacancytechnologypractical.Utils.log;


public class SignUp_Activity extends AppCompatActivity implements View.OnClickListener {


    private final String TAG = "SignUp_Activity";
    private Context context;

    private final String namevalidation = "^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$";
    private final String passwordvalidation = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[#$^+=!*()@%&]).{8,10}";
    private final String phonenumbervalidation = "^[0-9]*$";//"^(\\+\\d{1,2}\\s)?\\(?\\d{3}\\)?[\\s.-]\\d{3}[\\s.-]\\d{4}$";

    //View variable
    private EditText name, email, phone, password, conformpasswor;
    private TextView logintext;
    private Button signup;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    private String namedata, emaildata, phonedata, passworddata, conformpassworddata;

    private ProgressDialog progressDialog;


    private boolean validationfails = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_sign_up_);
        init();
    }

    private void init() {

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phonenumber);
        password = findViewById(R.id.password);
        conformpasswor = findViewById(R.id.conformpassword);
        signup = findViewById(R.id.signup);
        logintext = findViewById(R.id.logintext);

        signup.setOnClickListener(this);
        logintext.setOnClickListener(this);

        databaseReference = FirebaseDatabase.getInstance().getReference("User");
        firebaseAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.signup:

                namedata = name.getText().toString().trim();
                emaildata = email.getText().toString().trim();
                phonedata = phone.getText().toString().trim();
                passworddata = password.getText().toString().trim();
                conformpassworddata = password.getText().toString().trim();

                if (!validation(namedata, namevalidation)) {
                    log(TAG, "inside name validation");
                    name.setError(context.getResources().getString(R.string.nameerror));
//                    validationfails = true;
                    break;
                }
                if (!emailvalidation(emaildata)) {
                    email.setError(context.getResources().getString(R.string.emailerror));
//                    validationfails = true;
                    break;
                }
                if (!phonenumbervalidation(phonedata, phonenumbervalidation)) {
                    phone.setError(context.getResources().getString(R.string.phonenumber));
//                    validationfails = true;
                    break;
                }
                if (!validation(passworddata, passwordvalidation)) {
                    Snackbar snackbar1 = Snackbar.make(name, "Password need to contain at list one Number,Special Symbol,Capital and Small letter.", Snackbar.LENGTH_SHORT);
                    password.setError(context.getResources().getString(R.string.passworderror));
                    snackbar1.show();
//                    validationfails = true;
                    break;
                }
                if (!conformpassworddata.equals(passworddata)) {
                    conformpasswor.setError(context.getResources().getString(R.string.conformpassworderror));
//                    validationfails = true;
                    break;
                }

//

//                if (!validationfails) {

                try {
                    progressDialog = new ProgressDialog(context);
                    progressDialog.setMessage("Please Wait creating you account.");
                    progressDialog.setCancelable(false);
                    progressDialog.show();


                    firebaseAuth.createUserWithEmailAndPassword(emaildata, passworddata)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        log(TAG, "createUserWithEmail:success");
                                        User user = new User(namedata, emaildata, phonedata);
                                        FirebaseDatabase.getInstance().getReference("User")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                progressDialog.dismiss();

                                                //Uncomment this line if you want to move to log in screen after sign up.

//                                                if (firebaseAuth != null) {
//                                                    firebaseAuth.signOut();
//                                                    Intent intent = new Intent(context, LogIn_Activity.class);
//                                                    finishAffinity();
//                                                    startActivity(intent);
//                                                }

                                                startActivity(new Intent(context, MainActivity.class));

                                            }

                                        });

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Snackbar snackbar1 = Snackbar.make(name, "Something Went wrong try after some time.", Snackbar.LENGTH_SHORT);
                                        snackbar1.show();
                                        log(TAG, "createUserWithEmail:failure" + task.getException());
                                        progressDialog.dismiss();
                                    }

                                    // ...
                                }
                            });
                }catch (Exception e)
                {
                    Snackbar snackbar1 = Snackbar.make(name, "Something Went wrong try after some time.", Snackbar.LENGTH_SHORT);
                    snackbar1.show();
                }
//                } else {
//                    validationfails = false;
//                }

                break;
            case R.id.logintext:
//                startActivity(new Intent(context, LogIn_Activity.class));
                finish();
                break;

        }
    }


    private boolean validation(String data, String regularexpration) {
        return (!TextUtils.isEmpty(data) && data.matches(regularexpration));
    }

    private boolean emailvalidation(String data) {
        return (!TextUtils.isEmpty(data) && Patterns.EMAIL_ADDRESS.matcher(data).matches());
    }

    private boolean phonenumbervalidation(String data, String regularexpration) {
        return (!TextUtils.isEmpty(data) && (data.length()==10) && data.matches(regularexpration));
    }

}
