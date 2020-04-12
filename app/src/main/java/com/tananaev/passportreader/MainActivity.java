/*
 * Copyright 2016 - 2017 Anton Tananaev (anton.tananaev@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tananaev.passportreader;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import net.sf.scuba.smartcards.CardFileInputStream;
import net.sf.scuba.smartcards.CardService;

import org.jmrtd.BACKey;
import org.jmrtd.BACKeySpec;
import org.jmrtd.PassportService;
import org.jmrtd.lds.CardSecurityFile;
import org.jmrtd.lds.SecurityInfo;
import org.jmrtd.lds.icao.COMFile;
import org.jmrtd.lds.icao.DG1File;
import org.jmrtd.lds.icao.DG2File;
import org.jmrtd.lds.iso19794.FaceImageInfo;
import org.jmrtd.lds.iso19794.FaceInfo;
import org.jmrtd.lds.icao.MRZInfo;
import org.jmrtd.lds.PACEInfo;
import org.jmrtd.lds.SODFile;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import pantallas.MainCompletarDatos;
import pantallas.main_productos;

import static org.jmrtd.PassportService.DEFAULT_MAX_BLOCKSIZE;
import static org.jmrtd.PassportService.NORMAL_MAX_TRANCEIVE_LENGTH;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private final static String KEY_PASSPORT_NUMBER = "passportNumber";
    private final static String KEY_EXPIRATION_DATE = "expirationDate";
    private final static String KEY_BIRTH_DATE = "birthDate";

    private Calendar loadDate(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        if (!editText.getText().toString().isEmpty()) {
            try {
                calendar.setTimeInMillis(new SimpleDateFormat("yyyy-MM-dd", Locale.US)
                        .parse(editText.getText().toString()).getTime());
            } catch (ParseException e) {
                Log.w(MainActivity.class.getSimpleName(), e);
            }
        }
        return calendar;
    }

    private void saveDate(EditText editText, int year, int monthOfYear, int dayOfMonth, String preferenceKey) {
        String value = String.format(Locale.US, "%d-%02d-%02d", year, monthOfYear + 1, dayOfMonth);
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit().putString(preferenceKey, value).apply();
        editText.setText(value);
    }

    private EditText passportNumberView;
    private EditText expirationDateView;
    private EditText birthDateView;
    private boolean passportNumberFromIntent = false;
    private boolean encodePhotoToBase64 = false;
    private View camposLayout;
    private View loadingLayout;

    Button btnIrFormulario2;

    Button btnTomarFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Tomar foto desde app
        btnTomarFoto = findViewById(R.id.btnTomarFoto);
        btnTomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
        //Fin tomar foto desde app

        //Pasar a siguiente pantalla
        btnIrFormulario2 = (Button) findViewById(R.id.irFormulario2);

        btnIrFormulario2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainCompletarDatos.class);
                startActivity(intent);
            }
        });
        //Fin pasar a siguiente pantalla

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String dateOfBirth = getIntent().getStringExtra("dateOfBirth");
        String dateOfExpiry = getIntent().getStringExtra("dateOfExpiry");
        String passportNumber = getIntent().getStringExtra("passportNumber");
//        encodePhotoToBase64 = getIntent().getBooleanExtra("photoAsBase64", false);

        if (dateOfBirth != null) {
            PreferenceManager.getDefaultSharedPreferences(this)
                .edit().putString(KEY_BIRTH_DATE, dateOfBirth).apply();
        }
        if (dateOfExpiry != null) {
            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit().putString(KEY_EXPIRATION_DATE, dateOfExpiry).apply();
        }
        if (passportNumber != null) {
            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit().putString(KEY_PASSPORT_NUMBER, passportNumber).apply();
            passportNumberFromIntent = true;
        }

        passportNumberView = findViewById(R.id.input_passport_number);
        expirationDateView = findViewById(R.id.input_expiration_date);
        birthDateView = findViewById(R.id.input_date_of_birth);

        camposLayout = findViewById(R.id.campos);
        loadingLayout = findViewById(R.id.loading_layout);

        passportNumberView.setText(preferences.getString(KEY_PASSPORT_NUMBER, null));
        expirationDateView.setText(preferences.getString(KEY_EXPIRATION_DATE, null));
        birthDateView.setText(preferences.getString(KEY_BIRTH_DATE, null));

        passportNumberView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                PreferenceManager.getDefaultSharedPreferences(MainActivity.this)
                        .edit().putString(KEY_PASSPORT_NUMBER, s.toString()).apply();
            }
        });

        expirationDateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = loadDate(expirationDateView);
                DatePickerDialog dialog = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        saveDate(expirationDateView, year, monthOfYear, dayOfMonth, KEY_EXPIRATION_DATE);
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                getFragmentManager().beginTransaction().add(dialog, null).commit();
            }
        });

        birthDateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = loadDate(birthDateView);
                DatePickerDialog dialog = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        saveDate(birthDateView, year, monthOfYear, dayOfMonth, KEY_BIRTH_DATE);
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                getFragmentManager().beginTransaction().add(dialog, null).commit();
            }
        });

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_status:
                        Intent b = new Intent(MainActivity.this, StatusActivity.class);
                        startActivity(b);
                        break;
                }
                return false;
            }
        });
    }
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            imageView.setImageBitmap(imageBitmap);
        }
    }

    //LOGIN ITEM IN TOP BAR
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_login:
                finish();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        NfcAdapter adapter = NfcAdapter.getDefaultAdapter(this);
        if (adapter != null) {
            Intent intent = new Intent(getApplicationContext(), this.getClass());
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            String[][] filter = new String[][]{new String[]{"android.nfc.tech.IsoDep"}};
            adapter.enableForegroundDispatch(this, pendingIntent, null, filter);
        }

        if (passportNumberFromIntent) {
            // When the passport number field is populated from the caller, we hide the
            // soft keyboard as otherwise it can obscure the 'Reading data' progress indicator.
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        NfcAdapter adapter = NfcAdapter.getDefaultAdapter(this);
        if (adapter != null) {
            adapter.disableForegroundDispatch(this);
        }
    }

//    @Override
//    int getLayoutId() {
//        return R.layout.activity_main;
//    }
//
//    @Override
//    int getBottomNavigationMenuItemId() {
//         return R.id.action_main;
//    }

    private static String convertDate(String input) {
        if (input == null) {
            return null;
        }
        try {
            return new SimpleDateFormat("yyMMdd", Locale.US)
                    .format(new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(input));
        } catch (ParseException e) {
            Log.w(MainActivity.class.getSimpleName(), e);
            return null;
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            Tag tag = intent.getExtras().getParcelable(NfcAdapter.EXTRA_TAG);
            if (Arrays.asList(tag.getTechList()).contains("android.nfc.tech.IsoDep")) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                String passportNumber = preferences.getString(KEY_PASSPORT_NUMBER, null);
                String expirationDate = convertDate(preferences.getString(KEY_EXPIRATION_DATE, null));
                String birthDate = convertDate(preferences.getString(KEY_BIRTH_DATE, null));
                if (passportNumber != null && !passportNumber.isEmpty()
                        && expirationDate != null && !expirationDate.isEmpty()
                        && birthDate != null && !birthDate.isEmpty()) {
                    BACKeySpec bacKey = new BACKey(passportNumber, birthDate, expirationDate);
                    new ReadTask(IsoDep.get(tag), bacKey).execute();
                    camposLayout.setVisibility(View.GONE);
                    loadingLayout.setVisibility(View.VISIBLE);
                } else {
                    Snackbar.make(passportNumberView, R.string.error_input, Snackbar.LENGTH_SHORT).show();
                }
            }
        }
    }

    private static String exceptionStack(Throwable exception) {
        StringBuilder s = new StringBuilder();
        String exceptionMsg = exception.getMessage();
        if (exceptionMsg != null) {
            s.append(exceptionMsg);
            s.append(" - ");
        }
        s.append(exception.getClass().getSimpleName());
        StackTraceElement[] stack = exception.getStackTrace();

        if (stack.length > 0) {
            int count = 3;
            boolean first = true;
            boolean skip = false;
            String file = "";
            s.append(" (");
            for (StackTraceElement element : stack) {
                if (count > 0 && element.getClassName().startsWith("com.tananaev")) {
                    if (!first) {
                        s.append(" < ");
                    } else {
                        first = false;
                    }

                    if (skip) {
                        s.append("... < ");
                        skip = false;
                    }

                    if (file.equals(element.getFileName())) {
                        s.append("*");
                    } else {
                        file = element.getFileName();
                        s.append(file.substring(0, file.length() - 5)); // remove ".java"
                        count -= 1;
                    }
                    s.append(":").append(element.getLineNumber());
                } else {
                    skip = true;
                }
            }
            if (skip) {
                if (!first) {
                    s.append(" < ");
                }
                s.append("...");
            }
            s.append(")");
        }
        return s.toString();
    }

    private class ReadTask extends AsyncTask<Void, Void, Exception> {

        private IsoDep isoDep;
        private BACKeySpec bacKey;

        public ReadTask(IsoDep isoDep, BACKeySpec bacKey) {
            this.isoDep = isoDep;
            this.bacKey = bacKey;
        }

        private DG1File dg1File;
        private DG2File dg2File;
        private String imageBase64;
        private Bitmap bitmap;

        @Override
        protected Exception doInBackground(Void... params) {
            try {

                CardService cardService = CardService.getInstance(isoDep);
                cardService.open();

                PassportService service = new PassportService(cardService, NORMAL_MAX_TRANCEIVE_LENGTH, DEFAULT_MAX_BLOCKSIZE, true, false);
                service.open();

                boolean paceSucceeded = false;
                try {
                    CardSecurityFile cardSecurityFile = new CardSecurityFile(service.getInputStream(PassportService.EF_CARD_SECURITY));
                    Collection<SecurityInfo> securityInfoCollection = cardSecurityFile.getSecurityInfos();
                    for (SecurityInfo securityInfo : securityInfoCollection) {
                        if (securityInfo instanceof PACEInfo) {
                            PACEInfo paceInfo = (PACEInfo) securityInfo;
                            service.doPACE(bacKey, paceInfo.getObjectIdentifier(), PACEInfo.toParameterSpec(paceInfo.getParameterId()), null);
                            paceSucceeded = true;
                        }
                    }
                } catch (Exception e) {
                    Log.w(TAG, e);
                }

                service.sendSelectApplet(paceSucceeded);

                if (!paceSucceeded) {
                    try {
                        service.getInputStream(PassportService.EF_COM).read();
                    } catch (Exception e) {
                        service.doBAC(bacKey);
                    }
                }

                CardFileInputStream dg1In = service.getInputStream(PassportService.EF_DG1);
                dg1File = new DG1File(dg1In);

                CardFileInputStream dg2In = service.getInputStream(PassportService.EF_DG2);
                dg2File = new DG2File(dg2In);

                List<FaceImageInfo> allFaceImageInfos = new ArrayList<>();
                List<FaceInfo> faceInfos = dg2File.getFaceInfos();
                for (FaceInfo faceInfo : faceInfos) {
                    allFaceImageInfos.addAll(faceInfo.getFaceImageInfos());
                }

                if (!allFaceImageInfos.isEmpty()) {
                    FaceImageInfo faceImageInfo = allFaceImageInfos.iterator().next();

                    int imageLength = faceImageInfo.getImageLength();
                    DataInputStream dataInputStream = new DataInputStream(faceImageInfo.getImageInputStream());
                    byte[] buffer = new byte[imageLength];
                    dataInputStream.readFully(buffer, 0, imageLength);
                    InputStream inputStream = new ByteArrayInputStream(buffer, 0, imageLength);

                    bitmap = ImageUtil.decodeImage(
                            MainActivity.this, faceImageInfo.getMimeType(), inputStream);
                    imageBase64 = Base64.encodeToString(buffer, Base64.DEFAULT);
                }

            } catch (Exception e) {
                return e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Exception result) {
            camposLayout.setVisibility(View.VISIBLE);
            loadingLayout.setVisibility(View.GONE);

            if (result == null) {

                Intent intent;
                if (getCallingActivity() != null) {
                    intent = new Intent();
                } else {
                    intent = new Intent(MainActivity.this, ResultActivity.class);
                }

                MRZInfo mrzInfo = dg1File.getMRZInfo();

                intent.putExtra(ResultActivity.KEY_FIRST_NAME, mrzInfo.getSecondaryIdentifier().replace("<", " "));
                intent.putExtra(ResultActivity.KEY_LAST_NAME, mrzInfo.getPrimaryIdentifier().replace("<", " "));
                intent.putExtra(ResultActivity.KEY_GENDER, mrzInfo.getGender().toString());
                intent.putExtra(ResultActivity.KEY_STATE, mrzInfo.getIssuingState());
                intent.putExtra(ResultActivity.KEY_NATIONALITY, mrzInfo.getNationality());
                intent.putExtra(ResultActivity.KEY_CI, mrzInfo.getPersonalNumber());

//                if (bitmap != null) {
//                    if (encodePhotoToBase64) {
//                        intent.putExtra(ResultActivity.KEY_PHOTO_BASE64, imageBase64);
//                    } else {
//                        double ratio = 320.0 / bitmap.getHeight();
//                        int targetHeight = (int) (bitmap.getHeight() * ratio);
//                        int targetWidth = (int) (bitmap.getWidth() * ratio);
//
//                        intent.putExtra(ResultActivity.KEY_PHOTO,
//                            Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, false));
//                    }
//                }

                if (getCallingActivity() != null) {
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                } else {
                    startActivity(intent);
                }

            } else {
                Snackbar.make(passportNumberView, exceptionStack(result), Snackbar.LENGTH_LONG).show();
            }
        }

    }

}
