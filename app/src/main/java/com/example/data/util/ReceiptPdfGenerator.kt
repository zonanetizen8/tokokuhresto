package com.example.data.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.R
import com.example.data.local.entities.OrderEntity
import com.example.data.local.entities.OrderItemEntity
import com.example.data.local.entities.RestaurantProfileEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object ReceiptPdfGenerator {

    /**
     * Generates a 100% offline receipt PDF using Android native PdfDocument.
     * The restaurant logo is placed at the VERY TOP of the receipt before the restaurant name.
     */
    suspend fun generateReceiptPdf(
        context: Context,
        profile: RestaurantProfileEntity?,
        order: OrderEntity,
        items: List<OrderItemEntity>
    ): File? = withContext(Dispatchers.IO) {
        try {
            val paperWidth = if ((profile?.paperWidthMm ?: 58) == 80) 576 else 384
            // Dynamic page height based on number of items
            val baseHeight = 650
            val itemHeight = 35 * items.size
            val pageHeight = baseHeight + itemHeight

            val document = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(paperWidth, pageHeight, 1).create()
            val page = document.startPage(pageInfo)
            val canvas: Canvas = page.canvas

            // Background
            val bgPaint = Paint().apply { color = Color.WHITE }
            canvas.drawRect(0f, 0f, paperWidth.toFloat(), pageHeight.toFloat(), bgPaint)

            var y = 25f
            val centerX = paperWidth / 2f

            val textPaint = Paint().apply {
                isAntiAlias = true
                color = Color.parseColor("#222222")
                textSize = 12f
            }

            val boldPaint = Paint().apply {
                isAntiAlias = true
                color = Color.parseColor("#222222")
                textSize = 13f
                isFakeBoldText = true
            }

            val maroonPaint = Paint().apply {
                isAntiAlias = true
                color = Color.parseColor("#720000")
                textSize = 15f
                isFakeBoldText = true
            }

            val secondaryPaint = Paint().apply {
                isAntiAlias = true
                color = Color.parseColor("#666666")
                textSize = 11f
            }

            val linePaint = Paint().apply {
                color = Color.parseColor("#CCCCCC")
                strokeWidth = 1f
                pathEffect = DashPathEffect(floatArrayOf(5f, 5f), 0f)
            }

            // 1. RESTAURANT LOGO (AT THE VERY TOP)
            val logoPath = profile?.logoPath
            var hasDrawnLogo = false
            if (!logoPath.isNullOrBlank()) {
                val logoFile = File(logoPath)
                if (logoFile.exists()) {
                    try {
                        val logoBitmap = BitmapFactory.decodeFile(logoFile.absolutePath)
                        if (logoBitmap != null) {
                            val maxLogoWidth = paperWidth * 0.55f
                            val maxLogoHeight = 70f
                            val scale = minOf(maxLogoWidth / logoBitmap.width, maxLogoHeight / logoBitmap.height)
                            val targetW = logoBitmap.width * scale
                            val targetH = logoBitmap.height * scale

                            val destRect = RectF(
                                centerX - (targetW / 2f),
                                y,
                                centerX + (targetW / 2f),
                                y + targetH
                            )
                            canvas.drawBitmap(logoBitmap, null, destRect, null)
                            y += targetH + 12f
                            hasDrawnLogo = true
                            logoBitmap.recycle()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            if (!hasDrawnLogo) {
                // Draw brand header badge if no custom logo uploaded
                try {
                    val defaultLogo = BitmapFactory.decodeResource(context.resources, R.drawable.logo_tokokuh_resto)
                    if (defaultLogo != null) {
                        val maxW = paperWidth * 0.65f
                        val maxH = 40f
                        val scale = minOf(maxW / defaultLogo.width, maxH / defaultLogo.height)
                        val targetW = defaultLogo.width * scale
                        val targetH = defaultLogo.height * scale

                        // Maroon backing banner
                        val maroonBg = Paint().apply { color = Color.parseColor("#720000") }
                        val bannerH = targetH + 12f
                        canvas.drawRoundRect(20f, y, paperWidth - 20f, y + bannerH, 8f, 8f, maroonBg)

                        val destRect = RectF(
                            centerX - (targetW / 2f),
                            y + 6f,
                            centerX + (targetW / 2f),
                            y + 6f + targetH
                        )
                        canvas.drawBitmap(defaultLogo, null, destRect, null)
                        y += bannerH + 12f
                        defaultLogo.recycle()
                    }
                } catch (e: Exception) {
                    y += 10f
                }
            }

            // 2. RESTAURANT DETAILS
            val restoName = profile?.name?.takeIf { it.isNotBlank() } ?: "TOKOKUH RESTO"
            boldPaint.textAlign = Paint.Align.CENTER
            boldPaint.textSize = 14f
            canvas.drawText(restoName, centerX, y, boldPaint)
            y += 16f

            secondaryPaint.textAlign = Paint.Align.CENTER
            val address = profile?.address.orEmpty()
            if (address.isNotBlank()) {
                canvas.drawText(address, centerX, y, secondaryPaint)
                y += 14f
            }

            val phone = profile?.phone.orEmpty()
            if (phone.isNotBlank()) {
                canvas.drawText("Telp: $phone", centerX, y, secondaryPaint)
                y += 14f
            }

            val headerText = profile?.receiptHeader
            if (!headerText.isNullOrBlank()) {
                secondaryPaint.color = Color.parseColor("#720000")
                canvas.drawText(headerText, centerX, y, secondaryPaint)
                secondaryPaint.color = Color.parseColor("#666666")
                y += 14f
            }

            // Divider
            y += 4f
            canvas.drawLine(15f, y, paperWidth - 15f, y, linePaint)
            y += 16f

            // 3. ORDER METADATA
            textPaint.textAlign = Paint.Align.LEFT
            boldPaint.textAlign = Paint.Align.LEFT
            secondaryPaint.textAlign = Paint.Align.LEFT

            canvas.drawText("No. Struk : ${order.orderNumber}", 20f, y, boldPaint)
            y += 15f
            canvas.drawText("Waktu     : ${CurrencyFormatter.formatDateTime(order.createdAt)}", 20f, y, secondaryPaint)
            y += 15f
            canvas.drawText("Layanan   : ${order.orderType.displayName} ${order.tableName ?: ""}", 20f, y, secondaryPaint)
            y += 15f
            canvas.drawText("Kasir     : ${order.cashierName}", 20f, y, secondaryPaint)
            y += 15f
            canvas.drawText("Pelanggan : ${order.customerName}", 20f, y, secondaryPaint)
            y += 16f

            // Divider
            canvas.drawLine(15f, y, paperWidth - 15f, y, linePaint)
            y += 16f

            // 4. ITEMS LIST
            for (item in items) {
                // Item Name
                boldPaint.textSize = 11f
                canvas.drawText(item.menuName, 20f, y, boldPaint)
                y += 13f

                // Qty x Price and Subtotal
                secondaryPaint.textSize = 10f
                canvas.drawText("${item.quantity}x @ ${CurrencyFormatter.formatRupiah(item.unitPrice)}", 20f, y, secondaryPaint)

                textPaint.textAlign = Paint.Align.RIGHT
                textPaint.textSize = 11f
                canvas.drawText(CurrencyFormatter.formatRupiah(item.subtotal), paperWidth - 20f, y, textPaint)
                textPaint.textAlign = Paint.Align.LEFT

                if (!item.note.isNullOrBlank()) {
                    y += 12f
                    secondaryPaint.color = Color.parseColor("#888888")
                    canvas.drawText("  * ${item.note}", 20f, y, secondaryPaint)
                    secondaryPaint.color = Color.parseColor("#666666")
                }
                y += 15f
            }

            // Divider
            canvas.drawLine(15f, y, paperWidth - 15f, y, linePaint)
            y += 18f

            // 5. TOTALS
            fun drawRow(label: String, value: String, isBold: Boolean = false, isHighlight: Boolean = false) {
                val pLeft = if (isBold) boldPaint else textPaint
                val pRight = if (isHighlight) maroonPaint else (if (isBold) boldPaint else textPaint)
                pLeft.textAlign = Paint.Align.LEFT
                pRight.textAlign = Paint.Align.RIGHT

                canvas.drawText(label, 20f, y, pLeft)
                canvas.drawText(value, paperWidth - 20f, y, pRight)
                y += 16f
            }

            drawRow("Subtotal", CurrencyFormatter.formatRupiah(order.subtotal))
            if (order.discountAmount > 0) {
                drawRow("Diskon", "-${CurrencyFormatter.formatRupiah(order.discountAmount)}")
            }
            if (order.taxAmount > 0) {
                drawRow("Pajak (${order.taxPercent.toInt()}%)", CurrencyFormatter.formatRupiah(order.taxAmount))
            }
            if (order.serviceAmount > 0) {
                drawRow("Layanan (${order.servicePercent.toInt()}%)", CurrencyFormatter.formatRupiah(order.serviceAmount))
            }

            y += 2f
            canvas.drawLine(15f, y, paperWidth - 15f, y, linePaint)
            y += 18f

            drawRow("TOTAL BAYAR", CurrencyFormatter.formatRupiah(order.totalAmount), isBold = true, isHighlight = true)
            drawRow("Bayar (${order.paymentMethod.displayName})", CurrencyFormatter.formatRupiah(order.paidAmount))
            drawRow("Kembalian", CurrencyFormatter.formatRupiah(order.changeAmount), isBold = true)

            y += 12f

            // 6. FOOTER
            val footerText = profile?.receiptFooter?.takeIf { it.isNotBlank() } ?: "Terima Kasih Atas Kunjungan Anda!"
            secondaryPaint.textAlign = Paint.Align.CENTER
            secondaryPaint.textSize = 10f
            canvas.drawText(footerText, centerX, y, secondaryPaint)
            y += 16f

            // 7. TOKOKUH.COM CREDIT AT BOTTOM
            try {
                val creditLogo = BitmapFactory.decodeResource(context.resources, R.drawable.logo_credit_struk)
                if (creditLogo != null) {
                    val targetW = paperWidth * 0.45f
                    val scale = targetW / creditLogo.width
                    val targetH = creditLogo.height * scale

                    val destRect = RectF(
                        centerX - (targetW / 2f),
                        y,
                        centerX + (targetW / 2f),
                        y + targetH
                    )
                    canvas.drawBitmap(creditLogo, null, destRect, null)
                    y += targetH + 8f
                    creditLogo.recycle()
                }
            } catch (e: Exception) {
                val creditPaint = Paint().apply {
                    color = Color.parseColor("#888888")
                    textSize = 9f
                    textAlign = Paint.Align.CENTER
                }
                canvas.drawText("TOKOKUH.COM RESTO • 100% Offline POS", centerX, y, creditPaint)
                y += 12f
            }

            document.finishPage(page)

            // Save PDF file in cache
            val cacheDir = File(context.cacheDir, "receipts")
            if (!cacheDir.exists()) cacheDir.mkdirs()

            val pdfFile = File(cacheDir, "struk_${order.orderNumber}.pdf")
            val outputStream = FileOutputStream(pdfFile)
            document.writeTo(outputStream)
            outputStream.flush()
            outputStream.close()
            document.close()

            pdfFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Opens Android system Share sheet to send/export/print the generated PDF.
     */
    fun shareReceiptPdf(context: Context, pdfFile: File) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                pdfFile
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Struk Pembelian - ${pdfFile.nameWithoutExtension}")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(shareIntent, "Bagikan / Cetak Struk PDF")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Formats clean 32-column (58mm) or 48-column (80mm) text receipt layout
     * suitable for Bluetooth / USB ESC-POS thermal printers.
     */
    fun generateThermalText(
        profile: RestaurantProfileEntity?,
        order: OrderEntity,
        items: List<OrderItemEntity>,
        paperWidthMm: Int = 58
    ): String {
        val width = if (paperWidthMm == 80) 48 else 32
        val sb = StringBuilder()

        fun center(text: String): String {
            if (text.length >= width) return text.take(width)
            val pad = (width - text.length) / 2
            return " ".repeat(pad) + text
        }

        fun divider(): String = "-".repeat(width)

        fun row(left: String, right: String): String {
            val space = width - left.length - right.length
            return if (space > 0) left + " ".repeat(space) + right else "$left $right"
        }

        sb.appendLine(center(profile?.name?.takeIf { it.isNotBlank() } ?: "TOKOKUH RESTO"))
        profile?.brandTagline?.takeIf { it.isNotBlank() }?.let { sb.appendLine(center(it)) }
        profile?.address?.takeIf { it.isNotBlank() }?.let { sb.appendLine(center(it)) }
        profile?.phone?.takeIf { it.isNotBlank() }?.let { sb.appendLine(center("Telp: $it")) }
        profile?.receiptHeader?.takeIf { it.isNotBlank() }?.let { sb.appendLine(center(it)) }
        sb.appendLine(divider())

        sb.appendLine(row("No. Struk:", order.orderNumber))
        sb.appendLine(row("Waktu:", CurrencyFormatter.formatDateTime(order.createdAt)))
        sb.appendLine(row("Layanan:", "${order.orderType.displayName} ${order.tableName ?: ""}"))
        sb.appendLine(row("Kasir:", order.cashierName))
        sb.appendLine(row("Pelanggan:", order.customerName))
        sb.appendLine(divider())

        for (item in items) {
            sb.appendLine(item.menuName)
            sb.appendLine(row("  ${item.quantity}x @ ${CurrencyFormatter.formatRupiah(item.unitPrice)}", CurrencyFormatter.formatRupiah(item.subtotal)))
            if (!item.note.isNullOrBlank()) {
                sb.appendLine("  * ${item.note}")
            }
        }
        sb.appendLine(divider())

        sb.appendLine(row("Subtotal:", CurrencyFormatter.formatRupiah(order.subtotal)))
        if (order.discountAmount > 0) {
            sb.appendLine(row("Diskon:", "-${CurrencyFormatter.formatRupiah(order.discountAmount)}"))
        }
        if (order.taxAmount > 0) {
            sb.appendLine(row("Pajak (${order.taxPercent.toInt()}%):", CurrencyFormatter.formatRupiah(order.taxAmount)))
        }
        if (order.serviceAmount > 0) {
            sb.appendLine(row("Layanan (${order.servicePercent.toInt()}%):", CurrencyFormatter.formatRupiah(order.serviceAmount)))
        }
        sb.appendLine(divider())
        sb.appendLine(row("TOTAL BAYAR:", CurrencyFormatter.formatRupiah(order.totalAmount)))
        sb.appendLine(row("Bayar (${order.paymentMethod.displayName}):", CurrencyFormatter.formatRupiah(order.paidAmount)))
        sb.appendLine(row("Kembalian:", CurrencyFormatter.formatRupiah(order.changeAmount)))
        sb.appendLine(divider())

        sb.appendLine(center(profile?.receiptFooter ?: "Terima Kasih Atas Kunjungan Anda!"))
        sb.appendLine(center("TOKOKUH.COM RESTO • OFFLINE POS"))
        sb.appendLine("\n\n\n") // Feed lines for paper tear

        return sb.toString()
    }
}
