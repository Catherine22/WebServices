package com.catherine.webservices.security;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.catherine.webservices.MyApplication;
import com.catherine.webservices.interfaces.ADID_Callback;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import java.io.IOException;

/**
 * Created by Catherine on 2017/11/29.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class ADID_AsyncTask extends AsyncTask<String, Void, Void> {
    private String ADID = "";
    private Exception e;
    private ADID_Callback callback;

    public ADID_AsyncTask(ADID_Callback callback) {
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(String... arg0) {
        try {
            AdvertisingIdClient.Info info = AdvertisingIdClient.getAdvertisingIdInfo(MyApplication.INSTANCE);
            ADID = info.getId();
            if (TextUtils.isEmpty(ADID))
                this.e = new NullPointerException("Null ADID");
        } catch (IOException e) {
            //Your Google Play version is out of date. We cannot acquire your AdID. Please update to the newest Google Play version and restart.
            this.e = e;
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            //You do not have Google Play. We cannot acquire your AdID. Please install Google Play.
            this.e = e;
            e.printStackTrace();
        } catch (GooglePlayServicesRepairableException e) {
            //Google Play service error. We cannot acquire your AdID. Please try again later.
            this.e = e;
            e.printStackTrace();
        } catch (NullPointerException e) {
            //Your Google Play version is out of date. We cannot acquire your AdID. Please update to the newest Google Play version and restart.
            this.e = e;
            e.printStackTrace();
        } catch (Exception e) {
            this.e = e;
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(Void v) {
        super.onPostExecute(v);

        if (e != null)
            callback.onError(e);
        else
            callback.onResponse(ADID);
    }
}