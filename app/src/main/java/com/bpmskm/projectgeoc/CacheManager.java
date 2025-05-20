package com.bpmskm.projectgeoc;

import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CacheManager {

    private static final String TAG = "CacheManager";
    private static final String CACHES_COLLECTION = "Caches";
    private static final String FIELD_ADDITION_DATE = "additionDate";
    private static final String FIELD_DESCRIPTION = "description";
    private static final String FIELD_LOCATION = "location";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_POINTS = "points";
    private static final String FIELD_USERNAME = "username";

    private static List<Cache> cacheList = new ArrayList<>();

    public interface CacheFetchCallback {
        void onCachesFetched(List<Cache> caches);
        void onFetchFailed(String errorMessage);
    }

    public interface CacheCreateCallback {
        void onCacheCreatedSuccess(String documentId);
        void onCacheCreateFailed(String errorMessage);
    }

    public static List<Cache> getCacheList() {
        return new ArrayList<>(cacheList);
    }

    public static void setCacheList(List<Cache> caches) {
        if (caches != null) {
            cacheList = new ArrayList<>(caches);
        } else {
            cacheList = new ArrayList<>();
        }
    }

    public static void fetchCachesData(final CacheFetchCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(CACHES_COLLECTION)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Cache> fetchedCaches = new ArrayList<>();
                    if (queryDocumentSnapshots != null) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Cache cache = new Cache();
                            cache.setAdditionDate(document.getTimestamp(FIELD_ADDITION_DATE));
                            cache.setDescription(document.getString(FIELD_DESCRIPTION));
                            cache.setLocation(document.getGeoPoint(FIELD_LOCATION));
                            cache.setName(document.getString(FIELD_NAME));
                            cache.setPoints(document.getLong(FIELD_POINTS));
                            cache.setUsername(document.getString(FIELD_USERNAME));
                            fetchedCaches.add(cache);
                        }
                    }
                    setCacheList(fetchedCaches);
                    if (callback != null) {
                        callback.onCachesFetched(fetchedCaches);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching caches data", e);
                    if (callback != null) {
                        callback.onFetchFailed(e.getMessage());
                    }
                });
    }

    public static void createCacheData(String name, String description, GeoPoint location, Long points, String creatorUsername, final CacheCreateCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> cacheData = new HashMap<>();
        cacheData.put(FIELD_NAME, name);
        cacheData.put(FIELD_DESCRIPTION, description);
        cacheData.put(FIELD_LOCATION, location);
        cacheData.put(FIELD_POINTS, points);
        cacheData.put(FIELD_USERNAME, creatorUsername);
        cacheData.put(FIELD_ADDITION_DATE, Timestamp.now());

        db.collection(CACHES_COLLECTION)
                .add(cacheData)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Cache created successfully with ID: " + documentReference.getId());
                    if (callback != null) {
                        callback.onCacheCreatedSuccess(documentReference.getId());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error creating cache data", e);
                    if (callback != null) {
                        callback.onCacheCreateFailed(e.getMessage());
                    }
                });
    }
}