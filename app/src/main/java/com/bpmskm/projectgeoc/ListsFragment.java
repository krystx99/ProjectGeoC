package com.bpmskm.projectgeoc;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

public class ListsFragment extends Fragment {

    private LinearLayout playerRanksListLayout;
    private TextView textView_topTenTitle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lists, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        playerRanksListLayout = view.findViewById(R.id.player_ranks_list_layout);
        textView_topTenTitle = view.findViewById(R.id.textView_topTenTitle);
        populateTopTenUsers();
    }

    private void populateTopTenUsers() {
        if (getContext() == null || playerRanksListLayout == null) {
            return;
        }

        playerRanksListLayout.removeAllViews();

        List<User> topUsers = UserManager.getTopTenUsers();
        LayoutInflater inflater = LayoutInflater.from(getContext());

        if (topUsers != null && !topUsers.isEmpty()) {
            for (int i = 0; i < topUsers.size(); i++) {
                User user = topUsers.get(i);
                View playerRankItemView = inflater.inflate(R.layout.fragment_lists_player_item, playerRanksListLayout, false);

                TextView rankNumberTextView = playerRankItemView.findViewById(R.id.text_view_player_rank_number);
                TextView usernameTextView = playerRankItemView.findViewById(R.id.text_view_player_username);
                TextView pointsTextView = playerRankItemView.findViewById(R.id.text_view_player_points);

                rankNumberTextView.setText(String.valueOf(i + 1));
                usernameTextView.setText(user.getUsername());
                pointsTextView.setText(String.valueOf(user.getPoints()));

                playerRanksListLayout.addView(playerRankItemView);
            }
        } else {
            textView_topTenTitle.setText(R.string.textView_noUsers);
        }
    }
}