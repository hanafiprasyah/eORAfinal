package com.example.eorafinal;

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
import android.os.SystemClock;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

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

public class RegisterMahasiswa extends AppCompatActivity {

    private SimpleDateFormat dateFormatter;
    private Button btDatePicker;

    NestedScrollView nestedScrollView;

    private EditText edtNIM, edtPassword, edtNama, edtTempatLahir, edtEmail, edtNotel;
    private TextView tvTanggalLahir,back,tvJK,titleImage;
    private TextView register;

    ConnectivityManager conMgr;

    private Spinner spinner;

    ProgressDialog progressDialog;

    String NIMget,passwordget,namaget,tempat_lahirget,tgl_lahirget,jkget,emailget,notelget;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    String NIM              ="NIM", password        ="password", nama            ="nama", tempat_lahir    ="tempat_lahir", tgl_lahir       ="tgl_lahir", jk              ="jk", email           ="email", notel           ="notel";

    //UploadImageUI
    String ImagePath = "image_path" ;
    Bitmap bitmap;
    boolean check = true;
    Button SelectImageGallery;
    ImageView imageView;
    String ServerUploadPath ="https://prasyah.000webhostapp.com/inputDatawithImage.php" ;

    //prevent double click
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(Color.BLACK);
        setContentView(R.layout.activity_register_mahasiswa);
        init();

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
            } else {
                Intent i = new Intent(RegisterMahasiswa.this,NetworkErrorActivity.class);
                startActivity(i);
                //Toast.makeText(getApplicationContext(), "Tidak ada koneksi Internet :(",Toast.LENGTH_SHORT).show();
            }
        }

        //UploadImageUIDeclare
        Animation animForSelectImageButton = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        SelectImageGallery.setAnimation(animForSelectImageButton);
        //SelectPhoto
        SelectImageGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //jika perbedaan dari current_time dan last_click < x detik, jangan lakukan apapun
                if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Pilih Pas Foto"), 1);
            }
        });

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        btDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //jika perbedaan dari current_time dan last_click < x detik, jangan lakukan apapun
                if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                showDateDialog();
            }
        });

        Animation animBackforRegMahasiswa = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_in);
        back.setAnimation(animBackforRegMahasiswa);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //jika perbedaan dari current_time dan last_click < x detik, jangan lakukan apapun
                if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent o = new Intent(RegisterMahasiswa.this,MahasiswaLogin.class);
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

        Animation animSaveforRegMahasiswa = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_in);
        register.setAnimation(animSaveforRegMahasiswa);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //jika perbedaan dari current_time dan last_click < x detik, jangan lakukan apapun
                if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                NIMget             = edtNIM.getText().toString();
                passwordget        = edtPassword.getText().toString();
                namaget            = edtNama.getText().toString();
                tempat_lahirget    = edtTempatLahir.getText().toString();
                tgl_lahirget       = tvTanggalLahir.getText().toString();
                jkget              = tvJK.getText().toString();
                emailget           = edtEmail.getText().toString();
                notelget           = edtNotel.getText().toString();

                if (conMgr.getActiveNetworkInfo() != null
                        && conMgr.getActiveNetworkInfo().isAvailable()
                        && conMgr.getActiveNetworkInfo().isConnected()) {

                    if(NIMget.equals("") && passwordget.equals("") && namaget.equals("") && tempat_lahirget.equals("") &&
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
                                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(RegisterMahasiswa.this,R.style.ProgressBarMahasiswa);
                                builder.setMessage("Yakin dengan data anda?")
                                        .setCancelable(false)
                                        .setPositiveButton("YAKIN", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                hideKeyboard(RegisterMahasiswa.this);
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
                        },250);
                    }
                } else {
                    Intent i = new Intent(RegisterMahasiswa.this,NetworkErrorActivity.class);
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
        if(NIMget.equals("") || passwordget.equals("") || namaget.equals("") || tempat_lahirget.equals("") ||
                tgl_lahirget.equals("TANGGAL LAHIR") || jkget.equals("JENIS KELAMIN") || emailget.equals("") ||
                notelget.equals("") || imageView.getDrawable()==null){
            progressDialog.dismiss();
            FancyToast.makeText(RegisterMahasiswa.this,"Semua data harus diisi!",FancyToast.LENGTH_SHORT, FancyToast.ERROR, R.drawable.ic_errorwhite24, false).show();
        } else {
            if (emailget.isEmpty()){
                progressDialog.dismiss();
                FancyToast.makeText(RegisterMahasiswa.this,"Email harus diisi!",FancyToast.LENGTH_SHORT, FancyToast.ERROR, R.drawable.ic_mail, false).show();
            } else {
                if (emailget.trim().matches(emailPattern)){
                    ImageUploadToServerFunction();
                } else {
                    progressDialog.dismiss();
                    FancyToast.makeText(RegisterMahasiswa.this,"Isi email dengan benar!",FancyToast.LENGTH_SHORT, FancyToast.ERROR, R.drawable.ic_warningblack24, false).show();
                }
            }
        }
    }

    private void ImageUploadToServerFunction() {
        ByteArrayOutputStream byteArrayOutputStreamObject ;
        byteArrayOutputStreamObject = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStreamObject);
        byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();
        final String ConvertImage = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);

        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(RegisterMahasiswa.this,"Upload data ke server","Mohon Tunggu ..",false,false);
            }

            @Override
            protected void onPostExecute(String string1) {
                super.onPostExecute(string1);

                // Dismiss the progress dialog after done uploading.
                progressDialog.dismiss();

                // Printing uploading success message coming from server on android app.
                //Toast.makeText(RegisterMahasiswa.this,string1,Toast.LENGTH_SHORT).show();

                // Setting image as transparent after done uploading.
                imageView.setImageResource(android.R.color.transparent);
                imageView.setVisibility(View.GONE);

                //clear UI
                edtNIM.setText("");
                edtPassword.setText("");
                edtNama.setText("");
                edtTempatLahir.setText("");
                tvTanggalLahir.setText("");
                tvJK.setText("");
                //spinner.setSelection(-1);
                edtEmail.setText("");
                edtNotel.setText("");

                //if success then throw you to the next login act
                new AlertDialog.Builder(RegisterMahasiswa.this)
                        .setMessage("Berhasil Menambahkan Data !")
                        .setCancelable(false)
                        .setPositiveButton("LOGIN", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = getIntent();
                                setResult(RESULT_OK,i);
                                RegisterMahasiswa.this.finishAfterTransition();
                                overridePendingTransition(R.anim.fade_in,0);
                            }
                        })
                        .show();
            }

            @Override
            protected String doInBackground(Void... params) {
                ImageProcessClass imageProcessClass = new ImageProcessClass();
                HashMap<String,String> HashMapParams = new HashMap<String,String>();

                HashMapParams.put(NIM, NIMget);
                HashMapParams.put(password, passwordget);
                HashMapParams.put(nama, namaget);
                HashMapParams.put(tempat_lahir, tempat_lahirget);
                HashMapParams.put(tgl_lahir, tgl_lahirget);
                HashMapParams.put(jk, jkget);
                HashMapParams.put(email, emailget);
                HashMapParams.put(notel, notelget);
                HashMapParams.put(ImagePath, ConvertImage);

                String FinalData = imageProcessClass.ImageHttpRequest(ServerUploadPath, HashMapParams);
                return FinalData;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();
        AsyncTaskUploadClassOBJ.execute();
    }

    public class ImageProcessClass {
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
                Animation animShowImageforMhs = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.item_animation_falldown);
                imageView.setAnimation(animShowImageforMhs);
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
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.DialogThemeMahasiswa, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                /**
                 * Method ini dipanggil saat kita selesai memilih tanggal di DatePicker
                 */

                /**
                 * Set Calendar untuk menampung tanggal yang dipilih
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
        edtNIM = findViewById(R.id.etNIM);
        edtPassword = findViewById(R.id.etPassword);
        edtNama = findViewById(R.id.etNama);
        edtTempatLahir = findViewById(R.id.etTempatLahir);
        tvJK = findViewById(R.id.tv_jkresult);
        edtEmail = findViewById(R.id.etEmail);
        edtNotel = findViewById(R.id.etNotel);
        imageView = findViewById(R.id.imageView);
        spinner = findViewById(R.id.spinnerJK);
        titleImage = findViewById(R.id.tv_titleimage);

        //ProgressDialog
        progressDialog = new ProgressDialog(this,R.style.ProgressBarMahasiswa);

        //TextView
        tvTanggalLahir = findViewById(R.id.tv_dateresult);
        register = findViewById(R.id.signUpMHS);

        //Button
        btDatePicker = findViewById(R.id.bt_datepicker);
        back = findViewById(R.id.btnBackRegister);
        SelectImageGallery = findViewById(R.id.selectPhoto);

        Animation animForRegisterMhs = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.item_animation_falldown);
        edtNIM.setAnimation(animForRegisterMhs);
        edtPassword.setAnimation(animForRegisterMhs);
        edtNama.setAnimation(animForRegisterMhs);
        edtTempatLahir.setAnimation(animForRegisterMhs);
        edtEmail.setAnimation(animForRegisterMhs);
        edtNotel.setAnimation(animForRegisterMhs);
        tvTanggalLahir.setAnimation(animForRegisterMhs);
        tvJK.setAnimation(animForRegisterMhs);
        titleImage.setAnimation(animForRegisterMhs);
        btDatePicker.setAnimation(animForRegisterMhs);
        spinner.setAnimation(animForRegisterMhs);
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
        Intent o = new Intent(RegisterMahasiswa.this,MahasiswaLogin.class);
        startActivity(o);
        finishAfterTransition();
        overridePendingTransition(0,R.anim.slide_out);
    }
}
