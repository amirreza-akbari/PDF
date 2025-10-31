package com.mrbackend.pdf;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;

public class PdfDisplayActivity extends AppCompatActivity {

    ImageView imageView;
    Button btnNext, btnPrev;
    PdfRenderer pdfRenderer;
    PdfRenderer.Page currentPage;
    ParcelFileDescriptor fileDescriptor;
    int pageIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_display);

        imageView = findViewById(R.id.imageViewPdf);
        btnNext = findViewById(R.id.btnNext);
        btnPrev = findViewById(R.id.btnPrev);

        String path = getIntent().getStringExtra("path");
        if (path == null) {
            Toast.makeText(this, "خطا در دریافت فایل!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        File file = new File(path);
        try {
            fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
            pdfRenderer = new PdfRenderer(fileDescriptor);
            showPage(pageIndex);
        } catch (IOException e) {
            Toast.makeText(this, "خطا در باز کردن PDF!", Toast.LENGTH_SHORT).show();
        }

        btnNext.setOnClickListener(v -> nextPage());
        btnPrev.setOnClickListener(v -> prevPage());
    }

    private void showPage(int index) {
        if (pdfRenderer.getPageCount() <= index || index < 0) return;

        if (currentPage != null) currentPage.close();

        currentPage = pdfRenderer.openPage(index);
        Bitmap bitmap = Bitmap.createBitmap(
                currentPage.getWidth(),
                currentPage.getHeight(),
                Bitmap.Config.ARGB_8888
        );
        currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        imageView.setImageBitmap(bitmap);

        setTitle("صفحه " + (index + 1) + " از " + pdfRenderer.getPageCount());
    }

    private void nextPage() {
        if (pageIndex < pdfRenderer.getPageCount() - 1) {
            pageIndex++;
            showPage(pageIndex);
        }
    }

    private void prevPage() {
        if (pageIndex > 0) {
            pageIndex--;
            showPage(pageIndex);
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
