package com.roshanadke.materialwallet.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.roshanadke.materialwallet.R;
import com.roshanadke.materialwallet.model.UserInformation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateProfile extends AppCompatActivity {

    private EditText edt_name, edt_email, edt_mobile;
    private RadioGroup radioGroup;
    private MaterialRadioButton radioMale, radioFemale;

    private UserInformation userInfo;
    private String uId;
    private static final int CAMERA_IMAGE_REQUEST = 2;
    private static final int PICK_IMAGE_REQUEST = 1;
    byte[] byteImg;
    Uri uriProfileImg;
    int flag = 0;
    private StorageReference profileImageRef;
    private String profileImageurl;

    private Button btnUpdate;
    private CircleImageView imageView;
    AlertDialog.Builder builder;
    private ProgressBar progressBar;

    private FirebaseFirestore db;

    private String str_name, str_email, str_mobile, str_gender, str_birth, strDid, strUrl;
    String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initView();
        getUserInformation();


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = group.findViewById(checkedId);
                if (rb != null) {
                    str_gender = rb.getText().toString();
                }
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag == 1) {
                    saveProfilePhoto();
                } else {
                    updateProfileInfo();
                }
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImageDialog();
            }
        });

    }


    private void updateProfileInfo() {
        progressBar.setVisibility(View.VISIBLE);
        DocumentReference docRef = db.collection("users").document(strDid);
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", edt_name.getText().toString().trim());
        updates.put("email", edt_email.getText().toString());
        updates.put("mobile", edt_mobile.getText().toString());
        updates.put("gender", str_gender);
        if (profileImageurl != null) updates.put("url", profileImageurl);

        docRef.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(UpdateProfile.this, "User Updated", Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(UpdateProfile.this, "Failed to update!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setData() {
        edt_name.setText(str_name);
        edt_email.setText(str_email);
        edt_mobile.setText(str_mobile);

        if (str_gender.equals("Male")) {
            radioMale.setChecked(true);
        } else {
            radioFemale.setChecked(true);
        }
        progressBar.setVisibility(View.GONE);
    }

    private void initView() {
        uId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        db = FirebaseFirestore.getInstance();
        edt_name = findViewById(R.id.edt_name);
        edt_email = findViewById(R.id.edt_email);
        edt_mobile = findViewById(R.id.edt_mobile);
        imageView = findViewById(R.id.profile_image);

        progressBar = findViewById(R.id.progressBar);

        radioGroup = findViewById(R.id.radio_group);
        radioMale = findViewById(R.id.radio1);
        radioFemale = findViewById(R.id.radio2);

        btnUpdate = findViewById(R.id.btn_update_profile);

    }

    private void loadImage() {
        if (strUrl != null) {
            Glide.with(this)
                    .load(strUrl)
                    .placeholder(R.drawable.user_icon_2)
                    .into(imageView);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    private void saveProfilePhoto() {
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
                                    updateProfileInfo();
                                }
                            });

                }
            });
        }
    }

    private void getUserInformation() {
        progressBar.setVisibility(View.VISIBLE);
        CollectionReference collectionReference = db.collection("users");

        Query userQuery = collectionReference.whereEqualTo("uid", uId);

        userQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                        userInfo = documentSnapshot.toObject(UserInformation.class);
                        Log.d("tag", "User Data: " + userInfo.getName() + userInfo.getEmail());
                        displayUserDetails();
                    }
                } else {
                    Log.d("tag", "Query failed ");
                }
            }
        });
    }

    private void displayUserDetails() {
        str_name = userInfo.getName();
        str_email = userInfo.getEmail();
        str_mobile = userInfo.getMobile();
        str_gender = userInfo.getGender();
        str_birth = userInfo.getDob();
        strDid = userInfo.getDoc_id();
        strUrl = userInfo.getUrl();
        Log.d("tag", "onCreate: " + str_gender);
        loadImage();
        setData();

    }

    private void chooseImageDialog() {
        String[] array = {"Take Picture", "Choose Existing"};
        builder = new AlertDialog.Builder(this);

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
        /*Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
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
            byteImg = getBitmapAsByteArray(imageBitmap);
            if (imageBitmap != null) {
                imageView.setImageBitmap(imageBitmap);
            }
            flag = 1;
        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data.getData() != null) {

            uriProfileImg = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImg);
                byteImg = getBitmapAsByteArray(bitmap);
                imageView.setBackground(null);
                imageView.setImageBitmap(bitmap);
                flag = 1;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
        return outputStream.toByteArray();
    }

}
