package com.PDFKaro.imagetopdf.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.PDFKaro.imagetopdf.Adapters.ImageAdapter;
import com.PDFKaro.imagetopdf.BuildConfig;
import com.PDFKaro.imagetopdf.Interface.OnChangePic;
import com.PDFKaro.imagetopdf.Interface.RefreshList;
import com.PDFKaro.imagetopdf.Interface.Visibility;
import com.PDFKaro.imagetopdf.R;
import com.PDFKaro.imagetopdf.Utils.Constants;
import com.PDFKaro.imagetopdf.ui.AddImagesActivity;
import com.google.android.material.snackbar.Snackbar;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import hotchemi.android.rate.AppRate;

import static android.app.Activity.RESULT_OK;

public class AddImageFragment extends Fragment implements Visibility, OnChangePic {
    private List<Uri> uris;
    private ImageAdapter adapter;
    private RecyclerView recyclerView;
    String currentPhotoPath = "";
    Uri path;
    ImageView createPdf;
    int positionOfCrop;
    Dialog dialog;
    ConstraintLayout parent;
    ImageView add_image;
    Button cancel;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ImageView backbtn;
    List<String>finalUri;
    @SuppressLint("HandlerLeak")
    private int fromPos = -1;
    private int toPos = -1;
    Context context;
    Activity activity;
    RefreshList refreshList;
    public AddImageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view= inflater.inflate(R.layout.fragment_add_image, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Add Images");

        context=getContext();
        activity=getActivity();
        finalUri=new ArrayList<>();
        parent = view.findViewById(R.id.parent);
        add_image = view.findViewById(R.id.add_image);
        uris = new ArrayList<>();
        createPdf = view.findViewById(R.id.createPdfBtn);
        sharedPreferences=context.getSharedPreferences("Home2List",Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
        createPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT > 23) {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.WRITE_GALLERY);
                    } else {

                       CreatePdfThread thread = new CreatePdfThread();
                        thread.setPriority(Thread.MAX_PRIORITY);
                        thread.start();
                        AppRate.with(context).setInstallDays(1)
                                .setLaunchTimes(3)
                                .setRemindInterval(2)
                                .monitor();
                        AppRate.showRateDialogIfMeetsConditions(activity);
                    }
                }
                else {
                    CreatePdfThread thread = new CreatePdfThread();
                    thread.setPriority(Thread.MAX_PRIORITY);
                    thread.start();
                    AppRate.with(context).setInstallDays(1)
                            .setLaunchTimes(3)
                            .setRemindInterval(2)
                            .monitor();
                    AppRate.showRateDialogIfMeetsConditions(activity);
                }
            }

        });
        showDialog();
        add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();

            }
        });

        adapter = new ImageAdapter(context, uris,finalUri, new ImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(int position, View v) {
                if (v instanceof ImageView) {
                    startCrop(uris.get(position), Constants.CHANGE_PIC);
                    positionOfCrop = position;
                }
            }

        }, this);
        recyclerView = view.findViewById(R.id.recycler_view);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                toPos = target.getAdapterPosition();
                return false;
            }

            @Override
            public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
                switch (actionState) {
                    case ItemTouchHelper.ACTION_STATE_DRAG: {
                        fromPos = viewHolder.getAdapterPosition();
                        break;
                    }

                    case ItemTouchHelper.ACTION_STATE_IDLE: {
                        if (fromPos != -1 && toPos != -1
                                && fromPos != toPos) {
                            moveItem(fromPos, toPos);
                            fromPos = -1;
                            toPos = -1;
                        }
                        break;
                    }
                }
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }

            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG, ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.START | ItemTouchHelper.END);
            }

        });
        itemTouchHelper.attachToRecyclerView(recyclerView);
        GridLayoutManager manager = new GridLayoutManager(context, 3);
        recyclerView.setLayoutManager(manager);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
        //recyclerView.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setAdapter(adapter);

        return view;
    }


    private void moveItem(int oldPos, int newPos) {
        Uri temp = uris.get(oldPos);
        String temp1=finalUri.get(oldPos);
        uris.set(oldPos, uris.get(newPos));
        finalUri.set(oldPos,finalUri.get(newPos));
        uris.set(newPos, temp);
        finalUri.set(newPos,temp1);
        adapter.notifyItemChanged(oldPos);
        adapter.notifyItemChanged(newPos);
    }

    @Override
    public void setVisibility(Boolean b) {
        if (b) {
            createPdf.setVisibility(View.VISIBLE);
        } else createPdf.setVisibility(View.GONE);
    }

    private class CreatePdfThread extends Thread{
        @Override
        public void run() {

            try {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

                File filePath = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
                File dir = new File(filePath.getAbsolutePath() + "/PDFKaro");
                //File dir1=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/PDFKaro");
                if (!dir.exists()) {
                    dir.mkdir();
                }
//                if(!dir1.exists())
//                {
//                    dir1.mkdir();
//                }
                String  filename = System.currentTimeMillis() + ".pdf";
                Document document1 = new Document();
                PdfWriter.getInstance(document1, new FileOutputStream(dir + "/" + filename));
               // PdfWriter.getInstance(document1,new FileOutputStream(dir1+"/"+filename));
                document1.open();
                for (int j = 0; j < finalUri.size(); j++) {
                    int quality=30;
                    double qualitymode=quality*0.9;
                    Image image=Image.getInstance(String.valueOf(finalUri.get(j)));
                    image.setCompressionLevel((int) qualitymode);
                    image.setBorder(Rectangle.BOX);
                    image.setBorderWidth(0);
                    float scaler = ((document1.getPageSize().getWidth() - document1.leftMargin()
                            - document1.rightMargin() - 0) / image.getWidth()) * 100;
                    image.scalePercent(scaler);
                    image.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);
                    document1.add(image);
                    document1.newPage();

                }
                Message message=Message.obtain();
                message.obj=filename;
                message.setTarget(handler);
                message.sendToTarget();
                document1.close();
                Log.i("pdf", document1.toString());

            } catch (Exception e) {
                Log.i("error", e.getMessage());

            }
        }
        @SuppressLint("HandlerLeak")
        private Handler handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                Log.i("finaluri",finalUri.get(0));
                final String filename=(String)msg.obj;
                refreshList.sendName(filename,uris.get(0).toString(),finalUri.get(0));

                Snackbar.make(parent,"Pdf saved",Snackbar.LENGTH_LONG).setAction("Open",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                openPdf(filename);
                            }
                        }).show();
                uris.clear();
                finalUri.clear();
                adapter.notifyDataSetChanged();
            }
        };


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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (uris.size() != 0) {
            uris.clear();
        }
    }

    private void openPdf(String filename) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        File file = getImageFile(filename);
        Uri path;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            path = FileProvider.getUriForFile(context, "com.PDFKaro.imagetopdf.Utils.FileProvider", file);
        else
            path = Uri.fromFile(file);
        MimeTypeMap map = MimeTypeMap.getSingleton();
        String ext = MimeTypeMap.getFileExtensionFromUrl(file.getName());
        String type = map.getMimeTypeFromExtension(ext);
        intent.setDataAndType(path, type);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, path);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No Application available to view pdf", Toast.LENGTH_LONG).show();
        }
    }

    @SuppressLint("IntentReset")
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        String[] mimetypes = {"image/jpg", "image/png", "image/jpeg"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, Constants.OPENGALLERY);

    }

    private File getImageFile(String filename) {
        File filePath = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File dir = new File(filePath.getAbsolutePath() + "/PDFKaro");
        File file = new File(dir, filename);
        return file;
    }

    public void showDialog() {

        dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.select_image_dialog);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
        wlp.windowAnimations = R.style.DialogAnimation;
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setAttributes(wlp);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        LinearLayout camera = dialog.findViewById(R.id.camera);
        LinearLayout gallery = dialog.findViewById(R.id.galley);
        cancel = dialog.findViewById(R.id.cancelBtn);
        dialog.show();
        createPdf.setVisibility(View.GONE);
        add_image.setVisibility(View.GONE);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                add_image.setVisibility(View.VISIBLE);
                try {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                       requestPermissions( new String[]{Manifest.permission.CAMERA}, Constants.PICK_IMAGE);

                    } else {

                        openCamera();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions( new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.OPENGALLERY);

                } else {

                    add_image.setVisibility(View.VISIBLE);
                    Log.i("Click", "Gallery clicked");
                    openGallery();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                add_image.setVisibility(View.VISIBLE);
                dialog.dismiss();
            }
        });

    }

    private File getImageFile() throws IOException {

        String imageFileName = "JPEG_" + System.currentTimeMillis() + "_";
        File file = File.createTempFile(
                imageFileName, ".jpg", context.getCacheDir()
        );
        currentPhotoPath = "file:" + file.getAbsolutePath();
        return file;
    }

    private void openCamera() throws IOException {
        dialog.dismiss();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = getImageFile();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            path = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID.concat(".provider"), file);
        else
            path = Uri.fromFile(file); // 3
        intent.putExtra(MediaStore.EXTRA_OUTPUT, path);
        startActivityForResult(intent, Constants.PICK_IMAGE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dialog.cancel();
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
         CreatePdfThread thread = new CreatePdfThread();
            thread.start();
        }
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
                    finalUri.add(String.valueOf(imageUri));
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
            Uri uri = Uri.parse(currentPhotoPath);

            startCrop(uri, Constants.CROP_CAMERA);

        } else if (requestCode == Constants.OPENGALLERY && resultCode == RESULT_OK) {
            ClipData clipData=data.getClipData();
            if(clipData!=null)
            {
                int count=clipData.getItemCount();
                for(int i=0;i<count;i++)
                {
                    Uri uri=clipData.getItemAt(i).getUri();
                    uris.add(uri);
                    finalUri.add(getPath(uri));

                }
            }
            else {
                Uri uri=data.getData();
                finalUri.add(getPath(uri));

                uris.add(uri);
            }
            Log.i("uris",uris.toString());
            adapter.notifyDataSetChanged();


        } else if (requestCode == Constants.CHANGE_PIC) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                Uri imageUri = result.getUri();
                Log.i("cropped uri", imageUri.toString());
                uris.set(positionOfCrop, imageUri);
                finalUri.set(positionOfCrop,imageUri.toString());
                adapter.notifyItemChanged(positionOfCrop);
                adapter.notifyDataSetChanged();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception exception = result.getError();
                Toast.makeText(context, "Possible Error " + exception, Toast.LENGTH_SHORT).show();

            }
        }
    }
    private String getPath(Uri uri) {
        String[]  data = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(context, uri, data, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    @Override
    public void startCrop(Uri uri, int requestcode) {
        Intent intent = CropImage.activity(uri).setGuidelines(CropImageView.Guidelines.ON)
                .getIntent(context);
        startActivityForResult(intent, requestcode);
    }
}
