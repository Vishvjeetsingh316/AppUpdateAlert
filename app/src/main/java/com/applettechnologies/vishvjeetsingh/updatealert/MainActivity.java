package com.applettechnologies.vishvjeetsingh.updatealert;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.jsoup.Jsoup;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    //Initialize variable
    TextView tvCurrentVersion, tvLatestVersion;
    String sCurrentVersion, sLatestVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Assign Variable
        tvCurrentVersion = findViewById(R.id.tv_current_version);
        tvLatestVersion = findViewById(R.id.tv_latest_version);

        //Get Latest Version from Play Store
        new GetLatestVersion().execute();
    }

    private class GetLatestVersion extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                sLatestVersion = Jsoup
                        .connect("https://play.google.com/store/apps/details?id="
                                + getPackageName())
                        .timeout(30000)
                        .get()
                        .select("div.hAyfc:nth-child(4)>"+
                                "span:nth-child(2) > div:nth-child(1) "+
                                        "> span:nth-child(1)")
                        .first()
                        .ownText();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return sLatestVersion;
        }

        @Override
        protected void onPostExecute(String s) {
            //Get current version
            sCurrentVersion = BuildConfig.VERSION_NAME;
            //Set current version on TextView
            tvCurrentVersion.setText(sCurrentVersion);
            //Set latest version on TextView
            tvLatestVersion.setText((sLatestVersion));

            if (sLatestVersion != null){
                //Version convert to float
                float cVersion = Float.parseFloat(sCurrentVersion);
                float lVersion = Float.parseFloat(sLatestVersion);
                // check condition (latest version is grater than current version)
                if(lVersion > cVersion){
                    //create update AlertDialog
                    updateAlertDialog();
                }
            }
        }
    }

    private void updateAlertDialog() {
        //Initialize Alert
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //Set title
        builder.setTitle(getResources().getString(R.string.app_name));
        //Set Message
        builder.setMessage("Update Available");
        //Set Non Cancelable
        builder.setCancelable(false);

        //On Update
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Open Play Store
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=" +getPackageName())));
                //Dismiss Alert Dialog
                dialog.dismiss();
            }
        });

        //On Cancel
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Cancel Alert Dialog
                dialog.cancel();
            }
        });

        //Show Alert Dialog
        builder.show();

    }
}
