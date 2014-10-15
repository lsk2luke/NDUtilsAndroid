package com.nelepovds.ndutils.rest.flickr;

import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.nelepovds.ndutils.rest.BaseClass;

/**
 * Created by dmitrynelepov on 31.08.14.
 */
public class NDFlickrPhoto {


    /**
     * "id": "15091642342",
     * "owner": "126634477@N03",
     * "secret": "7b2f3c99fa",
     * "server": "3875",
     * "farm": 4,
     * "title": "Suczka01/Female01",
     * "ispublic": 1,
     * "isfriend": 0,
     * "isfamily": 0
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

    public static String[] sizes = new String[]{
            "Square", //small square 75x75
            "Large Square", //large square 150x150
            "Thumbnail", //thumbnail, 100 on longest side
            "Small", //small, 240 on longest side
            "Small 320", //small, 320 on longest side
            "Medium", //medium, 500 on longest side
            "Large", //medium 640, 640 on longest side
            "Original", //medium 800, 800 on longest side†
    };

    //https://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}_[mstzb].jpg
    public static final String ND_FLICKR_IMAGE_URL_SIZES = "https://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}_[mstzb].jpg";
    //https://farm{farm-id}.staticflickr.com/{server-id}/{id}_{o-secret}_o.(jpg|gif|png)
    public static final String ND_FLICKR_IMAGE_URL_ORIGINAL = "https://farm{farm-id}.staticflickr.com/{server-id}/{id}_{o-secret}_o.png";

    /**
     * s	small square 75x75
     * q	large square 150x150
     * t	thumbnail, 100 on longest side
     * m	small, 240 on longest side
     * n	small, 320 on longest side
     * -	medium, 500 on longest side
     * z	medium 640, 640 on longest side
     * c	medium 800, 800 on longest side†
     * b	large, 1024 on longest side*
     */
    public String getImagePath(String size) {
        String completeStr = "";
        if (size.equalsIgnoreCase("o")) {
            //Original
            completeStr = ND_FLICKR_IMAGE_URL_ORIGINAL
                    .replaceFirst("\\{farm-id\\}", this.farm.toString())
                    .replaceFirst("\\{server-id\\}", this.server)
                    .replaceFirst("\\{id\\}", this.photoId)
                    .replaceFirst("\\{o-secret\\}", this.secret);

        } else {
            completeStr = ND_FLICKR_IMAGE_URL_SIZES
                    .replaceFirst("\\{farm-id\\}", this.farm.toString())
                    .replaceFirst("\\{server-id\\}", this.server)
                    .replaceFirst("\\{id\\}", this.photoId)
                    .replaceFirst("\\{secret\\}", this.secret)
                    .replaceFirst("\\[mstzb\\]", size);
        }
        Log.wtf("FLICKR:", completeStr);
        return completeStr;
    }

    public static class NDFlickrPhotoSize extends BaseClass {
        public String label;
        public Integer height;
        public Integer width;
        public String source;
        public String media;
        public String url;
    }
}
