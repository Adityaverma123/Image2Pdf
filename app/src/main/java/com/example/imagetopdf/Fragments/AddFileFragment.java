package com.example.imagetopdf.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.imagetopdf.Adapters.ImageAdapter;
import com.example.imagetopdf.Adapters.OnChangePic;
import com.example.imagetopdf.BuildConfig;
import com.example.imagetopdf.R;
import com.example.imagetopdf.Utils.Constants;
import com.example.imagetopdf.ui.HomeScreen;
import com.example.imagetopdf.ui.PdfLists;
import com.google.android.material.snackbar.Snackbar;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class AddFileFragment extends Fragment implements OnChangePic {
    private List<Uri> uris;
    private ImageAdapter adapter;
    private RecyclerView recyclerView;
    RefreshList refreshList;

    String currentPhotoPath = "";
    Uri path;
    ImageView addGallery;
    List<Uri> cropUris;
    Button button;
    ImageView createPdf;
    int positionOfCrop;
    List<String> pdfs;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Dialog dialog;
    FrameLayout parent;
    Context context;
    Activity activity;
    ImageView add_image;
    ProgressBar progressBar;
    public AddFileFragment(Context context)
    {
        this.context=context;
    }
    @SuppressLint("CommitPrefEdits")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_add_file, container, false);
        activity = getActivity();
        parent=view.findViewById(R.id.parent);
        add_image=view.findViewById(R.id.add_image);
        uris = new ArrayList<>();
        cropUris = new ArrayList<>();
        pdfs = new ArrayList<>();
        progressBar=view.findViewById(R.id.progress_bar);
        createPdf = view.findViewById(R.id.createPdfBtn);
        sharedPreferences=context.getSharedPreferences("home2List",Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
        createPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.WRITE_GALLERY);
                } else {
                    if(uris.size()>0) {
                        createPdf();
                    }
                    else {
                        Toast.makeText(context,"Please Add Images",Toast.LENGTH_SHORT).show();
                    }
                }
            }

        });
        add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    showDialog();

            }
        });


        adapter = new ImageAdapter(context, uris, new ImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(int position, View v) {
                if (v instanceof ImageView) {
                    startCrop(uris.get(position), Constants.CHANGE_PIC);
                    positionOfCrop = position;
                }
            }

        });
        recyclerView = view.findViewById(R.id.recycler_view);
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
        GridLayoutManager manager = new GridLayoutManager(context, 3);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        return view;
    }
    private void createPdf()  {

        progressBar.setVisibility(View.VISIBLE);

        try {
            final String filename;
            File filePath= context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            File dir=new File(filePath.getAbsolutePath()+"/Image2Pdf");
            if(!dir.exists())
            {
                dir.mkdir();
            }
            filename=System.currentTimeMillis()+".pdf";
//            PdfDocument document = new PdfDocument();
            Document document1=new Document();
            PdfWriter.getInstance(document1,new FileOutputStream(dir+"/"+filename));
            document1.open();
            for (int i = 0; i < uris.size(); i++)
            {
               document1.newPage();
                Bitmap sample = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uris.get(i)));
                ByteArrayOutputStream stream=new ByteArrayOutputStream();
                sample.compress(Bitmap.CompressFormat.PNG,100,stream);
                Image image=Image.getInstance(stream.toByteArray());
                float scaler = ((document1.getPageSize().getWidth() - document1.leftMargin()
                        - document1.rightMargin() - 0) / image.getWidth()) * 100;
                image.scalePercent(scaler);
                image.setAlignment(Image.ALIGN_CENTER|Image.ALIGN_TOP);
                document1.add(image);
            }

            document1.close();
            refreshList.sendName(filename);
            Snackbar.make(parent,"Pdf saved",Snackbar.LENGTH_LONG).setAction("Open",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openPdf(filename);
                        }
                    }).show();
            uris.clear();
            adapter.notifyDataSetChanged();

            Log.i("pdf", document1.toString());

        }
        catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.i("error", e.getMessage());
        }
        progressBar.setVisibility(View.GONE);


    }
    private void saveToDirectory(Document document)  {
        final String filename;
        File filePath= context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File dir=new File(filePath.getAbsolutePath()+"/Image2Pdf");
        if(!dir.exists())
        {
            dir.mkdir();
        }
        filename=System.currentTimeMillis()+".pdf";
        Log.i("HomePath",dir.toString()+filename.toString());
        File file=new File(dir,filename);
        try {
            PdfWriter.getInstance(document,new FileOutputStream(dir+"/"+filename));
            Snackbar.make(parent,"Pdf saved",Snackbar.LENGTH_LONG).setAction("Open",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openPdf(filename);
                        }
                    }).show();
            uris.clear();
            adapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
        }


    }


    private void openPdf(String filename) {
        Intent intent=new Intent(Intent.ACTION_VIEW);
        File file=getImageFile(filename);
        Uri path;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            path = FileProvider.getUriForFile(context, "com.example.imagetopdf.Utils.FileProvider", file);
        else
            path = Uri.fromFile(file);
        MimeTypeMap map = MimeTypeMap.getSingleton();
        String ext = MimeTypeMap.getFileExtensionFromUrl(file.getName());
        String type = map.getMimeTypeFromExtension(ext);
        intent.setDataAndType(path,type);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, path);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try
        {
            startActivity(intent);
        }
        catch(ActivityNotFoundException e)
        {
            Toast.makeText(context, "No Application available to view pdf", Toast.LENGTH_LONG).show();
        }
    }
    private File getImageFile(String filename) {
        File filePath= context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File dir=new File(filePath.getAbsolutePath()+"/Image2Pdf");
        File file=new File(dir,filename);
        return file;
    }

    private void showDialog()
    {
        dialog=new Dialog(context);
        dialog.setContentView(R.layout.select_image_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        ImageView camera=dialog.findViewById(R.id.camera);
        ImageView  gallery=dialog.findViewById(R.id.galley);
        dialog.show();
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, Constants.PICK_IMAGE);

                    } else
                        openCamera();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.OPENGALLERY);

                } else {
                    Log.i("Click", "Gallery clicked");
                    openGallery();
                }
            }
        });

    }




    public interface RefreshList {
        void sendName(String name);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            refreshList = (RefreshList) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Error in retrieving data. Please try again");
        }
    }


    @SuppressLint("IntentReset")
    private void openGallery() {
        dialog.dismiss();
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        String[] mimetypes = {"image/jpg", "image/png", "image/jpeg"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, Constants.OPENGALLERY);

    }

    private void openCamera() throws IOException {
        dialog.dismiss();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = getImageFile();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) // 2
            path = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID.concat(".provider"), file);
        else
            path = Uri.fromFile(file); // 3
        intent.putExtra(MediaStore.EXTRA_OUTPUT, path); // 4
        startActivityForResult(intent, Constants.PICK_IMAGE);
    }

    private File getImageFile() throws IOException {

        String imageFileName = "JPEG_" + System.currentTimeMillis() + "_";
        File file = File.createTempFile(
                imageFileName, ".jpg", context.getCacheDir()
        );
        currentPhotoPath = "file:" + file.getAbsolutePath();
        return file;
    }


    private void openSource() {

        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(activity);
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
                    InputStream input = context.getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(input);
                    Log.i("imageuri", imageUri.toString());
//                    bitmaps.add(bitmap);
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception exception = result.getError();
                Toast.makeText(context, "Possible Error " + exception, Toast.LENGTH_SHORT).show();

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
                Toast.makeText(context, "Possible Error " + exception, Toast.LENGTH_SHORT).show();

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
                .getIntent(context);
        startActivityForResult(intent, requestcode);
    }

}
