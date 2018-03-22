package com.czsm.DD_driver;




import android.support.multidex.MultiDexApplication;

import com.czsm.DD_driver.helper.acra.ACRAReportSender;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

/**
 * Created by AAshour on 5/3/2016.
 */

@ReportsCrashes(
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text)
public class App extends MultiDexApplication {


    @Override
    public void onCreate() {
        super.onCreate();

        // The following line triggers the initialization of ACRA
        ACRA.init(this);
        // instantiate the report sender with the email credentials.
        // these will be used to send the crash report
//        ACRAReportSender reportSender = new ACRAReportSender("aurora.crash.report@gmail.com", "tgbyhn@23");
        ACRAReportSender reportSender = new ACRAReportSender("adavan2107@gmail.com", "adavan9321");
        // register it with ACRA.
        ACRA.getErrorReporter().setReportSender(reportSender);
    }
}
