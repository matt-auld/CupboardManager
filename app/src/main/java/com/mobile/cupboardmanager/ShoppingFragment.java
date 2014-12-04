package com.mobile.cupboardmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Display interactive list of ShoppingItems, with creation button
 */
public class ShoppingFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_shopping, container, false);
        rootView.findViewById(R.id.add_shopping_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ItemActivity.class);
                intent.putExtra(ItemActivity.ITEM_TYPE_INTENT_EXTRA,
                        ItemActivity.ITEM_SHOPPING_TYPE);
                startActivity(intent);
            }
        });

        return rootView;
    }
}
