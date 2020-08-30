package com.PDFKaro.imagetopdf.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.PDFKaro.imagetopdf.Fragments.AddImageFragment;
import com.PDFKaro.imagetopdf.Fragments.HomeFragment;
import com.PDFKaro.imagetopdf.Interface.RefreshList;
import com.PDFKaro.imagetopdf.R;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity implements RefreshList {
    AddImageFragment imageFragment;
    HomeFragment homeFragment;
    Toolbar toolbar;
    ConstraintLayout main_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar=findViewById(R.id.toolbar1);
        main_layout=findViewById(R.id.main_layout);
        setSupportActionBar(toolbar);
        imageFragment=new AddImageFragment();
        homeFragment=new HomeFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,homeFragment,null).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.bottom_nav_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.menu_rate:
                try {

                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + getPackageName())));
                }
                catch (ActivityNotFoundException e)
                {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id="+getPackageName())));
                }
            case R.id.menu_tc:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("https://pdfkaro.blogspot.com/p/pdf-karo-terms-conditions.html"))
                            );
                }
                catch (Exception e)
                {
                    Snackbar.make(main_layout,e.getMessage(),Snackbar.LENGTH_SHORT).show();
                }
            case R.id.menu_share:
                try
                {
                    Intent intent=new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String shareBody="Hey! \nLooking for an app to convert your images to Pdf with a single click? Check out  the PDF Karo app now! "
                        + "http://play.google.com/store/apps/details?id="+getPackageName();
                String shareSub="Pdf Karo";
                intent.putExtra(Intent.EXTRA_SUBJECT,shareSub);
                intent.putExtra(Intent.EXTRA_TEXT,shareBody);
                startActivity(Intent.createChooser(intent,"Share Using"));
                }
                catch (Exception e)
                {
                    Snackbar.make(main_layout,"Some error occured, Please try again later.",Snackbar.LENGTH_SHORT).show();
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void sendName(String name,String uri) {

        homeFragment.addReceivedName(name,uri);
    }
}
