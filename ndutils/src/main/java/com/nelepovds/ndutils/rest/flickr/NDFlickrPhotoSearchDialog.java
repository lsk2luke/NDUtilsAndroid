package com.nelepovds.ndutils.rest.flickr;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.nelepovds.ndutils.R;
import com.nelepovds.ndutils.rest.BaseClass;
import com.nelepovds.ndutils.rest.RestApi;
import com.nelepovds.ndutils.ui.RoundedTransformation;
import com.squareup.picasso.Picasso;

/**
 * Created by dmitrynelepov on 02.09.14.
 */
public class NDFlickrPhotoSearchDialog extends Dialog {

    public static interface INDFlickrPhotoSearchDialogListener {
        public void selectPhoto(NDFlickrPhoto photo, String searchText);
    }

    private INDFlickrPhotoSearchDialogListener listener;

    private final Flickr flickr;
    private Picasso picasso;

    private EditText editTextFlickrPhotosSearch;
    private GridView gridViewFlickPhotosSearch;
    private FlickrPhotosAdapter photosAdapter;
    private Button buttonFlickSearchCancel;

    private ProgressBar progressBarFlickrSearch;

    public int widthItems;
    public int heightItems;
    public int per_page = 50;

    private int placeHolderItems;

    public NDFlickrPhotoSearchDialog(Context context, Flickr flickr, Picasso picasso, int placeHolderItems, INDFlickrPhotoSearchDialogListener listener) {
        super(context);
        this.flickr = flickr;
        this.picasso = picasso;
        this.placeHolderItems = placeHolderItems;
        this.listener = listener;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        setContentView(R.layout.ndutils_flickr_search_photos_dialog);

        this.progressBarFlickrSearch = (ProgressBar) findViewById(R.id.progressBarFlickrSearch);
        this.progressBarFlickrSearch.setVisibility(View.GONE);
        this.buttonFlickSearchCancel = (Button) findViewById(R.id.buttonFlickSearchCancel);
        this.buttonFlickSearchCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        this.editTextFlickrPhotosSearch = (EditText) findViewById(R.id.editTextFlickrPhotosSearch);
        this.gridViewFlickPhotosSearch = (GridView) findViewById(R.id.gridViewFlickPhotosSearch);


        this.editTextFlickrPhotosSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    searchPicture(v.getText().toString());
                }
                return false;
            }


        });
        this.gridViewFlickPhotosSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listener != null) {
                    listener.selectPhoto(photosAdapter.getItem(position),editTextFlickrPhotosSearch.getText().toString());
                }
                dismiss();
            }
        });

    }

    private void searchPicture(String textSearch) {
        this.progressBarFlickrSearch.setVisibility(View.VISIBLE);
        this.flickr.photosSearch(getOwnerActivity(), textSearch,this.per_page, new RestApi.IRestApiListener() {
            @Override
            public <RT extends BaseClass> void complete(String apiMethod, Select cache, RT retObject) {
                loadImages((NDFlickrApi) retObject);

            }

            @Override
            public void error(String apiMethod, Select cache, Exception e) {
                progressBarFlickrSearch.setVisibility(View.GONE);
            }
        });
    }

    private void loadImages(NDFlickrApi flickrApi) {
        this.photosAdapter = new FlickrPhotosAdapter(flickrApi);
        this.gridViewFlickPhotosSearch.setAdapter(this.photosAdapter);
        this.gridViewFlickPhotosSearch.setNumColumns(5);
        this.progressBarFlickrSearch.setVisibility(View.GONE);
    }


    class FlickrPhotoGridItem extends RelativeLayout {

        private ImageView photoImageView;
        private NDFlickrPhoto photo;

        public FlickrPhotoGridItem(Context context) {
            super(context);
            this.photoImageView = new ImageView(context);
            this.addView(this.photoImageView, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        }

        public void setPhoto(NDFlickrPhoto photo) {
            this.photo = photo;
            picasso.load(photo.getImagePath("s")).transform(new RoundedTransformation(4, 0)).placeholder(placeHolderItems).into(photoImageView);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, widthMeasureSpec);//Square
        }
    }

    class FlickrPhotosAdapter extends BaseAdapter {

        public NDFlickrApi flickrApi;

        FlickrPhotosAdapter(NDFlickrApi flickrApi) {
            this.flickrApi = flickrApi;
        }

        @Override
        public int getCount() {
            return this.flickrApi.photos.photo.size();
        }

        @Override
        public NDFlickrPhoto getItem(int position) {
            return this.flickrApi.photos.photo.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            FlickrPhotoGridItem retView = (FlickrPhotoGridItem) convertView;
            if (retView == null) {
                retView = new FlickrPhotoGridItem(getContext());
            }
            retView.setPhoto(this.getItem(position));
            return retView;
        }
    }
}

