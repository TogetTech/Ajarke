package com.togettech.ajarke.ui.reservation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.togettech.ajarke.R;
import com.togettech.ajarke.ui.agence.adapter.AgenceSpinner;
import com.togettech.ajarke.utils.CameraUtils;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewReservationActivity extends AppCompatActivity {

    // Activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;

    // key to store image path in savedInstance state
    public static final String KEY_IMAGE_STORAGE_PATH = "image_path";

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    // Bitmap sampling size
    public static final int BITMAP_SAMPLE_SIZE = 8;

    // Gallery directory name to store the images or videos
    public static final String GALLERY_DIRECTORY_NAME = "Ajarke";

    // Image and Video file extensions
    public static final String IMAGE_EXTENSION = "jpg";

    private static String imageStoragePath;

    private ImageView view_img_cni;
    private ImageButton btn_capture_cni;

    //**FIREBASE************************************************************************************
    private FirebaseAuth mAuth;

    //**VARIABLES ACTIVITY**************************************************************************
    Spinner agence_spinner_name, agence_spinner_categorie,
            agence_spinner_destination, agence_spinner_bagage;

    EditText voyageur_name;
    TextView date_voyage, voyageur_heure;
    DatePickerDialog picker;
    TimePickerDialog timePickerDialog;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_reservation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //**Initialize Firebase Auth****************************************************************
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        //**End*************************************************************************************

        // Vérification de la disponibilité de la caméra
        if (!CameraUtils.isDeviceSupportCamera(getApplicationContext())) {
            Toast.makeText(getApplicationContext(),
                    "Désolé! Votre appareil ne prend pas en charge la caméra",
                    Toast.LENGTH_LONG).show();
            // fermera l'application si l'appareil n'a pas de caméra
            finish();
        }

        //**Variables*******************************************************************************
        agence_spinner_name = findViewById(R.id.agence_spinner_name);
        agence_spinner_name.setAdapter(new ArrayAdapter<>(this, android.R.layout.select_dialog_item,
                AgenceSpinner.AgenceNames));

        agence_spinner_categorie = findViewById(R.id.agence_spinner_categorie);
        agence_spinner_categorie.setAdapter(new ArrayAdapter<>(this, android.R.layout.select_dialog_item,
                AgenceSpinner.Categorie));

        agence_spinner_destination = findViewById(R.id.agence_spinner_destination);
        agence_spinner_destination.setAdapter(new ArrayAdapter<>(this, android.R.layout.select_dialog_item,
                AgenceSpinner.Destination));

        agence_spinner_bagage = findViewById(R.id.agence_spinner_bagage);
        agence_spinner_bagage.setAdapter(new ArrayAdapter<>(this, android.R.layout.select_dialog_item,
                AgenceSpinner.Bagage));

        //**Date voyage*****************************************************************************
        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());

        date_voyage = findViewById(R.id.date_voyage);
        date_voyage.setText(currentDate);

        date_voyage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(NewReservationActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                date_voyage.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        //**Heure voyage****************************************************************************
        voyageur_heure = findViewById(R.id.voyageur_heure);
        voyageur_heure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                // time picker dialog
                timePickerDialog = new TimePickerDialog(NewReservationActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                voyageur_heure.setText(sHour + ":" + sMinute);
                            }
                        }, hour, minutes, true);
                timePickerDialog.show();
            }
        });

        //******************************************************************************************

        voyageur_name = findViewById(R.id.voyageur_name);
        //Recuperer le d'utilisateurCountryNames
        voyageur_name.setText(user.getDisplayName());
        //******************************************************************************************

        view_img_cni = findViewById(R.id.view_img_cni);
        btn_capture_cni = findViewById(R.id.btn_capture_cni);

        /**
         * Capturer l'image en cliquant sur le bouton
         */
        btn_capture_cni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CameraUtils.checkPermissions(getApplicationContext())) {
                    captureImage();
                } else {
                    requestCameraPermission(MEDIA_TYPE_IMAGE);
                }
            }
        });

        // restauration du chemin de l'image de stockage à partir de l'état d'instance enregistré
        // sinon le chemin sera nul lors de la rotation de l'appareil
        restoreFromBundle(savedInstanceState);
    }

    /**
     * Restauration du chemin de l'image du magasin à partir de l'état d'instance enregistré
     */
    private void restoreFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(KEY_IMAGE_STORAGE_PATH)) {
                imageStoragePath = savedInstanceState.getString(KEY_IMAGE_STORAGE_PATH);
                if (!TextUtils.isEmpty(imageStoragePath)) {
                    if (imageStoragePath.substring(imageStoragePath.lastIndexOf(".")).equals("." + IMAGE_EXTENSION)) {
                        previewCapturedImage();
                    }
                }
            }
        }
    }

    /**
     * Demande d'autorisations à l'aide de la bibliothèque Dexter
     */
    private void requestCameraPermission(final int type) {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

                            if (type == MEDIA_TYPE_IMAGE) {
                                // capture picture
                                captureImage();
                            }

                        } else if (report.isAnyPermissionPermanentlyDenied()) {
                            showPermissionsAlert();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    /**
     * La capture de l'image de la caméra lancera la capture d'image demandée
     * par l'application de la caméra
     */
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = CameraUtils.getOutputMediaFile(MEDIA_TYPE_IMAGE);
        if (file != null) {
            imageStoragePath = file.getAbsolutePath();
        }
        Uri fileUri = CameraUtils.getOutputMediaFileUri(getApplicationContext(), file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // démarrer la capture d'image Intention
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    /**
     * Enregistrement du chemin d'image stocké dans l'état d'instance enregistré
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // enregistrer l'URL du fichier dans le bundle car elle sera nulle
        // lors des changements d'orientation de l'écran
        outState.putString(KEY_IMAGE_STORAGE_PATH, imageStoragePath);
    }

    /**
     * Restauration du chemin de l'image à partir de l'état d'instance enregistré
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // obtenir l'url du fichier
        imageStoragePath = savedInstanceState.getString(KEY_IMAGE_STORAGE_PATH);
    }

    /**
     * La méthode du résultat de l'activité sera appelée après la fermeture de la caméra
     */
    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // si le résultat capture l'image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Rafraîchir la galerie
                CameraUtils.refreshGallery(getApplicationContext(), imageStoragePath);
                // a réussi à capturer l'image
                // l'afficher en vue image
                previewCapturedImage();
            } else if (resultCode == RESULT_CANCELED) {
                // utilisateur a annulé la capture d'image
                Toast.makeText(getApplicationContext(),
                        "Capture d'image annulée par l'utilisateur", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // n'a pas réussi à capturer l'image
                Toast.makeText(getApplicationContext(),
                        "Désolé! Impossible de capturer l'image", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    /**
     * Afficher l'image de la galerie
     */
    private void previewCapturedImage() {
        try {

            view_img_cni.setVisibility(View.VISIBLE);
            btn_capture_cni.setVisibility(View.VISIBLE);
            Bitmap bitmap = CameraUtils.optimizeBitmap(BITMAP_SAMPLE_SIZE, imageStoragePath);

            view_img_cni.setImageBitmap(bitmap);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Boîte de dialogue d'alerte pour accéder aux paramètres
     * de l'application et activer les autorisations nécessaires
     */
    private void showPermissionsAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Autorisations requises!")
                .setMessage("La caméra a besoin de quelques autorisations pour fonctionner correctement. Accordez-les dans les paramètres.")
                .setPositiveButton("ALLER AUX PARAMÈTRES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        CameraUtils.openSettings(NewReservationActivity.this);
                    }
                })
                .setNegativeButton("ANNULER", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }


}
