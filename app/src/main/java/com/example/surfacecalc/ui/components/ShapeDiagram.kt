package com.example.surfacecalc.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.surfacecalc.data.model.SurfaceShape

@Composable
fun ShapeDiagramCard(
    shape: SurfaceShape,
    visible: Boolean = true,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Shape Diagram",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = shape.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .padding(top = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val strokeColor = MaterialTheme.colorScheme.primary
                    val dashColor = MaterialTheme.colorScheme.outline
                    val guideColor = MaterialTheme.colorScheme.tertiary

                    Canvas(modifier = Modifier.size(160.dp, 100.dp)) {
                        val w = size.width
                        val h = size.height
                        val stroke = Stroke(width = 2.5f)
                        val dashedStroke = Stroke(
                            width = 1.5f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
                        )

                        when (shape) {
                            SurfaceShape.CUBE -> {
                                val s = h * 0.5f
                                val x0 = w * 0.25f
                                val y0 = h * 0.4f
                                val d = w * 0.2f

                                val p = Path()
                                p.moveTo(x0, y0); p.lineTo(x0 + d, y0 - d)
                                p.lineTo(x0 + s + d, y0 - d); p.lineTo(x0 + s, y0); p.close()
                                p.moveTo(x0 + s, y0); p.lineTo(x0 + s + d, y0 - d)
                                p.lineTo(x0 + s + d, y0 + s - d); p.lineTo(x0 + s, y0 + s); p.close()
                                p.moveTo(x0, y0); p.lineTo(x0 + s, y0)
                                p.lineTo(x0 + s, y0 + s); p.lineTo(x0, y0 + s); p.close()
                                drawPath(p, strokeColor, style = stroke)

                                // Side guide
                                drawLine(guideColor, Offset(x0, y0 + s + 10f), Offset(x0 + s, y0 + s + 10f), 2f)
                            }
                            SurfaceShape.RECTANGULAR_PRISM -> {
                                val sw = w * 0.45f
                                val sh = h * 0.4f
                                val x0 = w * 0.18f
                                val y0 = h * 0.45f
                                val d = w * 0.22f

                                val p = Path()
                                p.moveTo(x0, y0); p.lineTo(x0 + d, y0 - d)
                                p.lineTo(x0 + sw + d, y0 - d); p.lineTo(x0 + sw, y0); p.close()
                                p.moveTo(x0 + sw, y0); p.lineTo(x0 + sw + d, y0 - d)
                                p.lineTo(x0 + sw + d, y0 + sh - d); p.lineTo(x0 + sw, y0 + sh); p.close()
                                p.moveTo(x0, y0); p.lineTo(x0 + sw, y0)
                                p.lineTo(x0 + sw, y0 + sh); p.lineTo(x0, y0 + sh); p.close()
                                drawPath(p, strokeColor, style = stroke)
                            }
                            SurfaceShape.CYLINDER -> {
                                val rx = w * 0.3f
                                val ry = h * 0.14f
                                val cx = w * 0.5f

                                drawOval(strokeColor, Offset(cx - rx, h * 0.15f), Size(rx * 2, ry * 2), style = stroke)
                                drawLine(strokeColor, Offset(cx - rx, h * 0.15f + ry), Offset(cx - rx, h * 0.65f + ry), 2.5f)
                                drawLine(strokeColor, Offset(cx + rx, h * 0.15f + ry), Offset(cx + rx, h * 0.65f + ry), 2.5f)
                                drawArc(strokeColor, 0f, 180f, false, Offset(cx - rx, h * 0.65f), Size(rx * 2, ry * 2), style = stroke)
                                drawArc(dashColor, 180f, 180f, false, Offset(cx - rx, h * 0.65f), Size(rx * 2, ry * 2), style = dashedStroke)

                                // Radius guide on top base
                                drawLine(guideColor, Offset(cx, h * 0.15f + ry), Offset(cx + rx, h * 0.15f + ry), 2f)
                            }
                            SurfaceShape.SPHERE -> {
                                val r = h * 0.4f
                                val cx = w * 0.5f
                                val cy = h * 0.5f
                                drawCircle(strokeColor, r, Offset(cx, cy), style = stroke)
                                drawOval(dashColor, Offset(cx - r, cy - r * 0.3f), Size(r * 2, r * 0.6f), style = dashedStroke)
                                // Radius guide
                                drawLine(guideColor, Offset(cx, cy), Offset(cx + r, cy), 2.5f)
                                drawCircle(guideColor, 3f, Offset(cx, cy))
                            }
                            SurfaceShape.CONE -> {
                                val rx = w * 0.3f
                                val ry = h * 0.14f
                                val cx = w * 0.5f
                                val apex = Offset(cx, h * 0.12f)
                                val baseY = h * 0.7f

                                drawLine(strokeColor, apex, Offset(cx - rx, baseY + ry), 2.5f)
                                drawLine(strokeColor, apex, Offset(cx + rx, baseY + ry), 2.5f)
                                drawArc(strokeColor, 0f, 180f, false, Offset(cx - rx, baseY), Size(rx * 2, ry * 2), style = stroke)
                                drawArc(dashColor, 180f, 180f, false, Offset(cx - rx, baseY), Size(rx * 2, ry * 2), style = dashedStroke)

                                // Radius guide
                                drawLine(guideColor, Offset(cx, baseY + ry), Offset(cx + rx, baseY + ry), 2f)
                                drawCircle(guideColor, 3f, Offset(cx, baseY + ry))
                            }
                            SurfaceShape.SQUARE_PYRAMID -> {
                                val apex = Offset(w * 0.5f, h * 0.12f)
                                val bL = Offset(w * 0.2f, h * 0.72f)
                                val bC = Offset(w * 0.5f, h * 0.88f)
                                val bR = Offset(w * 0.8f, h * 0.72f)

                                val p = Path()
                                p.moveTo(apex.x, apex.y); p.lineTo(bL.x, bL.y); p.lineTo(bC.x, bC.y); p.close()
                                p.moveTo(apex.x, apex.y); p.lineTo(bC.x, bC.y); p.lineTo(bR.x, bR.y); p.close()
                                drawPath(p, strokeColor, style = stroke)

                                // Slant height guide along face
                                drawLine(guideColor, apex, Offset((bC.x + bR.x) / 2, (bC.y + bR.y) / 2), 2f)
                            }
                            SurfaceShape.TRIANGULAR_PRISM -> {
                                val tApex = Offset(w * 0.32f, h * 0.22f)
                                val tL = Offset(w * 0.12f, h * 0.75f)
                                val tR = Offset(w * 0.52f, h * 0.75f)
                                val dx = w * 0.36f
                                val dy = -h * 0.14f

                                val p = Path()
                                p.moveTo(tApex.x, tApex.y); p.lineTo(tApex.x + dx, tApex.y + dy)
                                p.lineTo(tR.x + dx, tR.y + dy); p.lineTo(tR.x, tR.y); p.close()
                                p.moveTo(tApex.x, tApex.y); p.lineTo(tL.x, tL.y); p.lineTo(tR.x, tR.y); p.close()
                                p.moveTo(tL.x, tL.y); p.lineTo(tL.x + dx, tL.y + dy); p.lineTo(tR.x + dx, tR.y + dy)
                                drawPath(p, strokeColor, style = stroke)
                            }
                            SurfaceShape.HEMISPHERE -> {
                                val cx = w * 0.5f
                                val cy = h * 0.65f
                                val r = w * 0.32f
                                val ry = h * 0.14f

                                drawArc(strokeColor, 180f, 180f, false, Offset(cx - r, cy - r), Size(r * 2, r * 2), style = stroke)
                                drawArc(strokeColor, 0f, 180f, false, Offset(cx - r, cy - ry), Size(r * 2, ry * 2), style = stroke)
                                drawArc(dashColor, 180f, 180f, false, Offset(cx - r, cy - ry), Size(r * 2, ry * 2), style = dashedStroke)

                                // Radius guide
                                drawLine(guideColor, Offset(cx, cy), Offset(cx + r, cy), 2.5f)
                                drawCircle(guideColor, 3f, Offset(cx, cy))
                            }
                        }
                    }
                }

                // Legend of dimensions
                Text(
                    text = getDimensionLegend(shape),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

private fun getDimensionLegend(shape: SurfaceShape): String {
    return when (shape) {
        SurfaceShape.CUBE -> "s = side"
        SurfaceShape.RECTANGULAR_PRISM -> "l = length, w = width, h = height"
        SurfaceShape.CYLINDER -> "r = radius, h = height"
        SurfaceShape.SPHERE -> "r = radius"
        SurfaceShape.CONE -> "r = radius, l = slant height"
        SurfaceShape.SQUARE_PYRAMID -> "s = base side, l = slant height"
        SurfaceShape.TRIANGULAR_PRISM -> "a, b, c = triangle sides, L = prism length"
        SurfaceShape.HEMISPHERE -> "r = radius (flat base + dome)"
    }
}
