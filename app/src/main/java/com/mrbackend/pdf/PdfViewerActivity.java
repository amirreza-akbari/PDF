package com.mrbackend.pdf;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PdfViewerActivity extends AppCompatActivity {

    ListView listView;
    ImageView imageView;
    ArrayList<File> pdfFiles = new ArrayList<>();
    ArrayList<String> fileNames = new ArrayList<>();
    PdfRenderer pdfRenderer;
    PdfRenderer.Page currentPage;
    ParcelFileDescriptor fileDescriptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);

        listView = findViewById(R.id.listViewPdfs);
        imageView = findViewById(R.id.imageViewPdf);

        File dir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), "MyPDFs");

        if (!dir.exists() || dir.listFiles() == null) {
            Toast.makeText(this, "هیچ PDFی پیدا نشد!", Toast.LENGTH_SHORT).show();
            return;
        }

        for (File file : dir.listFiles()) {
            if (file.getName().endsWith(".pdf")) {
                pdfFiles.add(file);
                fileNames.add(file.getName());
            }
        }

        if (pdfFiles.isEmpty()) {
            Toast.makeText(this, "هیچ فایل PDF موجود نیست!", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, fileNames);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> showPdf(pdfFiles.get(position)));
    }

    private void showPdf(File file) {
        try {
            if (pdfRenderer != null) {
                pdfRenderer.close();
            }

            fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
            pdfRenderer = new PdfRenderer(fileDescriptor);

            if (pdfRenderer.getPageCount() > 0) {
                currentPage = pdfRenderer.openPage(0);
                Bitmap bitmap = Bitmap.createBitmap(
                        currentPage.getWidth(),
                        currentPage.getHeight(),
                        Bitmap.Config.ARGB_8888
                );
                currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                imageView.setImageBitmap(bitmap);
                currentPage.close();
            } else {
                Toast.makeText(this, "PDF خالی است!", Toast.LENGTH_SHORT).show();
            }

        } catch (IOException e) {
            Toast.makeText(this, "خطا در نمایش PDF!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        try {
            if (currentPage != null) currentPage.close();
            if (pdfRenderer != null) pdfRenderer.close();
            if (fileDescriptor != null) fileDescriptor.close();
        } catch (IOException ignored) {}
        super.onDestroy();
    }
}
