package com.example.eorafinal;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.shashank.sony.fancytoastlib.FancyToast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class RegisterDonatur extends AppCompatActivity {

    private SimpleDateFormat dateFormatter;
    private Button btDatePicker;

    NestedScrollView nestedScrollView;

    private EditText edtUsername, edtPassword, edtNama, edtPekerjaan, edtTempatLahir, edtEmail, edtNotel;
    private TextView tvTanggalLahir,tvJK,titleImage,register;

    TextView back;

    ConnectivityManager conMgr;
    private Spinner spinner;
    ProgressDialog progressDialog;

    String usernameget,passwordget,namaget,pekerjaanget,tempat_lahirget,tgl_lahirget,jkget,emailget,notelget;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    String username        = "username_login",
            password        = "password",
            nama            = "nama_donatur",
            pekerjaan       = "pekerjaan_donatur",
            tempat_lahir    = "tempat_lahir",
            tgl_lahir       = "tanggal_lahir",
            jk              = "jk_donatur",
            email           = "email_donatur",
            notel           = "notel_donatur";

    //UploadImageUI
    String ImagePath = "foto_donatur" ;
    Bitmap bitmap;
    boolean check = true;
    Button SelectImageGallery;
    ImageView imageView;
    String ServerUploadPathDonatur ="https://prasyah.000webhostapp.com/inputDatawithImageDonatur.php" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(Color.BLACK);
        setContentView(R.layout.activity_register_donatur);
        init();

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
            } else {
                Intent i = new Intent(RegisterDonatur.this,NetworkErrorActivity.class);
                startActivity(i);
                //Toast.makeText(getApplicationContext(), "Tidak ada koneksi Internet :(",Toast.LENGTH_SHORT).show();
            }
        }

        //UploadImageUIDeclare
        //SelectPhoto
        SelectImageGallery = findViewById(R.id.selectPhotoDonatur);
        Animation animForSelectImageButtonDonatur = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        SelectImageGallery.setAnimation(animForSelectImageButtonDonatur);
        SelectImageGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Pilih Foto Profil"), 1);

            }
        });

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        btDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();
            }
        });

        Animation animBackforRegDonatur = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_in);
        back.setAnimation(animBackforRegDonatur);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent o = new Intent(RegisterDonatur.this,DonaturLogin.class);
                startActivity(o);
                finishAfterTransition();
                overridePendingTransition(0,R.anim.slide_out);
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            String result = String.valueOf(spinner.getSelectedItem());
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (result.equals(String.valueOf(spinner.getSelectedItem()))){
                    tvJK.setText("JENIS KELAMIN");
                } else {
                    tvJK.setText(""+parent.getItemAtPosition(position).toString());
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Animation animSaveforRegDonatur = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_in);
        register.setAnimation(animSaveforRegDonatur);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameget        = edtUsername.getText().toString();
                passwordget        = edtPassword.getText().toString();
                namaget            = edtNama.getText().toString();
                pekerjaanget       = edtPekerjaan.getText().toString();
                tempat_lahirget    = edtTempatLahir.getText().toString();
                tgl_lahirget       = tvTanggalLahir.getText().toString();
                jkget              = tvJK.getText().toString();
                emailget           = edtEmail.getText().toString();
                notelget           = edtNotel.getText().toString();

                if (conMgr.getActiveNetworkInfo() != null
                        && conMgr.getActiveNetworkInfo().isAvailable()
                        && conMgr.getActiveNetworkInfo().isConnected()) {

                    if(usernameget.equals("") && passwordget.equals("") && namaget.equals("") && pekerjaanget.equals("") && tempat_lahirget.equals("") &&
                            tgl_lahirget.equals("TANGGAL LAHIR") && jkget.equals("JENIS KELAMIN") && emailget.equals("") &&
                            notelget.equals("") && imageView.getDrawable()==null) {

                        LayoutInflater inflater = getLayoutInflater();
                        View layout = inflater.inflate(R.layout.registerverified_toast, (ViewGroup)findViewById(R.id.customRegisterVerifiedToast));
                        TextView title = layout.findViewById(R.id.tv_textVerified);
                        title.setText("Anda belum mengisi data apapun");
                        Toast toast = new Toast(getApplicationContext());
                        toast.setGravity(Gravity.TOP,0,100);
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(layout);
                        toast.show();
                    } else {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(RegisterDonatur.this,R.style.ProgressBarDonatur);
                                builder.setMessage("Yakin dengan data anda?")
                                        .setCancelable(false)
                                        .setPositiveButton("YAKIN", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                hideKeyboard(RegisterDonatur.this);
                                                ValidasiData();
                                            }
                                        })
                                        .setNegativeButton("CEK ULANG", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        });
                                androidx.appcompat.app.AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            }
                        },150);
                    }
                } else {
                    Intent i = new Intent(RegisterDonatur.this,NetworkErrorActivity.class);
                    startActivity(i);
                    progressDialog.dismiss();
                    //Toast.makeText(getApplicationContext() ,"Tidak ada koneksi Internet :(", Toast.LENGTH_LONG).show();
                }
            }
        });

        //SlidingConfig
        nestedScrollView = findViewById(R.id.nestedRegister);
        if (nestedScrollView != null){
            nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    View view = getCurrentFocus();
                    if (scrollY > oldScrollY) {
                        //ScrollDown
                        if (view == null){
                            view = new View(getApplicationContext());
                        }
                        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
                    }
                    //ScrollUp
                    if (scrollY < oldScrollY) {
                    }
                    if (scrollY == 0) {
                        //TopOfView
                    }
                    //BottomOfView
                    if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                        if (view == null){
                            view = new View(getApplicationContext());
                        }
                        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
                    }
                }
            });
        }
    }

    void ValidasiData() {
        if(usernameget.equals("") || passwordget.equals("") || namaget.equals("") || pekerjaanget.equals("") || tempat_lahirget.equals("") ||
                tgl_lahirget.equals("TANGGAL LAHIR") || jkget.equals("JENIS KELAMIN") || emailget.equals("") ||
                notelget.equals("") || imageView.getDrawable()==null){
            progressDialog.dismiss();
            FancyToast.makeText(RegisterDonatur.this,"Semua data harus diisi!",FancyToast.LENGTH_SHORT, FancyToast.ERROR, R.drawable.ic_errorwhite24, false).show();
        } else {
            if (emailget.isEmpty()){
                progressDialog.dismiss();
                FancyToast.makeText(RegisterDonatur.this,"Email harus diisi!",FancyToast.LENGTH_SHORT, FancyToast.ERROR, R.drawable.ic_mail, false).show();
            } else {
                if (emailget.trim().matches(emailPattern)){
                    DonaturImageUploadToServerFunction();
                } else {
                    progressDialog.dismiss();
                    FancyToast.makeText(RegisterDonatur.this,"Isi email dengan benar!",FancyToast.LENGTH_SHORT, FancyToast.ERROR, R.drawable.ic_warningblack24, false).show();
                }
            }
        }
    }

    private void DonaturImageUploadToServerFunction() {
        ByteArrayOutputStream byteArrayOutputStreamObject ;
        byteArrayOutputStreamObject = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStreamObject);
        byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();
        final String ConvertImage = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);

        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(RegisterDonatur.this,"Upload data ke server","Mohon Tunggu ..",false,false);
            }

            @Override
            protected void onPostExecute(String string2) {
                super.onPostExecute(string2);

                // Dismiss the progress dialog after done uploading.
                progressDialog.dismiss();

                imageView.setImageResource(android.R.color.transparent);
                imageView.setVisibility(View.GONE);

                //clear UI
                edtUsername.setText("");
                edtPassword.setText("");
                edtNama.setText("");
                edtPekerjaan.setText("");
                edtTempatLahir.setText("");
                tvTanggalLahir.setText("");
                tvJK.setText("");
                //spinner.setSelection(-1);
                edtEmail.setText("");
                edtNotel.setText("");

                //if success then throw you to the next login act
                new AlertDialog.Builder(RegisterDonatur.this)
                        .setMessage("Berhasil Menambahkan Data !")
                        .setCancelable(false)
                        .setPositiveButton("LOGIN", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = getIntent();
                                setResult(RESULT_OK,i);
                                RegisterDonatur.this.finishAfterTransition();
                                overridePendingTransition(R.anim.fade_in,0);
                            }
                        })
                        .show();
            }

            @Override
            protected String doInBackground(Void... params) {
                ImageProcessClassDonatur imageProcessClassDonatur = new ImageProcessClassDonatur();
                HashMap<String,String> HashMapParams = new HashMap<>();

                HashMapParams.put(username, usernameget);
                HashMapParams.put(nama, namaget);
                HashMapParams.put(pekerjaan, pekerjaanget);
                HashMapParams.put(tempat_lahir, tempat_lahirget);
                HashMapParams.put(tgl_lahir, tgl_lahirget);
                HashMapParams.put(jk, jkget);
                HashMapParams.put(email, emailget);
                HashMapParams.put(notel, notelget);
                HashMapParams.put(password, passwordget);
                HashMapParams.put(ImagePath, ConvertImage);

                String FinalData = imageProcessClassDonatur.ImageHttpRequest(ServerUploadPathDonatur, HashMapParams);
                return FinalData;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();
        AsyncTaskUploadClassOBJ.execute();
    }

    public class ImageProcessClassDonatur{
        public String ImageHttpRequest(String requestURL, HashMap<String, String> PData) {
            StringBuilder stringBuilder = new StringBuilder();

            try {
                URL url;
                HttpURLConnection httpURLConnectionObject;
                OutputStream OutPutStream;
                BufferedWriter bufferedWriterObject;
                BufferedReader bufferedReaderObject;
                int RC;
                url = new URL(requestURL);
                httpURLConnectionObject = (HttpURLConnection) url.openConnection();
                httpURLConnectionObject.setReadTimeout(19000);
                httpURLConnectionObject.setConnectTimeout(19000);
                httpURLConnectionObject.setRequestMethod("POST");
                httpURLConnectionObject.setDoInput(true);
                httpURLConnectionObject.setDoOutput(true);

                OutPutStream = httpURLConnectionObject.getOutputStream();
                bufferedWriterObject = new BufferedWriter(
                        new OutputStreamWriter(OutPutStream, "UTF-8"));

                bufferedWriterObject.write(bufferedWriterDataFN(PData));
                bufferedWriterObject.flush();
                bufferedWriterObject.close();
                OutPutStream.close();

                RC = httpURLConnectionObject.getResponseCode();
                if (RC == HttpsURLConnection.HTTP_OK) {
                    bufferedReaderObject = new BufferedReader(new InputStreamReader(httpURLConnectionObject.getInputStream()));
                    stringBuilder = new StringBuilder();
                    String RC2;

                    while ((RC2 = bufferedReaderObject.readLine()) != null) {
                        stringBuilder.append(RC2);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }

        private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {

            StringBuilder stringBuilderObject;

            stringBuilderObject = new StringBuilder();

            for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {

                if (check)

                    check = false;
                else
                    stringBuilderObject.append("&");

                stringBuilderObject.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));

                stringBuilderObject.append("=");

                stringBuilderObject.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
            }

            return stringBuilderObject.toString();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imageView.setImageBitmap(bitmap);
                Animation animShowImageforDonatur = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.item_animation_falldown);
                imageView.setAnimation(animShowImageforDonatur);
                imageView.setVisibility(View.VISIBLE);
                titleImage.setVisibility(View.GONE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showDateDialog() {
        //Tanggal Terbaru
        Calendar newCalendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.DialogThemeDonatur, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                /**
                 * Method ini dipanggil saat kita selesai memilih tanggal di DatePicker
                 */

                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                /**
                 * Update TextView dengan tanggal yang kita pilih
                 */

                tvTanggalLahir.setText("" + dateFormatter.format(newDate.getTime()));
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void init() {
        edtUsername = findViewById(R.id.etUsernameDonatur);
        edtPassword = findViewById(R.id.etPasswordDonatur);
        edtNama = findViewById(R.id.etNamaDonatur);
        edtPekerjaan = findViewById(R.id.etPekerjaanDonatur);
        edtTempatLahir = findViewById(R.id.etTempatLahirDonatur);
        edtEmail = findViewById(R.id.etEmailDonatur);
        edtNotel = findViewById(R.id.etNotelDonatur);

        tvJK = findViewById(R.id.tv_jkresultDonatur);
        imageView = findViewById(R.id.imageViewDonatur);
        spinner = findViewById(R.id.spinnerJKDonatur);
        titleImage = findViewById(R.id.tv_titleimage);

        //ProgressDialog
        progressDialog = new ProgressDialog(this,R.style.ProgressBarDonatur);

        //TextView
        tvTanggalLahir = findViewById(R.id.tv_dateresultDonatur);
        register = findViewById(R.id.signUpDonatur);
        back = findViewById(R.id.btnBackRegisterDonatur);

        //Button
        btDatePicker = findViewById(R.id.bt_datepicker);

        Animation animForRegisterDonatur = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.item_animation_falldown);
        edtUsername.setAnimation(animForRegisterDonatur);
        edtPassword.setAnimation(animForRegisterDonatur);
        edtNama.setAnimation(animForRegisterDonatur);
        edtPekerjaan.setAnimation(animForRegisterDonatur);
        edtTempatLahir.setAnimation(animForRegisterDonatur);
        edtEmail.setAnimation(animForRegisterDonatur);
        edtNotel.setAnimation(animForRegisterDonatur);
        tvTanggalLahir.setAnimation(animForRegisterDonatur);
        tvJK.setAnimation(animForRegisterDonatur);
        titleImage.setAnimation(animForRegisterDonatur);
        btDatePicker.setAnimation(animForRegisterDonatur);
        spinner.setAnimation(animForRegisterDonatur);
    }

    public static void hideKeyboard(Activity activity){
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null){
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    public void hideKeyboardOnLayout(View view) {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        view = this.getCurrentFocus();
        if (view == null){
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    @Override
    public void onBackPressed() {
        Intent o = new Intent(RegisterDonatur.this,DonaturLogin.class);
        startActivity(o);
        finishAfterTransition();
        overridePendingTransition(0,R.anim.slide_out);
    }
}