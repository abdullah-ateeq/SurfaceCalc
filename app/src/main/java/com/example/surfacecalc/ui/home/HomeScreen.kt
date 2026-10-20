package com.example.surfacecalc.ui.home

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.surfacecalc.data.model.AreaUnit
import com.example.surfacecalc.data.model.SurfaceShape
import com.example.surfacecalc.domain.AreaUnitRegistry
import com.example.surfacecalc.domain.ShapeFormulaRegistry
import com.example.surfacecalc.domain.SurfaceCalcFormatter
import com.example.surfacecalc.domain.SurfaceCalcValidator
import com.example.surfacecalc.ui.components.NumericInputField
import com.example.surfacecalc.ui.components.ShapeDiagramCard
import com.example.surfacecalc.ui.components.ShapeVectorIcon
import com.example.surfacecalc.ui.viewmodel.SurfaceCalcViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    viewModel: SurfaceCalcViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val haptic = LocalHapticFeedback.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    var calculationStepsExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(state.saveNotification) {
        state.saveNotification?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSaveNotification()
        }
    }

    fun triggerHaptic(type: HapticFeedbackType = HapticFeedbackType.TextHandleMove) {
        if (state.hapticFeedback) {
            haptic.performHapticFeedback(type)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .animateContentSize()
    ) {
        // App Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "SurfaceCalc",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Calculate surface area of common 3D shapes.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(
                onClick = {
                    triggerHaptic()
                    viewModel.onReset()
                },
                modifier = Modifier.semantics { contentDescription = "Reset inputs to default" }
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // CHOOSE SHAPE Section
        Text(
            text = "CHOOSE SHAPE",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(SurfaceShape.values(), key = { it.name }) { shape ->
                val isSelected = shape == state.shape
                Surface(
                    modifier = Modifier
                        .clickable {
                            if (!isSelected) {
                                triggerHaptic()
                                viewModel.onShapeSelected(shape)
                            }
                        }
                        .semantics {
                            contentDescription = "Calculate surface area of a ${shape.displayName}."
                        },
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    tonalElevation = if (isSelected) 4.dp else 0.dp
                ) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                            .width(108.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ShapeVectorIcon(
                            shape = shape,
                            size = 32.dp,
                            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = shape.displayName,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = shape.description,
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Visual Shape Diagram
        if (state.showShapeDiagram) {
            ShapeDiagramCard(
                shape = state.shape,
                visible = true
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Formula Card
        if (state.showFormula) {
            val formulaObj = ShapeFormulaRegistry.getFormula(state.shape)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Functions,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = formulaObj.formula,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = formulaObj.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Dynamic Dimension Inputs
        val fields = SurfaceCalcValidator.getFieldsForShape(state.shape)
        fields.forEach { field ->
            val fieldValue = state.inputs[field.key] ?: ""
            val fieldUnit = state.inputUnits[field.key] ?: field.defaultUnit

            NumericInputField(
                label = field.label,
                value = fieldValue,
                unit = fieldUnit,
                onValueChange = { newVal ->
                    viewModel.onInputChange(field.key, newVal)
                },
                onUnitChange = { newUnit ->
                    triggerHaptic()
                    viewModel.onInputUnitChange(field.key, newUnit)
                },
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // RESULT UNIT Selector
        Text(
            text = "RESULT UNIT",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AreaUnitRegistry.ALL.forEach { def ->
                val isSelected = def.unit == state.resultUnit
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        triggerHaptic()
                        viewModel.onResultUnitChange(def.unit)
                    },
                    label = { Text(def.symbol, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Error message if any
        if (state.errorMessage != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = state.errorMessage ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(14.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // RESULT CARD
        if (state.result != null) {
            val res = state.result!!
            val formattedResult = SurfaceCalcFormatter.formatArea(
                res.rawValue,
                res.outputUnit,
                state.decimalPlaces,
                state.roundingMode
            )
            val unitDef = AreaUnitRegistry.get(res.outputUnit)

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TOTAL SURFACE AREA",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = MaterialTheme.colorScheme.primary
                        )

                        // Copy value only icon button
                        IconButton(
                            onClick = {
                                triggerHaptic()
                                clipboardManager.setText(AnnotatedString(formattedResult))
                                state.copy(saveNotification = "Copied value to clipboard")
                            },
                            modifier = Modifier.semantics {
                                contentDescription = "Copy surface area result value"
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy value",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = formattedResult,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.semantics {
                            contentDescription = "Calculated total surface area, $formattedResult."
                        }
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Includes all bases & external faces (${unitDef.name})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // RESULT ACTIONS: COPY, SAVE, SHARE
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                triggerHaptic()
                                val copyText = viewModel.getCopyText()
                                clipboardManager.setText(AnnotatedString(copyText))
                            },
                            modifier = Modifier
                                .weight(1f)
                                .semantics { contentDescription = "Copy surface area result." },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copy")
                        }

                        Button(
                            onClick = {
                                triggerHaptic()
                                viewModel.onSave()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.BookmarkBorder, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save")
                        }

                        OutlinedButton(
                            onClick = {
                                triggerHaptic()
                                val shareText = viewModel.getShareText()
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "Share Surface Area Result")
                                context.startActivity(shareIntent)
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Expandable "SHOW CALCULATION"
            if (state.showCalculationSteps) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { calculationStepsExpanded = !calculationStepsExpanded },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "CALCULATION STEPS",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Icon(
                                imageVector = if (calculationStepsExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = if (calculationStepsExpanded) "Collapse" else "Expand"
                            )
                        }

                        AnimatedVisibility(visible = calculationStepsExpanded) {
                            Column(modifier = Modifier.padding(top = 12.dp)) {
                                res.calculationSteps.forEach { step ->
                                    Text(
                                        text = step,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        // QUICK EXAMPLES
        Text(
            text = "QUICK EXAMPLES",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Surface(
                modifier = Modifier.clickable {
                    triggerHaptic()
                    viewModel.onShapeSelected(SurfaceShape.CUBE)
                },
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Text(
                    text = "Cube: 5 cm → 150 cm²",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }

            Surface(
                modifier = Modifier.clickable {
                    triggerHaptic()
                    viewModel.onShapeSelected(SurfaceShape.CYLINDER)
                },
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Text(
                    text = "Cylinder: r 3 cm, h 10 cm → 245.044 cm²",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }

            Surface(
                modifier = Modifier.clickable {
                    triggerHaptic()
                    viewModel.onShapeSelected(SurfaceShape.SPHERE)
                },
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Text(
                    text = "Sphere: r 3 cm → 113.097 cm²",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }

            Surface(
                modifier = Modifier.clickable {
                    triggerHaptic()
                    viewModel.onShapeSelected(SurfaceShape.CONE)
                },
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Text(
                    text = "Cone: r 3 cm, l 5 cm → 75.398 cm²",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }

            Surface(
                modifier = Modifier.clickable {
                    triggerHaptic()
                    viewModel.onShapeSelected(SurfaceShape.SQUARE_PYRAMID)
                },
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Text(
                    text = "Square Pyramid: s 5 cm, l 6 cm → 85 cm²",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }

            Surface(
                modifier = Modifier.clickable {
                    triggerHaptic()
                    viewModel.onShapeSelected(SurfaceShape.TRIANGULAR_PRISM)
                },
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Text(
                    text = "Triangular Prism: 3, 4, 5 cm, L 10 cm → 132 cm²",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        SnackbarHost(hostState = snackbarHostState)
    }
}
