package com.acework.shabaretailer.ui.activities.catalog

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.acework.shabaretailer.R
import com.acework.shabaretailer.ui.components.views.headers.HeaderWithoutSubtitle
import com.acework.shabaretailer.atlas.getImage
import com.acework.shabaretailer.atlas.getPrice
import com.acework.shabaretailer.ui.activities.orders.ByobActivity
import com.acework.shabaretailer.ui.theme.ShabaRetailersTheme
import com.acework.shabaretailer.ui.viewmodel.CatalogItemViewModel
import com.acework.shabaretailer.ui.viewmodel.CatalogItemViewModelFactory

class CatalogItemActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sku = intent.getStringExtra("sku")
        if (sku == null) {
            finish()
        } else {
            setContent {
                ShabaRetailersTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        CatalogItemPage(
                            sku = sku,
                            back = { finish() },
                            toByb = { toByob() }
                        )
                    }
                }
            }
        }
    }

    private fun toByob() {
        startActivity(Intent(this, ByobActivity::class.java))
        finish()
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CatalogItemPage(
    sku: String,
    catalogItemViewModel: CatalogItemViewModel = viewModel(factory = CatalogItemViewModelFactory(sku).Factory),
    back: () -> Unit,
    toByb: () -> Unit
) {
    val catalogItemUiState by catalogItemViewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        HeaderWithoutSubtitle(
            left = back,
            leftIcon = R.drawable.bi_arrow_left_short,
            right = toByb,
            rightIcon = R.drawable.bi_cart_fill,
            title = catalogItemUiState.item.name
        )


        /* CONTENT */
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(1f)
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                // item images
                val pagerState = rememberPagerState(pageCount = { 4 })
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    HorizontalPager(
                        modifier = Modifier.fillMaxWidth(),
                        state = pagerState
                    ) { page ->
                        Image(
                            contentDescription = stringResource(id = R.string.image_content_description),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(),
                            painter = painterResource(id = catalogItemUiState.item.getImage(page = page))
                        )
                    }
                    Row(modifier = Modifier.align(Alignment.BottomCenter)) {
                        repeat(4) {
                            val color =
                                if (it == pagerState.currentPage) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                            Box(
                                modifier = Modifier
                                    .padding(2.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .size(16.dp)
                            )
                        }
                    }
                }

                // price
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .border(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(
                        style = MaterialTheme.typography.titleSmall,
                        text = stringResource(id = R.string.price_lbl)
                    )
                    Text(
                        style = MaterialTheme.typography.titleLarge,
                        text = "USD %.2f".format(catalogItemUiState.item.getPrice().toDouble())
                    )
                }
            }
            if (!catalogItemUiState.showingBasic) {
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.onPrimary)
                        .fillMaxSize(1f)
                        .padding(8.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        style = MaterialTheme.typography.titleLarge,
                        text = stringResource(id = R.string.description)
                    )

                    Text(
                        style = MaterialTheme.typography.titleSmall,
                        text = catalogItemUiState.item.description
                    )

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            style = MaterialTheme.typography.titleLarge,
                            text = stringResource(id = R.string.size_lbl)
                        )

                        Text(
                            style = MaterialTheme.typography.titleSmall,
                            text = catalogItemUiState.item.size
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            style = MaterialTheme.typography.titleLarge,
                            text = stringResource(id = R.string.material_lbl)
                        )

                        Text(
                            style = MaterialTheme.typography.titleSmall,
                            text = catalogItemUiState.item.material
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            style = MaterialTheme.typography.titleLarge,
                            text = stringResource(id = R.string.weaving_lbl)
                        )

                        Text(
                            style = MaterialTheme.typography.titleSmall,
                            text = catalogItemUiState.item.weaving
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            style = MaterialTheme.typography.titleLarge,
                            text = stringResource(id = R.string.color_lbl)
                        )

                        Text(
                            style = MaterialTheme.typography.titleSmall,
                            text = catalogItemUiState.item.color
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            style = MaterialTheme.typography.titleLarge,
                            text = stringResource(id = R.string.strap_lbl)
                        )

                        Text(
                            style = MaterialTheme.typography.titleSmall,
                            text = catalogItemUiState.item.strap
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            style = MaterialTheme.typography.titleLarge,
                            text = stringResource(id = R.string.strap_length_lbl)
                        )

                        Text(
                            style = MaterialTheme.typography.titleSmall,
                            text = catalogItemUiState.item.strapLength
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            style = MaterialTheme.typography.titleLarge,
                            text = stringResource(id = R.string.insert_lbl)
                        )

                        Text(
                            style = MaterialTheme.typography.titleSmall,
                            text = catalogItemUiState.item.insert
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            style = MaterialTheme.typography.titleLarge,
                            text = stringResource(id = R.string.weight_lbl)
                        )

                        Text(
                            style = MaterialTheme.typography.titleSmall,
                            text = "${catalogItemUiState.item.weight} g"
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            style = MaterialTheme.typography.titleLarge,
                            text = stringResource(id = R.string.sku_lbl)
                        )

                        Text(
                            style = MaterialTheme.typography.titleSmall,
                            text = catalogItemUiState.item.sku
                        )
                    }

                    Text(
                        style = MaterialTheme.typography.titleLarge,
                        text = stringResource(id = R.string.features)
                    )

                    for (feature in catalogItemUiState.item.features) {
                        Text(
                            style = MaterialTheme.typography.titleSmall,
                            text = "\u2022 $feature"
                        )
                    }
                }
            }
        }

        /* BUTTONS */
        Row {
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (catalogItemUiState.showingBasic) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background,
                    contentColor = if (catalogItemUiState.showingBasic) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.primary
                ),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .height(48.dp)
                    .weight(1f),
                onClick = { catalogItemViewModel.showBasic() },
                shape = RoundedCornerShape(0.dp)
            ) {
                Text(text = stringResource(id = R.string.basic))
            }
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (catalogItemUiState.showingBasic) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.primary,
                    contentColor = if (catalogItemUiState.showingBasic) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background
                ),
                modifier = Modifier
                    .height(48.dp)
                    .weight(1f),
                onClick = { catalogItemViewModel.showMore() },
                shape = RoundedCornerShape(0.dp)
            ) {
                Text(text = stringResource(id = R.string.more))
            }
        }
    }
}