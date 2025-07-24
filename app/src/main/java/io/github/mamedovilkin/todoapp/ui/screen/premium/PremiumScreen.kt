package io.github.mamedovilkin.todoapp.ui.screen.premium

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.mamedovilkin.todoapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumScreen(
    windowWidthSizeClass: WindowWidthSizeClass,
    onBack: () -> Unit,
    onTryItFree: (String) -> Unit,
    isPremium: Boolean
) {
    val productIds = listOf("premium_monthly", "premium_annual")
    var selectedProductId by remember { mutableStateOf(productIds[0]) }
    val plans = listOf(stringResource(R.string.monthly), stringResource(R.string.annual, "-16%"))
    var selectedPlan by remember { mutableStateOf(plans[0]) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.2F)
                        ),
                        modifier = Modifier.clip(CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = stringResource(R.string.back),
                            tint = MaterialTheme.colorScheme.background
                        )
                    }
                },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_task),
                            contentDescription = stringResource(R.string.app_name),
                            tint = MaterialTheme.colorScheme.background,
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = stringResource(R.string.premium),
                            color = MaterialTheme.colorScheme.background,
                            style = MaterialTheme.typography.displayLarge
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1F)
                    .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = if (windowWidthSizeClass == WindowWidthSizeClass.Compact) {
                        Modifier.fillMaxSize()
                    } else {
                        Modifier
                            .width(600.dp)
                            .fillMaxHeight()
                    }
                ) {
                    val features = stringArrayResource(R.array.premium_features)

                    LazyColumn {

                        item { Spacer(modifier = Modifier.height(50.dp)) }

                        item {
                            Text(
                                text = stringResource(R.string.premium_summary),
                                color = MaterialTheme.colorScheme.background,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            )
                        }

                        item { Spacer(modifier = Modifier.height(50.dp)) }

                        item {
                            Text(
                                text = stringResource(R.string.whats_in_subscription),
                                color = MaterialTheme.colorScheme.background,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            )
                        }

                        items(features) { feature ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.background,
                                    modifier = Modifier.size(36.dp)
                                )
                                Text(
                                    text = feature,
                                    color = MaterialTheme.colorScheme.background,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = if (windowWidthSizeClass == WindowWidthSizeClass.Compact) {
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .padding(horizontal = 16.dp)
                    } else {
                        Modifier
                            .width(600.dp)
                            .padding(vertical = 8.dp)
                            .padding(horizontal = 16.dp)
                    }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .border(BorderStroke(2.dp, if (selectedPlan == plans[0]) MaterialTheme.colorScheme.primary else Color.Transparent), RoundedCornerShape(8.dp))
                                .clickable {
                                    selectedPlan = plans[0]
                                    selectedProductId = productIds[0]
                                }
                        ) {
                            RadioButton(
                                selected = (selectedPlan == plans[0]),
                                onClick = {
                                    selectedPlan = plans[0]
                                    selectedProductId = productIds[0]
                                }
                            )
                            Text(
                                text = plans[0],
                                color = if (selectedPlan == plans[0]) MaterialTheme.colorScheme.primary else Color.Unspecified,
                                modifier = Modifier.padding(end = 16.dp)
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .border(BorderStroke(2.dp, if (selectedPlan == plans[1]) MaterialTheme.colorScheme.primary else Color.Transparent), RoundedCornerShape(8.dp))
                                .clickable {
                                    selectedPlan = plans[1]
                                    selectedProductId = productIds[1]
                                }
                        ) {
                            RadioButton(
                                selected = (selectedPlan == plans[1]),
                                onClick = {
                                    selectedPlan = plans[1]
                                    selectedProductId = productIds[1]
                                }
                            )
                            Text(
                                text = plans[1],
                                color = if (selectedPlan == plans[1]) MaterialTheme.colorScheme.primary else Color.Unspecified,
                                modifier = Modifier.padding(end = 16.dp)
                            )
                        }
                    }
                    Button(
                        onClick = { onTryItFree(selectedProductId) },
                        enabled = !isPremium,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = stringResource(R.string.try_it_free))
                    }
                    Text(
                        text = if (selectedProductId == productIds[0]) {
                            stringResource(R.string.premium_monthly_terms)
                        } else {
                            stringResource(R.string.premium_annual_terms)
                        },
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}