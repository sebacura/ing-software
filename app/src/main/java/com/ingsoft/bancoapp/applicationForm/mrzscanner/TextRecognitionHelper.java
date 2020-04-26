package com.ingsoft.bancoapp.applicationForm.mrzscanner;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.ingsoft.bancoapp.R;
import com.ingsoft.bancoapp.applicationForm.mrzscanner.mrz.MrzParser;
import com.ingsoft.bancoapp.applicationForm.mrzscanner.mrz.MrzRecord;
import com.ingsoft.bancoapp.applicationForm.mrzscanner.mrz.types.MrzFormat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by jsjem on 17.11.2016.
 */
public class TextRecognitionHelper {

	private static final String TAG = "TextRecognitionHelper";

	private static final String TESSERACT_TRAINED_DATA_FOLDER = "tessdata";
	private static String TESSERACT_PATH = null;

	private final Context applicationContext;
	private final TessBaseAPI tessBaseApi;

	Pattern passportLine1Pattern = Pattern.compile("[A-Z0-9<]{2}[A-Z<]{3}[A-Z0-9<]{39}");
	Pattern passportLine2Pattern = Pattern.compile("[A-Z0-9<]{9}[0-9]{1}[A-Z<]{3}[0-9]{6}[0-9]{1}[FM<]{1}[0-9]{6}[0-9]{1}[A-Z0-9<]{14}[0-9]{1}[0-9]{1}");

	private List<MrzFormat> mrzFormats = new ArrayList<>();

	private static List<MrzFormat> supportedFormats = new ArrayList<>();

	private OnMRZScanned listener;

	public String code;
	public String expiry_date;
	public String date_of_birth;

	/**
	 * Constructor.
	 *
	 * @param context Application context.
	 */
	public TextRecognitionHelper(final Context context, final OnMRZScanned listener) {
		this.applicationContext = context.getApplicationContext();
		this.listener = listener;
		this.tessBaseApi = new TessBaseAPI();
		this.TESSERACT_PATH = context.getFilesDir().getAbsolutePath() + "/";
		prepareTesseract("ocrb");

		mrzFormats.add(MrzFormat.MRTD_TD1);

		supportedFormats.add(MrzFormat.MRTD_TD1);
	}

	/**
	 * Initialize tesseract engine.
	 *
	 * @param language Language code in ISO-639-3 format.
	 */
	public void prepareTesseract(final String language) {
		try {
			prepareDirectory(TESSERACT_PATH + TESSERACT_TRAINED_DATA_FOLDER);
		} catch (Exception e) {
			e.printStackTrace();
		}

		copyTessDataFiles(TESSERACT_TRAINED_DATA_FOLDER);
		tessBaseApi.init(TESSERACT_PATH, language);
	}

	private void prepareDirectory(String path) {

		File dir = new File(path);
		if (!dir.exists()) {
			if (!dir.mkdirs()) {
				Log.e(TAG,
						"ERROR: Creation of directory " + path + " failed, check does Android Manifest have permission to write to external storage.");
			}
		} else {
			Log.i(TAG, "Created directory " + path);
		}
	}

	private void copyTessDataFiles(String path) {
		try {
			String fileList[] = applicationContext.getAssets().list(path);

			for (String fileName : fileList) {
				String pathToDataFile = TESSERACT_PATH + path + "/" + fileName;
				if (!(new File(pathToDataFile)).exists()) {
					InputStream in = applicationContext.getAssets().open(path + "/" + fileName);
					OutputStream out = new FileOutputStream(pathToDataFile);
					byte[] buf = new byte[1024];
					int length;
					while ((length = in.read(buf)) > 0) {
						out.write(buf, 0, length);
					}
					in.close();
					out.close();
					Log.d(TAG, "Copied " + fileName + "to tessdata");
				}
			}
		} catch (IOException e) {
			Log.e(TAG, "Unable to copy files to tessdata " + e.getMessage());
		}
	}

	/**
	 * Set image for recognition.
	 *
	 * @param bitmap Image data.
	 */
	public void setBitmap(final Bitmap bitmap) {
		//tessBaseApi.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_BLOCK_VERT_TEXT);
		tessBaseApi.setImage(bitmap);
	}

	/**
	 * Get recognized text for image.
	 *
	 * @return Recognized text string.
	 */
	public void doOCR() {
		String text = tessBaseApi.getUTF8Text();
		Log.v(TAG, "OCRED TEXT: " + text);
		checkMRZ(text);
	}

	public void checkMRZ(String txt){
		final String mrzText = preProcessText(txt);

		if(mrzText != null) {
			Log.i("Found possible MRZ", mrzText);
			try {
				final MrzRecord mrzRecord = MrzParser.parse(mrzText);
				Log.i("Mrz", mrzRecord.toString());

				if(mrzRecord != null) {
					if(supportedFormats.contains(mrzRecord.format)) {
						boolean additionalPassportCheckOK = true;
						if (mrzRecord.validDocumentNumber && mrzRecord.validExpirationDate && mrzRecord.validDateOfBirth) {
							this.code = mrzRecord.documentNumber;
							this.expiry_date = mrzRecord.expirationDate.toString();
							this.date_of_birth = mrzRecord.dateOfBirth.toString();
							return;
						}
						if(additionalPassportCheckOK) {
							new Handler(Looper.getMainLooper()).post(new Runnable() {
								@Override
								public void run() {
									listener.onScanned(mrzRecord.toString());
								}
							});
							return;
						}
					}
				}
			} catch (Exception e){
				Log.i("MRZ Parser", "Failed");
			}
		}
	}

	private String preProcessText(String txt) {
		String[] lines = txt.split("\n");
		if(lines == null || lines.length < 1)
			return null;
		for(MrzFormat mrzFormat : mrzFormats) {
			for (int i = lines.length - 1; i >= 0; i--) {
				String line2 = lines[i].replace(" ", "");
				if(line2.length() >= mrzFormat.columns){
					if(i == 0)
						break;
					String line1 = lines[i - 1].replace(" ", "");
					if(line1.length() >= mrzFormat.columns)
						if(mrzFormat.rows == 2)
							return line1.substring(0, mrzFormat.columns) + "\n" +
									line2.substring(0, mrzFormat.columns);
						else if(mrzFormat.rows == 3){
							if(lines.length < 2 || i < 1)
								break;
							String line0 = lines[i - 2].replace(" ", "");
							if(line0.length() >= mrzFormat.columns)
								return line0.substring(0, mrzFormat.columns) + "\n" +
										line1.substring(0, mrzFormat.columns) + "\n" +
										line2.substring(0, mrzFormat.columns);
							else
								break;
						}
					else
						break;
				}
			}
		}
		return null;
	}

	/**
	 * Clear tesseract data.
	 */
	public void stop() {
		tessBaseApi.clear();
	}

	public interface OnMRZScanned{
		void onScanned(String mrzText);
	}
}
