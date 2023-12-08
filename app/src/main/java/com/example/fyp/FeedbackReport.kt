package com.example.fyp

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PersistableBundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.itextpdf.io.IOException
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream


class FeedbackReport:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.feedback_report)
        val button = findViewById<Button>(R.id.Generate)
        button.setOnClickListener {
            setupChartsAndGeneratePDF()
        }
    }

    fun setupChartsAndGeneratePDF() {
        setContentView(R.layout.feedback_chart)

        // Setup charts with a callback when done
        setupCharts {
            // Delay to ensure charts are rendered
            Handler(Looper.getMainLooper()).postDelayed({
                val bitmap = createBitmapFromLayout(this, R.layout.feedback_chart)
                createPdfFromBitmap(this, bitmap, "output.pdf")
            }, 1000)
        }
    }

    fun setupCharts(onChartsReady: () -> Unit) {
        val tasks = listOf(setupBarChart(), setupPieChart())

        Tasks.whenAllComplete(tasks).addOnCompleteListener {
            onChartsReady()
        }
    }


    fun createBitmapFromLayout(activity: Activity, layoutResId: Int): Bitmap {
        // Inflate the layout
        val layoutInflater = LayoutInflater.from(activity)
        val layoutView = layoutInflater.inflate(layoutResId, null)

        // Measure the view at the exact dimensions (otherwise the view will be as big as the parent)
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        layoutView.measure(
            View.MeasureSpec.makeMeasureSpec(displayMetrics.widthPixels, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )

        val bitmap = Bitmap.createBitmap(layoutView.measuredWidth, layoutView.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        layoutView.layout(0, 0, layoutView.measuredWidth, layoutView.measuredHeight)
        layoutView.draw(canvas)

        return bitmap
    }

    fun createPdfFromBitmap(context: Context, bitmap: Bitmap, fileName: String) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
        val page = pdfDocument.startPage(pageInfo)

        val canvas = page.canvas
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        pdfDocument.finishPage(page)

        // Write the document to a file
        try {
            val file = File(context.getExternalFilesDir(null), fileName)
            pdfDocument.writeTo(FileOutputStream(file))
            Toast.makeText(context, "PDF saved to ${file.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Error saving PDF", Toast.LENGTH_SHORT).show()
        }

        pdfDocument.close()
    }

    fun graph(){
        setContentView(R.layout.feedback_chart)
        setupBarChart()
        setupPieChart()
    }

    fun setupBarChart(): Task<Void> {
        val barChart = findViewById<BarChart>(R.id.barChart)
        barChart.axisLeft.isEnabled = false
        val completionSource = TaskCompletionSource<Void>()
        // Hide the top X-axis
        barChart.xAxis.setDrawAxisLine(true) // This disables drawing the line for the X-axis.
        barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM // Ensures the X-axis is at the bottom.

        // Make sure the right Y-axis is enabled
        barChart.axisRight.isEnabled = true

        // Optional: Customize the appearance of the right Y-axis
        barChart.axisRight.setDrawAxisLine(true)
        barChart.axisRight.setDrawGridLines(false)
        // Initialize counters
        var one = 0f
        var two = 0f
        var three = 0f
        var four = 0f
        var five = 0f

        val db = FirebaseFirestore.getInstance()
        val collectionReference = db.collection("Feedback")
        collectionReference.get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    querySnapshot.documents.forEach { document ->
                        val appValue = document.getString("appValue")?.toDouble() ?: 0.0
                        val serviceValue = document.getString("serviceValue")?.toDouble() ?: 0.0
                        val performanceValue = document.getString("performanceValue")?.toDouble() ?: 0.0

                        // Increment counters based on rating values
                        arrayOf(appValue, serviceValue, performanceValue).forEach { rating ->
                            when (rating) {
                                1.0 -> one++
                                2.0 -> two++
                                3.0 -> three++
                                4.0 -> four++
                                5.0 -> five++
                                else -> println("Unknown number")
                            }
                        }
                    }

                    // Create a list of BarEntries
                    val entries = ArrayList<BarEntry>().apply {
                        add(BarEntry(1f, one))
                        add(BarEntry(2f, two))
                        add(BarEntry(3f, three))
                        add(BarEntry(4f, four))
                        add(BarEntry(5f, five))
                    }

                    // Update the chart
                    val dataSet = BarDataSet(entries, "Ratings")
                    val data = BarData(dataSet)
                    barChart.data = data
                    barChart.invalidate() // Refresh the chart
                    completionSource.setResult(null)
                } else {
                    Log.d("Firestore", "No documents found")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error fetching documents: ", exception)
            }
        return completionSource.task
    }

    fun setupPieChart(): Task<Void> {
        val pieChart = findViewById<PieChart>(R.id.pieChart)
        val completionSource = TaskCompletionSource<Void>()

        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(40.0f, "Category 1"))
        entries.add(PieEntry(30.0f, "Category 2"))
        entries.add(PieEntry(20.0f, "Category 3"))
        entries.add(PieEntry(10.0f, "Category 4"))

        val dataSet = PieDataSet(entries, "Categories")
        dataSet.setColors(*ColorTemplate.COLORFUL_COLORS)

        val data = PieData(dataSet)
        pieChart.data = data
        pieChart.invalidate() // Refresh the chart
        completionSource.setResult(null)
        return completionSource.task
    }

}