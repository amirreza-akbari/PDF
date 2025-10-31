package com.mrbackend.pdf;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    EditText editTextContent;
    Button btnCreatePdf, btnViewPdfs;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextContent = findViewById(R.id.editTextContent);
        btnCreatePdf = findViewById(R.id.btnCreatePdf);
        btnViewPdfs = findViewById(R.id.btnViewPdfs);

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        }, PackageManager.PERMISSION_GRANTED);

        btnCreatePdf.setOnClickListener(v -> {
            String text = editTextContent.getText().toString().trim();
            if (text.isEmpty()) {
                Toast.makeText(this, "لطفاً متنی وارد کنید!", Toast.LENGTH_SHORT).show();
                return;
            }
            createPdf(text);
        });

        btnViewPdfs.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PdfListActivity.class);
            startActivity(intent);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void createPdf(String text) {
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        paint.setColor(Color.BLACK);
        paint.setTextSize(16);
        canvas.drawText(text, 80, 100, paint);
        pdfDocument.finishPage(page);

        File dir = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "MyPDFs");
        if (!dir.exists()) dir.mkdirs();

        File file = new File(dir, "MyPdf_" + System.currentTimeMillis() + ".pdf");
        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(this, "PDF با موفقیت ذخیره شد!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "خطا در ذخیره PDF!", Toast.LENGTH_SHORT).show();
        }

        pdfDocument.close();
    }
}
