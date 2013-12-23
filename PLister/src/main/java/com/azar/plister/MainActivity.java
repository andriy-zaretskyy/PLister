package com.azar.plister;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.azar.plister.model.Bucket;
import com.azar.plister.model.SimpleBucket;
import com.azar.plister.model.SimpleStorage;
import com.azar.plister.model.Storage;
import com.azar.plister.model.StorageException;

import java.util.Date;

public final class MainActivity extends ActionBarActivity {
    private Storage storage;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main2);

        storage = new SimpleStorage(getFilesDir());

        Button addnew = (Button) findViewById(R.id.addnew);
        addnew.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 0);
            }
        });


        listView = (ListView) findViewById(R.id.listView);

        listView.setOnItemClickListener(new ListView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bucket b = (Bucket) listView.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(), ViewListActivity.class);
                intent.putExtra("bucket_uid", b.getUid());
                startActivity(intent);
            }
        });

        final Activity currentActivity = this;
        listView.setOnItemLongClickListener(new ListView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int bucketPos = position;
                AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Bucket b = (Bucket) listView.getItemAtPosition(bucketPos);
                        storage.getBuckets().remove(b);
                        storage.save();
                        updateListView();

                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

                builder.setMessage("Do you really want to delete the item?");
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
        });

        updateListView();
    }

    private void updateListView() {
        ArrayAdapter<Bucket> adapter = new ArrayAdapter<Bucket>(this, R.layout.list_item, storage.getBuckets());
        listView.setAdapter(adapter);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri targetUri = data.getData();
            SimpleBucket bucket = new SimpleBucket();
            bucket.setImageUri(targetUri);
            bucket.setName((new Date()).toString());

            try {
                this.storage.getBuckets().add(bucket);
                this.storage.save();
            } catch (StorageException e) {
                e.printStackTrace();
            }

            try {
                this.updateListView();
            } catch (StorageException e) {
                e.printStackTrace();
            }
        }
    }
}
