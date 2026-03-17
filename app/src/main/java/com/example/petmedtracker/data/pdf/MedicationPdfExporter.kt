package com.example.petmedtracker.data.pdf

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.petmedtracker.models.Medication
import com.example.petmedtracker.models.Pet
import java.io.File
import java.io.FileOutputStream

class MedicationPdfExporter(private val context: Context) {

    fun exportToPdf(pet: Pet, medications: List<Medication>): Uri? {
        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
        val document = PdfDocument()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint().apply { isAntiAlias = true }
        var y = MARGIN.toFloat()

        paint.textSize = TITLE_SIZE
        canvas.drawText("Medication schedule for ${pet.name}", MARGIN.toFloat(), y, paint)
        y += LINE_HEIGHT * 2

        if (pet.species.isNotEmpty() || pet.breed.isNotEmpty()) {
            paint.textSize = BODY_SIZE
            val details = listOfNotNull(
                pet.species.takeIf { it.isNotEmpty() }?.let { "Species: $it" },
                pet.breed.takeIf { it.isNotEmpty() }?.let { "Breed: $it" }
            ).joinToString(", ")
            canvas.drawText(details, MARGIN.toFloat(), y, paint)
            y += LINE_HEIGHT * 2
        }

        paint.textSize = SECTION_SIZE
        for ((index, med) in medications.withIndex()) {
            if (y > PAGE_HEIGHT - BOTTOM_MARGIN) break
            canvas.drawText("Medication ${index + 1}: ${med.medicationName}", MARGIN.toFloat(), y, paint)
            y += LINE_HEIGHT
            paint.textSize = BODY_SIZE
            canvas.drawText("Dosage: ${med.dosage}", MARGIN.toFloat(), y, paint)
            y += LINE_HEIGHT
            canvas.drawText("Frequency: ${med.frequency}", MARGIN.toFloat(), y, paint)
            y += LINE_HEIGHT
            canvas.drawText("Start date: ${med.startDate}", MARGIN.toFloat(), y, paint)
            y += LINE_HEIGHT
            canvas.drawText("Duration: ${med.duration}", MARGIN.toFloat(), y, paint)
            y += LINE_HEIGHT
            if (med.notesInstructions.isNotEmpty()) {
                canvas.drawText("Instructions: ${med.notesInstructions}", MARGIN.toFloat(), y, paint)
                y += LINE_HEIGHT
            }
            y += LINE_HEIGHT
            paint.textSize = SECTION_SIZE
        }

        document.finishPage(page)
        val file = File(context.cacheDir, "medication_${pet.id}_${System.currentTimeMillis()}.pdf")
        FileOutputStream(file).use { document.writeTo(it) }
        document.close()
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    private companion object {
        const val PAGE_WIDTH = 612
        const val PAGE_HEIGHT = 792
        const val MARGIN = 72
        const val BOTTOM_MARGIN = 72
        const val TITLE_SIZE = 22f
        const val SECTION_SIZE = 14f
        const val BODY_SIZE = 12f
        const val LINE_HEIGHT = 18
    }
}
