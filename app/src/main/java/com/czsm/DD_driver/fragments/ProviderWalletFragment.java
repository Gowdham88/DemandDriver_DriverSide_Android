package com.czsm.DD_driver.fragments;


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

import com.czsm.DD_driver.R;
import com.czsm.DD_driver.Service.CapPhoto;
import com.czsm.DD_driver.activities.LoginActivity;
import com.czsm.DD_driver.controller.AllinAllController;
import com.czsm.DD_driver.helper.RESTClient;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProviderWalletFragment extends Fragment {

    private AllinAllController allinAllController;
    private TextView balanceTextview;
    private TextView schemeTextview;
    private Button signOutButton;
    private SharedPreferences SharedPref;
    private SwipeRefreshLayout swipeRefreshLayout;

    public ProviderWalletFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        SharedPref = getActivity().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view          = inflater.inflate(R.layout.fragment_provider_wallet, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        signOutButton      = (Button) view.findViewById(R.id.fragment_provider_signout_button);
        balanceTextview    = (TextView) view.findViewById(R.id.fragment_wallet_balance_textview);
        schemeTextview     = (TextView) view.findViewById(R.id.fragment_wallet_scheme_textview);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
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

                        SharedPreferences.Editor editor = SharedPref.edit();
                        editor.putBoolean("firstLaunch", true);
                        editor.remove("providerId");
                        editor.apply();
                        Intent service = new Intent(getActivity(), CapPhoto.class);
                        getActivity().stopService(service);
                        FirebaseAuth.getInstance().signOut();
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


}
