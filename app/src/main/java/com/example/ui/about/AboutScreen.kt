package com.example.ui.about

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.TokokuhAccentYellow
import com.example.ui.theme.TokokuhBackground
import com.example.ui.theme.TokokuhBorder
import com.example.ui.theme.TokokuhMaroonDark
import com.example.ui.theme.TokokuhMaroonPrimary
import com.example.ui.theme.TokokuhSuccess
import com.example.ui.theme.TokokuhSurface
import com.example.ui.theme.TokokuhTextPrimary
import com.example.ui.theme.TokokuhTextSecondary
import com.example.ui.theme.TokokuhWhite
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    val context = LocalContext.current
    val versionName = remember {
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "1.0.0"
        } catch (e: Exception) {
            "1.0.0"
        }
    }

    val currentYear = remember {
        Calendar.getInstance().get(Calendar.YEAR)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Tentang TOKOKUH RESTO",
                            fontWeight = FontWeight.Bold,
                            color = TokokuhWhite,
                            fontSize = 17.sp
                        )
                        Text(
                            text = "Identitas Aplikasi & Pengembang Resmi",
                            color = TokokuhWhite.copy(alpha = 0.85f),
                            fontSize = 11.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("about_back_btn")
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali ke Dashboard",
                            tint = TokokuhWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TokokuhMaroonPrimary
                )
            )
        },
        containerColor = TokokuhBackground
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ================= 1. OFFICIAL LOGO HERO BANNER =================
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = TokokuhMaroonPrimary),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_tokokuh_resto),
                        contentDescription = "Official Logo TOKOKUH RESTO",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(64.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Bagian dari Ekosistem TOKOKUH.COM",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TokokuhWhite.copy(alpha = 0.95f)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = TokokuhMaroonDark
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CloudOff,
                                contentDescription = null,
                                tint = TokokuhAccentYellow,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "100% OFFLINE STANDALONE SYSTEM",
                                color = TokokuhWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // ================= 2. DESKRIPSI RESMI =================
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = TokokuhWhite),
                border = BorderStroke(1.dp, TokokuhBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = TokokuhMaroonPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Deskripsi Aplikasi",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TokokuhMaroonDark
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "TOKOKUH RESTO adalah sistem POS (Point of Sale) dan manajemen restoran profesional yang dirancang khusus untuk operasional mandiri 100% offline tanpa membutuhkan koneksi internet. Seluruh data transaksi, menu, stok, karyawan, dan laporan tersimpan aman di penyimpanan lokal perangkat Anda.",
                        fontSize = 13.sp,
                        color = TokokuhTextPrimary,
                        lineHeight = 20.sp
                    )
                }
            }

            // ================= 3. IDENTITAS DEVELOPER RESMI =================
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = TokokuhWhite),
                border = BorderStroke(1.dp, TokokuhBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Badge,
                            contentDescription = null,
                            tint = TokokuhMaroonPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Identitas Pengembang & Spesifikasi",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TokokuhMaroonDark
                        )
                    }

                    HorizontalDivider(color = TokokuhBorder, modifier = Modifier.padding(vertical = 4.dp))

                    SpecRow("Nama Aplikasi", "TOKOKUH RESTO")
                    SpecRow("Dikembangkan oleh", "LOGIS DENIS PRABOWO")
                    SpecRow("Brand Pengembang", "TOKOKUH.COM")
                    SpecRow("Kategori", "Aplikasi Kasir dan Manajemen Restoran")
                    SpecRow("Platform", "Android")
                    SpecRow("Versi", "$versionName (Release Offline)")
                    SpecRow("Sistem", "100% Offline POS System")
                    SpecRow("Database Mesin", "Room SQLite Standalone Local Storage")
                }
            }

            // ================= 4. JAMINAN PRIVASI & INTEGRITAS OFFLINE =================
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = TokokuhWhite),
                border = BorderStroke(1.dp, TokokuhBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Security,
                            contentDescription = null,
                            tint = TokokuhSuccess,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Jaminan Kedaulatan Data & Privasi",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TokokuhMaroonDark
                        )
                    }

                    FeatureCheckItem("100% Beroperasi Tanpa Internet atau Kuota Data")
                    FeatureCheckItem("Database SQLite Lokal via Jetpack Room tanpa Server Luar")
                    FeatureCheckItem("Foto Menu & Logo Restoran Tersimpan Permanen di Memori HP")
                    FeatureCheckItem("Bebas Biaya Langganan Bulanan & Bebas Ketergantungan Cloud")
                    FeatureCheckItem("Perhitungan Finansial Akurat Menggunakan Tipe Data Long Rupiah")
                    FeatureCheckItem("Fitur Backup & Restore Lengkap via Storage Access Framework")
                }
            }

            // ================= 5. CREDIT LOGO & DYNAMIC COPYRIGHT =================
            Image(
                painter = painterResource(id = R.drawable.logo_credit_struk),
                contentDescription = "TOKOKUH.COM RESTO",
                modifier = Modifier
                    .height(30.dp)
                    .fillMaxWidth(0.6f)
            )

            Text(
                text = "© $currentYear TOKOKUH.COM. Developed by Logis Denis Prabowo. All Rights Reserved.",
                fontSize = 11.sp,
                color = TokokuhTextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(0.9f)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun FeatureCheckItem(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            Icons.Default.CheckCircle,
            contentDescription = null,
            tint = TokokuhSuccess,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            color = TokokuhTextPrimary
        )
    }
}

@Composable
private fun SpecRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = TokokuhTextSecondary,
            modifier = Modifier.weight(1.2f)
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TokokuhTextPrimary,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1.8f)
        )
    }
}
