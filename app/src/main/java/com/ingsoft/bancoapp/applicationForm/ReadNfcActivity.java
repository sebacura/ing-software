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
package com.ingsoft.bancoapp.applicationForm;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ingsoft.bancoapp.ImageUtil;
import com.ingsoft.bancoapp.R;
import com.ingsoft.bancoapp.applicationForm.mrzscanner.FrameOverlay;
import com.ingsoft.bancoapp.applicationForm.mrzscanner.TextRecognitionHelper;
import com.ingsoft.bancoapp.applicationForm.mrzscanner.mrz.MrzRecord;
import com.ingsoft.bancoapp.myApplications.StatusActivity;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraOptions;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.controls.Flash;
import com.otaliastudios.cameraview.frame.Frame;
import com.otaliastudios.cameraview.frame.FrameProcessor;

import net.sf.scuba.smartcards.CardFileInputStream;
import net.sf.scuba.smartcards.CardService;

import org.jmrtd.BACKey;
import org.jmrtd.BACKeySpec;
import org.jmrtd.PassportService;
import org.jmrtd.lds.CardSecurityFile;
import org.jmrtd.lds.SecurityInfo;
import org.jmrtd.lds.icao.DG1File;
import org.jmrtd.lds.icao.DG2File;
import org.jmrtd.lds.iso19794.FaceImageInfo;
import org.jmrtd.lds.iso19794.FaceInfo;
import org.jmrtd.lds.icao.MRZInfo;
import org.jmrtd.lds.PACEInfo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.jmrtd.PassportService.DEFAULT_MAX_BLOCKSIZE;
import static org.jmrtd.PassportService.NORMAL_MAX_TRANCEIVE_LENGTH;

public class ReadNfcActivity extends AppCompatActivity {
    private static final String TAG = ReadNfcActivity.class.getSimpleName();

    private CameraView camera;
    private FrameOverlay viewFinder;
    private TextRecognitionHelper textRecognitionHelper;
    private AtomicBoolean processing = new AtomicBoolean(false);
    private ReadNfcActivity.ProcessOCR processOCR;
    private Bitmap originalBitmap = null;
    private Bitmap scannable = null;
    private View main_layout;
    private View footer;


    private boolean mrzOk=false;

    private boolean passportNumberFromIntent = false;

    private View camposLayout;
    private View loadingLayout;
    private Handler timer = new Handler();

    Button btnIrFormulario2;

    NfcAdapter adapter;

    View btnLeerMrz;
    private  Menu menu;

    //Timer para error al leer mrz
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // Start your application main_activity
            if (!mrzOk) {
                Intent i = new Intent(getApplicationContext(), ReadNfcActivity.class);
                startActivity(i);
                Toast.makeText(getApplicationContext(), "No se pudo leer la cédula correctamente, intente nuevamente!", Toast.LENGTH_LONG).show();

                // Close this activity
                finish();
            }
        }
    }; // FIN Timer


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Controlo que tenga NFC y que lo tenga prendido
        NfcManager manager = (NfcManager) getSystemService(Context.NFC_SERVICE);
        adapter = manager.getDefaultAdapter();

        if (adapter != null && !adapter.isEnabled()) {
//            findViewById(R.id.provisorio).setVisibility(View.VISIBLE);
            findViewById(R.id.btnLeerMrz).setVisibility(View.GONE);

            Toast.makeText(getApplicationContext(), "El NFC de su celular se encuentra apagado, enciéndalo para poder continuar", Toast.LENGTH_LONG).show();

        }else if (adapter==null) {
//            findViewById(R.id.provisorio).setVisibility(View.VISIBLE);
            findViewById(R.id.btnLeerMrz).setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "Su celular no cuenta con NFC, no es posible utilizar la aplicación!", Toast.LENGTH_LONG).show();
        } else {
            findViewById(R.id.btnLeerMrz).setVisibility(View.VISIBLE);
        }


        //Seguir sin mrz
        findViewById(R.id.provisorio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PhotoActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });
        //Fin seguir sin mrz

        //Abrir camara para leer mrz
        btnLeerMrz = findViewById(R.id.btnLeerMrz);
        btnLeerMrz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera = findViewById(R.id.camera);
                camera.setVisibility(View.VISIBLE);
                camera.setLifecycleOwner(ReadNfcActivity.this);
                main_layout = findViewById(R.id.main_layout);
                main_layout.setVisibility(View.GONE);
                footer = findViewById(R.id.footer);
                footer.setVisibility(View.GONE);
                menu.findItem(R.id.flash_menu).setVisible(true);


                if (!mrzOk) {
                    camera.addCameraListener(new CameraListener() {
                        @Override
                        public void onCameraOpened(@NonNull CameraOptions options) {
                            viewFinder = new FrameOverlay(ReadNfcActivity.this);

                            // Timer para cerrar la camara despues de 20 segundos
                            timer.postDelayed(runnable, 20000);


                            camera.addView(viewFinder);
                            camera.addFrameProcessor(frameProcessor);
                            }
                    });

                    }
                }

        });
        //Fin abrir camara

        //MZR READER
        textRecognitionHelper = new TextRecognitionHelper(this, new TextRecognitionHelper.OnMRZScanned() {
            @Override
            public void onScanned(MrzRecord mrzRecord) {
                try {
                    FileOutputStream fos = new FileOutputStream(getFilesDir().getAbsolutePath() + "/" + "mrzimage.png");
                    originalBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }


                try {
                    FileOutputStream fos = new FileOutputStream(getFilesDir().getAbsolutePath() + "/" + "scannable.png");
                    scannable.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //Si tiene los valores correctos del MRZ, cierra la camara y prosigue al NFC
                if (mrzRecord.validCodeNumber && mrzRecord.documentNumber!= null && mrzRecord.validExpirationDate && mrzRecord.validDateOfBirth) {
                    ((TextView) findViewById(R.id.date_of_birth)).setText(mrzRecord.dateOfBirth.toString());
                    ((TextView) findViewById(R.id.expiry_date)).setText(mrzRecord.expirationDate.toString());
                    ((TextView) findViewById(R.id.ci_code)).setText(mrzRecord.documentNumber);
                    camera.setVisibility(View.GONE);
                    findViewById(R.id.paso2).setVisibility(View.VISIBLE);
//                    main_layout.setVisibility(View.VISIBLE);
                    footer.setVisibility(View.VISIBLE);
                    menu.findItem(R.id.flash_menu).setVisible(false);
                    camera.setFlash(Flash.OFF);
                    mrzOk = true;
                    processing.set(false);
                    camera.removeFrameProcessor(frameProcessor);
                    timer.removeCallbacks(runnable);
                    camera.close();
                    return;
                }
            }
        });
        //FIN LEER MRZ

        //Pasar a siguiente pantalla
        btnIrFormulario2 = (Button) findViewById(R.id.irFormulario2);
        btnIrFormulario2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PhotoActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });
        //Fin pasar a siguiente pantalla


        camposLayout = findViewById(R.id.campos);
        loadingLayout = findViewById(R.id.loading_layout);

        // bottom nav bar
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.action_main);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_main:
                        return true;
                    case R.id.action_status:
                        Intent a = new Intent(getApplicationContext(), StatusActivity.class);
                        startActivity(a);
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
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

    private static String convertDate(String input) {
        if (input == null) {
            return null;
        }
        try {
            return new SimpleDateFormat("yyMMdd", Locale.US)
                    .format(new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(input));
        } catch (ParseException e) {
            Log.w(ReadNfcActivity.class.getSimpleName(), e);
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
                TextView ci_code   =  findViewById(R.id.ci_code);
                TextView expiry_date   = findViewById(R.id.expiry_date);
                TextView date_of_birth   =  findViewById(R.id.date_of_birth);

                String passportNumber = ci_code.getText().toString();
                String expirationDate = convertDate(expiry_date.getText().toString());
                String birthDate = convertDate(date_of_birth.getText().toString());
                if (passportNumber != null && !passportNumber.isEmpty()
                        && expirationDate != null && !expirationDate.isEmpty()
                        && birthDate != null && !birthDate.isEmpty()) {
                    BACKeySpec bacKey = new BACKey(passportNumber, birthDate, expirationDate);
                    new ReadTask(IsoDep.get(tag), bacKey).execute();
                    camposLayout.setVisibility(View.GONE);
                    loadingLayout.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(this, "Primero debe leer la parte posterior de su cédula", Toast.LENGTH_LONG).show();
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
                if (count > 0 && element.getClassName().startsWith("com.ingsoft")) {
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

                    //Para imprimirla en la app
                    bitmap = ImageUtil.decodeImage(
                            ReadNfcActivity.this, faceImageInfo.getMimeType(), inputStream);

                    imageBase64 = Base64.encodeToString(buffer, Base64.DEFAULT);

                    Log.d("imageBase64", imageBase64);

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

                Intent resultActivityIntent;
                if (getCallingActivity() != null) {
                    resultActivityIntent = new Intent();
                } else {
                    resultActivityIntent = new Intent(getApplicationContext(), ReadNfcResultActivity.class);
                }

                MRZInfo mrzInfo = dg1File.getMRZInfo();

                resultActivityIntent.putExtra(ReadNfcResultActivity.KEY_FIRST_NAME, mrzInfo.getSecondaryIdentifier().replace("<", " "));
                resultActivityIntent.putExtra(ReadNfcResultActivity.KEY_LAST_NAME, mrzInfo.getPrimaryIdentifier().replace("<", " "));
                resultActivityIntent.putExtra(ReadNfcResultActivity.KEY_GENDER, mrzInfo.getGender().toString());
                resultActivityIntent.putExtra(ReadNfcResultActivity.KEY_STATE, mrzInfo.getIssuingState());
                resultActivityIntent.putExtra(ReadNfcResultActivity.KEY_NATIONALITY, mrzInfo.getNationality());
                resultActivityIntent.putExtra(ReadNfcResultActivity.KEY_CI, mrzInfo.getPersonalNumber());

                if (bitmap != null) {
                    resultActivityIntent.putExtra(ReadNfcResultActivity.KEY_CI_PHOTO_BASE64, imageBase64);
                }

                if (getCallingActivity() != null) {
                    setResult(Activity.RESULT_OK, resultActivityIntent);
                    finish();
                } else {
                    startActivity(resultActivityIntent);
                    overridePendingTransition(0,0);
                }

            } else {
                timer.removeCallbacks(runnable);
                Toast.makeText(getApplicationContext(), "No se pudo leer la cédula correctamente, intente nuevamente!", Toast.LENGTH_LONG).show();
                return;

            }
            //Pasar a siguiente pantalla
            btnIrFormulario2 = (Button) findViewById(R.id.irFormulario2);
            btnIrFormulario2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), PhotoActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                }
            });
            //Fin pasar a siguiente pantalla
        }


    }

    //PARA LEER EL MRZ CON LA CAMARA
    private FrameProcessor frameProcessor = new FrameProcessor() {
        @Override
        public void process(@NonNull Frame frame) {
            if (!mrzOk && frame.getData() != null && !processing.get()) {
                processing.set(true);

                YuvImage yuvImage = new YuvImage(frame.getData(), ImageFormat.NV21, frame.getSize().getWidth(), frame.getSize().getHeight(), null);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                yuvImage.compressToJpeg(new Rect(0, 0, frame.getSize().getWidth(), frame.getSize().getHeight()), 100, os);
                byte[] jpegByteArray = os.toByteArray();

                Bitmap bitmap = BitmapFactory.decodeByteArray(jpegByteArray, 0, jpegByteArray.length);

                if(bitmap != null) {
                    bitmap = rotateImage(bitmap, frame.getRotation());

                    bitmap = getViewFinderArea(bitmap);

                    originalBitmap = bitmap;

                    scannable = getScannableArea(bitmap);

                    processOCR = new ReadNfcActivity.ProcessOCR();
                    processOCR.setBitmap(scannable);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            processOCR.execute();

                        }
                    });
                }
            }
        }
    };

    private Bitmap getViewFinderArea(Bitmap bitmap) {
        int sizeInPixel = getResources().getDimensionPixelSize(R.dimen.frame_margin);
        int center = bitmap.getHeight() / 2;

        int left = sizeInPixel;
        int right = bitmap.getWidth() - sizeInPixel;
        int width = right - left;
        int frameHeight = (int) (width / 1.42f); // Passport's size (ISO/IEC 7810 ID-3) is 125mm × 88mm

        int top = center - (frameHeight / 2);

        bitmap = Bitmap.createBitmap(bitmap, left, top,
                width, frameHeight);

        return bitmap;
    }

    private Bitmap getScannableArea(Bitmap bitmap){
        int top = bitmap.getHeight() * 4 / 10;

        bitmap = Bitmap.createBitmap(bitmap, 0, top,
                bitmap.getWidth(), bitmap.getHeight() - top);

        return bitmap;
    }

    private Bitmap rotateImage(Bitmap bitmap, int rotate){
        Log.v(TAG, "Rotation: " + rotate);

        if (rotate != 0) {

            // Getting width & height of the given image.
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();

            // Setting pre rotate
            Matrix mtx = new Matrix();
            mtx.preRotate(rotate);

            // Rotating Bitmap
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
        }

        // Convert to ARGB_8888, required by tess
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        return bitmap;
    }

    /**
     * reduces the size of the image
     * @param image
     * @param maxSize
     * @return
     */
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
}

    private class ProcessOCR extends AsyncTask {

        Bitmap bitmap = null;

        @Override
        protected Object doInBackground(Object[] objects) {
            if (bitmap != null) {

                textRecognitionHelper.setBitmap(bitmap);

                textRecognitionHelper.doOCR();

                textRecognitionHelper.stop();

            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            processing.set(false);
        }

        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
        }
    }

    // Agrega boton de flash solo con la cámara abierta
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mrz_scanner, menu);
        this.menu = menu;
        menu.findItem(R.id.flash_menu).setVisible(false);
        return true;
    }

    //Prender flash
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.flash_menu:
                if(camera.getFlash() == Flash.OFF){
                    camera.setFlash(Flash.TORCH);
                    item.setIcon(R.drawable.ic_flash_off_black_24dp);
                } else{
                    camera.setFlash(Flash.OFF);
                    item.setIcon(R.drawable.ic_flash_on_black_24dp);
                }
                return true;
        }
        return false;
    }

}
