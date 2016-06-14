package com.aurorasdp.allinall.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.aurorasdp.allinall.R;
import com.aurorasdp.allinall.activities.LoginActivity;
import com.aurorasdp.allinall.controller.AllinAllController;
import com.aurorasdp.allinall.helper.RESTClient;
import com.aurorasdp.allinall.helper.Util;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProviderWalletFragment extends Fragment implements RESTClient.ServiceResponseInterface {

    private AllinAllController allinAllController;
    private TextView balanceTextview;
    private TextView schemeTextview;
    private Button signOutButton;
    private SharedPreferences allinallSharedPref;
    private SwipeRefreshLayout swipeRefreshLayout;

    public ProviderWalletFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        allinAllController = new AllinAllController(getContext(), this);
        allinallSharedPref = getActivity().getSharedPreferences(getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_provider_wallet, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        signOutButton = (Button) view.findViewById(R.id.fragment_provider_signout_button);
        balanceTextview = (TextView) view.findViewById(R.id.fragment_wallet_balance_textview);
        schemeTextview = (TextView) view.findViewById(R.id.fragment_wallet_scheme_textview);
        allinAllController.getWalletData(RESTClient.ID);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                allinAllController.getWalletData(RESTClient.ID);
            }
        });
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog();
            }
        });
        return view;
    }

    private void showConfirmDialog() {
        new android.support.v7.app.AlertDialog.Builder(getContext())
                //set message, title, and icon
                .setTitle("Sign out")
                .setMessage("Do you want to sign out?")
                .setIcon(R.drawable.ic_launcher)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        allinAllController.providerSignOut(RESTClient.ID);
                        SharedPreferences.Editor editor = allinallSharedPref.edit();
                        editor.putBoolean("firstLaunch", true);
                        editor.remove("providerId");
                        editor.apply();
                        RESTClient.ID = null;
                        Intent loginIntent = new Intent(getContext(), LoginActivity.class);
                        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(loginIntent);
                        getActivity().finish();

                    }
                })

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

    }

    @Override
    public void sendServiceResult(String serviceResult) {
        if (swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
        try {
            if (serviceResult.equalsIgnoreCase(getString(R.string.provider_get_wallet_success))) {
                balanceTextview.setText("RS. " + RESTClient.BALANCE);
                schemeTextview.setText(RESTClient.SCHEME);
            }
        } catch (IllegalStateException e) {
            e.getStackTrace();
        }
    }

    @Override
    public void requestFailed() {
        Util.requestFailed(getContext());
    }
}
