package com.acework.shabaretailer.ui.activities.retailer

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.acework.shabaretailer.R
import com.acework.shabaretailer.ui.theme.ShabaRetailersTheme
import kotlinx.coroutines.launch

class WelcomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShabaRetailersTheme {
                Welcome { toLogIn() }
            }
        }
    }

    private fun toLogIn() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Welcome(
    close: () -> Unit
) {
    val scope = rememberCoroutineScope()

    // container
    Box(modifier = Modifier.fillMaxSize()) {
        val pagerState = rememberPagerState(pageCount = { 4 })

        // picture slider
        HorizontalPager(
            modifier = Modifier.fillMaxWidth(),
            state = pagerState
        ) { page ->
            Image(
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(
                    id =
                    when (page) {
                        0 -> R.drawable.shaba_welcome_1
                        1 -> R.drawable.shaba_welcome_2
                        2 -> R.drawable.shaba_welcome_3
                        else -> R.drawable.shaba_welcome_4
                    }
                ), contentDescription = stringResource(id = R.string.image_content_description)
            )
        }

        // navigation buttons
        Row(
            horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            if (pagerState.currentPage == 0) {
                // no button
                Spacer(modifier = Modifier.weight(1f))
            } else {
                // previous button
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.size(128.dp, 48.dp),
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    },
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Text(text = stringResource(id = R.string.previous))
                }
            }

            // next/okay button
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.size(128.dp, 48.dp),
                onClick = {
                    if (pagerState.currentPage == 3) {
                        close()
                    } else {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                shape = RoundedCornerShape(0.dp)
            ) {
                Text(text = stringResource(id = if (pagerState.currentPage == 3) R.string.okay else R.string.next))
            }
        }
    }
}