package com.bpmskm.projectgeoc;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import androidx.core.content.ContextCompat;


import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener {
    private static final String TAG = "MapFragment";
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
        Log.d(TAG, "Mapa jest gotowa");

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
        googleMap.setOnMarkerClickListener(this);


        CacheManager.fetchCachesData(new CacheManager.CacheFetchCallback() {
            @Override
            public void onCachesFetched(List<Cache> caches) {
                populateMarkers();
            }

            @Override
            public void onFetchFailed(String errorMessage) {
                Log.e(TAG, "Nie udało się pobrać danych skrytek: " + errorMessage);
                if (getContext() != null) {
                    Toast.makeText(getContext(), R.string.cache_fetch_failed, Toast.LENGTH_LONG).show();
                }
            }
        });

        Log.d(TAG, "Kontrolki mapy zostały włączone.");
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        if (getActivity() != null) {
            CacheEditFragment cacheEditFragment = new CacheEditFragment(latLng);

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, cacheEditFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    private void populateMarkers() {
        if (googleMap == null || !isMapReady) {
            Log.w(TAG, "Mapa nie jest gotowa do dodania markerów.");
            return;
        }

        googleMap.clear();

        List<Cache> caches = CacheManager.getCacheList();
        if (caches.isEmpty()) {
            Log.d(TAG, "Lista skrytek jest pusta, brak markerów do dodania.");
            return;
        }

        for (int i = 0; i < caches.size(); i++) {
            Cache cache = caches.get(i);
            if (cache.getLocation() != null && cache.getName() != null) {
                LatLng position = new LatLng(cache.getLocation().getLatitude(), cache.getLocation().getLongitude());
                String title = cache.getName();
                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_location_pin);

                MarkerOptions markerOptions = new MarkerOptions()
                        .icon(icon)
                        .position(position)
                        .title(title);

                Marker newMarker = googleMap.addMarker(markerOptions);
                if (newMarker != null) {
                    newMarker.setTag(i);
                    markersList.add(newMarker);
                }
            } else {
                Log.w(TAG, "Skrytka z brakującą lokalizacją lub nazwą, pomijanie.");
            }
        }
        Log.d(TAG, "Markery zostały zaktualizowane na mapie.");
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

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        Object tag = marker.getTag();
        if (tag instanceof Integer) {
            int cacheIndex = (Integer) tag;
            List<Cache> caches = CacheManager.getCacheList();

            if (cacheIndex >= 0 && cacheIndex < caches.size()) {
                Cache selectedCache = caches.get(cacheIndex);
                Log.d(TAG, "Kliknięto marker dla kesza: " + selectedCache.getName() + " o indeksie: " + cacheIndex);

                if (getActivity() != null) {
                    CacheViewFragment cacheViewFragment = new CacheViewFragment(selectedCache);

                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, cacheViewFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                return true;
            } else {
                Log.e(TAG, "Nieprawidłowy indeks kesza w tagu markera: " + cacheIndex);
            }
        }
        return false;
    }
}