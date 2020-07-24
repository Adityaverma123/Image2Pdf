package com.example.imagetopdf.Fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
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
import com.example.imagetopdf.BuildConfig;
import com.example.imagetopdf.R;
import com.google.android.material.snackbar.Snackbar;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.yalantis.ucrop.UCrop;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class AddFileFragment extends Fragment {

   private Button addFileBtn;
    private Context context;
    private List<Uri>uris=new ArrayList<>();;
    private ImageAdapter adapter;
    private RecyclerView recyclerView;
    private Dialog dialog;
    private  Uri file;
    public static  final int PICK_IMAGE=1;
    public static final int PIC_CROP=2;
    String currentPhotoPath = "";
    Uri path;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_add_file, container, false);
        addFileBtn=view.findViewById(R.id.addFileBtn);
        context=getActivity();

        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},2);

        }
        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, PICK_IMAGE);

        }
        ImageView imageView=view.findViewById(R.id.addImage);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, PICK_IMAGE);
                        return;
                    }
                    else
                    openCamera();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
//        uris.clear();
        adapter=new ImageAdapter(context,uris);
        recyclerView=view.findViewById(R.id.recycler_view);
        LinearLayoutManager manager=new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);

        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        addFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSource();
            }
        });

        return view;
    }

    private void openCamera() throws IOException {
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file=getImageFile();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) // 2
            path = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID.concat(".provider"), file);
        else
            path = Uri.fromFile(file); // 3
        intent.putExtra(MediaStore.EXTRA_OUTPUT, path); // 4
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivityForResult(intent, PICK_IMAGE);
    }

    private File getImageFile() throws IOException {

        String imageFileName = "JPEG_" + System.currentTimeMillis() + "_";
//        File storageDir = new File(
//                Environment.getExternalStoragePublicDirectory(
//                        Environment.DIRECTORY_DCIM
//                ), "Camera"
//        );
        File file = File.createTempFile(
                imageFileName, ".jpg", getContext().getCacheDir()
        );
        currentPhotoPath = "file:" + file.getAbsolutePath();
        return file;
    }


    private void openSource() {
//dialog.dismiss();
            CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(getContext(),this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK) {

                try {
                    Uri imageUri = result.getUri();

                    InputStream input = getContext().getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap=BitmapFactory.decodeStream(input);
                    Log.i("imageuri", imageUri.toString());
                    uris.add(imageUri);
                    adapter.notifyDataSetChanged();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            else if(resultCode==CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception exception=result.getError();
                Toast.makeText(context,"Possible Error "+exception,Toast.LENGTH_SHORT).show();

            }
        }
        else if(requestCode==PICK_IMAGE && resultCode==RESULT_OK)
        {
//            Uri uri=data.getData();
//            startCrop(path);
                Uri uri=Uri.parse(currentPhotoPath);
            startCrop(uri,uri);

        }
        else if(requestCode==UCrop.REQUEST_CROP && resultCode==RESULT_OK)
        {
            Uri resultUri=UCrop.getOutput(data);
            Log.i("resultUri",resultUri.toString());
           showImage(resultUri);
        }
        else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
    }
    }

    private void showImage(Uri resultUri) {
        try {
            File file= new File(resultUri.getPath());
            InputStream inputStream = new FileInputStream(file);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            uris.add(resultUri);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        }

    private void startCrop(Uri uri,Uri destination)
    {
//        CropImage.activity(uri).setGuidelines(CropImageView.Guidelines.ON)
//                .setAspectRatio(1,1)
//                .start(context,this);
        UCrop.of(uri,destination)
                .withMaxResultSize(450,450)

                .start(getContext(),this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==PICK_IMAGE && grantResults[0]!=PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, PICK_IMAGE);

        }
        else {
            try {
                openCamera();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
