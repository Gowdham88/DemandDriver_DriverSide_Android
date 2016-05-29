package com.aurorasdp.allinall.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aurorasdp.allinall.R;
import com.aurorasdp.allinall.helper.RESTClient;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProviderWalletFragment extends Fragment {

    private TextView balanceTextview;
    private TextView schemeTextview;

    public ProviderWalletFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_provider_wallet, container, false);

        balanceTextview = (TextView) view.findViewById(R.id.fragment_wallet_balance_textview);
        schemeTextview = (TextView) view.findViewById(R.id.fragment_wallet_scheme_textview);
        balanceTextview.setText("RS. " + RESTClient.BALANCE);
        schemeTextview.setText(RESTClient.SCHEME);
        return view;
    }

}
