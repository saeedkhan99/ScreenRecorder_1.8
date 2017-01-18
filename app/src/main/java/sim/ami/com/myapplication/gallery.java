package sim.ami.com.myapplication;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import com.ami.com.ami.utils.Utils;
import com.ami.customui.GalleryAdapter;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import android.support.v7.widget.RecyclerView;

import static android.os.Environment.DIRECTORY_MOVIES;

public class gallery extends Activity {


    private RecyclerView recyclerView;
    private GalleryAdapter galleryAdapter;
    InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }



        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);

        //Init Ads
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.instertitial_ad_unit_id));

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });

        requestNewInterstitial();


    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
            .build();
        mInterstitialAd.loadAd(adRequest);
    }


    @Override
    protected void onResume() {
        super.onResume();

        File folder = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_MOVIES) + "/AMIRecorder");
        ArrayList<File> listVideo = Utils.getAllFile(folder,"video");
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        //use one of overloaded setDataSource() functions to set your data source
        int size = listVideo.size();
        for(int i =0; i < size; i++){
            try{
                retriever.setDataSource(this, Uri.fromFile(listVideo.get(i)));
            }catch (Exception e){
                Log.e("Exception", "Remove video i = " +i+" - "+listVideo.get(i).getName());
                File file = listVideo.get(i);
                listVideo.remove(i);
                file.delete();
                size = size - 1;
                i = i -1;
            }
        }
        galleryAdapter = new GalleryAdapter(listVideo,this);
        recyclerView.setAdapter(galleryAdapter);

    }

    @Override
    public void onBackPressed() {
        //ChatHeadService.visiblePopupView();
        if(mInterstitialAd.isLoaded()){
            mInterstitialAd.show();
        }
        sendCommandToService();
        super.onBackPressed();
    }
    private void sendCommandToService(){
        Intent intent = new Intent(gallery.this,ChatHeadService.class);
        intent.putExtra(Constant.COMMAND,Constant.CMD_UPDATE_CONFIG);
        startService(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return true;
    }
}
