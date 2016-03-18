package com.bcp.SFA_Native;

import android.app.Application;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

/**
 * Created by IT-SUPERMASTER on 07/12/2015.
 */

@ReportsCrashes(
        mailTo = "it.yogi@borwita.co.id",
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.Msg_Toast_Error
)

public class FnACRACrashReport extends Application {
    @Override
    public void onCreate(){
        super.onCreate();
        ACRA.init(this);
    }
}
