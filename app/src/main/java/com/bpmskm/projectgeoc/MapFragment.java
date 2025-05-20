package com.bpmskm.projectgeoc;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {
    private MapView mapView;
    private GoogleMap googleMap;
    private List<Marker> markersList = new ArrayList<>();
    private boolean isMapReady = false;

    private ImageView refresh;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        refresh = view.findViewById(R.id.refresh_image_view);

        refresh.setOnClickListener((v) -> {
            requireActivity().recreate();
        });

        updateIconColor();

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
        Log.d("MapFragment", "Mapa jest gotowa");

        LatLng defaultLocation = new LatLng(51.938237, 15.505255);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12.5f));

        SharedPreferences prefs = requireActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("dark_mode", false);

        if (isDarkMode){
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style_dark));
        } else {
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style_light));
        }

        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setScrollGesturesEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        googleMap.getUiSettings().setTiltGesturesEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
        }
        else {
            googleMap.setMyLocationEnabled(true);
        }

        googleMap.setOnMapLongClickListener(this);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        CacheManager.fetchCachesData(new CacheManager.CacheFetchCallback() {
            @Override
            public void onCachesFetched(List<Cache> caches) {
                populateMarkers();
            }
            @Override
            public void onFetchFailed(String errorMessage) {
                Log.e(requireContext().getAttributionTag(), "Nie udało się pobrać danych skrytek: " + errorMessage);
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Błąd ładowania skrytek: " + errorMessage, Toast.LENGTH_LONG).show();
                }
            }
        });

        Log.d("MapFragment", "Kontrolki mapy zostały włączone.");
    }
    @Override
    public void onMapLongClick(@NonNull LatLng latLng)
    {
        MarkerOptions marketOptions = new MarkerOptions()
                .position(latLng)
                .title("Nowa pinezka")
                .snippet("Opis");

        googleMap.addMarker(marketOptions);
        //DO FIREBASE
        Toast.makeText(getContext(), "Dodano pinezkę na: " + latLng.latitude + "," + latLng.longitude, Toast.LENGTH_SHORT).show();
    }

    private void populateMarkers() {
        if (googleMap == null || !isMapReady) {
            Log.w(this.getTag(), "Mapa nie jest gotowa do dodania markerów.");
            return;
        }

        googleMap.clear();

        List<Cache> caches = CacheManager.getCacheList();
        if (caches.isEmpty()) {
            Log.d(this.getTag(), "Lista skrytek jest pusta, brak markerów do dodania.");
            return;
        }

        for (Cache cache : caches) {
            if (cache.getLocation() != null && cache.getName() != null) {
                LatLng position = new LatLng(cache.getLocation().getLatitude(), cache.getLocation().getLongitude());
                String title = cache.getName();

                MarkerOptions marketOptions = new MarkerOptions()
                        .position(position)
                        .title(title);

                markersList.add(googleMap.addMarker(marketOptions)) ;
            } else {
                Log.w(this.getTag(), "Skrytka z brakującą lokalizacją lub nazwą, pomijanie.");
            }
        }
        Log.d(this.getTag(), "Markery zostały zaktualizowane na mapie.");
    }

    private void updateIconColor() {
        if (getContext() == null || refresh == null) {
            return;
        }
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            refresh.setColorFilter(ContextCompat.getColor(getContext(), android.R.color.white), PorterDuff.Mode.SRC_ATOP);
        } else {
            refresh.setColorFilter(ContextCompat.getColor(getContext(), android.R.color.black), PorterDuff.Mode.SRC_ATOP);
        }
    }
}