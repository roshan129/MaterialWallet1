package com.roshanadke.materialwallet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestFutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.roshanadke.materialwallet.activities.UpdateProfile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.roshanadke.materialwallet.activities.UpdateProfile.getBitmapAsByteArray;

public class DisplayImageActivity extends AppCompatActivity {

    ImageView imageView;
    ProgressBar progressBar;
    int compressFlag = 0;
    String url, strDid;
    Uri uriProfileImg;
    byte[] byteImg;
    private StorageReference profileImageRef;
    private String profileImageurl;
    int flag = 0;
    private static final int CAMERA_IMAGE_REQUEST = 2;
    private static final int PICK_IMAGE_REQUEST = 1;
    String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dsiplay_image);
        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.black, this.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }

        initView();

        Glide.with(this).load(url)
                .placeholder(R.drawable.user_icon_2)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(imageView);


    }

    private void initView() {
        imageView = findViewById(R.id.image_expanded_profile);
        strDid = getIntent().getStringExtra("did");
        profileImageRef = FirebaseStorage.getInstance()
                .getReference();
        progressBar = findViewById(R.id.progressBar);
        url = getIntent().getStringExtra("url");

    }

    private void chooseImageDialog() {
        String[] array = {"Take Picture", "Choose Existing"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Profile Picture");
        builder.setItems(array, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {

                    case 0:
                        chooseFromCamera();
                        break;
                    case 1:
                        pickFromGallery();
                        break;
                    default:
                        break;
                }
            }
        });
        builder.show();
    }

    private void chooseFromCamera() {
       /* Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, CAMERA_IMAGE_REQUEST);
        }*/
        dispatchTakePictureIntent();

    }

    private void pickFromGallery() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture: "), PICK_IMAGE_REQUEST);
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.roshanadke.materialwallet",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_IMAGE_REQUEST);
            }
        }
    }


    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
            uriProfileImg = Uri.parse("not null");
            File file = new File(mCurrentPhotoPath);
            Bitmap imageBitmap = null;
            try {
                imageBitmap = MediaStore.Images.Media
                        .getBitmap(this.getContentResolver(), Uri.fromFile(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
            compressFlag = 1;
            byteImg = getBitmapAsByteArray(imageBitmap);
            if (imageBitmap != null) {
                imageView.setImageBitmap(imageBitmap);
            }
            flag = 1;
            updateProfilePhoto();
        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data.getData() != null) {
            uriProfileImg = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImg);
                byteImg = getBitmapAsByteArray(bitmap);
                // Log.d(TAG, String.valueOf(bitmap));
                imageView.setBackground(null);
                imageView.setImageBitmap(bitmap);
                flag = 1;
                updateProfilePhoto();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateProfilePhoto() {
        progressBar.setVisibility(View.VISIBLE);
        profileImageRef = FirebaseStorage.getInstance()
                .getReference();
        final StorageReference storageRef = profileImageRef
                .child("profilepics/" + System.currentTimeMillis() + " .jpg");

        if (uriProfileImg != null) {

            storageRef.putBytes(byteImg).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            profileImageurl = uri.toString();
                            Log.d("UpdatePro", "onSuccess: first");
                            Log.d("UpdatePro", "Image url: " + profileImageurl);
                            flag = 0;
                            updateProfileInfo();

                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressBar.setVisibility(View.GONE);
                                    Log.d("UpdatePro", "onFailure: " + e.getMessage());
                                    flag = 0;
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(DisplayImageActivity.this, "Poor Internet Connection", Toast.LENGTH_SHORT).show();
                                }
                            });

                }
            });
        }
    }


    private void updateProfileInfo() {
        progressBar.setVisibility(View.VISIBLE);
        DocumentReference docRef = FirebaseFirestore.getInstance()
                .collection("users").document(strDid);
        Map<String, Object> updates = new HashMap<>();
        if (profileImageurl != null) updates.put("url", profileImageurl);

        docRef.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(DisplayImageActivity.this, "Image Updated", Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(DisplayImageActivity.this, "Failed to update!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_edit_profile) {
            chooseImageDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    public byte[] getBitmapAsByteArray(Bitmap bitmap) {

        if (compressFlag == 1) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
            return outputStream.toByteArray();
        } else {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
            return outputStream.toByteArray();
        }
    }


}
