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

package com.nesst.services;


import android.location.Location;

import com.nesstbase.NesstBase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import promise.commons.model.Result;
import promise.commons.model.function.FilterFunction;
import promise.commons.util.DoubleConverter;
import promise.model.store.PreferenceStore;


/**
 * Created on 3/23/18 by dev4vin.
 */
public class LocationStorage {
    private static String COLUMN = "RecentLocations";
    private static PreferenceStore<Location> preferenceStore;

    static {
        preferenceStore =
                new PreferenceStore<Location>(
                        NesstBase.TEMP_PREFERENCE_NAME,

                        new DoubleConverter<Location, JSONObject, JSONObject>() {
                            @Override
                            public Location deserialize(JSONObject jsonObject) {
                                return null;
                            }

                            @Override
                            public JSONObject serialize(Location location) {
                                return null;
                            }
                        }
    ) {
                    @Override
                    public FilterFunction<JSONObject> findIndexFunction(Location movement) {
                        return jsonObject -> {
                            try {
                                double lat = jsonObject.getDouble("lat");
                                double lon = jsonObject.getDouble("lon");
                                return (movement.location().getLatitude() != lat
                                        || movement.location().getLongitude() != lon)
                                        || (movement.date().getTime() - jsonObject.getLong("time")) < 60 * 1000;
                            } catch (JSONException e) {
                                return false;
                            }
                        };
                    }
                };
    }

    public static void getLocation(Result<Location, Exception> callBack) {
        preferenceStore.get(
                COLUMN,
                new Result<>()
                        .withCallBack(
                                movementExtras -> {
                                    int index =
                                            movementExtras
                                                    .all()
                                                    .findIndex(
                                                            movement ->
                                                                    (new Date().getTime() - movement.date().getTime())
                                                                            < 2 * 60 * 1000);
                                    if (index != -1) callBack.response(movementExtras.all().get(index).location());
                                    else
                                        callBack.error(new EmptyError(Helper.INSTANCE.string(R.string.no_recents_found)));
                                })
                        .error(throwable -> callBack.error(new EmptyError(throwable.getMessage()))));
    }

    public static void locations(ResponseCallBack<List<Movement>, EmptyError> callBack) {
        preferenceStore.get(
                COLUMN,
                new ResponseCallBack<Extras<Movement>, Throwable>()
                        .response(movementExtras -> callBack.response(movementExtras.all()))
                        .error(throwable -> callBack.error(new EmptyError(throwable.getMessage()))));
    }

    public static void setLocation(Location location) {
        preferenceStore.save(
                COLUMN,
                new Movement().name("not known").location(location).date(new Date()),
                new ResponseCallBack<Boolean, Throwable>().response(abolean -> {
                }));
    }

    public static void clear() {
        preferenceStore.clear(COLUMN, new ResponseCallBack<>());
    }
}

