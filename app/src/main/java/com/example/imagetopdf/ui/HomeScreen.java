package com.example.imagetopdf.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.imagetopdf.Adapters.ImageAdapter;
import com.example.imagetopdf.Adapters.OnChangePic;
import com.example.imagetopdf.BuildConfig;
import com.example.imagetopdf.Model.PdfModel;
import com.example.imagetopdf.R;
import com.example.imagetopdf.Utils.Constants;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class HomeScreen extends AppCompatActivity implements OnChangePic, Serializable {
    private Button addFileBtn;
    private List<Uri> uris;
    ;
    private ImageAdapter adapter;
    private RecyclerView recyclerView;


    String currentPhotoPath = "";
    Uri path;
    ImageView addGallery;
    List<Uri> cropUris;

    ImageView createPdf;
    int positionOfCrop;
    List<String> pdfs;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        addFileBtn = findViewById(R.id.addFileBtn);
        addGallery = findViewById(R.id.add_gallery);
        progressDialog=new ProgressDialog(this);
        uris = new ArrayList<>();
        cropUris = new ArrayList<>();
        pdfs = new ArrayList<>();
        createPdf = findViewById(R.id.createPdfBtn);
        sharedPreferences=getSharedPreferences(Constants.SHARED_PREFS,Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
        createPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ActivityCompat.checkSelfPermission(HomeScreen.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(HomeScreen.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.WRITE_GALLERY);
                } else {
                    if(uris.size()>0) {
                        showDialog();
                        createPdf();
                    }
                    else {
                        Toast.makeText(HomeScreen.this,"Please Add Images",Toast.LENGTH_SHORT).show();
                    }
                }
            }

        });
        addGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ActivityCompat.checkSelfPermission(HomeScreen.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(HomeScreen.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.OPENGALLERY);

                } else {
                    Log.i("Click", "Gallery clicked");
                    openGallery();
                }

            }
        });

        ImageView imageView = findViewById(R.id.addImage);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    if (ActivityCompat.checkSelfPermission(HomeScreen.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(HomeScreen.this, new String[]{Manifest.permission.CAMERA}, Constants.PICK_IMAGE);

                    } else
                        openCamera();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
//        uris.clear();
        adapter = new ImageAdapter(this, uris, new ImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(int position, View v) {
                if (v instanceof ImageView) {
                    startCrop(uris.get(position), Constants.CHANGE_PIC);
                    positionOfCrop = position;
                }
            }

        });
        recyclerView = findViewById(R.id.recycler_view);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPositon = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                Collections.swap(uris, fromPositon, toPosition);
                adapter.notifyItemMoved(fromPositon, toPosition);
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);
//        LinearLayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        GridLayoutManager manager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        addFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSource();
            }
        });
    }
    private void saveToDirectory(PdfDocument document)  {
           dismissDialog();
                File filePath= this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File dir=new File(filePath.getAbsolutePath()+"/Image2Pdf");
        if(!dir.exists())
        {
            dir.mkdir();
        }
        String filename=System.currentTimeMillis()+".pdf";
        Log.i("HomePath",dir.toString()+filename.toString());
        File file=new File(dir,filename);
        try {

            OutputStream outputStream=new FileOutputStream(file);
            document.writeTo(outputStream);
            Toast.makeText(getApplicationContext(),"Pdf saved!",Toast.LENGTH_SHORT).show();
            outputStream.flush();

            Intent intent=new Intent(this,PdfLists.class);
            editor.putString(Constants.LIST_KEY,filename).apply();
            editor.commit();
            startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }


    }
    private void showDialog()
    {
        progressDialog.show();
        progressDialog.setMessage("Please wait...");
    }
    private void dismissDialog()
    {
        progressDialog.dismiss();
    }

    private void createPdf()  {

        try {
                PdfDocument document = new PdfDocument();
                for (int i = 0; i < uris.size(); i++)
                {
                    WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                    Display display = wm.getDefaultDisplay();
                    DisplayMetrics displaymetrics = new DisplayMetrics();
                    this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                    float height = displaymetrics.heightPixels ;
                    float width = displaymetrics.widthPixels ;
                    int convertHeight = (int) height, convertWidth = (int) width;
                    Bitmap sample = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(uris.get(i)));
                    PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 1).create();
                    PdfDocument.Page page = document.startPage(pageInfo);
                    Canvas canvas = page.getCanvas();
                    Paint paint = new Paint();
                    canvas.drawPaint(paint);
                    Bitmap bitmap=Bitmap.createScaledBitmap(sample,convertWidth,convertHeight,true);
                    paint.setColor(Color.BLUE);
                    canvas.drawBitmap(bitmap,20,20,null);
                    //Paint paint=new Paint();

                    document.finishPage(page);
                }
            saveToDirectory(document);
                document.close();

                Log.i("pdf", document.toString());
//            PdfModel model=new PdfModel(document);
//            Intent intent=new Intent(this,PdfLists.class);
//            intent.putExtra(Constants.LIST_KEY,model);
//            startActivity(intent);
        }
        catch (Exception e) {
            dismissDialog();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.i("error", e.getMessage());
            }


    }

    public interface FragmentListener {
        void onInputSend(List<String> StringUris);
    }


    @SuppressLint("IntentReset")
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        String[] mimetypes = {"image/jpg", "image/png", "image/jpeg"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, Constants.OPENGALLERY);

    }

    private void openCamera() throws IOException {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = getImageFile();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) // 2
            path = FileProvider.getUriForFile(HomeScreen.this, BuildConfig.APPLICATION_ID.concat(".provider"), file);
        else
            path = Uri.fromFile(file); // 3
        intent.putExtra(MediaStore.EXTRA_OUTPUT, path); // 4
        startActivityForResult(intent, Constants.PICK_IMAGE);
    }

    private File getImageFile() throws IOException {

        String imageFileName = "JPEG_" + System.currentTimeMillis() + "_";
//        File storageDir = new File(
//                Environment.getExternalStoragePublicDirectory(
//                        Environment.DIRECTORY_DCIM
//                ), "Camera"
//        );
        File file = File.createTempFile(
                imageFileName, ".jpg", getApplicationContext().getCacheDir()
        );
        currentPhotoPath = "file:" + file.getAbsolutePath();
        return file;
    }


    private void openSource() {
//dialog.dismiss();
        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.CROP_CAMERA) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                try {
                    Uri imageUri = result.getUri();
                    uris.add(imageUri);
                    cropUris.add(imageUri);
                    InputStream input = HomeScreen.this.getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(input);
                    Log.i("imageuri", imageUri.toString());
//                    bitmaps.add(bitmap);
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception exception = result.getError();
                Toast.makeText(this, "Possible Error " + exception, Toast.LENGTH_SHORT).show();

            }
        } else if (requestCode == Constants.PICK_IMAGE && resultCode == RESULT_OK) {
//            Uri uri=data.getData();
            Uri uri = Uri.parse(currentPhotoPath);

            startCrop(uri, Constants.CROP_CAMERA);

        } else if (requestCode == Constants.OPENGALLERY && resultCode == RESULT_OK) {
            if (data != null) {

                    try {
                        Uri uri = data.getData();
                        startCrop(uri,Constants.CROP_CAMERA);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

        } else if (requestCode == Constants.CHANGE_PIC) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                Uri imageUri = result.getUri();
                cropUris.add(imageUri);
                Log.i("cropped uri", imageUri.toString());
                uris.set(positionOfCrop, imageUri);
                adapter.notifyItemChanged(positionOfCrop);
                adapter.notifyDataSetChanged();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception exception = result.getError();
                Toast.makeText(this, "Possible Error " + exception, Toast.LENGTH_SHORT).show();

            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.PICK_IMAGE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            {
                try {
                    openCamera();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (requestCode == Constants.OPENGALLERY && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        }
        if (requestCode == Constants.WRITE_GALLERY && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            createPdf();
        }
    }

    @Override
    public void startCrop(Uri uri, int requestcode) {
        Intent intent = CropImage.activity(uri).setGuidelines(CropImageView.Guidelines.ON)
                .setActivityTitle("Crop")
                .getIntent(this);
        startActivityForResult(intent, requestcode);
    }
}
