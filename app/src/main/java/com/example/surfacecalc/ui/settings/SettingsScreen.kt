package com.example.surfacecalc.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.surfacecalc.data.model.ThemeMode
import com.example.surfacecalc.ui.viewmodel.SurfaceCalcViewModel
import java.math.RoundingMode

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    viewModel: SurfaceCalcViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    var showClearHistoryDialog by remember { mutableStateOf(false) }
    var showResetSettingsDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Preferences and calculation precision",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Appearance
        SettingsSectionHeader(title = "APPEARANCE")

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Theme",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ThemeMode.values().forEach { mode ->
                        val selected = state.themeMode == mode
                        FilterChip(
                            selected = selected,
                            onClick = { viewModel.onSetThemeMode(mode) },
                            label = { Text(mode.name.lowercase().replaceFirstChar { it.uppercase() }) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant)

                SettingsToggleRow(
                    title = "Dynamic Color",
                    subtitle = "Use Material You dynamic color palette on Android 12+",
                    checked = state.dynamicColor,
                    onCheckedChange = { viewModel.onSetDynamicColor(it) }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Behavior
        SettingsSectionHeader(title = "BEHAVIOR")

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                SettingsToggleRow(
                    title = "Haptic Feedback",
                    subtitle = "Vibrate on shape selection, unit changes, and actions",
                    checked = state.hapticFeedback,
                    onCheckedChange = { viewModel.onSetHapticFeedback(it) }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant)

                SettingsToggleRow(
                    title = "Animations",
                    subtitle = "Smooth UI transitions between shapes and results",
                    checked = state.animationsEnabled,
                    onCheckedChange = { viewModel.onSetAnimationsEnabled(it) }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Number Display
        SettingsSectionHeader(title = "NUMBER DISPLAY")

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Decimal Places",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Current: ${state.decimalPlaces} decimal places (trailing zeros stripped)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                val decimalOptions = listOf(0, 1, 2, 3, 4, 6, 8, 10, 12)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    decimalOptions.forEach { places ->
                        val selected = state.decimalPlaces == places
                        FilterChip(
                            selected = selected,
                            onClick = { viewModel.onSetDecimalPlaces(places) },
                            label = { Text("$places") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant)

                Text(
                    text = "Rounding Mode",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Applied strictly to the final displayed area value",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                val roundingModes = listOf(
                    RoundingMode.HALF_UP to "Half Up",
                    RoundingMode.HALF_DOWN to "Half Down",
                    RoundingMode.HALF_EVEN to "Half Even",
                    RoundingMode.UP to "Up",
                    RoundingMode.DOWN to "Down",
                    RoundingMode.CEILING to "Ceiling",
                    RoundingMode.FLOOR to "Floor"
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    roundingModes.forEach { (mode, label) ->
                        val selected = state.roundingMode == mode
                        FilterChip(
                            selected = selected,
                            onClick = { viewModel.onSetRoundingMode(mode) },
                            label = { Text(label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Display
        SettingsSectionHeader(title = "DISPLAY")

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                SettingsToggleRow(
                    title = "Show Formula",
                    subtitle = "Display formula banner above dimension inputs",
                    checked = state.showFormula,
                    onCheckedChange = { viewModel.onSetShowFormula(it) }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant)

                SettingsToggleRow(
                    title = "Show Calculation Steps",
                    subtitle = "Expandable step-by-step substitution under results",
                    checked = state.showCalculationSteps,
                    onCheckedChange = { viewModel.onSetShowCalculationSteps(it) }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant)

                SettingsToggleRow(
                    title = "Show Shape Diagram",
                    subtitle = "Illustrate 3D shape and labeled dimensions",
                    checked = state.showShapeDiagram,
                    onCheckedChange = { viewModel.onSetShowShapeDiagram(it) }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // History
        SettingsSectionHeader(title = "HISTORY & DATA")

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                SettingsToggleRow(
                    title = "Confirm History Deletion",
                    subtitle = "Prompt confirmation before removing calculations",
                    checked = state.confirmHistoryDelete,
                    onCheckedChange = { viewModel.onSetConfirmHistoryDelete(it) }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant)

                SettingsToggleRow(
                    title = "Preserve Last Calculation",
                    subtitle = "Restore current inputs when reopening the app",
                    checked = state.preserveLastCalculation,
                    onCheckedChange = { viewModel.onSetPreserveLastCalculation(it) }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { showClearHistoryDialog = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Clear History", color = MaterialTheme.colorScheme.error)
                    }

                    OutlinedButton(
                        onClick = { showResetSettingsDialog = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Reset Settings")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }

    if (showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            title = { Text("Clear History") },
            text = { Text("Clear all recent surface-area calculations?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onClearHistory()
                        showClearHistoryDialog = false
                    }
                ) {
                    Text("Clear", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showResetSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showResetSettingsDialog = false },
            title = { Text("Reset Settings") },
            text = { Text("Reset all settings to default values?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onResetSettings()
                        showResetSettingsDialog = false
                    }
                ) {
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetSettingsDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun SettingsToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
