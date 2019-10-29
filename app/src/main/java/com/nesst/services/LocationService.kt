/*
 * Copyright 2017, Nesst
 * Licensed under the Apache License, Version 2.0, "Nesst Inc".
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nesst.services

import android.app.Activity
import android.content.Intent
import android.location.Location
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import com.nesst.R
import com.nesst.hasGPSProvider
import promise.PromiseLocation
import promise.location.config.LocationParams
import promise.location.providers.LocationGooglePlayServicesProvider
import promise.location.providers.LocationManagerProvider

class LocationService
/*private android.os.Handler handler = new android.os.Handler(Looper.getMainLooper());
  private int tries = 0;*/
    (private val activity: Activity) {

    fun findLocation(listener: Listener) {
        if (imprint != null)
            listener.onAcquireLocation(imprint)
        else if (!locationServicesEnabled(activity)) {
            turnOnLocation(activity, { on ->
                if (on)
                    PromiseLocation.with(activity)
                        .location(if (activity.hasGPSProvider()) LocationManagerProvider() else LocationGooglePlayServicesProvider())
                        .config(LocationParams.NAVIGATION)
                        .continuous()
                        .start { location ->
                            listener.onAcquireLocation(location)
                            LocationStorage.setLocation(location)
                            /*if (tries == 3) {
                          listener.onAcquireLocation(location);
                          LocationStorage.setAppLocation(appLocation);
                        } else {
                          findLocation(listener);
                          tries++;
                        }*/
                        }
            })
        } else
            PromiseLocation.with(activity)
                .location(if (activity.hasGPSProvider()) LocationManagerProvider() else LocationGooglePlayServicesProvider())
                .config(LocationParams.NAVIGATION)
                .continuous()
                .start { location ->
                    listener.onAcquireLocation(location)
                    LocationStorage.setLocation(location)
                    /*if (tries == 3) {
                            listener.onAcquireLocation(appLocation);
                            LocationStorage.setAppLocation(appLocation);
                          } else {
                            findLocation(listener);
                            tries++;
                          }*/
                }
        /*LocationStorage.getAppLocation(
        new ResponseCallBack<AppLocation, EmptyError>()
            .response(listener::onAcquireLocation)
            .error(
                emptyError -> {
                  PromiseLocation.with(activity)
                      .appLocation(new LocationManagerProvider())
                      .config(LocationParams.BEST_EFFORT)
                      .continuous()
                      .start(
                          appLocation -> {
                            listener.onAcquireLocation(appLocation);
                            LocationStorage.setAppLocation(appLocation);
                          *//*if (tries == 3) {
                            listener.onAcquireLocation(appLocation);
                            LocationStorage.setAppLocation(appLocation);
                          } else {
                            findLocation(listener);
                            tries++;
                          }*//*
                          });
                }));*/
    }

    /**
     * Created by dev4vin on 9/19/17.
     */
    interface Listener {
        fun onAcquireLocation(location: Location)

        fun onAcquireLocation(name: String)
    }

    interface LocationOnListener {
        fun onTurnedOn(on: Boolean)
    }

    companion object {
        val GPS_PERMISSIONS = 0x1115
        /**
         * Constant used in the location settings dialog.
         */
        val REQUEST_CHECK_SETTINGS = 0x1
        private val imprint: Location? = null/*new AppLocation("") {{
    setLatitude(-1.2650073);
    setLongitude(36.7409917);
  }}*/

        fun locationServicesEnabled(activity: Activity): Boolean {

            val locationProviders = Settings.Secure.getString(
                activity.contentResolver, Settings.Secure.LOCATION_PROVIDERS_ALLOWED
            )
            return locationProviders != null && locationProviders != ""
        }

        fun turnOnLocation(activity: Activity, listener: LocationOnListener) {
            if (activity.isFinishing || activity.isDestroyed) return
            activity.runOnUiThread {
                AlertDialog.Builder(activity)
                    .setTitle(activity.getString(R.string.location_services))
                    .setMessage(activity.getString(R.string.this_app_requires_location_services_kindly_turn_on_location_services))
                    .setPositiveButton(
                        activity.getString(R.string.turn_on)
                    ) { dialogInterface, i ->
                        Receiver.bind({ listener.onTurnedOn(true) })
                        activity.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    }
                    .setNegativeButton(activity.getString(R.string.decline)) { dialogInterface, i ->
                        listener.onTurnedOn(
                            false
                        )
                    }
                    .setCancelable(false)
                    .show()
            }
        }

        fun stopUpdates(activity: Activity) {
            if (locationServicesEnabled(activity))
                PromiseLocation.with(activity).location(LocationManagerProvider()).stop()
        }
    }

}
