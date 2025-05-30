package com.bpmskm.projectgeoc;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private TextView usernameTextView;
    private TextView registerDateTextView;
    private TextView pointsTextView;
    private TextView stepCountTextView;
    private Button logoutButton;
    private Button generatePdfButton;
    private ImageView userProfileIconImageView;
    private ImageView refresh;
    private int krokCount = 0;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        usernameTextView = view.findViewById(R.id.username_text_view);
        registerDateTextView = view.findViewById(R.id.register_date_display_text_view);
        pointsTextView = view.findViewById(R.id.points_display_text_view);
        stepCountTextView = view.findViewById(R.id.step_count_display_text_view);
        logoutButton = view.findViewById(R.id.logout_button);
        userProfileIconImageView = view.findViewById(R.id.user_profile_icon_image_view);
        refresh = view.findViewById(R.id.refresh_image_view);
        generatePdfButton = view.findViewById(R.id.pdf_button);
        logoutButton.setOnClickListener(v -> {
            AuthenticationManager.signOut(requireContext());
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finishAffinity();
        });

        refresh.setOnClickListener((v) -> {
            UserManager.sendUserSteps(new UserManager.UserStepsUpdateCallback() {
                @Override
                public void onStepsUpdateSuccess() {
                    AuthenticationManager.fetchCurrentUserData(requireActivity(), new AuthenticationManager.UserDataFetchCallback() {
                        @Override
                        public void onSuccess() {
                            requireActivity().recreate();
                        }
                        @Override
                        public void onFailure(String errorMessage) {
                            Log.e(TAG, "Could not fetch user data: " + errorMessage);
                        }
                    });
                }

                @Override
                public void onStepsUpdateFailure(String errorMessage) {
                    Log.e(TAG, "Could not send user data: " + errorMessage);
                }
            });
        });
        generatePdfButton.setOnClickListener(v -> generatePdf());
        updateIconColor();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        populateUserData();
        updateStepCountDisplay();
    }

    private void populateUserData() {
        User currentUser = UserManager.getCurrentUser();
        if (currentUser != null) {
            if (usernameTextView != null) {
                usernameTextView.setText(currentUser.getUsername());
            }
            if (registerDateTextView != null) {
                registerDateTextView.setText(currentUser.getRegisterDate());
            }
            if (pointsTextView != null) {
                pointsTextView.setText(String.valueOf(currentUser.getPoints()));
            }
            if (stepCountTextView != null) {
                krokCount = currentUser.getSteps();
            }
        }
    }

    public void updateStepCountDisplay() {
        if (stepCountTextView != null) {
            stepCountTextView.setText(String.valueOf(krokCount));
        }
    }

    private void updateIconColor() {
        if (getContext() == null || userProfileIconImageView == null || refresh == null) {
            return;
        }
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            userProfileIconImageView.setColorFilter(ContextCompat.getColor(getContext(), android.R.color.white), PorterDuff.Mode.SRC_ATOP);
            refresh.setColorFilter(ContextCompat.getColor(getContext(), android.R.color.white), PorterDuff.Mode.SRC_ATOP);
        } else {
            userProfileIconImageView.setColorFilter(ContextCompat.getColor(getContext(), android.R.color.black), PorterDuff.Mode.SRC_ATOP);
            refresh.setColorFilter(ContextCompat.getColor(getContext(), android.R.color.black), PorterDuff.Mode.SRC_ATOP);
        }
    }

    public void setKrokCount(int krokCount) {
        this.krokCount = krokCount;
        updateStepCountDisplay();
    }
    private void generatePdf() {
        User currentUser = UserManager.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "Brak danych użytkownika", Toast.LENGTH_SHORT).show();
            return;
        }

        PdfDocument document = new PdfDocument();
        Paint paint = new Paint();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();


        // --- RYSUJ LOGO W TLE ---
        Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.app_logo);
        if (logo != null) {
            Paint logoPaint = new Paint();
            logoPaint.setAlpha(40); // Przezroczystość 0–255 (im mniej, tym bardziej przezroczyste)
            Bitmap scaledLogo = Bitmap.createScaledBitmap(logo, 300, 300, true);
            canvas.drawBitmap(scaledLogo, 0, 100, logoPaint); // środek strony
        }
        // --- NAGŁÓWEK: NAZWA APLIKACJI ---
        paint.setTextSize(24);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setFakeBoldText(true);
        paint.setColor(Color.BLACK);
        canvas.drawText("Raport z Geocaching++", canvas.getWidth() / 2f, 100, paint);
        // --- DANE PROFILU ---
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setFakeBoldText(false);
        paint.setTextSize(14);
        int x = 50;
        int y = 180;

        canvas.drawText("Nazwa: " + currentUser.getUsername(), x, y, paint); y += 20;
        canvas.drawText("Data rejestracji: " + currentUser.getRegisterDate(), x, y, paint); y += 20;
        canvas.drawText("Punkty: " + currentUser.getPoints(), x, y, paint); y += 20;
        canvas.drawText("Kroki: " + currentUser.getSteps(), x, y, paint); y += 40;

        // --- OBRAZEK PROFILOWY ---
        userProfileIconImageView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(userProfileIconImageView.getDrawingCache());
        userProfileIconImageView.setDrawingCacheEnabled(false);
        if (bitmap != null) {
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
            canvas.drawBitmap(scaledBitmap, x, y, paint);
        }

        document.finishPage(page);

        try {
            File pdfDirPath = new File(requireContext().getExternalFilesDir(null), "pdfs");
            if (!pdfDirPath.exists()) pdfDirPath.mkdirs();

            File file = new File(pdfDirPath, "profil_uzytkownika.pdf");
            FileOutputStream fos = new FileOutputStream(file);
            document.writeTo(fos);
            fos.close();
            Toast.makeText(getContext(), "Zapisano PDF: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Błąd zapisu PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        document.close();
    }

}