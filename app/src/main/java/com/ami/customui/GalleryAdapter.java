package com.ami.customui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.analytics.HitBuilders;

import org.m4m.samples.ComposerAudioEffectActivity;
import org.m4m.samples.ComposerCutActivity;
import org.m4m.samples.ComposerTimeScalingActivity;
import org.m4m.samples.ComposerVideoEffectActivity;
import org.m4m.samples.SamplesMainActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import sim.ami.com.myapplication.AppImpl;
import sim.ami.com.myapplication.Constant;
import sim.ami.com.myapplication.R;

/**
 * Created by hi on 5/13/16.
 */
public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MyViewHolder> {
    private ArrayList<File> moviesList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, duration, size;
        private ImageView thumbanail,playVideo;
        private ImageView shareAction,magicButton,deleteButton;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.textViewName);
            duration = (TextView) view.findViewById(R.id.textViewDuration);
            size = (TextView) view.findViewById(R.id.textViewSize);
            thumbanail = (ImageView)view.findViewById(R.id.imageViewThumbanail);
            playVideo = (ImageView)view.findViewById(R.id.imageViewPlay);
            shareAction = (ImageView)view.findViewById(R.id.imageButtonShare);
            magicButton = (ImageView)view.findViewById(R.id.imageButtonEdit);
            deleteButton = (ImageView)view.findViewById(R.id.imageButtonDelete);



        }
    }


    public GalleryAdapter(ArrayList<File> moviesList, Context context) {
        this.moviesList = moviesList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_item_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        File movie = moviesList.get(position);
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        //use one of overloaded setDataSource() functions to set your data source
        Log.e("Setdata source at",movie.getName());
        retriever.setDataSource(this.context, Uri.fromFile(movie));

        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInMillisec = Long.parseLong(time );
        String timeFormat = String.format("%d min : %d sec",
                TimeUnit.MILLISECONDS.toMinutes(timeInMillisec),
                TimeUnit.MILLISECONDS.toSeconds(timeInMillisec) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeInMillisec)));
        holder.name.setText(movie.getName());
        holder.duration.setText(timeFormat);
        holder.size.setText(""+movie.length()/(1024*1024)+" Mb");
        Glide.with(this.context).load(movie).into(holder.thumbanail);

        //Set tag
        holder.playVideo.setTag(holder);
        holder.shareAction.setTag(holder);
        holder.deleteButton.setTag(holder);
        holder.magicButton.setTag(holder);

        holder.playVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(context,"Click to play media",Toast.LENGTH_SHORT).show();
                MyViewHolder myViewHolder = (MyViewHolder)v.getTag();
                int index = myViewHolder.getPosition();
                playResult(index);
            }
        });
        holder.shareAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context,"Click to share",Toast.LENGTH_SHORT).show();
                MyViewHolder myViewHolder = (MyViewHolder)v.getTag();
                int index = myViewHolder.getPosition();
                shareVideo(index);
            }
        });
        holder.magicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyViewHolder myViewHolder = (MyViewHolder)v.getTag();
                final int index = myViewHolder.getPosition();

                PopupMenu popupMenu = new PopupMenu(context,v);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.item_cut_video:
                               // Toast.makeText(context,"Cut video",Toast.LENGTH_SHORT).show();
                               // AppImpl.tracker().setScreenName("Gallery-Cut-Video");
                               // AppImpl.tracker().send(new HitBuilders.ScreenViewBuilder().build());
                                startEditVideo(index, Constant.CMD_CUT_VIDEO);
                                return true;
                            case R.id.item_video_effect:
                                //Toast.makeText(context,"Video effect",Toast.LENGTH_SHORT).show();
                               // AppImpl.tracker().setScreenName("Gallery-Effect-Video");
                               // AppImpl.tracker().send(new HitBuilders.ScreenViewBuilder().build());
                                startEditVideo(index,Constant.CMD_EFFECT_VICEO);
                                return true;
                            case R.id.item_time_scaling:
                                //Toast.makeText(context,"Time Scaling",Toast.LENGTH_SHORT).show();
                              //  AppImpl.tracker().setScreenName("Gallery-TimeScaling-Video");
                               // AppImpl.tracker().send(new HitBuilders.ScreenViewBuilder().build());
                                startEditVideo(index,Constant.CMD_TIME_SCALING);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.inflate(R.menu.popup_edit_menu);
                popupMenu.show();
            }
        });
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(context,"Click to delete",Toast.LENGTH_SHORT).show();

                MyViewHolder myViewHolder = (MyViewHolder)v.getTag();
                int index = myViewHolder.getPosition();
                confirmDeleteVideo(index);
            }
        });

    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }

    public void deleteVideo(int index){
        AppImpl.tracker().setScreenName("Gallery-Delete-Video");
        AppImpl.tracker().send(new HitBuilders.ScreenViewBuilder().build());
        moviesList.remove(index);
        notifyDataSetChanged();
    }

    public void confirmDeleteVideo(final  int index){
        AlertDialog alertDialog =    new AlertDialog.Builder(context)
                .setMessage(R.string.dialog_confirm_delete)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        new VideoDelete(moviesList.get(index)).run();
                        deleteVideo(index);
                        dialog.cancel();
                    }
                })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                .create();
        alertDialog.show();
    }

    private static class VideoDelete implements Runnable{

        private final File mFile;

        private VideoDelete(File file) {
            mFile = file;
        }

        @Override
        public void run() {
            try {
                mFile.delete();
            }catch (Exception e){
                Log.e("DeleteFile Exception",e.toString());
            }

        }
    }
    private void shareVideo(int index){
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        Uri screenshotUri = Uri.fromFile(moviesList.get(index));
        Log.e("ShareVideo","URI = "+screenshotUri);
        sharingIntent.setType("video/*");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
        sharingIntent.addFlags(1);
        context.startActivity(Intent.createChooser(sharingIntent, "Share Video"));
    }
    private void startEditVideo(int index, String cmd){
        Uri screenshotUri = Uri.fromFile(moviesList.get(index));
        String fileName = moviesList.get(index).getName();
        switch (cmd){
            case Constant.CMD_CUT_VIDEO:
                startActivityByClazz(screenshotUri,ComposerCutActivity.class.getName(),fileName);
                break;
            case Constant.CMD_EFFECT_VICEO:
                startActivityByClazz(screenshotUri,ComposerVideoEffectActivity.class.getName(),fileName);
                break;
            case Constant.CMD_REPLACE_AUDIO:
                startActivityByClazz(screenshotUri,ComposerAudioEffectActivity.class.getName(),fileName);
                break;
            case Constant.CMD_TIME_SCALING:
                startActivityByClazz(screenshotUri,ComposerTimeScalingActivity.class.getName(),fileName);
                break;
            default:
                Log.e("ERROR","UNKNOWN CMD TYPE");
                break;
        }

    }

    private void startActivityByClazz(Uri videoUri, String className,String fileName){
        Intent intent = null;
        try {
            Log.e("Start Activity","Video URI = "+videoUri + "fileName = "+ fileName);
            intent = new Intent(context, Class.forName(className));
            intent.putExtra(Constant.EXTRA_DATA,videoUri.toString());
            intent.putExtra(Constant.EXTRA_DATA_FILE_NAME,fileName);
            context.startActivity(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.e("ERROR","Class "+className +" Not found");
        }

    }
    private void playResult(int index ) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri screenshotUri = Uri.fromFile(moviesList.get(index));
        intent.setDataAndType(screenshotUri, "video/mp4");
        context.startActivity(intent);
    }

}
