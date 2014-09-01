package com.nelepovds.ndutils.rest.flickr;

import com.google.gson.annotations.SerializedName;
import com.nelepovds.ndutils.rest.BaseClass;

/**
 * Created by dmitrynelepov on 31.08.14.
 */
public class NDFlickrPhoto {
    /**
     "id": "15091642342",
     "owner": "126634477@N03",
     "secret": "7b2f3c99fa",
     "server": "3875",
     "farm": 4,
     "title": "Suczka01/Female01",
     "ispublic": 1,
     "isfriend": 0,
     "isfamily": 0
     */
    @SerializedName(value = "id")
    public String photoId;

    @SerializedName(value = "secret")
    public String secret;

    @SerializedName(value = "server")
    public String server;

    @SerializedName(value = "farm")
    public Integer farm;

    @SerializedName(value = "title")
    public String title;
}
