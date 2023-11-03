package com.example.fyp


import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.itextpdf.forms.PdfAcroForm
import com.itextpdf.io.IOException
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.PdfWriter
import java.io.File
import java.io.FileOutputStream


class Contract :AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.contract)
       val button = findViewById<Button>(R.id.generate)
        button.setOnClickListener(){
            main()
        }

    }

    fun fillPdfTemplate(inputPdf: String, outputPdf: String, data: Map<String, String>) {
        PdfReader(inputPdf).use { reader ->
            PdfWriter(outputPdf).use { writer ->
                PdfDocument(reader, writer).use { pdfDoc ->
                    val form = PdfAcroForm.getAcroForm(pdfDoc, true)

                    data.forEach { (fieldName, fieldValue) ->
                        val formField = form.getField(fieldName)
                        formField?.setValue(fieldValue)
                    }

                    form.flattenFields() // Make the form read-only if you don't need to edit after saving.
                    pdfDoc.close()
                }
            }
        }
    }

    fun main() {
        val context = this // Assuming 'this' is a Context instance.
        val inputPdf = "rental-agreement-room.pdf" // The template PDF with placeholders

        // The path where the filled PDF will be saved
        val outputPdfPath =
            File(context.getExternalFilesDir(null), "rental-agreement-room-filled.pdf").absolutePath

        val data = mapOf(
            "Address" to "John Doe"

        )

        try {
            context.assets.open(inputPdf).use { assetInputStream ->
                PdfReader(assetInputStream).use { reader ->
                    PdfWriter(outputPdfPath).use { writer ->
                        PdfDocument(reader, writer).use { pdfDoc ->
                            val form = PdfAcroForm.getAcroForm(pdfDoc, true)

                            data.forEach { (fieldName, fieldValue) ->
                                val formField = form.getField(fieldName)
                                formField?.setValue(fieldValue)
                            }

                            form.flattenFields() // Make the form read-only if you don't need to edit after saving.
                        }
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}


