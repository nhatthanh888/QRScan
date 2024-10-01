package com.example.qrscan.customcamera;

import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import me.dm7.barcodescanner.core.CameraUtils;
import me.dm7.barcodescanner.core.CameraWrapper;

public class CameraHandler extends HandlerThread {
    private final BarcodeScanner mScannerView;

    public CameraHandler(BarcodeScanner scannerView) {
        super("CameraHandlerThread");
        this.mScannerView = scannerView;
        this.start();
    }

    public void startCamera(final int cameraId) {
        Handler localHandler = new Handler(this.getLooper());
        localHandler.post(() -> {
            final Camera camera = CameraUtils.getCameraInstance(cameraId);
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(() -> CameraHandler.this.mScannerView.setupCameraPreview(CameraWrapper.getWrapper(camera, cameraId)));
        });
    }
}