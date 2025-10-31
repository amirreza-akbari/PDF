package com.mrbackend.pdf;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;

public class PdfListActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<File> pdfFiles;
    ArrayList<String> pdfNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_list);

        listView = findViewById(R.id.listViewPdfs);

        File dir = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "MyPDFs");

        if (!dir.exists()) {
            Toast.makeText(this, "پوشه‌ای برای PDF پیدا نشد!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        pdfFiles = new ArrayList<>();
        pdfNames = new ArrayList<>();

        File[] files = dir.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                if (file.getName().endsWith(".pdf")) {
                    pdfFiles.add(file);
                    pdfNames.add(file.getName());
                }
            }
        } else {
            Toast.makeText(this, "هیچ فایل PDF وجود ندارد!", Toast.LENGTH_SHORT).show();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, pdfNames);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            File selectedPdf = pdfFiles.get(i);
            Intent intent = new Intent(PdfListActivity.this, PdfToTextActivity.class);
            intent.putExtra("pdf_path", selectedPdf.getAbsolutePath());
            startActivity(intent);
        });
    }
}
