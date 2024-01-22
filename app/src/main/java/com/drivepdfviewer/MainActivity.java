package com.drivepdfviewer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.widget.Toast;


import com.drivepdfviewer.Adapters.List_PDF_View_Adapter;
import com.drivepdfviewer.Adapters.ZoomRecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    protected ParcelFileDescriptor parcelFileDescriptor;

    private PdfRenderer pdfRenderer;

    List_PDF_View_Adapter adapter;
    ZoomRecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.pagesRecyclerView);

        copyPdfFromAssetsToInternalStorage();

        try {
            openRenderer();
            setupRecyclerView();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }

    private void copyPdfFromAssetsToInternalStorage() {
        try {
            InputStream inputStream = getAssets().open("qp.pdf");
            File file = new File(getFilesDir(), "qp.pdf");
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int size;
            while ((size = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, size);
            }
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void openRenderer() throws IOException {
        File file = new File(getFilesDir(), "qp.pdf");
        parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
        pdfRenderer = new PdfRenderer(parcelFileDescriptor);
    }


    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new List_PDF_View_Adapter(this, pdfRenderer);
        recyclerView.setAdapter(adapter);

    }


}