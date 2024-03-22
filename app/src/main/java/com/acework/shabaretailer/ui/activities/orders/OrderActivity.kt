package com.acework.shabaretailer.ui.activities.orders

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.acework.shabaretailer.R
import com.acework.shabaretailer.atlas.getItemName
import com.acework.shabaretailer.atlas.getTotal
import com.acework.shabaretailer.model.OrderItem
import com.acework.shabaretailer.ui.components.buttons.ButtonBar
import com.acework.shabaretailer.ui.components.buttons.ButtonBarOutlined
import com.acework.shabaretailer.ui.components.dialogs.DialogConfirmation
import com.acework.shabaretailer.ui.components.dialogs.DialogLoading
import com.acework.shabaretailer.ui.components.snackbars.SnackbarWithAction
import com.acework.shabaretailer.ui.components.views.headers.Header
import com.acework.shabaretailer.ui.theme.ShabaRetailersTheme
import com.acework.shabaretailer.ui.viewmodel.OrderVM
import com.acework.shabaretailer.ui.viewmodel.OrderVMFactory
import kotlinx.coroutines.launch
import java.util.Date

class OrderActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val orderId = intent.getStringExtra("orderId")
        if (orderId == null) {
            finish()
        } else {
            setContent {
                ShabaRetailersTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        ActivityRoot(
                            back = { finish() },
                            copyOrderNumber = { copyText("Order number", orderId) },
                            copyShipmentTrackingNumber = {
                                copyText(
                                    "Shipment tracking number",
                                    it
                                )
                            },
                            orderId = orderId
                        )
                    }
                }
            }
        }
    }

    private fun copyText(label: String, value: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData = ClipData.newPlainText(label, value)
        clipboard.setPrimaryClip(clip)
    }
}

@Composable
private fun ActivityRoot(
    back: () -> Unit,
    copyOrderNumber: () -> Unit,
    copyShipmentTrackingNumber: (String) -> Unit,
    orderId: String,
    orderVM: OrderVM = viewModel(factory = OrderVMFactory(orderId).Factory)
) {
    val uiState by orderVM.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()


    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Header(
                left = back,
                leftIcon = R.drawable.bi_arrow_left_short,
                right = { orderVM.orderActionClicked() },
                rightIcon = uiState.order.status.let {
                    when (it) {
                        "Canceled" -> R.drawable.bi_arrow_counterclockwise
                        "Pending" -> R.drawable.bi_x_circle_fill
                        "Confirmed" -> R.drawable.bi_hourglass_split
                        "Dispatched" -> R.drawable.bi_check_circle_fill
                        else -> R.drawable.bi_check_lg
                    }
                },
                subtitle = 0,
                subtitleString = "#: %s".format(uiState.order.id),
                title = R.string.order_details
            )

            LazyColumn(
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.order.orderItems.size) { index ->
                    OrderItem(
                        orderItem = uiState.order.orderItems[index],
                        orderVM = orderVM
                    )
                }
            }

            Column(
                modifier = Modifier
                    .border(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            style = MaterialTheme.typography.labelMedium,
                            text = stringResource(id = R.string.timestamp)
                        )
                        Text(
                            style = MaterialTheme.typography.titleSmall,
                            text = orderVM.dateFormatter.format(Date(uiState.order.timestamp))
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            style = MaterialTheme.typography.labelMedium,
                            text = stringResource(id = R.string.status)
                        )
                        Text(
                            style = MaterialTheme.typography.titleSmall,
                            text = uiState.order.status
                        )
                    }
                }

                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            style = MaterialTheme.typography.labelMedium,
                            text = stringResource(id = R.string.shipping_customs_cost)
                        )
                        uiState.order.shippingCosts.let {
                            val shippingCosts = it.total - it.bagTotal
                            Text(
                                style = MaterialTheme.typography.titleSmall,
                                text = "$%.2f".format(shippingCosts)
                            )
                        }
                    }

                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            style = MaterialTheme.typography.labelMedium,
                            text = stringResource(id = R.string.total)
                        )
                        Text(
                            style = MaterialTheme.typography.titleSmall,
                            text = "$%.2f".format(uiState.order.shippingCosts.total)
                        )
                    }
                }

                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            style = MaterialTheme.typography.labelMedium,
                            text = stringResource(id = R.string.delivery_location)
                        )
                        Text(
                            style = MaterialTheme.typography.titleSmall,
                            text = "${uiState.order.country}, ${uiState.order.city}"
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            style = MaterialTheme.typography.labelMedium,
                            text = stringResource(id = R.string.shipment_tracking_number)
                        )
                        Text(
                            style = MaterialTheme.typography.titleSmall,
                            text = uiState.order.shipmentTrackingNumber
                        )
                    }
                }


                val trackingNumberCopied = stringResource(id = R.string.tracking_number_copied)
                ButtonBar(onClick = {
                    copyShipmentTrackingNumber(uiState.order.shipmentTrackingNumber)
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(message = trackingNumberCopied)
                    }
                }, text = stringResource(id = R.string.copy_tracking_number))
                val orderNumberCopied = stringResource(id = R.string.order_number_copied)
                ButtonBarOutlined(onClick = {
                    copyOrderNumber()
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(message = orderNumberCopied)
                    }
                }, text = stringResource(id = R.string.copy_order_number))
            }
        }

        if (uiState.loading) {
            DialogLoading(text = stringResource(id = R.string.loading))
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        if (uiState.loadingError) {
            SnackbarWithAction(
                buttonText = R.string.retry_u,
                onClick = { orderVM.loadOrder() },
                text = R.string.order_loading_error
            )
        }

        if (uiState.confirmOrderCancellation) {
            DialogConfirmation(
                confirm = { orderVM.cancelOrder() },
                dismiss = { orderVM.orderCancellationRevoked() },
                msg = stringResource(id = R.string.do_you_want_to_cancel_order),
                title = stringResource(id = R.string.cancel_order)
            )
        }

        if (uiState.confirmOrderRestoration) {
            DialogConfirmation(
                confirm = { orderVM.restoreOrder() },
                dismiss = { orderVM.orderRestorationRevoked() },
                msg = stringResource(id = R.string.do_you_want_to_restore_order),
                title = stringResource(id = R.string.restore_order)
            )
        }

        if (uiState.confirmOrderReception) {
            DialogConfirmation(
                confirm = { orderVM.orderReceived() },
                dismiss = { orderVM.orderReceptionRevoked() },
                msg = stringResource(id = R.string.have_you_received_order),
                title = stringResource(id = R.string.order_received)
            )
        }

        if (uiState.updateError) {
            val orderUpdatingError = stringResource(id = R.string.order_updating_error)
            LaunchedEffect(snackbarHostState) {
                val result = snackbarHostState.showSnackbar(message = orderUpdatingError)
                if (result == SnackbarResult.Dismissed) orderVM.snackbarDisplayed()
            }
        }

        if (uiState.orderUpdated) {
            val orderUpdated = stringResource(id = R.string.order_updated)
            LaunchedEffect(snackbarHostState) {
                val result = snackbarHostState.showSnackbar(message = orderUpdated)
                if (result == SnackbarResult.Dismissed) orderVM.snackbarDisplayed()
            }
        }
    }
}

@Composable
fun OrderItem(
    orderItem: OrderItem,
    orderVM: OrderVM,
) {
    Column(
        modifier = Modifier
            .border(1.dp, MaterialTheme.colorScheme.surfaceVariant)
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(style = MaterialTheme.typography.titleMedium, text = getItemName(orderItem.sku))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = orderVM.getInsertDrawable(orderItem.insertColor)),
                contentDescription = stringResource(id = R.string.image_content_description)
            )

            Text(
                style = MaterialTheme.typography.titleSmall,
                text = "$%d x %d = $%,d".format(
                    orderItem.price,
                    orderItem.quantity,
                    orderItem.price * orderItem.quantity
                )
            )
        }
    }
}