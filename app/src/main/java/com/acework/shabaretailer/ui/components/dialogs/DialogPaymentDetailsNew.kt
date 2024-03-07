package com.acework.shabaretailer.ui.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.acework.shabaretailer.R
import kotlinx.coroutines.launch

@Composable
fun DialogPaymentDetailsNew(
    copyAccountNumber: () -> Unit,
    copyCreditCardNumber: ()->Unit,
    dismiss: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Dialog(onDismissRequest = dismiss) {
        Box {
            Column(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.background,
                        RoundedCornerShape(0.dp)
                    )
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    style = MaterialTheme.typography.titleLarge,
                    text = stringResource(id = R.string.payment_details)
                )

                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            style = MaterialTheme.typography.bodyLarge,
                            text = stringResource(id = R.string.account_number)
                        )
                        Text(
                            style = MaterialTheme.typography.titleSmall,
                            text = stringResource(id = R.string.number_account_new)
                        )
                    }
                    Button(
                        onClick = {
                            copyAccountNumber()
                            scope.launch {
                                snackbarHostState.showSnackbar(message = "Account number copied")
                            }
                        },
                        shape = RoundedCornerShape(0.dp)
                    ) {
                        Text(
                            style = MaterialTheme.typography.titleLarge,
                            text = stringResource(id = R.string.copy)
                        )
                    }
                }

                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            style = MaterialTheme.typography.bodyLarge,
                            text = stringResource(id = R.string.credit_card_number)
                        )
                        Text(
                            style = MaterialTheme.typography.titleSmall,
                            text = stringResource(id = R.string.number_credit_card)
                        )
                    }
                    Button(
                        onClick = {
                            copyCreditCardNumber()
                            scope.launch {
                                snackbarHostState.showSnackbar(message = "Credit card number copied")
                            }
                        },
                        shape = RoundedCornerShape(0.dp)
                    ) {
                        Text(
                            style = MaterialTheme.typography.titleLarge,
                            text = stringResource(id = R.string.copy)
                        )
                    }
                }

                Text(
                    style = MaterialTheme.typography.bodyLarge,
                    text = stringResource(id = R.string.currency)
                )

                Text(
                    style = MaterialTheme.typography.titleSmall,
                    text = stringResource(id = R.string.usd)
                )

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = dismiss,
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Text(
                        style = MaterialTheme.typography.titleLarge,
                        text = stringResource(id = R.string.okay)
                    )
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}