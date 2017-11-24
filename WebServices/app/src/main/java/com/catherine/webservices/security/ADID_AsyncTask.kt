package com.catherine.webservices.security

import android.os.AsyncTask
import android.text.TextUtils
import com.catherine.webservices.MyApplication
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import java.io.IOException


/**
 * Created by Catherine on 2017/9/11.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
class ADID_AsyncTask(private val callback: ADID_Callback) : AsyncTask<String, Void, Void>() {
    private var ADID = ""
    private var e: Exception? = null
    override fun doInBackground(vararg p0: String?): Void? {
        try {
            val info = AdvertisingIdClient.getAdvertisingIdInfo(MyApplication.INSTANCE.applicationContext)
            ADID = info.id
            if (TextUtils.isEmpty(ADID))
                this.e = NullPointerException("Null ADID")
        } catch (e: GooglePlayServicesNotAvailableException) {
            e.printStackTrace()
            this.e = e
            //You do not have Google Play. We cannot acquire your AdID. Please install Google Play.
        } catch (e: GooglePlayServicesRepairableException) {
            e.printStackTrace()
            this.e = e
            //Google Play service error. We cannot acquire your AdID. Please try again later.
        } catch (e: IOException) {
            e.printStackTrace()
            this.e = e
            //Your Google Play version is out of date. We cannot acquire your AdID. Please update to the newest Google Play version and restart.
        } catch (e: NullPointerException) {
            e.printStackTrace()
            this.e = e
            //Your Google Play version is out of date. We cannot acquire your AdID. Please update to the newest Google Play version and restart.
        } catch (e: Exception) {
            e.printStackTrace()
            this.e = e
        }
        return null
    }

    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)

        if (e != null)
            callback.onError(e!!)
        else
            callback.onResponse(ADID)
    }

    interface ADID_Callback {
        fun onResponse(ADID: String)
        fun onError(e: Exception)
    }
}