package com.kpr.media.player;

import java.io.IOException;
import java.util.ArrayList;
import android.app.Activity;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class Kpr_mediaplayerActivity extends Activity implements OnClickListener 
{
	
	/** Called when the activity is first created. */
	Cursor csr;
	TextView songsList;
	ListView songsPathList;
	ArrayList<String> songsPathArrayList;
	ArrayAdapter<String> stringArrayAdapter;
	ArrayList<String> songspath;
	MediaPlayer mMediaPlayer;
	ImageButton play_button, pause_button, stop_button;
	SeekBar seekbar;
	
	private final Handler handler = new Handler();

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		songsList = (TextView) findViewById(R.id.songs);
		songsPathList = (ListView) findViewById(R.id.listView1);
		play_button = (ImageButton) findViewById(R.id.play_button);
		pause_button = (ImageButton) findViewById(R.id.pause_button);
		stop_button = (ImageButton) findViewById(R.id.stop_button);
	    seekbar = (SeekBar) findViewById(R.id.seekBar1);
	    //seekbar.setMax(mMediaPlayer.getDuration());
		
		songsPathArrayList = new ArrayList<String>();
		stringArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, songsPathArrayList);

		songsPathList.setAdapter(stringArrayAdapter);
		songspath=new ArrayList<String>();
		mMediaPlayer = new MediaPlayer();

		play_button.setOnClickListener(this);
		pause_button.setOnClickListener(this);
		stop_button.setOnClickListener(this);

		String[] proj = { MediaStore.Audio.Media._ID,
						  MediaStore.Audio.Media.DATA,
						  MediaStore.Audio.Media.DISPLAY_NAME,
						  MediaStore.Video.Media.SIZE, MediaStore.Audio.Media.ALBUM };

		csr = managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, proj,null, null, null);

		if(csr!=null){
		songsList.append("Count " + Integer.toString(csr.getColumnCount()) + " \n");
		songsList.append(Integer.toString(csr.getCount()) + "\n");
		}
		if (csr.moveToFirst()) 
		{
			do {
				String path,name;
				int pathCol = csr.getColumnIndex(MediaStore.Audio.Media.DATA);
				int nameCol = csr.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
				path = csr.getString(pathCol);
				name=csr.getString(nameCol);
				songsPathArrayList.add(name);
				songspath.add(path);
			} while (csr.moveToNext());
		}

		stringArrayAdapter.notifyDataSetChanged();

		songsPathList.setOnItemClickListener(new OnItemClickListener() 
		{
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) 
			{
				// TODO Auto-generated method stub
				try 
				{
					mMediaPlayer.reset();
					mMediaPlayer.setDataSource(songspath.get(pos));
					mMediaPlayer.prepare();
					mMediaPlayer.start();
					startPlayProgressUpdater(); 
				} 
				catch (IllegalArgumentException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(getBaseContext(), "Error Occured",Toast.LENGTH_LONG);
				} 
				catch (IllegalStateException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(getBaseContext(), "Error Occured",Toast.LENGTH_LONG);
				} 
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(getBaseContext(), "Error Occured",Toast.LENGTH_LONG);
				}

			}
		});
	}

	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
		// TODO Auto-generated method stub
		if (mMediaPlayer != null)
		{
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}

	public void onClick(View v) 
	{
		// TODO Auto-generated method stub
		switch (v.getId()) 
		{
		case R.id.play_button:
			mMediaPlayer.start();
			//startPlayProgressUpdater();
			break;
		case R.id.pause_button:
			mMediaPlayer.pause();
			break;
		case R.id.stop_button:
			mMediaPlayer.reset();
			break;
		default:
			break;
		}
	}
	
    public void startPlayProgressUpdater() 
    {
    	seekbar.setProgress(mMediaPlayer.getCurrentPosition());
    	
		if (mMediaPlayer.isPlaying()) 
		{
			Runnable notification = new Runnable() 
			{
		        public void run() 
		        {
		        	startPlayProgressUpdater();
				}
		    };
		    handler.postDelayed(notification,1000);
    	}
		else
    	{
    		mMediaPlayer.pause();
    		seekbar.setProgress(0);
    	}
    } 

 }