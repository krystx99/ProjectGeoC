package com.bpmskm.projectgeoc;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CacheViewFragment extends Fragment {

    private TextView cacheNameTextView;
    private TextView cacheDescriptionValueTextView;
    private TextView pointsDisplayTextView;
    private TextView cacheDateValueTextView;
    private TextView cacheUsernameValueTextView;
    private ImageView cacheIconImageView;
    private Button logCacheButton;

    public CacheViewFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cache_view, container, false);

        cacheNameTextView = view.findViewById(R.id.cacheName_text_view);
        cacheDescriptionValueTextView = view.findViewById(R.id.cacheDescription_value);
        pointsDisplayTextView = view.findViewById(R.id.points_display_text_view);
        cacheDateValueTextView = view.findViewById(R.id.cacheDate_value);
        cacheUsernameValueTextView = view.findViewById(R.id.cacheUsername_value);
        cacheIconImageView = view.findViewById(R.id.cache_icon_image_view);
        logCacheButton = view.findViewById(R.id.logCache_button);

        logCacheButton.setOnClickListener(v -> {
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}