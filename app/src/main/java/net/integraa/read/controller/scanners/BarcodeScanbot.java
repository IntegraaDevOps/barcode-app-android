package net.integraa.read.controller.scanners;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import net.integraa.read.controller.Scanner;
import net.integraa.read.controller.interfaces.Barcode;

import java.util.ArrayList;
import java.util.List;
import io.scanbot.sap.IScanbotSDKLicenseErrorHandler;
import io.scanbot.sap.SdkFeature;
import io.scanbot.sap.SdkLicenseInfo;
import io.scanbot.sap.Status;
import io.scanbot.sdk.SdkLicenseError;
import io.scanbot.sdk.barcode.BarcodeFormat;
import io.scanbot.sdk.barcode.BarcodeFormatCommonConfiguration;
import io.scanbot.sdk.barcode.BarcodeFormatConfigurationBase;
import io.scanbot.sdk.barcode.BarcodeFormatUpcEanConfiguration;
import io.scanbot.sdk.barcode.BarcodeItem;
import io.scanbot.sdk.barcode.BarcodeScanner;
import io.scanbot.sdk.barcode.BarcodeScannerConfiguration;
import io.scanbot.sdk.barcode.BarcodeScannerFrameHandler;
import io.scanbot.sdk.barcode.BarcodeScannerResult;
import io.scanbot.sdk.barcode.ui.IBarcodeScannerViewCallback;
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDK;
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDKInitializer;
import io.scanbot.sdk.camera.CaptureInfo;
import io.scanbot.sdk.camera.FrameHandlerResult;
import io.scanbot.sdk.pdf.PdfImagesExtractor;
import io.scanbot.sdk.barcode.ui.BarcodeScannerView;

public class BarcodeScanbot extends Barcode {
    protected BarcodeScannerView barcodeScannerView=null;
    protected BarcodeScanner barcodeScanner=null;
    //protected String licenseKey = "RdD7Xno2htgQvnFXQwDxpZoTqVRwoAL0ExJ4xqOFpO5G33a881o/+itrFINsr9NElQp4YyAkxN6R/Wi5NjZbeWRvDQt7zDmNqBQyq7nwALJnMDVKN/FoRkyKyHKUov6CgLQXdw31dlsgS8avd1D/FGMY/TaGTbjaemQMpbr/GwULjR1tpdDoQorqES8pUlfqudFhqwomJo83f81AAg6lxesnN7UzYK60Jmm7PezMl/jzgtt6aERjLu1u/5D7Wk8zNfXnLY3TQWnkNZpNEnYTuYo2hGvYMAltavzBbPjSQ/AU7ulsOqw5wnxeOs8PFii5q5e0Nz0z+qm5HH2pOtODSg==\nU2NhbmJvdFNESwpuZXQuaW50ZWdyYWEucmVhZAoxNzY0NTQ3MTk5CjUxMgoz\n";

    private void init() {
        new ScanbotBarcodeScannerSDKInitializer()
                .withLogging(true,false)
                .license(Scanner.getApplication(),licenseKey)
                .licenceErrorHandler(new IScanbotSDKLicenseErrorHandler() {
                    @Override
                    public void handleLicenceStatusError(Status status, SdkFeature feature, String statusMessage) {
                        Log.d("BarcodeScanbot", "+++> License status: "+status.name()+". Status message: "+statusMessage);
                        if(feature.equals(SdkFeature.NoSdkFeature)){
                            Log.d("BarcodeScanbot","+++> Feature not available:"+feature.name());
                        }
                    }
                })
                .pdfImagesExtractorType(PdfImagesExtractor.Type.PDFIUM) // select PDFium or default android pdf library to extract barcode image
                // Uncomment to switch back to the legacy camera approach in Ready-To-Use UI screens
                // .useCameraXRtuUi(false)
                //.sdkFilesDirectory(this, getExternalFilesDir(null)!!)
                .initialize(Scanner.getApplication());
        Log.d("BarcodeScanbot", "Scanbot Barcode Scanner SDK was initialized");
        SdkLicenseInfo licenseInfo = new ScanbotBarcodeScannerSDK(Scanner.getApplication()).getLicenseInfo();
        Log.d("BarcodeScanbot", "License status: "+licenseInfo.getStatus());
        Log.d("BarcodeScanbot", "License isValid: "+licenseInfo.isValid());
        Log.d("BarcodeScanbot", "License expirationDate: "+licenseInfo.getExpirationDate());
    }

    @Override
    public void initialize(Activity activity, String licenseKey) {
        super.initialize(activity, licenseKey);
        init();
    }

    @Override
    public void applyTypes(List<Barcode.Type> types) {
        super.applyTypes(types);
        if(barcodeScanner==null) {
            return;
        }
        BarcodeScannerConfiguration barcodeScannerConfiguration= barcodeScanner.copyCurrentConfiguration();
        List<BarcodeFormatConfigurationBase> listBarcodeFormatConfigurationBase = new ArrayList<>(types.size());
        for(Barcode.Type type:types) {
            switch (type) {
                case ITF:
                    listBarcodeFormatConfigurationBase.add(BarcodeFormatConfigurationBase.barcodeFormatItfConfiguration());
                    break;
                case Code128:
                    listBarcodeFormatConfigurationBase.add(BarcodeFormatConfigurationBase.barcodeFormatCode128Configuration());
                    break;
                case Code93:
                    listBarcodeFormatConfigurationBase.add(BarcodeFormatConfigurationBase.barcodeFormatCode93Configuration());
                    break;
                case Code39:
                    listBarcodeFormatConfigurationBase.add(BarcodeFormatConfigurationBase.barcodeFormatCode39Configuration());
                    break;
                case AZTEC:
                    listBarcodeFormatConfigurationBase.add(BarcodeFormatConfigurationBase.barcodeFormatAztecConfiguration());
                    break;
                case Pdf417:
                    listBarcodeFormatConfigurationBase.add(BarcodeFormatConfigurationBase.barcodeFormatPdf417Configuration());
                    break;
                case DataMatrix:
                    listBarcodeFormatConfigurationBase.add(BarcodeFormatConfigurationBase.barcodeFormatDataMatrixConfiguration());
                    break;
                case QrCode:
                    listBarcodeFormatConfigurationBase.add(BarcodeFormatConfigurationBase.barcodeFormatQrCodeConfiguration());
                    break;
                case EAN13:
                    if(true) {
                        BarcodeFormatUpcEanConfiguration conf = BarcodeFormatConfigurationBase.barcodeFormatUpcEanConfiguration();
                        conf.setEan13(true);
                        conf.setEan8(false);
                        conf.setUpca(false);
                        listBarcodeFormatConfigurationBase.add(conf);
                    }
                    break;
                case UPCA:
                    if(true) {
                        BarcodeFormatUpcEanConfiguration conf=BarcodeFormatConfigurationBase.barcodeFormatUpcEanConfiguration();
                        conf.setUpca(true);
                        conf.setEan8(false);
                        conf.setEan13(false);
                        listBarcodeFormatConfigurationBase.add(conf);
                    }
                    break;
                case EAN8:
                    if(true) {
                        BarcodeFormatUpcEanConfiguration conf=BarcodeFormatConfigurationBase.barcodeFormatUpcEanConfiguration();
                        conf.setEan8(true);
                        conf.setEan13(false);
                        conf.setUpca(false);
                        listBarcodeFormatConfigurationBase.add(conf);
                    }
                    break;
                case UPCE:
                    if(true) {
                        BarcodeFormatCommonConfiguration conf = new BarcodeFormatCommonConfiguration();
                        conf.setFormats(new ArrayList<BarcodeFormat>(1){{add(BarcodeFormat.UPC_E);}});
                        listBarcodeFormatConfigurationBase.add(conf);
                    }
                    break;
                case Code11:
                    listBarcodeFormatConfigurationBase.add(BarcodeFormatConfigurationBase.barcodeFormatCode11Configuration());
                    break;
                case Code25:
                    listBarcodeFormatConfigurationBase.add(BarcodeFormatConfigurationBase.barcodeFormatCode2Of5Configuration());
                    break;
                case Codebar:
                    if(true) {
                        BarcodeFormatCommonConfiguration conf = new BarcodeFormatCommonConfiguration();
                        conf.setFormats(new ArrayList<BarcodeFormat>(1){{add(BarcodeFormat.CODABAR);}});
                        listBarcodeFormatConfigurationBase.add(conf);
                    }
                    break;
                case MSI_PLESSEY:
                    listBarcodeFormatConfigurationBase.add(BarcodeFormatConfigurationBase.barcodeFormatMsiPlesseyConfiguration());
                    break;
                case MicroQrCode:
                    if(true) {
                        BarcodeFormatCommonConfiguration conf = new BarcodeFormatCommonConfiguration();
                        conf.setFormats(new ArrayList<BarcodeFormat>(1){{add(BarcodeFormat.MICRO_QR_CODE);}});
                        listBarcodeFormatConfigurationBase.add(conf);
                    }
                    break;
                case MaxiCode:
                    listBarcodeFormatConfigurationBase.add(BarcodeFormatConfigurationBase.barcodeFormatMaxiCodeConfiguration());
                    break;
                case DotCode:
                    break;
                case Kix:
                    break;
                case RM4SCC:
                    break;
                case GS1_DATABAR:
                    listBarcodeFormatConfigurationBase.add(BarcodeFormatConfigurationBase.barcodeFormatDataBarConfiguration());
                    break;
                case GS1_DATABAR_EXPANDED:
                    listBarcodeFormatConfigurationBase.add(BarcodeFormatConfigurationBase.barcodeFormatDataBarExpandedConfiguration());
                    break;
                case GS1_DATABAR_LIMITED:
                    listBarcodeFormatConfigurationBase.add(BarcodeFormatConfigurationBase.barcodeFormatDataBarLimitedConfiguration());
                    break;
                case MicroPdf417:
                    listBarcodeFormatConfigurationBase.add(BarcodeFormatConfigurationBase.barcodeFormatMicroPdf417Configuration());
                    break;
                case Code32:
                    if(true) {
                        BarcodeFormatCommonConfiguration conf = new BarcodeFormatCommonConfiguration();
                        conf.setFormats(new ArrayList<BarcodeFormat>(1){{add(BarcodeFormat.CODE_32);}});
                        listBarcodeFormatConfigurationBase.add(conf);
                    }
                   break;
                case LAPA4SC:
                    break;
                case IATA_TWO_OF_FIVE:
                    if(true) {
                        BarcodeFormatCommonConfiguration conf = new BarcodeFormatCommonConfiguration();
                        conf.setFormats(new ArrayList<BarcodeFormat>(1){{add(BarcodeFormat.IATA_2_OF_5);}});
                        listBarcodeFormatConfigurationBase.add(conf);
                    }
                    break;
                case MATRIX_TWO_OF_FIVE:
                    break;
            }
        }
        barcodeScannerConfiguration.setBarcodeFormatConfigurations(listBarcodeFormatConfigurationBase);
        barcodeScanner.setConfiguration(barcodeScannerConfiguration);
    }

    public void applyTypesAlternative(List<Barcode.Type> types) {
        super.applyTypes(types);
        if(barcodeScanner==null) {
            return;
        }
        BarcodeScannerConfiguration barcodeScannerConfiguration = barcodeScanner.copyCurrentConfiguration();
        BarcodeFormatCommonConfiguration barcodeFormatCommonConfiguration = null;
        if (barcodeScannerConfiguration.getBarcodeFormatConfigurations().size()==1 && barcodeScannerConfiguration.getBarcodeFormatConfigurations().get(0) instanceof BarcodeFormatCommonConfiguration) {
            barcodeFormatCommonConfiguration= (BarcodeFormatCommonConfiguration) barcodeScannerConfiguration.getBarcodeFormatConfigurations().get(0);
        }
        else {
            barcodeFormatCommonConfiguration = new BarcodeFormatCommonConfiguration();
        }
        //barcodeScannerConfiguration.getBarcodeFormatConfigurations().get(0).
        List<BarcodeFormat> listBarcodeFormat = new ArrayList<>(types.size());
        for(Barcode.Type type:types) {
            switch (type) {
                case ITF:
                    listBarcodeFormat.add(BarcodeFormat.ITF);
                    break;
                case Code128:
                    listBarcodeFormat.add(BarcodeFormat.CODE_128);
                    break;
                case Code93:
                    listBarcodeFormat.add(BarcodeFormat.CODE_93);
                    break;
                case Code39:
                    listBarcodeFormat.add(BarcodeFormat.CODE_39);
                    break;
                case AZTEC:
                    listBarcodeFormat.add(BarcodeFormat.AZTEC);
                    break;
                case Pdf417:
                    listBarcodeFormat.add(BarcodeFormat.PDF_417);
                    break;
                case DataMatrix:
                    listBarcodeFormat.add(BarcodeFormat.DATA_MATRIX);
                    break;
                case QrCode:
                    listBarcodeFormat.add(BarcodeFormat.QR_CODE);
                    break;
                case EAN13:
                    listBarcodeFormat.add(BarcodeFormat.EAN_13);
                    break;
                case UPCA:
                    listBarcodeFormat.add(BarcodeFormat.UPC_A);
                    break;
                case EAN8:
                    listBarcodeFormat.add(BarcodeFormat.EAN_8);
                    break;
                case Code11:
                    listBarcodeFormat.add(BarcodeFormat.CODE_11);
                    break;
                case Code25:
                    listBarcodeFormat.add(BarcodeFormat.CODE_25);
                    break;
                case Codebar:
                    listBarcodeFormat.add(BarcodeFormat.CODABAR);
                    break;
                case MSI_PLESSEY:
                    listBarcodeFormat.add(BarcodeFormat.MSI_PLESSEY);
                    break;
                case MicroQrCode:
                    listBarcodeFormat.add(BarcodeFormat.MICRO_QR_CODE);
                    break;
                case MaxiCode:
                    listBarcodeFormat.add(BarcodeFormat.MAXI_CODE);
                    break;
                case DotCode:
                    break;
                case Kix:
                    break;
                case RM4SCC:
                    break;
                case GS1_DATABAR:
                    listBarcodeFormat.add(BarcodeFormat.DATABAR);
                    break;
                case GS1_DATABAR_EXPANDED:
                    listBarcodeFormat.add(BarcodeFormat.DATABAR_EXPANDED);
                    break;
                case GS1_DATABAR_LIMITED:
                    listBarcodeFormat.add(BarcodeFormat.DATABAR_LIMITED);
                    break;
                case MicroPdf417:
                    listBarcodeFormat.add(BarcodeFormat.MICRO_PDF_417);
                    break;
                case Code32:
                    listBarcodeFormat.add(BarcodeFormat.CODE_32);
                    break;
                case LAPA4SC:
                    break;
                case IATA_TWO_OF_FIVE:
                    listBarcodeFormat.add(BarcodeFormat.IATA_2_OF_5);
                    break;
                case MATRIX_TWO_OF_FIVE:
                    break;
                case UPCE:
                    listBarcodeFormat.add(BarcodeFormat.UPC_E);
                    break;
            }
        }
        barcodeFormatCommonConfiguration.setFormats(listBarcodeFormat);
        //List<BarcodeFormatConfigurationBase> listbarcodeScannerConfiguration = barcodeScannerConfiguration.getBarcodeFormatConfigurations();
        //listbarcodeScannerConfiguration.clear();
        List<BarcodeFormatConfigurationBase> listBarcodeFormatConfigurationBase = new ArrayList<>(1);
        listBarcodeFormatConfigurationBase.add(barcodeFormatCommonConfiguration);
        barcodeScannerConfiguration.setBarcodeFormatConfigurations(listBarcodeFormatConfigurationBase);
        barcodeScanner.setConfiguration(barcodeScannerConfiguration);
    }

    @Override
    public View getView() {
        barcodeScannerView = new BarcodeScannerView(activity, null);
        barcodeScanner = new ScanbotBarcodeScannerSDK(activity).createBarcodeScanner();
        applyTypes(types);
        barcodeScannerView.initCamera();
        barcodeScannerView.initScanningBehavior(barcodeScanner, new BarcodeScannerFrameHandler.BarcodeScannerResultHandler() {
            @Override
            public boolean handle(@NonNull FrameHandlerResult<? extends BarcodeScannerResult, ? extends SdkLicenseError> frameHandlerResult) {
                if (frameHandlerResult instanceof FrameHandlerResult.Success) {
                    FrameHandlerResult.Success<? extends BarcodeScannerResult> res = ((FrameHandlerResult.Success<? extends BarcodeScannerResult>) frameHandlerResult);
                    if(res.getValue()!=null&&res.getValue().getBarcodes()!=null) {
                        ArrayList<Barcode.Result> results = new ArrayList<>(1);
                        for (BarcodeItem item :res.getValue().getBarcodes()) {
                            results.add(new Result(item));
                        }
                        BarcodeScanbot.this.onCodeScanned(results);
                    }
                }
                return false;
            }
        }, new IBarcodeScannerViewCallback() {
            @Override
            public void onSelectionOverlayBarcodeClicked(@NonNull BarcodeItem barcodeItem) {

            }

            @Override
            public void onPictureTaken(@NonNull byte[] bytes, @NonNull CaptureInfo captureInfo) {

            }

            @Override
            public void onCameraOpen() {
                //barcodeScannerView.getViewController().useFlash(true);
            }
        });
        return barcodeScannerView;
    }

    @Override
    public void onResume() {
        if(barcodeScannerView==null) {
            return;
        }
        barcodeScannerView.getViewController().onResume();
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Use onActivityResult to handle permission rejection
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, 100);
        }
    }

    @Override
    public void onPause() {
        if(barcodeScannerView==null) {
            return;
        }
        barcodeScannerView.getViewController().onPause();
    }

    @Override
    public void start() {
        if(barcodeScannerView==null) {
            return;
        }
        barcodeScannerView.getViewController().setFrameProcessingEnabled(true);
    }

    @Override
    public void stop() {
        if(barcodeScannerView==null) {
            return;
        }
        barcodeScannerView.getViewController().setFrameProcessingEnabled(false);
    }

    @Override
    public boolean hasPause() {
        if(barcodeScannerView==null) {
            return false;
        }
        return !barcodeScannerView.getViewController().isFrameProcessingEnabled();
    }

    @Override
    public void pause() {
        if(barcodeScannerView==null) {
            return;
        }
        barcodeScannerView.getViewController().setFrameProcessingEnabled(false);
    }

    @Override
    public void resume() {
        if(barcodeScannerView==null) {
            return;
        }
        barcodeScannerView.getViewController().setFrameProcessingEnabled(true);
    }

    @Override
    public void setVibrate(boolean value) {
        if(barcodeScannerView==null) {
            return;
        }
    }

    @Override
    public void setSound(boolean value) {
        if(barcodeScannerView==null) {
            return;
        }
        barcodeScannerView.setSoundEffectsEnabled(true);
    }

    class Result implements Barcode.Result {
        BarcodeItem item;
        Result(BarcodeItem item) {
            this.item=item;
        }

        @Override
        public String getValue() {
            return item.getText();
        }

        @Override
        public Type getType() {
            return null;
        }
    }
}
