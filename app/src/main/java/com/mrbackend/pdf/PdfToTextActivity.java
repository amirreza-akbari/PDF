package com.mrbackend.pdf;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PdfToTextActivity extends AppCompatActivity {

    EditText editTextPdfContent;
    Button btnCopyText, btnSaveText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_to_text);

        editTextPdfContent = findViewById(R.id.editTextPdfContent);
        btnCopyText = findViewById(R.id.btnCopyText);
        btnSaveText = findViewById(R.id.btnSaveText);

        String pdfPath = getIntent().getStringExtra("pdf_path");
        if (pdfPath != null) {
            extractTextFromPdf(pdfPath);
        } else {
            Toast.makeText(this, "فایل PDF یافت نشد!", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnCopyText.setOnClickListener(v -> {
            String text = editTextPdfContent.getText().toString();
            if (!text.isEmpty()) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("PDF Text", text);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "متن کپی شد!", Toast.LENGTH_SHORT).show();
            }
        });

        btnSaveText.setOnClickListener(v -> {
            String text = editTextPdfContent.getText().toString();
            if (!text.isEmpty()) {
                saveTextToFile(text);
            }
        });
    }

    private void extractTextFromPdf(String filePath) {
        try {
            PdfReader reader = new PdfReader(filePath);
            StringBuilder text = new StringBuilder();
            for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                text.append(PdfTextExtractor.getTextFromPage(reader, i));
                text.append("\n\n");
            }
            reader.close();
            editTextPdfContent.setText(text.toString());
        } catch (IOException e) {
            Toast.makeText(this, "خطا در خواندن PDF!", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveTextToFile(String text) {
        try {
            File dir = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "PDFtoText");
            if (!dir.exists()) dir.mkdirs();

            String fileName = "Text_" + System.currentTimeMillis() + ".txt";
            File file = new File(dir, fileName);

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(text.getBytes());
            fos.close();

            Toast.makeText(this, "ذخیره شد: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(this, "خطا در ذخیره فایل!", Toast.LENGTH_SHORT).show();
        }
    }
}
