package com.drivepdfviewer;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;

import com.drivepdfviewer.Adapters.List_PDF_View_Adapter;
import com.drivepdfviewer.Adapters.ZoomRecyclerView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class OpenFile extends AppCompatActivity {
    ActivityResultLauncher<String> pickPdfLauncher;
    ZoomRecyclerView zoomRecyclerView;
    List_PDF_View_Adapter adapter;
    PdfRenderer  pdfRenderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_file);
        zoomRecyclerView = (ZoomRecyclerView) findViewById(R.id.pagesRecyclerView);

        pickPdfLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                this::handlePdfResult);
        pickPdfLauncher.launch("application/pdf");
    }

    public void handlePdfResult(Uri pdfUri) {
        if (pdfUri != null) {
            // Convert Uri to File
            File file = uriToFile(pdfUri);
            ParcelFileDescriptor parcelFileDescriptor;
            try {
                parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            try {
                pdfRenderer = new PdfRenderer(parcelFileDescriptor);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            zoomRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            adapter = new List_PDF_View_Adapter(this, pdfRenderer);
            zoomRecyclerView.setAdapter(adapter);


        }
    }

    private File uriToFile(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            File pdfFile = new File(getCacheDir(), "temp.pdf");
            org.apache.commons.io.FileUtils.copyInputStreamToFile(inputStream, pdfFile);
            return pdfFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}