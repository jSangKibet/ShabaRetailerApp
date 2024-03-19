package com.acework.shabaretailer.ui.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.acework.shabaretailer.R
import com.acework.shabaretailer.model.ShippingCosts
import com.acework.shabaretailer.ui.components.buttons.ButtonBar

@Composable
fun DialogCostBreakdown(
    dismiss: () -> Unit,
    shippingCosts: ShippingCosts
) {
    Dialog(onDismissRequest = dismiss) {
        Column(
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.background,
                    RoundedCornerShape(0.dp)
                )
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                style = MaterialTheme.typography.titleLarge,
                text = stringResource(id = R.string.cost_breakdown)
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    style = MaterialTheme.typography.titleSmall,
                    text = stringResource(id = R.string.cost_of_bags_label)
                )

                Text(
                    style = MaterialTheme.typography.titleMedium,
                    text = "$%.2f".format(shippingCosts.bagTotal)
                )
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    style = MaterialTheme.typography.titleSmall,
                    text = stringResource(id = R.string.weight_price_label)
                )

                Text(
                    style = MaterialTheme.typography.titleMedium,
                    text = "$%.2f".format(shippingCosts.weightPrice)
                )
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    style = MaterialTheme.typography.titleSmall,
                    text = stringResource(id = R.string.duties_label)
                )

                Text(
                    style = MaterialTheme.typography.titleMedium,
                    text = "$%.2f".format(shippingCosts.duties)
                )
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    style = MaterialTheme.typography.titleSmall,
                    text = stringResource(id = R.string.taxes_label)
                )

                Text(
                    style = MaterialTheme.typography.titleMedium,
                    text = "$%.2f".format(shippingCosts.taxes)
                )
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    style = MaterialTheme.typography.titleSmall,
                    text = stringResource(id = R.string.dhl_shipping_fees_label)
                )

                Text(
                    style = MaterialTheme.typography.titleMedium,
                    text = "$%.2f".format(shippingCosts.dhlFees)
                )
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    style = MaterialTheme.typography.titleSmall,
                    text = stringResource(id = R.string.dhl_express_fee_label)
                )

                Text(
                    style = MaterialTheme.typography.titleMedium,
                    text = "$%.2f".format(shippingCosts.dhlExpressFee)
                )
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    style = MaterialTheme.typography.titleMedium,
                    text = stringResource(id = R.string.total_shipping_costs_label)
                )

                Text(
                    style = MaterialTheme.typography.titleMedium,
                    text = "$%.2f".format(shippingCosts.getTotalShippingCosts())
                )
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    style = MaterialTheme.typography.titleMedium,
                    text = stringResource(id = R.string.total_order_cost_label)
                )

                Text(
                    style = MaterialTheme.typography.titleMedium,
                    text = "$%.2f".format(shippingCosts.total)
                )
            }

            Text(
                fontStyle = FontStyle.Italic,
                style = MaterialTheme.typography.bodySmall,
                text = stringResource(id = R.string.costs_in_usd)
            )

            ButtonBar(onClick = dismiss, text = stringResource(id = R.string.okay_u))
        }
    }
}