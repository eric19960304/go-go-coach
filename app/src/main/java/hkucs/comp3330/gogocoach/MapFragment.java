package hkucs.comp3330.gogocoach;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import hkucs.comp3330.gogocoach.firebase.Classes;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private SupportMapFragment mMapFragment;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;


    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());


        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        return view;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission( getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED ) {
            mMap.setMyLocationEnabled(true);
        }
        LatLng southwest = new LatLng(22.224705, 113.887081);
        LatLng northeast = new LatLng(22.483973, 114.300893);
        LatLngBounds bound = new LatLngBounds(southwest, northeast);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bound, 10);
        mMap.moveCamera(cameraUpdate);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = mDatabase.child("classes");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                if(dataSnapshot.exists()){
                    for (DataSnapshot coachesSnapshot: dataSnapshot.getChildren()) {

                        for (DataSnapshot coachClassesSnapshot: coachesSnapshot.getChildren()) {
                            Classes c = (Classes) coachClassesSnapshot.getValue(Classes.class);
                            (new DownloadAvatarTask()).execute(c);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting data failed, log a message
                Log.d("class database", "loadClasses:onCancelled", databaseError.toException());
            }
        });
    }

    private class DownloadAvatarTask extends AsyncTask<Classes, Void, Bitmap> {

        private Classes classes;

        protected Bitmap doInBackground(Classes data[]) {
            classes = data[0];
            Bitmap avatar = loadImageFromNetwork(classes.photoUrl);
            Bitmap avatarBitmap = Bitmap.createScaledBitmap(avatar, 150, 150, false);

            // prepare bitmap
            Bitmap tempBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.map_marker);
            Bitmap markerBitmap = Bitmap.createScaledBitmap(tempBitmap, 150, 135, false);

            Bitmap avatarMarkerBitmap = verticalMergeBitmap(avatarBitmap, markerBitmap);

            return avatarMarkerBitmap;
        }

        protected void onPostExecute(Bitmap avatarMarkerBitmap) {
            // adding marker
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(classes.latitude, classes.longitude))
                    .title(classes.name+": "+classes.className)
                    .icon(BitmapDescriptorFactory.fromBitmap(avatarMarkerBitmap))
            );

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener(){
                @Override
                public boolean onMarkerClick(Marker m) {
                    Intent intent = new Intent(getActivity(), DetailClassActivity.class);
                    intent.putExtra("classes", classes);
                    startActivityForResult(intent, 1);
                    return true;
                }
            });
        }

        private Bitmap loadImageFromNetwork(String url) {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream
                        ((InputStream)new URL(url).getContent());
                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        private Bitmap verticalMergeBitmap(Bitmap a, Bitmap b){
            // a on top of b
            Bitmap result = Bitmap.createBitmap(a.getWidth(), a.getHeight()+b.getHeight(), b.getConfig());

            Canvas canvas = new Canvas(result);
            canvas.drawBitmap(getCroppedBitmap(a), 0f, 0f, null);
            canvas.drawBitmap(b, 0f, a.getHeight(), null);
            return result;
        }

        public Bitmap getCroppedBitmap(Bitmap bitmap) {
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                    bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                    bitmap.getWidth() / 2, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);
            //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
            //return _bmp;
            return output;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("myTest", "resultCode:"+resultCode);
        if (resultCode == Activity.RESULT_OK) {

            final String action = data.getExtras().getString("action");
            Log.d("myTest", action);
            if(action.equals(DetailClassActivity.ACTION_BROWSE_PROFILE)){
                Log.d("myTest", "ACTION_BROWSE_PROFILE");
                String userId = data.getExtras().getString("userId");
                String photoUrl = data.getExtras().getString("photoUrl");

                Fragment fragment = new DisplayProfileFragment();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putString("userId", userId);
                bundle.putString("photoUrl", photoUrl);
                fragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.frame_layout, fragment);

                fragmentTransaction.commit();
            }
            if(action.equals(DetailClassActivity.ACTION_BOOKING)){
                final Classes classToBook = (Classes) data.getExtras().getSerializable("classToBook");
                // TODO
            }
        }
    }
}