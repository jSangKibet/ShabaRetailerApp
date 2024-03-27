package com.acework.shabaretailer.ui.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.acework.shabaretailer.R

@Composable
fun DialogLoading(text: String) {
    Dialog(onDismissRequest = {}) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.background,
                    RoundedCornerShape(4.dp)
                )
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            val loadingComposition by rememberLottieComposition(
                spec = LottieCompositionSpec.RawRes(
                    R.raw.loading
                )
            )
            Text(
                style = MaterialTheme.typography.titleSmall,
                text = text
            )
            LottieAnimation(
                composition = loadingComposition,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier
                    .height(125.dp)
                    .width(200.dp)
            )
        }
    }
}