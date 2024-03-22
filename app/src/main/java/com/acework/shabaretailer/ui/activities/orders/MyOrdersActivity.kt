package com.acework.shabaretailer.ui.activities.orders

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.acework.shabaretailer.PostalService
import com.acework.shabaretailer.R
import com.acework.shabaretailer.model.Order
import com.acework.shabaretailer.ui.components.views.headers.HeaderWithoutAction
import com.acework.shabaretailer.ui.theme.ShabaRetailersTheme
import com.acework.shabaretailer.ui.viewmodel.MyOrdersVM

class MyOrdersActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShabaRetailersTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyOrders(
                        back = { finish() },
                        openOrder = { openOrder(it) })
                }
            }
        }
    }

    private fun openOrder(order: Order) {
        val orderActivityIntent = Intent(this, OrderActivity::class.java)
        orderActivityIntent.putExtra("orderId", order.id)
        startActivity(orderActivityIntent)
    }
}

@Composable
fun MyOrders(
    back: () -> Unit,
    openOrder: (Order) -> Unit,
    myOrdersVM: MyOrdersVM = viewModel()
) {
    val uiState by myOrdersVM.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        HeaderWithoutAction(
            back = back,
            subtitle = PostalService.retailer.name,
            title = stringResource(id = R.string.my_orders)
        )

        LazyColumn(
            modifier = Modifier
                .padding(8.dp)
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.orders.size) { index ->
                Order(
                    openOrder = { openOrder(uiState.orders[index]) },
                    order = uiState.orders[index]
                )
            }
        }
    }
}

@Composable
fun Order(
    openOrder: () -> Unit,
    order: Order
) {
    Column(
        modifier = Modifier
            .border(1.dp, MaterialTheme.colorScheme.surfaceVariant)
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    style = MaterialTheme.typography.labelMedium,
                    text = stringResource(id = R.string.order_number)
                )
                Text(
                    style = MaterialTheme.typography.titleSmall,
                    text = order.id
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    style = MaterialTheme.typography.labelMedium,
                    text = stringResource(id = R.string.status)
                )
                Text(
                    style = MaterialTheme.typography.titleSmall,
                    text = order.status
                )
            }
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                style = MaterialTheme.typography.labelMedium,
                text = stringResource(id = R.string.total)
            )
            Text(
                style = MaterialTheme.typography.titleSmall,
                text = "$%.2f".format(order.shippingCosts.total)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Button(
                modifier = Modifier.height(48.dp),
                onClick = {
                    openOrder()
                },
                shape = RoundedCornerShape(0.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.open),
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}