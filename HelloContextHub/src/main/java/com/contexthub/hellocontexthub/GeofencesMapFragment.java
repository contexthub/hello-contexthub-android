package com.contexthub.hellocontexthub;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.chaione.contexthub.sdk.SensorPipelineEvent;
import com.chaione.contexthub.sdk.SensorPipelineListener;
import com.chaione.contexthub.sdk.ContextHub;
import com.chaione.contexthub.sdk.GeofenceProxy;
import com.chaione.contexthub.sdk.callbacks.Callback;
import com.chaione.contexthub.sdk.dev.MockLocationProvider;
import com.chaione.contexthub.sdk.model.Geofence;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andy on 9/30/14.
 */
public class GeofencesMapFragment extends SupportMapFragment implements SensorPipelineListener, Callback<List<Geofence>>, GoogleMap.OnMapLongClickListener, GoogleMap.OnMapClickListener {

    private static final int GEOFENCE_RADIUS = 500;
    private static final int ZOOM_LEVEL = 13;
    private static final LatLng LOCATION_CHAIONE_WOODWAY = new LatLng(29.763553,-95.461784);

    private MockLocationProvider mockLocationProvider;
    private Marker currentLocation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mockLocationProvider = new MockLocationProvider(getActivity());
        setCurrentLocation(LOCATION_CHAIONE_WOODWAY);

        getMap().setOnMapClickListener(this);
        getMap().setOnMapLongClickListener(this);

        getActivity().setProgressBarIndeterminateVisibility(true);
        GeofenceProxy proxy = new GeofenceProxy();
        proxy.listGeofences(this);
    }

    @Override
    public void onSuccess(List<Geofence> result) {
        getActivity().setProgressBarIndeterminateVisibility(false);
        for(Geofence geofence: result) {
            drawGeofence(geofence);
        }
    }

    private void drawGeofence(Geofence geofence) {
        LatLng coordinates = new LatLng(geofence.getLatitude(), geofence.getLongitude());
        getMap().addCircle(new CircleOptions().center(coordinates)
                .radius(geofence.getRadius()).strokeWidth(0)
                .fillColor(getResources().getColor(R.color.circle_fill)));
    }

    @Override
    public void onFailure(Exception e) {
        getActivity().setProgressBarIndeterminateVisibility(false);
        Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        ContextHub.getInstance().addSensorPipelineListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        ContextHub.getInstance().removeSensorPipelineListener(this);
    }

    @Override
    public void onEventReceived(final SensorPipelineEvent event) {
        if(event.getName().equals("location_changed")) {
            handleLocationChange(event);
        }

        /* Since some context events may be received on a background thread, use a handler to ensure
           the toast message is shown on the UI thread */
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(HelloContextHubApp.getInstance(), event.getEventDetails().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean shouldPostEvent(SensorPipelineEvent event) {
        return true;
    }

    @Override
    public void onBeforeEventPosted(SensorPipelineEvent event) {

    }

    @Override
    public void onEventPosted(SensorPipelineEvent event) {

    }

    /**
     * Extract the coordinates from the context event and update the current loation pin on the map
     * @param event the sensor pipeline event
     */
    private void handleLocationChange(SensorPipelineEvent event) {
        try {
            JSONObject data = event.getEventDetails().getJSONObject("data");
            double latitude = data.getDouble("latitude");
            double longitude = data.getDouble("longitude");
            setCurrentLocation(new LatLng(latitude, longitude));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setCurrentLocation(LatLng coordinates) {
        if(currentLocation != null) currentLocation.remove();
        currentLocation = getMap().addMarker(new MarkerOptions().position(coordinates).title("Current Location"));
        getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, ZOOM_LEVEL));
    }

    @Override
    public void onMapClick(LatLng latLng) {
        setCurrentLocation(latLng);
        mockLocationProvider.setMockLocation(latLng);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        getActivity().setProgressBarIndeterminateVisibility(true);
        ArrayList<String> tags = new ArrayList<String>();
        tags.add("hello-contexthub");
        GeofenceProxy proxy = new GeofenceProxy();
        proxy.createGeofence("sample", latLng.latitude, latLng.longitude,
                GEOFENCE_RADIUS, tags, createGeofenceCallback);
    }

    private Callback<Geofence> createGeofenceCallback = new Callback<Geofence>() {
        @Override
        public void onSuccess(Geofence result) {
            getActivity().setProgressBarIndeterminateVisibility(false);
            drawGeofence(result);
        }

        @Override
        public void onFailure(Exception e) {
            getActivity().setProgressBarIndeterminateVisibility(false);
            Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };
}
