package com.bpmskm.projectgeoc;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private MapView mapView;
    private GoogleMap googleMap;
    private boolean isMapReady = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        isMapReady = true;
        Log.d("MapFragment","Mapa jest gotowa");
        LatLng warszawa = new LatLng(52.237049, 21.017532);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(warszawa,10));

        //kontrolki
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setOnMapLongClickListener(this);

        // Opcjonalnie: Dodaj markery, polylinie itp. tutaj, jeśli chcesz
    }
    @Override
    public void onMapLongClick(@NonNull LatLng latLng)
    {
        MarkerOptions marketOptions = new MarkerOptions()
                .position(latLng)
                .title("Nowa pinezka")
                .snippet("Opis");

        googleMap.addMarker(marketOptions);

        Toast.makeText(getContext(), "Dodano pinezkęna: "+ latLng.latitude + "," + latLng.longitude, Toast.LENGTH_SHORT).show();

    }
}