package com.drivepdfviewer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.graphics.Matrix;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import com.drivepdfviewer.Adapters.List_PDF_View_Adapter;
import com.drivepdfviewer.Adapters.PdfPagesAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    RecyclerView pdf_pages_list_recycler;
    private PdfRenderer pdfRenderer;
    private ParcelFileDescriptor parcelFileDescriptor;

    List_PDF_View_Adapter Adapter;
    private float scaleFactor = 1.0f;
    private ScaleGestureDetector scaleGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pdf_pages_list_recycler = findViewById(R.id.pagesRecyclerView);
        copyPdfFromAssetsToInternalStorage();

        try {
            openRenderer();
            setupRecyclerView();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

    }

    private void copyPdfFromAssetsToInternalStorage() {
        try {
            InputStream inputStream = getAssets().open("test.pdf");
            File file = new File(getFilesDir(), "test.pdf");
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
        File file = new File(getFilesDir(), "test.pdf");
        parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
        pdfRenderer = new PdfRenderer(parcelFileDescriptor);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupRecyclerView() {
        pdf_pages_list_recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        Adapter = new List_PDF_View_Adapter(this, pdfRenderer);
        pdf_pages_list_recycler.setAdapter(Adapter);

    }


    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        private static final float MAX_ZOOM = 20.0f; // Increase the maximum zoom as needed

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(1.0f, Math.min(scaleFactor, MAX_ZOOM)); // Adjust the maximum zoom

            pdf_pages_list_recycler.setScaleX(scaleFactor);
            pdf_pages_list_recycler.setScaleY(scaleFactor);
            return true;
        }
    }
}



