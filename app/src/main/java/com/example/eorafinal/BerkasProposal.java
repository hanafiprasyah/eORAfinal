package com.example.eorafinal;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.shashank.sony.fancytoastlib.FancyToast;

import net.gotev.uploadservice.MultipartUploadRequest;

import org.json.JSONObject;

import java.util.Objects;
import java.util.UUID;

public class BerkasProposal extends AppCompatActivity {

    ConnectivityManager conMgr;
    TextView tvNIMberkas,back,btnSkip;
    ImageView infoKRS,infoLHS,infoUKT,infoPrestasi;

    //KEPERLUAN UPLOAD FILE
    Button pilihFileKRS, uploadFileKRS, pilihFileLHS, uploadFileLHS, pilihFileUKT, uploadFileUKT, pilihFilePrestasi, uploadFilePrestasi, btOke;
    EditText ETFileNameKRS, ETFileNameLHS, ETFileNameUKT, ETFileNamePrestasi;
    public static final String UPLOAD_KRS           = "https://prasyah.000webhostapp.com/BerkasPDF/uploadKRS.php";
    public static final String UPLOAD_LHS           = "https://prasyah.000webhostapp.com/BerkasPDF/uploadLHS.php";
    public static final String UPLOAD_UKT           = "https://prasyah.000webhostapp.com/BerkasPDF/uploadUKT.php";
    public static final String UPLOAD_Prestasi      = "https://prasyah.000webhostapp.com/BerkasPDF/uploadPrestasi.php";
    //Pdf request code
    private int PICK_PDF_REQUEST = 1;
    //storage permission code
    private static final int STORAGE_PERMISSION_CODE = 123;
    //Uri to store the file uri
    private Uri filePath;

    //prevent double click
    private long mLastClickTime = 0;

    //Keperluan Update
    String NIM;
    private ProgressDialog loading;

    //ShowLayout
    LinearLayout LHSlayout,LHSsheet,UKTlayout,UKTsheet, PrestasiLayout, PrestasiSheet;

    private boolean isPilihFileKRSClicked = false;
    private boolean isPilihFileLHSClicked = false;
    private boolean isPilihFileUKTClicked = false;
    private boolean isPilihFilePrestasiClicked = false;

    View separator;
    TextView descInfo,tvOpsional;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(Color.BLACK);
        setContentView(R.layout.activity_berkas_proposal);

        //NIM
        Bundle nim = getIntent().getExtras();
        String revNIMforBerkas = Objects.requireNonNull(nim).getString("NIM");
        tvNIMberkas = findViewById(R.id.tv_NIMforBerkas);
        tvNIMberkas.setText(revNIMforBerkas);
        YoYo.with(Techniques.RollIn).playOn(tvNIMberkas);

        //DesainInfoKRS
        infoKRS = findViewById(R.id.info_uploadKRS);
        YoYo.with(Techniques.Shake).delay(800).repeat(1).playOn(infoKRS);
        final Dialog info_KRS = new Dialog(this, R.style.DialogAnimationFadeIn);
        infoKRS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //jika perbedaan dari current_time dan last_click < x detik, jangan lakukan apapun
                if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                info_KRS.setContentView(R.layout.popup_info_krs);
                Objects.requireNonNull(info_KRS.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                info_KRS.show();
            }
        });

        //Inisiasi LHS Upload File
        LHSlayout = findViewById(R.id.uploadLHS_layout);
        LHSsheet = findViewById(R.id.uploadLHS_sheet);
        UKTlayout = findViewById(R.id.uploadUKT_layout);
        UKTsheet = findViewById(R.id.uploadUKT_sheet);
        PrestasiLayout = findViewById(R.id.uploadPrestasi1_layout);
        PrestasiSheet = findViewById(R.id.uploadPrestasi1_sheet);

        infoLHS = findViewById(R.id.info_LHS);
        final Dialog info_LHS = new Dialog(this, R.style.DialogAnimationFadeIn);
        infoLHS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //jika perbedaan dari current_time dan last_click < x detik, jangan lakukan apapun
                if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                info_LHS.setContentView(R.layout.popup_info_lhs);
                Objects.requireNonNull(info_LHS.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                info_LHS.show();
            }
        });

        infoUKT = findViewById(R.id.info_UKT);
        final Dialog info_UKT = new Dialog(this, R.style.DialogAnimationFadeIn);
        infoUKT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //jika perbedaan dari current_time dan last_click < x detik, jangan lakukan apapun
                if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                info_UKT.setContentView(R.layout.popup_info_ukt);
                Objects.requireNonNull(info_UKT.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                info_UKT.show();
            }
        });

        infoPrestasi = findViewById(R.id.info_Prestasi);
        final Dialog info_Prestasi = new Dialog(this, R.style.DialogAnimationFadeIn);
        infoPrestasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //jika perbedaan dari current_time dan last_click < x detik, jangan lakukan apapun
                if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                info_Prestasi.setContentView(R.layout.popup_info_prestasi);
                Objects.requireNonNull(info_Prestasi.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                info_Prestasi.show();
            }
        });

        separator = findViewById(R.id.separatorBerkas);
        descInfo = findViewById(R.id.tv_descInfoPrestasi);
        tvOpsional = findViewById(R.id.tv_opsional);
        btnSkip = findViewById(R.id.tv_skipPrestasiLayout);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
                requestStoragePermission();
            } else {
                Intent i = new Intent(BerkasProposal.this,NetworkErrorActivity.class);
                startActivity(i);
                finish();
                //Toast.makeText(getApplicationContext(), "Tidak ada koneksi Internet :(",Toast.LENGTH_SHORT).show();
            }
        }

        ETFileNameKRS = findViewById(R.id.etNameKRS);
        ETFileNameLHS = findViewById(R.id.etNameLHS);
        ETFileNameUKT = findViewById(R.id.etNameUKT);
        ETFileNamePrestasi = findViewById(R.id.etNamePrestasi);

        //Select File KRS
        pilihFileKRS = findViewById(R.id.buttonChooseFileKRS);
        pilihFileKRS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //jika perbedaan dari current_time dan last_click < x detik, jangan lakukan apapun
                if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                isPilihFileKRSClicked = true;
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("file/pdf");
                startActivityForResult(Intent.createChooser(intent, "Select Pdf"), PICK_PDF_REQUEST);
            }
        });
        //Upload KRS
        uploadFileKRS = findViewById(R.id.buttonUploadFileKRS);
        uploadFileKRS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //jika perbedaan dari current_time dan last_click < x detik, jangan lakukan apapun
                if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                uploadDocumentKRS();
            }
        });

        //Selet File LHS
        pilihFileLHS = findViewById(R.id.buttonChooseFileLHS);
        pilihFileLHS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //jika perbedaan dari current_time dan last_click < x detik, jangan lakukan apapun
                if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                isPilihFileLHSClicked = true;
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("file/pdf");
                startActivityForResult(Intent.createChooser(intent, "Select Pdf"), PICK_PDF_REQUEST);
            }
        });
        //Upload LHS
        uploadFileLHS = findViewById(R.id.buttonUploadFileLHS);
        uploadFileLHS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //jika perbedaan dari current_time dan last_click < x detik, jangan lakukan apapun
                if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                uploadDocumentLHS();
            }
        });

        //Select File UKT
        pilihFileUKT = findViewById(R.id.buttonChooseFileUKT);
        pilihFileUKT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //jika perbedaan dari current_time dan last_click < x detik, jangan lakukan apapun
                if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                isPilihFileUKTClicked = true;
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("file/pdf");
                startActivityForResult(Intent.createChooser(intent, "Select Pdf"), PICK_PDF_REQUEST);
            }
        });
        //Upload UKT
        uploadFileUKT = findViewById(R.id.buttonUploadFileUKT);
        uploadFileUKT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //jika perbedaan dari current_time dan last_click < x detik, jangan lakukan apapun
                if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                uploadDocumentUKT();
            }
        });

        //Select File Prestasi
        pilihFilePrestasi = findViewById(R.id.buttonChooseFilePrestasi);
        pilihFilePrestasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //jika perbedaan dari current_time dan last_click < x detik, jangan lakukan apapun
                if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                isPilihFilePrestasiClicked = true;
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("file/pdf");
                startActivityForResult(Intent.createChooser(intent, "Select Pdf"), PICK_PDF_REQUEST);
            }
        });
        //Upload Prestasi
        uploadFilePrestasi = findViewById(R.id.buttonUploadFilePrestasi);
        uploadFilePrestasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //jika perbedaan dari current_time dan last_click < x detik, jangan lakukan apapun
                if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                uploadDocumentPrestasi();
            }
        });

        //Ganti Status Mahasiswa Menjadi MENUNGGU
        btOke = findViewById(R.id.bt_Done);
        btOke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //jika perbedaan dari current_time dan last_click < x detik, jangan lakukan apapun
                if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                gantiStatus();
            }
        });

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //jika perbedaan dari current_time dan last_click < x detik, jangan lakukan apapun
                if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                btOke.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.Landing)
                        .playOn(btOke);

                //hide Prestasi Layout
                separator.setVisibility(View.GONE);
                YoYo.with(Techniques.SlideOutUp)
                        .playOn(separator);

                tvOpsional.setVisibility(View.GONE);
                YoYo.with(Techniques.SlideOutUp)
                        .playOn(tvOpsional);

                descInfo.setVisibility(View.GONE);
                YoYo.with(Techniques.SlideOutUp)
                        .playOn(descInfo);

                YoYo.with(Techniques.Shake)
                        .repeat(2)
                        .playOn(infoPrestasi);

                PrestasiLayout.setVisibility(View.GONE);
                YoYo.with(Techniques.SlideOutUp)
                        .playOn(PrestasiLayout);

                PrestasiSheet.setVisibility(View.GONE);
                YoYo.with(Techniques.SlideOutUp)
                        .playOn(PrestasiSheet);

                btnSkip.setVisibility(View.GONE);
                YoYo.with(Techniques.SlideOutUp)
                        .playOn(btnSkip);
            }
        });

        back = findViewById(R.id.btnBacktoMhsHome);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //jika perbedaan dari current_time dan last_click < x detik, jangan lakukan apapun
                if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                FancyToast.makeText(getApplicationContext(), "Silakan Lengkapi Berkas Anda",FancyToast.LENGTH_SHORT, FancyToast.INFO, R.drawable.ic_edit, false).show();
            }
        });
    }

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    public void uploadDocumentKRS() {
        String name = ETFileNameKRS.getText().toString().trim();
        String NIM  = tvNIMberkas.getText().toString();

        String path = FilePath.getPath(this, filePath);

        if (path == null) {
            Toast.makeText(this, "Mohon pindahkan file PDF anda ke penyimpanan internal", Toast.LENGTH_LONG).show();
        } else {
            //Uploading code
            try {
                String uploadId = UUID.randomUUID().toString();
                //Creating a multi part request
                new MultipartUploadRequest(this, uploadId, UPLOAD_KRS)
                        .addFileToUpload(path, "pdf") //Adding file
                        .addParameter("name", name)
                        .addParameter("NIM", NIM)
                        .setMaxRetries(2)
                        .startUpload(); //Starting the upload

                FancyToast.makeText(getApplicationContext(), "KRS Berhasil di Upload",FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, R.drawable.ic_done, false).show();
                ETFileNameKRS.setText("File sudah di upload");
                ETFileNameKRS.setEnabled(false);

                pilihFileKRS.setEnabled(false);
                uploadFileKRS.setEnabled(false);

                pilihFileKRS.setText("-");
                uploadFileKRS.setText("-");

                //show LHS Layout
                YoYo.with(Techniques.Shake)
                        .repeat(2)
                        .playOn(infoLHS);
                LHSlayout.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.SlideInDown)
                        .playOn(LHSlayout);

                LHSsheet.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.SlideInDown)
                        .playOn(LHSsheet);

            } catch (Exception exc) {
                Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void uploadDocumentLHS() {
        String name = ETFileNameLHS.getText().toString().trim();
        String NIM  = tvNIMberkas.getText().toString();

        String pathLHS = FilePath.getPath(this, filePath);

        if (pathLHS == null) {
            Toast.makeText(this, "Mohon pindahkan file PDF anda ke penyimpanan internal", Toast.LENGTH_LONG).show();
        } else {
            //Uploading code
            try {
                String uploadId = UUID.randomUUID().toString();

                new MultipartUploadRequest(this, uploadId, UPLOAD_LHS)
                        .addFileToUpload(pathLHS, "pdf") //Adding file
                        .addParameter("name", name)
                        .addParameter("NIM", NIM)
                        .setMaxRetries(2)
                        .startUpload(); //Starting the upload

                FancyToast.makeText(getApplicationContext(), "LHS Berhasil di Upload",FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, R.drawable.ic_done, false).show();
                ETFileNameLHS.setText("File sudah di upload");

                ETFileNameLHS.setEnabled(false);
                pilihFileLHS.setEnabled(false);
                uploadFileLHS.setEnabled(false);

                pilihFileLHS.setText("-");
                uploadFileLHS.setText("-");
                pilihFileKRS.setText("-");
                uploadFileKRS.setText("-");

                //show UKT Layout
                YoYo.with(Techniques.Shake)
                        .repeat(2)
                        .playOn(infoUKT);
                UKTlayout.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.SlideInDown)
                        .playOn(UKTlayout);

                UKTsheet.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.SlideInDown)
                        .playOn(UKTsheet);

            } catch (Exception exc) {
                Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void uploadDocumentUKT() {
        String name = ETFileNameUKT.getText().toString().trim();
        String NIM  = tvNIMberkas.getText().toString();

        String pathUKT = FilePath.getPath(this, filePath);

        if (pathUKT == null) {
            Toast.makeText(this, "Mohon pindahkan file PDF anda ke penyimpanan internal", Toast.LENGTH_LONG).show();
        } else {
            //Uploading code
            try {
                String uploadId = UUID.randomUUID().toString();

                new MultipartUploadRequest(this, uploadId, UPLOAD_UKT)
                        .addFileToUpload(pathUKT, "pdf") //Adding file
                        .addParameter("name", name)
                        .addParameter("NIM", NIM)
                        .setMaxRetries(2)
                        .startUpload(); //Starting the upload

                FancyToast.makeText(getApplicationContext(), "UKT Berhasil di Upload",FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, R.drawable.ic_done, false).show();
                ETFileNameUKT.setText("File sudah di upload");
                ETFileNameUKT.setEnabled(false);
                pilihFileUKT.setEnabled(false);
                uploadFileUKT.setEnabled(false);
                pilihFileUKT.setText("-");
                uploadFileUKT.setText("-");

                pilihFileLHS.setText("-");
                uploadFileLHS.setText("-");
                pilihFileKRS.setText("-");
                uploadFileKRS.setText("-");

                //show Prestasi Layout
                separator.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.SlideInDown)
                        .playOn(separator);

                tvOpsional.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.SlideInDown)
                        .playOn(tvOpsional);

                descInfo.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.SlideInDown)
                        .playOn(descInfo);

                YoYo.with(Techniques.Shake)
                        .repeat(2)
                        .playOn(infoPrestasi);

                PrestasiLayout.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.SlideInDown)
                        .playOn(PrestasiLayout);

                PrestasiSheet.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.SlideInDown)
                        .playOn(PrestasiSheet);

                btnSkip.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.SlideInDown)
                        .playOn(PrestasiSheet);

            } catch (Exception exc) {
                Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void uploadDocumentPrestasi() {
        String name = ETFileNamePrestasi.getText().toString().trim();
        String NIM  = tvNIMberkas.getText().toString();

        String pathPrestasi = FilePath.getPath(this, filePath);

        if (pathPrestasi == null) {
            Toast.makeText(this, "Mohon pindahkan file PDF anda ke penyimpanan internal", Toast.LENGTH_LONG).show();
        } else {
            //Uploading code
            try {
                String uploadId = UUID.randomUUID().toString();

                new MultipartUploadRequest(this, uploadId, UPLOAD_Prestasi)
                        .addFileToUpload(pathPrestasi, "pdf") //Adding file
                        .addParameter("name", name)
                        .addParameter("NIM", NIM)
                        .setMaxRetries(2)
                        .startUpload(); //Starting the upload

                FancyToast.makeText(getApplicationContext(), "Sertifikat Berhasil di Upload",FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, R.drawable.ic_done, false).show();
                ETFileNamePrestasi.setText("File sudah di upload");
                ETFileNamePrestasi.setEnabled(false);
                pilihFilePrestasi.setEnabled(false);
                pilihFilePrestasi.setText("-");
                uploadFilePrestasi.setEnabled(false);
                uploadFilePrestasi.setText("-");

                pilihFileUKT.setText("-");
                uploadFileUKT.setText("-");

                pilihFileLHS.setText("-");
                uploadFileLHS.setText("-");
                pilihFileKRS.setText("-");
                uploadFileKRS.setText("-");

                btnSkip.setVisibility(View.GONE);
                YoYo.with(Techniques.FadeOut);

                //show Button OKE
                btOke.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.Landing)
                        .playOn(btOke);

            } catch (Exception exc) {
                Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void gantiStatus(){
        loading = new ProgressDialog(BerkasProposal.this,R.style.ProgressBarMahasiswa);
        loading.setMessage("Mohon tunggu ..");
        loading.setCancelable(false);
        loading.show();
        hideKeyboardAfter(BerkasProposal.this);

        NIM = tvNIMberkas.getText().toString();
        if (!NIM.equals("")){
            updateData();
        } else {
            Toast.makeText(this, "Data gagal diambil!", Toast.LENGTH_LONG).show();
        }
    }

    private void updateData() {
        AndroidNetworking.post("https://prasyah.000webhostapp.com/BerkasPDF/updateStatus.php")
                .addBodyParameter("NIM",""+NIM)
                .setTag("Update Data")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loading.dismiss();
                        Log.d("responEdit",""+response);
                        try{
                            Boolean status = response.getBoolean("status");
                            if(status){
                                new AlertDialog.Builder(BerkasProposal.this)
                                        .setMessage("Berkas anda sudah lengkap. Silakan geser ke atas untuk refresh data!")
                                        .setCancelable(false)
                                        .setPositiveButton("LANJUTKAN", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i = getIntent();
                                                setResult(RESULT_OK,i);
                                                BerkasProposal.this.finishAndRemoveTask();
                                            }
                                        })
                                        .show();
                            }else{
                                new AlertDialog.Builder(BerkasProposal.this)
                                        .setMessage("Gagal Mengupdate Data")
                                        .setCancelable(false)
                                        .setPositiveButton("Kembali", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i = getIntent();
                                                setResult(RESULT_CANCELED,i);
                                            }
                                        })
                                        .show();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();

            if (isPilihFileKRSClicked){
                pilihFileKRS.setText("Ganti File");
                ETFileNameKRS.setEnabled(true);
                ETFileNameKRS.setHint("Isi nama file sesuai petunjuk ..");
                ETFileNameKRS.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        uploadFileKRS.setEnabled(false);
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s.toString().equals("")){
                            uploadFileKRS.setEnabled(false);
                        } else {
                            uploadFileKRS.setEnabled(true);
                        }
                    }
                });
            }
            if (isPilihFileLHSClicked){
                pilihFileLHS.setText("Ganti File");
                ETFileNameLHS.setEnabled(true);
                ETFileNameLHS.setHint("Isi nama file sesuai petunjuk ..");
                ETFileNameLHS.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        uploadFileLHS.setEnabled(false);
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s.toString().equals("")){
                            uploadFileLHS.setEnabled(false);
                        } else {
                            uploadFileLHS.setEnabled(true);
                        }
                    }
                });

                //disable KRS
                ETFileNameKRS.setEnabled(false);
                uploadFileKRS.setEnabled(false);
            }
            if (isPilihFileUKTClicked){
                pilihFileUKT.setText("Ganti File");
                ETFileNameUKT.setEnabled(true);
                ETFileNameUKT.setHint("Isi nama file sesuai petunjuk ..");
                ETFileNameUKT.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        uploadFileUKT.setEnabled(false);
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s.toString().equals("")){
                            uploadFileUKT.setEnabled(false);
                        } else {
                            uploadFileUKT.setEnabled(true);
                        }
                    }
                });
                //disable KRS dan LHS
                ETFileNameKRS.setEnabled(false);
                ETFileNameLHS.setEnabled(false);
                uploadFileKRS.setEnabled(false);
                uploadFileLHS.setEnabled(false);
            }
            if (isPilihFilePrestasiClicked){
                pilihFilePrestasi.setText("Ganti File");
                ETFileNamePrestasi.setEnabled(true);
                ETFileNamePrestasi.setHint("Isi nama file sesuai petunjuk ..");
                ETFileNamePrestasi.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        uploadFilePrestasi.setEnabled(false);
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s.toString().equals("")){
                            uploadFilePrestasi.setEnabled(false);
                        } else {
                            uploadFilePrestasi.setEnabled(true);
                        }
                    }
                });

                //disable KRS, LHS dan UKT
                ETFileNameKRS.setEnabled(false);
                ETFileNameLHS.setEnabled(false);
                ETFileNameUKT.setEnabled(false);
                uploadFileKRS.setEnabled(false);
                uploadFileLHS.setEnabled(false);
                uploadFileUKT.setEnabled(false);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, "Izin diberikan", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Izin ditolak", Toast.LENGTH_LONG).show();
            }
        }
    }

    public static void hideKeyboardAfter(Activity activity){
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null){
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    public void hideKeyboardOnLayoutBerkasProposal(View view) {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        view = this.getCurrentFocus();
        if (view == null){
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    @Override
    public void onBackPressed() {
        FancyToast.makeText(getApplicationContext(), "Silakan Lengkapi Berkas Anda",FancyToast.LENGTH_SHORT, FancyToast.INFO, R.drawable.ic_edit, false).show();
    }
}