package com.acework.shabaretailer.ui.activities.orders

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.acework.shabaretailer.PostalService
import com.acework.shabaretailer.R
import com.acework.shabaretailer.atlas.STATE_ERROR
import com.acework.shabaretailer.ui.components.buttons.ButtonIconBlack
import com.acework.shabaretailer.ui.components.buttons.ButtonIconOutlinedGray
import com.acework.shabaretailer.ui.components.dialogs.DialogLoading
import com.acework.shabaretailer.ui.components.dialogs.DialogPaymentDetails
import com.acework.shabaretailer.ui.components.dialogs.DialogSuccess
import com.acework.shabaretailer.ui.components.snackbars.SnackbarModal
import com.acework.shabaretailer.ui.components.views.headers.HeaderWithoutAction
import com.acework.shabaretailer.ui.theme.ShabaRetailersTheme
import com.acework.shabaretailer.ui.viewmodel.ByobUiState
import com.acework.shabaretailer.ui.viewmodel.ByobViewModel

class ByobActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShabaRetailersTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    ActivityRoot(
                        back = { finish() },
                        copyAccountNumber = { copyAccountNumberToClipboard() },
                        viewOrderTC = { openTC() }
                    )
                }
            }
        }
    }

    private fun openTC() {
        val browserIntent =
            Intent(Intent.ACTION_VIEW, Uri.parse("https://www.theshaba.com/terms-of-use"))
        startActivity(browserIntent)
    }

    private fun copyAccountNumberToClipboard() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData = ClipData.newPlainText("Account number", "0100009411857")
        clipboard.setPrimaryClip(clip)
    }

}

@Composable
private fun ActivityRoot(
    byobViewModel: ByobViewModel = viewModel(),
    back: () -> Unit,
    copyAccountNumber: () -> Unit,
    viewOrderTC: () -> Unit
) {
    val byobUiState by byobViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    Box(modifier = Modifier.fillMaxSize()) {
        if (byobUiState.currentSection == ByobViewModel.BUILD_BOX) {
            BuildBox(
                back = back,
                byobViewModel = byobViewModel,
                byobUiState = byobUiState
            )
        }

        if (byobUiState.currentSection == ByobViewModel.CHOOSE_INSERT_COLORS) {
            ChooseInserts(
                byobViewModel = byobViewModel,
                byobUiState = byobUiState
            )
        }

        if (byobUiState.currentSection == ByobViewModel.CONFIRM_ORDER) {
            OrderSummary(
                byobViewModel = byobViewModel,
                byobUiState = byobUiState,
                copyAccountNumber = copyAccountNumber,
                viewOrderTC = viewOrderTC
            )
        }

        if (byobUiState.loading) {
            DialogLoading(text = stringResource(id = byobUiState.loadingMessage))
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        if (byobUiState.errorPlacingOrder) {
            LaunchedEffect(snackbarHostState) {
                snackbarHostState.showSnackbar(message = "There was an error placing your order. Please try again.")
                byobViewModel.errorConsumed()
            }
        }

        if (byobUiState.orderPlaced) {
            DialogSuccess(action = back, text = stringResource(id = R.string.order_placed))
        }

        if (byobUiState.loadingRates == STATE_ERROR) {
            SnackbarModal(
                text = stringResource(id = R.string.error_getting_shipping_rates),
                action = { byobViewModel.getRating() },
                actionText = stringResource(id = R.string.retry_u)
            )
        }
    }
}

@Composable
fun BuildBox(
    back: () -> Unit,
    byobViewModel: ByobViewModel,
    byobUiState: ByobUiState
) {
    Column(modifier = Modifier.fillMaxSize()) {
        HeaderWithoutAction(
            back = back,
            subtitle = stringResource(id = R.string.choose_bags_in_box),
            title = stringResource(id = R.string.build_your_box)
        )

        Column(
            modifier = Modifier
                .padding(16.dp)
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                style = MaterialTheme.typography.bodyLarge,
                text = stringResource(id = R.string.choose_bags)
            )

            Column(
                modifier = Modifier
                    .border(
                        1.dp, Color(0x33000000), RoundedCornerShape(0.dp)
                    )
                    .fillMaxWidth()
                    .padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    style = MaterialTheme.typography.labelSmall,
                    text = stringResource(id = R.string.wahura_bucket_bag)
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    ButtonIconBlack(enabled = byobUiState.wahura > 0,
                        iconResId = R.drawable.bi_dash_lg,
                        onClick = { byobViewModel.decrementWahura() })

                    Text(
                        style = MaterialTheme.typography.displaySmall,
                        text = (byobUiState.wahura / 2).toString()
                    )

                    ButtonIconBlack(enabled = (byobUiState.wahura + byobUiState.twende) < 7,
                        iconResId = R.drawable.bi_plus_lg,
                        onClick = { byobViewModel.incrementWahura() })
                }
            }

            Column(
                modifier = Modifier
                    .border(
                        1.dp, Color(0x33000000), RoundedCornerShape(0.dp)
                    )
                    .fillMaxWidth()
                    .padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    style = MaterialTheme.typography.labelSmall,
                    text = stringResource(id = R.string.twende_sling_bag)
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    ButtonIconBlack(enabled = byobUiState.twende > 0,
                        iconResId = R.drawable.bi_dash_lg,
                        onClick = { byobViewModel.decrementTwende() })

                    Text(
                        style = MaterialTheme.typography.displaySmall,
                        text = byobUiState.twende.toString()
                    )

                    ButtonIconBlack(enabled = (byobUiState.wahura + byobUiState.twende) < 8,
                        iconResId = R.drawable.bi_plus_lg,
                        onClick = { byobViewModel.incrementTwende() })
                }
            }

            Text(
                style = MaterialTheme.typography.titleSmall,
                text = "USD %.2f".format(byobViewModel.getTotal().toDouble())
            )

            Column(modifier = Modifier.fillMaxWidth()) {
                val progress = (byobUiState.wahura + byobUiState.twende) / 8f

                val progressAnimate by animateFloatAsState(
                    targetValue = progress, label = "animation"
                )

                LinearProgressIndicator(
                    modifier = Modifier
                        .border(
                            1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(0.dp)
                        )
                        .fillMaxWidth()
                        .height(48.dp),
                    progress = progressAnimate,
                    trackColor = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    style = MaterialTheme.typography.labelSmall,
                    text = stringResource(id = if (progress < 1) R.string.keep_adding_bags else R.string.box_full)
                )
            }

            Button(
                enabled = (byobUiState.wahura + byobUiState.twende) == 8,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                onClick = { byobViewModel.setSection(ByobViewModel.CHOOSE_INSERT_COLORS) },
                shape = RoundedCornerShape(0.dp)
            ) {
                Text(
                    style = MaterialTheme.typography.titleSmall,
                    text = stringResource(id = R.string.byb_step_2)
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChooseInserts(
    byobViewModel: ByobViewModel,
    byobUiState: ByobUiState
) {
    BackHandler {
        byobViewModel.setSection(ByobViewModel.BUILD_BOX)
    }

    // page
    Column(modifier = Modifier.fillMaxSize()) {

        // header
        HeaderWithoutAction(
            back = { byobViewModel.setSection(ByobViewModel.BUILD_BOX) },
            subtitle = stringResource(id = R.string.choose_insert_colors_ext),
            title = stringResource(id = R.string.choose_insert_colors)
        )

        // content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // prompt
            Text(
                style = MaterialTheme.typography.bodySmall,
                text = stringResource(id = R.string.tap_color_to_add)
            )

            // wahura inserts
            if (byobUiState.wahura > 0) {
                Column(
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.surfaceVariant
                        )
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {

                    // bag title
                    Text(
                        style = MaterialTheme.typography.bodySmall,
                        text = stringResource(id = R.string.wahura_bucket_bag)
                    )

                    // remaining bags
                    val remaining = byobViewModel.getWahuraPendingInserts()
                    if (remaining > 0) {
                        Text(
                            style = MaterialTheme.typography.bodyLarge,
                            text = "$remaining left"
                        )
                    }

                    // mustard insert selection
                    if (byobUiState.wahuraMustard > 0) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            // decrement button
                            ButtonIconOutlinedGray(
                                enabled = true,
                                iconResId = R.drawable.bi_dash_lg
                            ) { byobViewModel.decrementWahuraMustard() }

                            // number of inserts
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "${byobUiState.wahuraMustard}x")
                                Image(
                                    contentDescription = stringResource(id = R.string.image_content_description),
                                    modifier = Modifier
                                        .height(32.dp)
                                        .width(32.dp),
                                    painter = painterResource(id = R.drawable.mustard_circle)
                                )
                            }

                            // increment button
                            ButtonIconOutlinedGray(
                                enabled = remaining > 0,
                                iconResId = R.drawable.bi_plus_lg
                            ) { byobViewModel.incrementWahuraMustard() }
                        }
                    }

                    // dark brown insert selection
                    if (byobUiState.wahuraDarkBrown > 0) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            // decrement button
                            ButtonIconOutlinedGray(
                                enabled = true,
                                iconResId = R.drawable.bi_dash_lg
                            ) { byobViewModel.decrementWahuraDarkBrown() }

                            // number of inserts
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "${byobUiState.wahuraDarkBrown}x")
                                Image(
                                    contentDescription = stringResource(id = R.string.image_content_description),
                                    modifier = Modifier
                                        .height(32.dp)
                                        .width(32.dp),
                                    painter = painterResource(id = R.drawable.dark_brown_circle)
                                )
                            }

                            // increment button
                            ButtonIconOutlinedGray(
                                enabled = remaining > 0,
                                iconResId = R.drawable.bi_plus_lg
                            ) { byobViewModel.incrementWahuraDarkBrown() }
                        }
                    }

                    // dusty pink insert selection
                    if (byobUiState.wahuraDustyPink > 0) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            // decrement button
                            ButtonIconOutlinedGray(
                                enabled = true,
                                iconResId = R.drawable.bi_dash_lg
                            ) { byobViewModel.decrementWahuraDustyPink() }

                            // number of inserts
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "${byobUiState.wahuraDustyPink}x")
                                Image(
                                    contentDescription = stringResource(id = R.string.image_content_description),
                                    modifier = Modifier
                                        .height(32.dp)
                                        .width(32.dp),
                                    painter = painterResource(id = R.drawable.dusty_pink_circle)
                                )
                            }

                            // increment button
                            ButtonIconOutlinedGray(
                                enabled = remaining > 0,
                                iconResId = R.drawable.bi_plus_lg
                            ) { byobViewModel.incrementWahuraDustyPink() }
                        }
                    }

                    // taupe insert selection
                    if (byobUiState.wahuraTaupe > 0) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            // decrement button
                            ButtonIconOutlinedGray(
                                enabled = true,
                                iconResId = R.drawable.bi_dash_lg
                            ) { byobViewModel.decrementWahuraTaupe() }

                            // number of inserts
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "${byobUiState.wahuraTaupe}x")
                                Image(
                                    contentDescription = stringResource(id = R.string.image_content_description),
                                    modifier = Modifier
                                        .height(32.dp)
                                        .width(32.dp),
                                    painter = painterResource(id = R.drawable.taupe_circle)
                                )
                            }

                            // increment button
                            ButtonIconOutlinedGray(
                                enabled = remaining > 0,
                                iconResId = R.drawable.bi_plus_lg
                            ) { byobViewModel.incrementWahuraTaupe() }
                        }
                    }

                    // black insert selection
                    if (byobUiState.wahuraBlack > 0) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            // decrement button
                            ButtonIconOutlinedGray(
                                enabled = true,
                                iconResId = R.drawable.bi_dash_lg
                            ) { byobViewModel.decrementWahuraBlack() }

                            // number of inserts
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "${byobUiState.wahuraBlack}x")
                                Image(
                                    contentDescription = stringResource(id = R.string.image_content_description),
                                    modifier = Modifier
                                        .height(32.dp)
                                        .width(32.dp),
                                    painter = painterResource(id = R.drawable.black_circle)
                                )
                            }

                            // increment button
                            ButtonIconOutlinedGray(
                                enabled = remaining > 0,
                                iconResId = R.drawable.bi_plus_lg
                            ) { byobViewModel.incrementWahuraBlack() }
                        }
                    }

                    // insert colors bar
                    if (remaining > 0) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (byobUiState.wahuraMustard < 1) {
                                InsertColorButton(
                                    image = R.drawable.mustard_circle,
                                    onClick = { byobViewModel.incrementWahuraMustard() },
                                    text = stringResource(id = R.string.mustard)
                                )
                            }
                            if (byobUiState.wahuraDarkBrown < 1) {
                                InsertColorButton(
                                    image = R.drawable.dark_brown_circle,
                                    onClick = { byobViewModel.incrementWahuraDarkBrown() },
                                    text = stringResource(id = R.string.dark_brown)
                                )
                            }
                            if (byobUiState.wahuraDustyPink < 1) {
                                InsertColorButton(
                                    image = R.drawable.dusty_pink_circle,
                                    onClick = { byobViewModel.incrementWahuraDustyPink() },
                                    text = stringResource(id = R.string.dusty_pink)
                                )
                            }
                            if (byobUiState.wahuraTaupe < 1) {
                                InsertColorButton(
                                    image = R.drawable.taupe_circle,
                                    onClick = { byobViewModel.incrementWahuraTaupe() },
                                    text = stringResource(id = R.string.taupe)
                                )
                            }
                            if (byobUiState.wahuraBlack < 1) {
                                InsertColorButton(
                                    image = R.drawable.black_circle,
                                    onClick = { byobViewModel.incrementWahuraBlack() },
                                    text = stringResource(id = R.string.black)
                                )
                            }
                        }
                    }
                }
            }

            // twende inserts
            if (byobUiState.twende > 0) {
                Column(
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.surfaceVariant
                        )
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {

                    // bag title
                    Text(
                        style = MaterialTheme.typography.bodySmall,
                        text = stringResource(id = R.string.twende_sling_bag)
                    )

                    // remaining bags
                    val remaining = byobViewModel.getTwendePendingInserts()
                    if (remaining > 0) {
                        Text(
                            style = MaterialTheme.typography.bodyLarge,
                            text = "$remaining left"
                        )
                    }

                    // mustard insert selection
                    if (byobUiState.twendeMustard > 0) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            // decrement button
                            ButtonIconOutlinedGray(
                                enabled = true,
                                iconResId = R.drawable.bi_dash_lg
                            ) { byobViewModel.decrementTwendeMustard() }

                            // number of inserts
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "${byobUiState.twendeMustard}x")
                                Image(
                                    contentDescription = stringResource(id = R.string.image_content_description),
                                    modifier = Modifier
                                        .height(32.dp)
                                        .width(32.dp),
                                    painter = painterResource(id = R.drawable.mustard_circle)
                                )
                            }

                            // increment button
                            ButtonIconOutlinedGray(
                                enabled = remaining > 0,
                                iconResId = R.drawable.bi_plus_lg
                            ) { byobViewModel.incrementTwendeMustard() }
                        }
                    }

                    // dark brown insert selection
                    if (byobUiState.twendeDarkBrown > 0) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            // decrement button
                            ButtonIconOutlinedGray(
                                enabled = true,
                                iconResId = R.drawable.bi_dash_lg
                            ) { byobViewModel.decrementTwendeDarkBrown() }

                            // number of inserts
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "${byobUiState.twendeDarkBrown}x")
                                Image(
                                    contentDescription = stringResource(id = R.string.image_content_description),
                                    modifier = Modifier
                                        .height(32.dp)
                                        .width(32.dp),
                                    painter = painterResource(id = R.drawable.dark_brown_circle)
                                )
                            }

                            // increment button
                            ButtonIconOutlinedGray(
                                enabled = remaining > 0,
                                iconResId = R.drawable.bi_plus_lg
                            ) { byobViewModel.incrementTwendeDarkBrown() }
                        }
                    }

                    // dusty pink insert selection
                    if (byobUiState.twendeDustyPink > 0) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            // decrement button
                            ButtonIconOutlinedGray(
                                enabled = true,
                                iconResId = R.drawable.bi_dash_lg
                            ) { byobViewModel.decrementTwendeDustyPink() }

                            // number of inserts
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "${byobUiState.twendeDustyPink}x")
                                Image(
                                    contentDescription = stringResource(id = R.string.image_content_description),
                                    modifier = Modifier
                                        .height(32.dp)
                                        .width(32.dp),
                                    painter = painterResource(id = R.drawable.dusty_pink_circle)
                                )
                            }

                            // increment button
                            ButtonIconOutlinedGray(
                                enabled = remaining > 0,
                                iconResId = R.drawable.bi_plus_lg
                            ) { byobViewModel.incrementTwendeDustyPink() }
                        }
                    }

                    // taupe insert selection
                    if (byobUiState.twendeTaupe > 0) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            // decrement button
                            ButtonIconOutlinedGray(
                                enabled = true,
                                iconResId = R.drawable.bi_dash_lg
                            ) { byobViewModel.decrementTwendeTaupe() }

                            // number of inserts
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "${byobUiState.twendeTaupe}x")
                                Image(
                                    contentDescription = stringResource(id = R.string.image_content_description),
                                    modifier = Modifier
                                        .height(32.dp)
                                        .width(32.dp),
                                    painter = painterResource(id = R.drawable.taupe_circle)
                                )
                            }

                            // increment button
                            ButtonIconOutlinedGray(
                                enabled = remaining > 0,
                                iconResId = R.drawable.bi_plus_lg
                            ) { byobViewModel.incrementTwendeTaupe() }
                        }
                    }

                    // black insert selection
                    if (byobUiState.twendeBlack > 0) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            // decrement button
                            ButtonIconOutlinedGray(
                                enabled = true,
                                iconResId = R.drawable.bi_dash_lg
                            ) { byobViewModel.decrementTwendeBlack() }

                            // number of inserts
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "${byobUiState.twendeBlack}x")
                                Image(
                                    contentDescription = stringResource(id = R.string.image_content_description),
                                    modifier = Modifier
                                        .height(32.dp)
                                        .width(32.dp),
                                    painter = painterResource(id = R.drawable.black_circle)
                                )
                            }

                            // increment button
                            ButtonIconOutlinedGray(
                                enabled = remaining > 0,
                                iconResId = R.drawable.bi_plus_lg
                            ) { byobViewModel.incrementTwendeBlack() }
                        }
                    }

                    // insert colors bar
                    if (remaining > 0) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (byobUiState.twendeMustard < 1) {
                                InsertColorButton(
                                    image = R.drawable.mustard_circle,
                                    onClick = { byobViewModel.incrementTwendeMustard() },
                                    text = stringResource(id = R.string.mustard)
                                )
                            }
                            if (byobUiState.twendeDarkBrown < 1) {
                                InsertColorButton(
                                    image = R.drawable.dark_brown_circle,
                                    onClick = { byobViewModel.incrementTwendeDarkBrown() },
                                    text = stringResource(id = R.string.dark_brown)
                                )
                            }
                            if (byobUiState.twendeDustyPink < 1) {
                                InsertColorButton(
                                    image = R.drawable.dusty_pink_circle,
                                    onClick = { byobViewModel.incrementTwendeDustyPink() },
                                    text = stringResource(id = R.string.dusty_pink)
                                )
                            }
                            if (byobUiState.twendeTaupe < 1) {
                                InsertColorButton(
                                    image = R.drawable.taupe_circle,
                                    onClick = { byobViewModel.incrementTwendeTaupe() },
                                    text = stringResource(id = R.string.taupe)
                                )
                            }
                            if (byobUiState.twendeBlack < 1) {
                                InsertColorButton(
                                    image = R.drawable.black_circle,
                                    onClick = { byobViewModel.incrementTwendeBlack() },
                                    text = stringResource(id = R.string.black)
                                )
                            }
                        }
                    }
                }
            }

            // to summary
            Button(
                enabled = (byobViewModel.getWahuraPendingInserts() + byobViewModel.getTwendePendingInserts()) < 1,
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    byobViewModel.setSection(ByobViewModel.CONFIRM_ORDER)
                    byobViewModel.getRating()
                },
                shape = RoundedCornerShape(0.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.view_summary),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
fun OrderSummary(
    byobViewModel: ByobViewModel,
    byobUiState: ByobUiState,
    copyAccountNumber: () -> Unit,
    viewOrderTC: () -> Unit
) {
    BackHandler {
        byobViewModel.setSection(ByobViewModel.CHOOSE_INSERT_COLORS)
    }

    Column {
        // header
        HeaderWithoutAction(
            back = { byobViewModel.setSection(ByobViewModel.CHOOSE_INSERT_COLORS) },
            subtitle = stringResource(id = R.string.check_order_details),
            title = stringResource(id = R.string.confirm_order)
        )

        // content
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // details title
            Text(
                style = MaterialTheme.typography.bodyLarge,
                text = stringResource(id = R.string.order_details)
            )

            val bagsTotal = byobViewModel.getTotal()

            // bags total
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    style = MaterialTheme.typography.titleSmall,
                    text = stringResource(id = R.string.bags_total_lbl)
                )

                Text(
                    style = MaterialTheme.typography.titleLarge,
                    text = " $%.2f".format(bagsTotal.toDouble())
                )
            }

            // transport cost
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    style = MaterialTheme.typography.titleSmall,
                    text = stringResource(id = R.string.shipping_fee_lbl)
                )

                Text(
                    style = MaterialTheme.typography.titleLarge,
                    text = "$%.2f".format(byobUiState.shipping)
                )
            }

            // total cost
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    style = MaterialTheme.typography.titleSmall,
                    text = stringResource(id = R.string.total_cost_lbl)
                )

                Text(
                    style = MaterialTheme.typography.titleLarge,
                    text = "$%.2f".format(bagsTotal + byobUiState.shipping)
                )
            }

            // retailer info title
            Text(
                style = MaterialTheme.typography.bodyLarge,
                text = stringResource(id = R.string.recipient_information)
            )

            // name
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    style = MaterialTheme.typography.titleSmall,
                    text = stringResource(id = R.string.name_lbl)
                )

                Text(
                    style = MaterialTheme.typography.titleLarge,
                    text = PostalService.retailer.name
                )
            }


            // country
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    style = MaterialTheme.typography.titleSmall,
                    text = stringResource(id = R.string.country_lbl)
                )

                Text(
                    style = MaterialTheme.typography.titleLarge,
                    text = PostalService.retailer.country
                )
            }

            // city
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    style = MaterialTheme.typography.titleSmall,
                    text = stringResource(id = R.string.city_lbl)
                )

                Text(
                    style = MaterialTheme.typography.titleLarge,
                    text = PostalService.retailer.city
                )
            }

            // email
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    style = MaterialTheme.typography.titleSmall,
                    text = stringResource(id = R.string.email_lbl)
                )

                Text(
                    style = MaterialTheme.typography.titleLarge,
                    text = PostalService.retailer.email
                )
            }

            // order confirmation title
            Text(
                style = MaterialTheme.typography.bodyLarge,
                text = stringResource(id = R.string.order_confirmation)
            )

            // T&C check
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = byobUiState.orderTcAccepted,
                    onCheckedChange = { byobViewModel.updateOrderTcAccepted(it) })

                Text(
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodySmall,
                    text = stringResource(id = R.string.order_tc_accepted)
                )
            }

            // T&C
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = viewOrderTC,
                shape = RoundedCornerShape(0.dp)
            ) {
                Text(
                    style = MaterialTheme.typography.titleMedium,
                    text = stringResource(id = R.string.tc)
                )
            }

            // bank details
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { byobViewModel.updateShowBankDetails(true) },
                shape = RoundedCornerShape(0.dp)
            ) {
                Text(
                    style = MaterialTheme.typography.titleMedium,
                    text = stringResource(id = R.string.payment_details)
                )
            }

            // confirm
            Button(
                enabled = byobUiState.orderTcAccepted && !byobUiState.orderPlaced,
                modifier = Modifier.fillMaxWidth(),
                onClick = { byobViewModel.getShipment() },
                shape = RoundedCornerShape(0.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.confirm_order),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }

    // bank details dialog
    if (byobUiState.showBankDetails) {
        DialogPaymentDetails(
            copyAccountNumber = copyAccountNumber,
            dismiss = { byobViewModel.updateShowBankDetails(false) }
        )
    }
}

@Composable
fun InsertColorButton(image: Int, onClick: () -> Unit, text: String) {
    Button(
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.tertiary
        ),
        contentPadding = PaddingValues(8.dp),
        modifier = Modifier.defaultMinSize(minWidth = 80.dp),
        onClick = onClick,
        shape = RoundedCornerShape(3.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                contentDescription = stringResource(id = R.string.image_content_description),
                modifier = Modifier
                    .height(32.dp)
                    .width(32.dp),
                painter = painterResource(id = image)
            )
            Text(
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodySmall,
                text = text
            )
        }
    }
}