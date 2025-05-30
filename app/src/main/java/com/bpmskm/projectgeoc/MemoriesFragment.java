package com.bpmskm.projectgeoc;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class MemoriesFragment extends Fragment {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_GALLERY_PICK = 2;

    private ImageView memoriesIconImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_memories, container, false);

        Button cameraButton = view.findViewById(R.id.cameraButton);
        Button galleryButton = view.findViewById(R.id.galleryButton);
        memoriesIconImageView = view.findViewById(R.id.memories_icon_image_view);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        updateIconColor();
        return view;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Upewnij się, że jest aplikacja obsługująca kamerę
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(requireContext(), "Brak aplikacji aparatu", Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_GALLERY_PICK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == requireActivity().RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {

                Toast.makeText(requireContext(), "Zdjęcie zrobione!", Toast.LENGTH_SHORT).show();
            } else if (requestCode == REQUEST_GALLERY_PICK) {

                if (data != null) {
                    Uri selectedImageUri = data.getData();

                    Toast.makeText(requireContext(), "Zdjęcie wybrane z galerii!", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (resultCode == requireActivity().RESULT_CANCELED) {
            Toast.makeText(requireContext(), "Operacja anulowana", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateIconColor() {
        if (getContext() == null || memoriesIconImageView == null) {
            return;
        }
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            memoriesIconImageView.setColorFilter(ContextCompat.getColor(getContext(), android.R.color.white), PorterDuff.Mode.SRC_ATOP);
        } else {
            memoriesIconImageView.setColorFilter(ContextCompat.getColor(getContext(), android.R.color.black), PorterDuff.Mode.SRC_ATOP);
        }
    }
}