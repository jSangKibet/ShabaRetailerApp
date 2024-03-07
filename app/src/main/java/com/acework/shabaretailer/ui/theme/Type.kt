package com.acework.shabaretailer.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.acework.shabaretailer.R

val workSansFamily = FontFamily(
    Font(R.font.work_sans_bold, FontWeight.Bold),
    Font(R.font.work_sans_semibold, FontWeight.SemiBold),
    Font(R.font.work_sans_regular, FontWeight.Normal)
)

val Typography = Typography(
    displaySmall = TextStyle(
        fontFamily = workSansFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = workSansFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        textDecoration = TextDecoration.Underline
    ),
    titleLarge = TextStyle(
        fontFamily = workSansFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    ),
    titleMedium = TextStyle(
        fontFamily = workSansFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp
    ),
    titleSmall = TextStyle(
        fontFamily = workSansFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = workSansFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = workSansFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp
    ),
    bodySmall = TextStyle(
        fontFamily = workSansFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    labelSmall = TextStyle(
        fontFamily = workSansFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),

    // ESO
    labelMedium = TextStyle(
        fontFamily = workSansFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        textDecoration = TextDecoration.Underline
    )
)