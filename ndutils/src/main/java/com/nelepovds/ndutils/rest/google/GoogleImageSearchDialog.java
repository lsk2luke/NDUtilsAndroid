package com.nelepovds.ndutils.rest.google;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nelepovds.ndutils.R;
import com.nelepovds.ndutils.rest.NDRestBaseAPI;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by dmitrynelepov on 23.12.14.
 */
public class GoogleImageSearchDialog extends Dialog implements View.OnClickListener {
    
    public static interface IGoogleImageSearchDialogListener {

        void selectImage(GoogleApiImageInfo item, String searchText);
    }

    private IGoogleImageSearchDialogListener listener;

    private EditText editTextNDSearchTextGoogle;

    private ImageView imageViewNDSearchGoogle;

    private RelativeLayout relativeLayoutWaitingScreen;

    private GridView gridView;

    private GoogleApiImageAdapter adapter;

    private GoogleApiImageSearch googleApiClient;

    protected ArrayList<GoogleApiImageInfo> googleApiImageInfos = new ArrayList<>();

    private Picasso picasso;
    private String searchText;

    public GoogleImageSearchDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        setContentView(R.layout.dialog_google_image_search);
        this.editTextNDSearchTextGoogle = (EditText) findViewById(R.id.editText_ND_SearchTextGoogle);
        this.imageViewNDSearchGoogle = (ImageView) findViewById(R.id.imageView_ND_SearchGoogle);
        this.relativeLayoutWaitingScreen = (RelativeLayout) findViewById(R.id.relativeLayout_WaitingScreen);
        this.gridView = (GridView) findViewById(R.id.gridView);

        this.googleApiClient = new GoogleApiImageSearch("https://ajax.googleapis.com/ajax/services/search", IGoogleApiImageSearch.class);

        this.relativeLayoutWaitingScreen.setVisibility(View.GONE);

        this.imageViewNDSearchGoogle.setOnClickListener(this);

        this.adapter = new GoogleApiImageAdapter();
        this.gridView.setAdapter(this.adapter);
        this.gridView.setNumColumns(2);
        this.gridView.setVerticalSpacing((int) context.getResources().getDimension(R.dimen.size4));
        this.gridView.setHorizontalSpacing((int) context.getResources().getDimension(R.dimen.size4));
        this.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listener != null) {
                    listener.selectImage((GoogleApiImageInfo) parent.getItemAtPosition(position), editTextNDSearchTextGoogle.getText().toString());
                    dismiss();
                }
            }
        });
    }

    @Override
    public void show() {
        super.show();
        if (this.searchText != null) {
            this.editTextNDSearchTextGoogle.setText(this.searchText);
            this.search();
        }
    }

    public GoogleImageSearchDialog setPicasso(Picasso picasso) {
        this.picasso = picasso;
        return this;
    }

    public GoogleImageSearchDialog setListener(IGoogleImageSearchDialogListener listener) {
        this.listener = listener;
        return this;
    }

    public GoogleImageSearchDialog setSearchText(String searchText) {
        this.searchText = searchText;
        return this;
    }

    @Override
    public void onClick(View v) {
        if (this.imageViewNDSearchGoogle.getId() == v.getId()) {
            this.search();
        }
    }

    private void search() {
        if (this.editTextNDSearchTextGoogle.getText().length() > 0) {
            this.relativeLayoutWaitingScreen.setVisibility(View.VISIBLE);
            this.googleApiClient.service.images(this.editTextNDSearchTextGoogle.getText().toString(), new Callback<GoogleApiResponse>() {
                @Override
                public void success(GoogleApiResponse googleApiResponse, Response response) {
                    googleApiImageInfos = googleApiResponse.responseData.results;
                    adapter.notifyDataSetChanged();
                    relativeLayoutWaitingScreen.setVisibility(View.GONE);

                }

                @Override
                public void failure(RetrofitError retrofitError) {
                    relativeLayoutWaitingScreen.setVisibility(View.GONE);
                }
            });
        }
    }

    public static interface IGoogleApiImageSearch {
        @GET("/images?v=1.0&as_filetype=jpg&rsz=8")
        public void images(@Query("q") String query, Callback<GoogleApiResponse> apiResponseCallback);
    }


    private class GoogleApiImageSearch extends NDRestBaseAPI<IGoogleApiImageSearch> {

        public GoogleApiImageSearch(String endPoint, Class<IGoogleApiImageSearch> restInterfaceClass) {
            super(endPoint, restInterfaceClass);
        }

        @Override
        protected String getWatcherID() {
            return null;
        }
    }

    class GoogleApiImageAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return googleApiImageInfos.size();
        }

        @Override
        public GoogleApiImageInfo getItem(int position) {
            return googleApiImageInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView retView = new ImageView(getContext()) {
                @Override
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    super.onMeasure(widthMeasureSpec, widthMeasureSpec);
                }
            };

            GoogleApiImageInfo item = this.getItem(position);
            if (picasso != null) {
                picasso.load(item.tbUrl)
                        .fit()
                        .centerCrop()
                        .noFade()
                        .into(retView);
            }

            return retView;
        }
    }

}
