//package com.czsm.driverin.receiver;
//
///**
// * Created by macbook on 02/08/16.
// */
//import android.util.Log;
//
//import com.google.firebase.iid.FirebaseInstanceId;
//import com.google.firebase.iid.FirebaseInstanceIdService;
//
///**
// * Created by Snow on 5/31/2016.
// */
//
//public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
//
//    private static final String TAG = "MyFirebaseIIDService";
//
//    private boolean alreadyRegistered = false;
//
//    /**
//     * Called if InstanceID token is updated. This may occur if the security of
//     * the previous token had been compromised. Note that this is called when the InstanceID token
//     * is initially generated so this is where you would retrieve the token.
//     */
//    // [START refresh_token]
//    @Override
//    public void onTokenRefresh() {
//        // Get updated InstanceID token.
//        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//        Log.e(TAG, "Refreshed token: " + refreshedToken);
//
//        // TODO: Implement this method to send any registration to your app's servers.
//        sendRegistrationToServer(refreshedToken);
//    }
//    // [END refresh_token]
//
//    /**
//     * Persist token to third-party servers.
//     *
//     * Modify this method to associate the user's FCM InstanceID token with any server-side account
//     * maintained by your application.
//     *
//     * @param token The new token.
//     */
//    private void sendRegistrationToServer(String token) {
//        // Add custom implementation, as needed.
//
////        if (!alreadyRegistered) {
////            SharedPreferences allinAllSharedPreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
////            SharedPreferences.Editor editor = allinAllSharedPreferences.edit();
////            editor.putString("regID", token);
////            editor.apply();
////            alreadyRegistered = true;
////        }
////        if (LoginActivity.loading != null && LoginActivity.loading.isShowing())
////            LoginActivity.loading.dismiss();
//
//    }
//}
