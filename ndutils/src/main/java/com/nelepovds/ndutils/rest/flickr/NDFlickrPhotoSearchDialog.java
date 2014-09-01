package com.nelepovds.ndutils.rest.flickr;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.nelepovds.ndutils.R;
import com.nelepovds.ndutils.rest.BaseClass;
import com.nelepovds.ndutils.rest.RestApi;

/**
 * Created by dmitrynelepov on 02.09.14.
 */
public class NDFlickrPhotoSearchDialog extends Dialog {

    private final Flickr flickr;

    public EditText editTextFlickrPhotosSearch;
    private GridView gridViewFlickPhotosSearch;

    public NDFlickrPhotoSearchDialog(Context context, Flickr flickr){
        super(context);
        this.flickr = flickr;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.ndutils_flickr_search_photos_dialog);
        this.editTextFlickrPhotosSearch = (EditText)findViewById(R.id.editTextFlickrPhotosSearch);
        this.gridViewFlickPhotosSearch = (GridView)findViewById(R.id.gridViewFlickPhotosSearch);
        this.editTextFlickrPhotosSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    searchPicture(v.getText().toString());
                }
                return false;
            }


        });
    }

    private void searchPicture(String textSearch) {

    this.flickr.photosSearch(getOwnerActivity(), textSearch, new RestApi.IRestApiListener() {
        @Override
        public <RT extends BaseClass> void complete(String apiMethod, Select cache, RT retObject) {
            loadImages((NDFlickrApi) retObject);
        }

        @Override
        public void error(String apiMethod, Select cache, Exception e) {

        }
    });
}

    private void loadImages(NDFlickrApi retObject) {

    }


}
