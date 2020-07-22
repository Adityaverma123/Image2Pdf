package com.example.imagetopdf.Fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.imagetopdf.Adapters.ImageAdapter;
import com.example.imagetopdf.R;
import com.google.android.material.snackbar.Snackbar;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class AddFileFragment extends Fragment {

    private static final int CROP_PIC =3 ;
    private Button addFileBtn;
    private Context context;
    private List<Uri>uris=new ArrayList<>();;
    private ImageAdapter adapter;
    private RecyclerView recyclerView;
    private Dialog dialog;
    private static final int PICK_IMAGE =1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_add_file, container, false);
        addFileBtn=view.findViewById(R.id.addFileBtn);
        context=getActivity();
//        uris.clear();
        adapter=new ImageAdapter(context,uris);
        recyclerView=view.findViewById(R.id.recycler_view);
        LinearLayoutManager manager=new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
        ImageView addImage=view.findViewById(R.id.addImage);

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera();
            }
        });
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        addFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSource();
            }
        });
        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, PICK_IMAGE);

        }
        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},2);

        }
        return view;
    }


    private void openSource() {
//dialog.dismiss();
            CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(getContext(),this);
    }
    private void openCamera()
    {
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.setType("image/*");

        startActivityForResult(intent,PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {

            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK)
            {
                Uri imageUri=result.getUri();
                Log.i("imageuri",imageUri.toString());
                uris.add(imageUri);
                adapter.notifyDataSetChanged();
            }
            else if(resultCode==CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception exception=result.getError();
                Toast.makeText(context,"Possible Error "+exception,Toast.LENGTH_SHORT).show();

            }

        }
        else if(requestCode==PICK_IMAGE && data!=null)
        {

            Uri uri=data.getData();
            launchImageCrop(uri);


        }
        else if (requestCode == CROP_PIC) {
            // get the returned data
            Bundle extras = data.getExtras();
            // get the cropped bitmap
            Bitmap thePic = extras.getParcelable("data");


        }
    }

    private void launchImageCrop(Uri uri) {
        CropImage.activity(uri).setGuidelines(CropImageView.Guidelines.ON)
        .start(getContext(),this);
    }


}
