package com.mobile.cupboardmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Display interactive list of CupboardItems, with creation button
 */
public class CupboardFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cupboard, container, false);
        rootView.findViewById(R.id.add_cupboard_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ItemActivity.class);
                intent.putExtra(ItemActivity.ITEM_TYPE_INTENT_EXTRA,
                        ItemActivity.ITEM_CUPBOARD_TYPE);
                startActivity(intent);
            }
        });

        return rootView;
    }
}
