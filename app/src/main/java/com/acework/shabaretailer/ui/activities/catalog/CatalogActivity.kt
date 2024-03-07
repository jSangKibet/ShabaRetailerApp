package com.acework.shabaretailer.ui.activities.catalog

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.acework.shabaretailer.R
import com.acework.shabaretailer.ui.activities.retailer.AboutActivity
import com.acework.shabaretailer.MainActivity
import com.acework.shabaretailer.PostalService
import com.acework.shabaretailer.atlas.STATE_ERROR
import com.acework.shabaretailer.atlas.items
import com.acework.shabaretailer.model.Item
import com.acework.shabaretailer.ui.activities.orders.ByobActivity
import com.acework.shabaretailer.ui.activities.orders.MyOrdersActivity
import com.acework.shabaretailer.ui.activities.retailer.CreateRetailerActivity
import com.acework.shabaretailer.ui.activities.retailer.EditRetailerActivity
import com.acework.shabaretailer.ui.activities.retailer.EmailVerificationActivity
import com.acework.shabaretailer.ui.components.buttons.ButtonBar
import com.acework.shabaretailer.ui.components.buttons.ButtonBarOutlined
import com.acework.shabaretailer.ui.components.buttons.ButtonIconBlack
import com.acework.shabaretailer.ui.components.buttons.ButtonIconTextWhite
import com.acework.shabaretailer.ui.components.dialogs.DialogConfirmation
import com.acework.shabaretailer.ui.components.dialogs.DialogLoading
import com.acework.shabaretailer.ui.components.dialogs.DialogPaymentDetailsNew
import com.acework.shabaretailer.ui.components.snackbars.SnackbarModal
import com.acework.shabaretailer.ui.theme.ShabaRetailersTheme
import com.acework.shabaretailer.ui.viewmodel.CatalogUiState
import com.acework.shabaretailer.ui.viewmodel.CatalogViewModel
import kotlinx.coroutines.launch

class CatalogActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShabaRetailersTheme {
                ActivityRoot(
                    exitApp = { finish() },
                    copyAccountNumber = { copyAccountNumber() },
                    copyCreditCardNumber = { copyCreditCardNumber() },
                    openItem = { openItem(it) },
                    onSignedOut = { signedOut() },
                    toByb = { toByob() },
                    toEditRetailer = { toEditRetailer() },
                    toFinishCreatingRetailer = { toFinishCreatingRetailer() },
                    toMyOrders = { toMyOrders() },
                    toOpenTC = { openTC() },
                    toVerifyEmail = { toVerifyEmail() },
                    showAbout = { showAbout() }
                )
            }
        }
    }

    private fun openItem(sku: String) {
        val intent = Intent(this, CatalogItemActivity::class.java)
        intent.putExtra("sku", sku)
        startActivity(intent)
    }

    private fun toByob() {
        startActivity(Intent(this, ByobActivity::class.java))
    }

    private fun toEditRetailer() {
        startActivity(Intent(this, EditRetailerActivity::class.java))
    }

    private fun toFinishCreatingRetailer() {
        startActivity(Intent(this, CreateRetailerActivity::class.java))
        finish()
    }

    private fun toMyOrders() {
        startActivity(Intent(this, MyOrdersActivity::class.java))
    }

    private fun copyAccountNumber() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData = ClipData.newPlainText("Account number", "0100009411857")
        clipboard.setPrimaryClip(clip)
    }

    private fun copyCreditCardNumber() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData = ClipData.newPlainText("Credit card number", "4069061021158318")
        clipboard.setPrimaryClip(clip)
    }

    private fun openTC() {
        val browserIntent =
            Intent(Intent.ACTION_VIEW, Uri.parse("https://www.theshaba.com/terms-of-use"))
        startActivity(browserIntent)
    }

    private fun signedOut() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun showAbout() {
        startActivity(Intent(this, AboutActivity::class.java))
    }

    private fun toVerifyEmail() {
        startActivity(Intent(this, EmailVerificationActivity::class.java))
    }
}

@Composable
private fun ActivityRoot(
    exitApp: () -> Unit,
    copyAccountNumber: () -> Unit,
    copyCreditCardNumber: () -> Unit,
    openItem: (String) -> Unit,
    onSignedOut: () -> Unit,
    toByb: () -> Unit,
    toEditRetailer: () -> Unit,
    toFinishCreatingRetailer: () -> Unit,
    toMyOrders: () -> Unit,
    toOpenTC: () -> Unit,
    toVerifyEmail: () -> Unit,
    showAbout: () -> Unit,
    viewModel: CatalogViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    fun toggleDrawer() {
        coroutineScope.launch {
            drawerState.apply {
                if (isClosed) open() else close()
            }
        }
    }

    if (uiState.signedOut) onSignedOut()

    // parent view: hosts sidebar and wraps around catalog
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.background,
                drawerShape = RoundedCornerShape(0.dp)
            ) {
                Drawer(
                    uiState = uiState,
                    closeDrawer = { toggleDrawer() },
                    logout = { viewModel.setConfirmLoggingOut(true) },
                    showPaymentDetails = { viewModel.setShowingBankDetails(true) },
                    toEditRetailer = toEditRetailer,
                    toMyOrders = toMyOrders,
                    toOpenTC = toOpenTC,
                    showAbout = showAbout
                )
            }
        },
        drawerState = drawerState
    ) {
        Catalog(
            copyAccountNumber = copyAccountNumber,
            copyCreditCardNumber = copyCreditCardNumber,
            exitApp = exitApp,
            openDrawer = { toggleDrawer() },
            openItem = openItem,
            toByb = toByb,
            toFinishCreatingRetailer = toFinishCreatingRetailer,
            toVerifyEmail = toVerifyEmail,
            uiState = uiState,
            viewModel = viewModel
        )
    }
}

@Composable
private fun Catalog(
    copyAccountNumber: () -> Unit,
    copyCreditCardNumber: () -> Unit,
    exitApp: () -> Unit,
    openDrawer: () -> Unit,
    openItem: (String) -> Unit,
    toByb: () -> Unit,
    toFinishCreatingRetailer: () -> Unit,
    toVerifyEmail: () -> Unit,
    uiState: CatalogUiState,
    viewModel: CatalogViewModel,
) {
    Box(modifier = Modifier.fillMaxSize()) {

        // content
        Column(modifier = Modifier.fillMaxSize()) {
            // header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ButtonIconTextWhite(
                        icon = painterResource(id = R.drawable.bi_list),
                        onClick = openDrawer,
                        text = stringResource(id = R.string.menu)
                    )

                    ButtonIconTextWhite(
                        icon = painterResource(id = R.drawable.bi_cart_fill),
                        iconLeft = false,
                        onClick = toByb,
                        text = stringResource(id = R.string.place_order)
                    )
                }
                Image(
                    modifier = Modifier.align(Alignment.Center),
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = stringResource(id = R.string.image_content_description)
                )
            }

            // content
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                // item list
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items["1"]?.let {
                        Bag(item = it, openItem = openItem)
                    }
                    items["3"]?.let {
                        Bag(item = it, openItem = openItem)
                    }
                }
            }
        }

        // dialogs
        if (uiState.showBankDetails) {
            DialogPaymentDetailsNew(
                copyAccountNumber = copyAccountNumber,
                copyCreditCardNumber = copyCreditCardNumber,
                dismiss = { viewModel.setShowingBankDetails(false) }
            )
        }

        if (uiState.confirmLoggingOut) {
            DialogConfirmation(
                confirm = { viewModel.logout() },
                dismiss = { viewModel.setConfirmLoggingOut(false) },
                msg = stringResource(id = R.string.do_you_want_to_logout),
                title = stringResource(id = R.string.logout)
            )
        }

        // loading & errors
        if (uiState.loading) {
            DialogLoading(text = stringResource(id = R.string.loading))
        } else {

            // error lvl 1: local signed in user not loaded
            if (uiState.user == null) {
                SnackbarModal(
                    action = exitApp,
                    actionText = stringResource(id = R.string.okay_u),
                    text = stringResource(id = R.string.error_loading_user)
                )
            } else {

                // error lvl 2: local signed in user not verified
                if (!uiState.userVerified) {
                    SnackbarModal(
                        action = toVerifyEmail,
                        actionText = stringResource(id = R.string.verify_u),
                        text = stringResource(id = R.string.user_not_verified)
                    )
                } else {

                    // error lvl 3: there was an error loading the retailer
                    if (uiState.retailerLoading == STATE_ERROR) {
                        SnackbarModal(
                            action = { viewModel.loadRetailer() },
                            actionText = stringResource(id = R.string.retry_u),
                            text = stringResource(id = R.string.error_loading_retailer)
                        )
                    } else {

                        // error lvl 4: the retailer has not been created
                        if (!uiState.retailerExists) {
                            SnackbarModal(
                                action = toFinishCreatingRetailer,
                                actionText = stringResource(id = R.string.finish_u),
                                text = stringResource(id = R.string.finish_creating_retailer)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Bag(item: Item, openItem: (String) -> Unit) {
    Column(
        modifier = Modifier
            .border(
                1.dp,
                Color(0x33000000),
                RoundedCornerShape(0.dp)
            )
            .fillMaxWidth()
    ) {
        Box {
            Image(
                contentDescription = stringResource(id = R.string.image_content_description),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .align(Alignment.Center),
                painter = painterResource(id = if (item.sku == "1") R.drawable.twende_model else R.drawable.wahura_model)
            )
            Button(
                modifier = Modifier
                    .align(Alignment.Center)
                    .height(48.dp),
                onClick = { openItem(item.sku) },
                shape = RoundedCornerShape(0.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.view),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            ) {
                Text(
                    style = MaterialTheme.typography.titleLarge,
                    text = item.name
                )
                Text(
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                    text = item.description
                )
            }
        }
    }
}

@Composable
private fun Drawer(
    uiState: CatalogUiState,
    closeDrawer: () -> Unit,
    logout: () -> Unit,
    showAbout: () -> Unit,
    showPaymentDetails: () -> Unit,
    toEditRetailer: () -> Unit,
    toMyOrders: () -> Unit,
    toOpenTC: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DrawerHeader(uiState = uiState, toEditRetailer = toEditRetailer)
        ButtonBar(
            onClick = {
                toMyOrders()
                closeDrawer()
            },
            text = stringResource(id = R.string.my_orders)
        )
        ButtonBar(
            onClick = {
                showPaymentDetails()
                closeDrawer()
            },
            text = stringResource(id = R.string.payment_details)
        )
        ButtonBar(
            onClick = {
                toOpenTC()
                closeDrawer()
            },
            text = stringResource(id = R.string.terms_conditions)
        )
        Spacer(modifier = Modifier.weight(1f))


        ButtonBarOutlined(
            onClick = {
                showAbout()
                closeDrawer()
            },
            text = stringResource(id = R.string.about_shaba)
        )
        ButtonBar(
            onClick = {
                logout()
                closeDrawer()
            },
            text = stringResource(id = R.string.logout)
        )
    }
}

@Composable
private fun DrawerHeader(
    uiState: CatalogUiState,
    toEditRetailer: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .border(1.dp, MaterialTheme.colorScheme.surfaceVariant)
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (uiState.retailerExists) {
                Text(
                    style = MaterialTheme.typography.titleMedium,
                    text = PostalService.retailer.name
                )
                Text(
                    style = MaterialTheme.typography.titleSmall,
                    text = PostalService.retailer.email
                )
            }
        }

        // edit button
        ButtonIconBlack(
            enabled = true,
            iconResId = R.drawable.bi_pen_fill,
            onClick = toEditRetailer
        )
    }
}