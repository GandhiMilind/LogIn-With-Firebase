package milind.bacancytechnologypractical.Activities;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.util.ArrayList;
import java.util.List;

import milind.bacancytechnologypractical.Adapter.LocationAdapter;
import milind.bacancytechnologypractical.ConnectionDetector;
import milind.bacancytechnologypractical.ModelClasses.LatLng;
import milind.bacancytechnologypractical.R;

import static milind.bacancytechnologypractical.Utils.log;



public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "MainActivity";
    private Context context;

    // Permission variables
    private String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};
    private String rationale = "Please provide location permission to get you location update.";
    private Permissions.Options options = new Permissions.Options()
            .setRationaleDialogTitle("Info")
            .setSettingsDialogTitle("Warning");


    //For Location update variables
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;


    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;


    //Activity view variables
    private RecyclerView locationrecyclerview;
    private Button logout;
    private TextView useremail;

    //Extra variables
    private ArrayList<LatLng> locationlist = new ArrayList<>();
    private LocationAdapter locationAdapter;


    private ConnectionDetector cd;
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;
    private AlertDialog.Builder locationalertbuilder;
    private AlertDialog locationalertdialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        init();
        permission();

    }

    private void permission() {
//
        Permissions.check(this/*context*/, permissions, rationale/*rationale*/, options/*options*/, new PermissionHandler() {
            @Override
            public void onGranted() {
                // do your task.
//                if (cd.hasInternetConnection()) {
                getTheLocation();
//                } else {
//                    new AlertDialog.Builder(context)
//                            .setMessage(context.getResources().getString(R.string.locationmessage))
//                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//                                    context.startActivity(new Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS));
//                                }
//                            })
//                            .setCancelable(false)
//                            .show();
//                }
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                super.onDenied(context, deniedPermissions);
                permission();
            }
        });


    }

    private void getTheLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            fusedLocationProviderClient = new FusedLocationProviderClient(this);

            locationRequest = new LocationRequest();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setFastestInterval(200);
            locationRequest.setInterval(400);

            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);

                    log(TAG, "Current Lat:" + locationResult.getLastLocation().getLatitude());
                    log(TAG, "Current Lat:" + locationResult.getLastLocation().getLongitude());

                    if (locationalertbuilder != null && locationalertdialog != null && locationalertdialog.isShowing()) {
                        locationalertdialog.dismiss();
                        locationalertdialog = null;
                        locationalertbuilder = null;
                    }

                    if (cd.hasInternetConnection()) {
                        if (alertDialogBuilder != null && alertDialog != null && alertDialog.isShowing()) {
                            alertDialog.dismiss();
                            alertDialog = null;
                            alertDialogBuilder = null;
                        }
                        LatLng latLng = new LatLng();
                        latLng.setLatitude("" + locationResult.getLastLocation().getLatitude());
                        latLng.setLongitude("" + locationResult.getLastLocation().getLongitude());
                        locationlist.add(latLng);
                        locationAdapter.notifyDatasetChange();
                        locationrecyclerview.scrollToPosition(locationAdapter.getItemCount() - 1);
                        databaseReference.child("latlng").child(firebaseUser.getUid()).setValue(locationlist);
                    } else {
                        if (alertDialog == null) {
                            alertDialogBuilder = new AlertDialog.Builder(context);
                            alertDialogBuilder.setMessage(context.getResources().getString(R.string.internetmessage))
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                            alertDialogBuilder = null;
                                            alertDialog = null;
                                            paramDialogInterface.cancel();
                                        }
                                    })
                                    .setCancelable(false);
                            alertDialog = alertDialogBuilder.show();
                        }
                    }
                }

                @Override
                public void onLocationAvailability(LocationAvailability locationAvailability) {
                    super.onLocationAvailability(locationAvailability);
                    if (!locationAvailability.isLocationAvailable()) {
//                        Toast.makeText(context, "Please on Your Location!", Toast.LENGTH_LONG).show();
                        if (locationalertbuilder == null) {
                            locationalertbuilder = new AlertDialog.Builder(context);
                            locationalertbuilder.setMessage(context.getResources().getString(R.string.locationmessage))
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                            context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                            locationalertbuilder=null;
                                            locationalertdialog=null;
                                        }
                                    })
                                    .setCancelable(false);
                            locationalertdialog = locationalertbuilder.show();
//                                        .setNegativeButton(context.getResources().getString(), null)
//                                        .show();
                        }

                    }
                }
            };

            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, getMainLooper());
        }
    }

    private void init() {

        logout = findViewById(R.id.logout);
        useremail = findViewById(R.id.useremail);
        logout.setOnClickListener(this);

        cd = new ConnectionDetector(getApplicationContext());
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        useremail.setText(firebaseUser.getEmail());
        databaseReference = FirebaseDatabase.getInstance().getReference("User");

        locationrecyclerview = findViewById(R.id.locationrecyclerview);
        locationrecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        locationAdapter = new LocationAdapter(this, locationlist);
        locationrecyclerview.setAdapter(locationAdapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.logout:

                if (firebaseAuth != null) {
                    firebaseAuth.signOut();
                    Intent intent = new Intent(context, LogIn_Activity.class);
                    finishAffinity();
                    startActivity(intent);
                }

                break;

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
        System.exit(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }
}
