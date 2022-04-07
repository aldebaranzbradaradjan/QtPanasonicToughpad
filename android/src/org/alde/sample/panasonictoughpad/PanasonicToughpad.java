package org.alde.sample.panasonictoughpad;

import com.panasonic.toughpad.android.api.ToughpadApi;
import com.panasonic.toughpad.android.api.ToughpadApiListener;
import com.panasonic.toughpad.android.api.barcode.BarcodeData;
import com.panasonic.toughpad.android.api.barcode.BarcodeException;
import com.panasonic.toughpad.android.api.barcode.BarcodeListener;
import com.panasonic.toughpad.android.api.barcode.BarcodeReader;
import com.panasonic.toughpad.android.api.barcode.BarcodeReaderManager;
import com.panasonic.toughpad.android.api.appbtn.AppButtonManager;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.content.Intent;

import java.util.List;
import java.util.concurrent.TimeoutException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.lang.reflect.Method;

public class PanasonicToughpad {

    public PanasonicToughpad() {}

    public static ToughpadBarcode createToughpadBarcode( Context ctx ) {
        ToughpadBarcode bar = new ToughpadBarcode();
        bar.initApi( ctx );
        return bar;
    }
}

class ToughpadBarcode implements ToughpadApiListener, BarcodeListener {

    private static native void readBarcode(String device, String symbology, String data);

    private static final String TAG = "Toughpad";

    private List<BarcodeReader> readers;
    private BarcodeReader selectedReader;

    public ToughpadBarcode() {}

    public void enableReader() {
        selectLaserDevice();
        enableSelectedReaderDevice(true);
        Log.d(TAG, "Enabling reader device");
    }

    public void pressSoftwareTrigger(boolean flag) {
        try {
            if ( !isSelectedReaderDeviceEnabled() && flag ) return;
            selectedReader.pressSoftwareTrigger(flag);
            Log.d(TAG, "Toggle reader device");
        } catch (BarcodeException ex) {
            handleError(ex);
        }
    }

    public void initApi(Context ctx) {
        if (!ToughpadApi.isAlreadyInitialized()) {
            readers = null;
            selectedReader = null;
            ToughpadApi.initialize(ctx, this);
            ctx.startService(new Intent(ctx, ButtonService.class));
        }
    }

    public void destroyApi() {
        ToughpadApi.destroy();
    }

    private void handleError(final Exception ex) {
        Log.e(TAG, "Toughpad API error", ex);
    }

    public void onApiConnected(int version) {
        readers = BarcodeReaderManager.getBarcodeReaders();
        Log.d(TAG, "Toughpad API initialized");
        Log.d(TAG, "Available devices : ");
        for (BarcodeReader reader : readers) {
            Log.d(TAG, " => " + reader.getDeviceName() + ", " + reader.getBarcodeType());
        }
    }

    public void onApiDisconnected() {
        Log.d(TAG, "ToughpadBarcode.onApiDisconnected");
    }

    public void onRead(BarcodeReader bsObj, final BarcodeData result) {
        readBarcode(bsObj.getDeviceName(), result.getSymbology(), result.getTextData());
        Log.d(TAG, "ToughpadBarcode.onRead : " + bsObj.getDeviceName() + " "
                   + result.getSymbology() + " " + result.getTextData());
        pressSoftwareTrigger(false);
    }

    public void disableReaderDevice(BarcodeReader reader) {
        try {
            selectedReader.disable();
            selectedReader.clearBarcodeListener();
            Log.d(TAG, "Device : " + selectedReader.getDeviceName() + " disabled");
        } catch (BarcodeException ex) {
            handleError(ex);
        }
    }

    public void enableReaderDevice(BarcodeReader reader) {
        EnableReaderTask task = new EnableReaderTask();
        task.execute(selectedReader);
    }

    public boolean hasSelectedReaderDevice() {
        return this.selectedReader != null;
    }

    public boolean isSelectedReaderDeviceEnabled() {
        return selectedReader != null && selectedReader.isEnabled();
    }

    public void enableSelectedReaderDevice(boolean flag) {
        if (selectedReader.isEnabled() && !flag) {
            disableReaderDevice(selectedReader);
        } else if (!selectedReader.isEnabled() && flag) {
            enableReaderDevice(selectedReader);
        }
    }

    public void selectLaserDevice() {
        for (int i = 0; i < readers.size(); i++) {
            if (readers.get(i).getBarcodeType() != BarcodeReader.BARCODE_TYPE_CAMERA) {
                selectReader(i);
                break;
            }
        }
    }

    public void selectReader(int position) {
        selectedReader = readers.get(position);
        Log.d(TAG, "Select Device: " + selectedReader.getDeviceName());
    }

    public void unselectReader() {
        selectedReader = null;
        Log.d(TAG, "Unselect device");
    }

    public String getDeviceTypeString(BarcodeReader reader) {
        String deviceType = "Unknown";
        switch (reader.getBarcodeType()) {
            case BarcodeReader.BARCODE_TYPE_CAMERA:
                deviceType = "BARCODE_TYPE_CAMERA";
                break;
            case BarcodeReader.BARCODE_TYPE_ONE_DIMENSIONAL:
                deviceType = "BARCODE_TYPE_ONE_DIMENSIONAL";
                break;
            case BarcodeReader.BARCODE_TYPE_TWO_DIMENSIONAL:
                deviceType = "BARCODE_TYPE_TWO_DIMENSIONAL";
                break;
        }
        return deviceType;
    }

    private class EnableReaderTask extends AsyncTask<BarcodeReader, Void, Boolean> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(BarcodeReader... params) {
            Log.d(TAG, "EnableReaderTask.doInBackground");
            try {
                params[0].enable(10000);
                params[0].addBarcodeListener(ToughpadBarcode.this);
                return true;
            } catch (BarcodeException ex) {
                handleError(ex);
                return false;
            } catch (TimeoutException ex) {
                handleError(ex);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
        }
    }
}

