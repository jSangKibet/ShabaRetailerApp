package com.acework.shabaretailer.ui.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.acework.shabaretailer.R

@Composable
fun DialogSuccess(action: () -> Unit, text: String) {
    Dialog(onDismissRequest = {}) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.background,
                    RoundedCornerShape(4.dp)
                )
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val loadingComposition by rememberLottieComposition(
                spec = LottieCompositionSpec.RawRes(
                    R.raw.success
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
                    .height(120.dp)
                    .width(120.dp)
            )
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = action,
                shape = RoundedCornerShape(0.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.okay).uppercase(),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}