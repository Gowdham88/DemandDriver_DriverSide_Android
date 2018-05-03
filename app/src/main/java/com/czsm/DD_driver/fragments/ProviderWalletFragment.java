package com.czsm.DD_driver.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.czsm.DD_driver.PreferencesHelper;
import com.czsm.DD_driver.R;
import com.czsm.DD_driver.Service.CapPhoto;
import com.czsm.DD_driver.activities.AboutActivity;
import com.czsm.DD_driver.activities.LoginActivity;
import com.czsm.DD_driver.activities.LoginScreenActivity;
import com.czsm.DD_driver.activities.PrivacyActivity;
import com.czsm.DD_driver.activities.RefundActivity;
import com.czsm.DD_driver.activities.TermsandConditionsActivity;
import com.czsm.DD_driver.controller.AllinAllController;
import com.czsm.DD_driver.helper.RESTClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProviderWalletFragment extends Fragment {

    private AllinAllController allinAllController;
    private TextView balanceTextview,AbtTxt;
    private TextView schemeTextview;
    private Button signOutButton;
    ImageView AbtImgView;
    private SharedPreferences SharedPref;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FirebaseAuth mAuth;
    TextView PhoneTxt,EmailTxt;
    String contact="+919941123110",RideCost;
    public ProviderWalletFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
//        SharedPref = getActivity().getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view          = inflater.inflate(R.layout.fragment_provider_wallet, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.colorPrimary);
        AbtImgView=(ImageView)view.findViewById(R.id.about_imgeview);
        AbtTxt=(TextView)view.findViewById(R.id.abt_txt);
        signOutButton      = (Button) view.findViewById(R.id.fragment_provider_signout_button);
        balanceTextview    = (TextView) view.findViewById(R.id.fragment_wallet_balance_textview);
        schemeTextview     = (TextView) view.findViewById(R.id.fragment_wallet_scheme_textview);
        PhoneTxt=(TextView)view.findViewById(R.id.phonetxt);
        EmailTxt=(TextView)view.findViewById(R.id.emaitxt);
        mAuth = FirebaseAuth.getInstance();
        RideCost = PreferencesHelper.getPreference(getActivity(), PreferencesHelper.PREFERENCE_PRICE);
        balanceTextview.setText("Rs."+RideCost);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                swipeRefreshLayout.setRefreshing(false);
            }
        });
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog();
            }
        });

        PhoneTxt.setOnClickListener(new View.OnClickListener() {
            Intent call = new Intent(Intent.ACTION_DIAL);
            @Override
            public void onClick(View v){
                call.setData(Uri.parse("tel:"+ contact));
                startActivity(call);
            }
        });
        EmailTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","gowdhaman@czsm.co.in", null));
                startActivity(Intent.createChooser(intent, "Choose an Email client :"));
            }
        });

        AbtImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheet();
            }
        });

        AbtTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheet();
            }
        });
        return view;

    }

    private void showConfirmDialog() {
        new android.support.v7.app.AlertDialog.Builder(getContext())
                //set message, title, and icon
                .setTitle("Sign out")
                .setMessage("Do you want to sign out?")
                .setIcon(R.drawable.logo01)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        PreferencesHelper.signOut(getContext());
                        mAuth.signOut();
                        Intent intent = new Intent(getContext(), LoginScreenActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        intent.putExtra("EXIT", true);
                        startActivity(intent);
                        getActivity().finish();

                    }
                })

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

    }
    private void showBottomSheet() {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity());
        LayoutInflater factory = LayoutInflater.from(getActivity());
        View bottomSheetView = factory.inflate(R.layout.buttom_sheet, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

       TextView about = (TextView) bottomSheetView.findViewById(R.id.aboutus_title);
        TextView terms = (TextView) bottomSheetView.findViewById(R.id.Tandc_title);
        TextView privacy = (TextView)bottomSheetView.findViewById(R.id.Privacy);
        TextView refund = (TextView)bottomSheetView.findViewById(R.id.refund_title);
        TextView cancel = (TextView)bottomSheetView.findViewById(R.id.cancel_txt);
        LinearLayout cancelLay = (LinearLayout) bottomSheetView.findViewById(R.id.cance_lay);


        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(getActivity(), AboutActivity.class);
                startActivity(intent);

                bottomSheetDialog.dismiss();


            }
        });


        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent(getActivity(), TermsandConditionsActivity.class);
                startActivity(intent1);
                bottomSheetDialog.dismiss();
            }
        });

        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2=new Intent(getActivity(), PrivacyActivity.class);
                startActivity(intent2);
                bottomSheetDialog.dismiss();
            }
        });

        refund.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent3=new Intent(getActivity(), RefundActivity.class);
                startActivity(intent3);
                bottomSheetDialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });

        cancelLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });

    }

}
