
/*
 * Copyright 2020, {{App}}
 * Licensed under the Apache License, Version 2.0, "{{App}} Inc".
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package co.app.common.photo;

import android.os.Parcel;
import android.os.Parcelable;

public class Photo implements Parcelable {

    public static String ONLINE = "online";
    public static String OFFLINE = "offline";
    private String url;
    private String type = "OFFLINE";

    public Photo() {
    }

    public String url() {
        return url;
    }

    public Photo url(String url) {
        this.url = url;
        return this;
    }

    public String type() {
        return type;
    }

    public Photo type(String type) {
        this.type = type;
        return this;
    }

    public boolean isOnLine() {
        return type.equals(ONLINE);
    }

    public boolean isOffLine() {
        return !isOnLine();
    }

    public Photo online() {
        return type(ONLINE);
    }

    public Photo offline() {
        return type(OFFLINE);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeString(this.type);
    }

    protected Photo(Parcel in) {
        this.url = in.readString();
        this.type = in.readString();
    }

    public static final Parcelable.Creator<Photo> CREATOR = new Parcelable.Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel source) {
            return new Photo(source);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };
}
