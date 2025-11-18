package net.integraa.read.controller.scanners;

import android.app.Activity;
import android.view.View;

import androidx.annotation.NonNull;

import com.scandit.datacapture.barcode.capture.*;
import com.scandit.datacapture.barcode.data.Symbology;
import com.scandit.datacapture.barcode.data.SymbologyDescription;
import com.scandit.datacapture.barcode.ui.overlay.BarcodeCaptureOverlay;
import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.common.feedback.Feedback;
import com.scandit.datacapture.core.data.FrameData;
import com.scandit.datacapture.core.source.Camera;
import com.scandit.datacapture.core.source.CameraSettings;
import com.scandit.datacapture.core.source.FrameSourceState;
import com.scandit.datacapture.core.ui.DataCaptureView;
import com.scandit.datacapture.core.ui.style.Brush;
import com.scandit.datacapture.core.ui.viewfinder.RectangularViewfinder;
import com.scandit.datacapture.core.ui.viewfinder.RectangularViewfinderStyle;

import net.integraa.read.controller.interfaces.Barcode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class BarcodeScandit extends Barcode {
    protected DataCaptureContext dataCaptureContext;
    protected BarcodeCapture barcodeCapture;
    protected Camera camera;
    protected CameraSettings cameraSettings;
    protected DataCaptureView dataCaptureView;
    protected BarcodeCaptureOverlay overlay;

    private void init() {
        dataCaptureContext = DataCaptureContext.forLicenseKey(licenseKey);
        cameraSettings = BarcodeCapture.createRecommendedCameraSettings();
        setZoom(zoom);
        camera = Camera.getDefaultCamera(cameraSettings);
        if (camera == null) {
            return;
        }
        applyTypes(types);
        dataCaptureContext.setFrameSource(camera);
    }

    @Override
    public void initialize(Activity activity, String licenseKey) {
        super.initialize(activity, licenseKey);
        init();
    }

    @Override
    public void applyTypes(List<Type> types) {
        super.applyTypes(types);
        if (dataCaptureContext == null) {
            return;
        }
        BarcodeCaptureSettings barcodeCaptureSettings = new BarcodeCaptureSettings();
        HashSet<Symbology> symbologies = new HashSet<>();
        for(Barcode.Type type:types) {
            switch (type) {
                case ITF:
                    symbologies.add(Symbology.INTERLEAVED_TWO_OF_FIVE);
                    break;
                case Code128:
                    symbologies.add(Symbology.CODE128);
                    break;
                case Code93:
                    symbologies.add(Symbology.CODE93);
                    break;
                case Code39:
                    symbologies.add(Symbology.CODE39);
                    break;
                case AZTEC:
                    symbologies.add(Symbology.AZTEC);
                    break;
                case Pdf417:
                    symbologies.add(Symbology.PDF417);
                    break;
                case DataMatrix:
                    symbologies.add(Symbology.DATA_MATRIX);
                    break;
                case QrCode:
                    symbologies.add(Symbology.QR);
                    break;
                case EAN13:
                    symbologies.add(Symbology.EAN13_UPCA);
                    break;
                case UPCA:
                    symbologies.add(Symbology.EAN13_UPCA);
                    break;
                case EAN8:
                    symbologies.add(Symbology.EAN8);
                    break;
                case UPCE:
                    symbologies.add(Symbology.UPCE);
                    break;
                case Code11:
                    symbologies.add(Symbology.CODE11);
                    break;
                case Code25:
                    symbologies.add(Symbology.CODE25);
                    break;
                case Codebar:
                    symbologies.add(Symbology.CODABAR);
                    break;
                case MSI_PLESSEY:
                    symbologies.add(Symbology.MSI_PLESSEY);
                    break;
                case MicroQrCode:
                    symbologies.add(Symbology.MICRO_QR);
                    break;
                case MaxiCode:
                    symbologies.add(Symbology.MAXI_CODE);
                    break;
                case DotCode:
                    symbologies.add(Symbology.DOT_CODE);
                    break;
                case Kix:
                    symbologies.add(Symbology.KIX);
                    break;
                case RM4SCC:
                    symbologies.add(Symbology.ROYAL_MAIL_4STATE);
                    break;
                case GS1_DATABAR:
                    symbologies.add(Symbology.GS1_DATABAR);
                    break;
                case GS1_DATABAR_EXPANDED:
                    symbologies.add(Symbology.GS1_DATABAR_EXPANDED);
                    break;
                case GS1_DATABAR_LIMITED:
                    symbologies.add(Symbology.GS1_DATABAR_LIMITED);
                    break;
                case MicroPdf417:
                    symbologies.add(Symbology.MICRO_PDF417);
                    break;
                case Code32:
                    symbologies.add(Symbology.CODE32);
                    break;
                case LAPA4SC:
                    symbologies.add(Symbology.LAPA4SC);
                    break;
                case IATA_TWO_OF_FIVE:
                    symbologies.add(Symbology.IATA_TWO_OF_FIVE);
                    break;
                case MATRIX_TWO_OF_FIVE:
                    symbologies.add(Symbology.MATRIX_TWO_OF_FIVE);
                    break;
            }
        }
        barcodeCaptureSettings.enableSymbologies(symbologies);
        barcodeCapture = BarcodeCapture.forDataCaptureContext(dataCaptureContext, barcodeCaptureSettings);
        barcodeCapture.addListener(new BarcodeCaptureListener() {
            @Override
            public void onBarcodeScanned(@NonNull BarcodeCapture barcodeCapture, @NonNull BarcodeCaptureSession session, @NonNull FrameData frameData) {
                if (session.getNewlyRecognizedBarcode() == null) return;
                com.scandit.datacapture.barcode.data.Barcode barcode = session.getNewlyRecognizedBarcode();
                if(barcode!=null) {
                    ArrayList<Barcode.Result> results = new ArrayList<>(1);
                    results.add(new BarcodeScandit.Result(barcode));
                    BarcodeScandit.this.onCodeScanned(results);
                }
            }
            @Override
            public void onSessionUpdated(@NonNull BarcodeCapture barcodeCapture, @NonNull BarcodeCaptureSession barcodeCaptureSession, @NonNull FrameData frameData) {
            }
            @Override
            public void onObservationStarted(@NonNull BarcodeCapture barcodeCapture) {
            }
            @Override
            public void onObservationStopped(@NonNull BarcodeCapture barcodeCapture) {
            }
        });
    }
    @Override
    public void onResume() {
        if (barcodeCapture!=null) {
            barcodeCapture.setEnabled(true);
        }
        if (camera!=null) {
            camera.switchToDesiredState(FrameSourceState.ON, null);
        }
    }

    @Override
    public void onPause() {
        if (barcodeCapture!=null) {
            barcodeCapture.setEnabled(false);
        }
        if (camera!=null) {
            camera.switchToDesiredState(FrameSourceState.OFF, null);
        }
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public View getView() {
        dataCaptureView = DataCaptureView.newInstance(activity, dataCaptureContext);
        overlay = BarcodeCaptureOverlay.newInstance(barcodeCapture, dataCaptureView);
        overlay.setViewfinder(new RectangularViewfinder(RectangularViewfinderStyle.SQUARE));
        return dataCaptureView;
    }

    @Override
    public void start() {
        if(barcodeCapture==null) {
            return;
        }
        barcodeCapture.setEnabled(true);
    }

    @Override
    public void stop() {
        if(barcodeCapture==null) {
            return;
        }
        barcodeCapture.setEnabled(false);
    }

    @Override
    public boolean hasPause() {
        if(barcodeCapture==null) {
            return false;
        }
        return !barcodeCapture.isEnabled();
    }

    @Override
    public void pause() {
        if(barcodeCapture==null) {
            return;
        }
        barcodeCapture.setEnabled(false);
    }

    @Override
    public void resume() {
        if(barcodeCapture==null) {
            return;
        }
        barcodeCapture.setEnabled(true);
    }

    @Override
    public void setVibrate(boolean value) {

    }

    @Override
    public void setSound(boolean value) {

    }

    @Override
    public void setZoom(float value) {
        super.setZoom(value);
        if(cameraSettings==null) {
            return;
        }
        cameraSettings.setZoomFactor(zoom);
    }

    class Result implements Barcode.Result {
        com.scandit.datacapture.barcode.data.Barcode item;
        Result(com.scandit.datacapture.barcode.data.Barcode item) {
            this.item=item;
        }

        @Override
        public String getValue() {
            return item.getData();
        }

        @Override
        public Type getType() {
            return null;
        }
    }
}
