package com.dancingdrone;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.musicg.wave.Wave;

import de.yadrone.android.PlayerActivity;
import de.yadrone.android.R;

public class MusicSelect extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_select);

        Wave wave = new Wave();

        // Set up list adapter.
        ContentResolver cr = getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cur = cr.query(uri, null, selection, null, sortOrder);
        startManagingCursor(cur);
        ListAdapter adapter = new SimpleCursorAdapter(
                this, // Context.
                android.R.layout.two_line_list_item,
                cur,
                new String[]{MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DISPLAY_NAME},
                new int[]{android.R.id.text1, android.R.id.text2});
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        Cursor c = ((SimpleCursorAdapter)l.getAdapter()).getCursor();
        c.moveToPosition(position);

        Intent intent = new Intent(this, PlayerActivity.class);
        Bundle b = new Bundle();

        String uri = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA));
        String name = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
        b.putParcelable(PlayerActivity.FILE_URI, Uri.parse(uri));
        b.putString(PlayerActivity.SONG_NAME, name);
        intent.putExtras(b);
        startActivity(intent);
        super.onListItemClick(l, v, position, id);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_music_select, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
