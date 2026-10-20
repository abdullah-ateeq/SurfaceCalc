package com.example.surfacecalc.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.surfacecalc.data.model.SurfaceShape

@Composable
fun ShapeVectorIcon(
    shape: SurfaceShape,
    modifier: Modifier = Modifier,
    size: Dp = 28.dp,
    tint: Color = MaterialTheme.colorScheme.primary
) {
    Canvas(modifier = modifier.size(size)) {
        val strokeWidth = 2.dp.toPx()
        when (shape) {
            SurfaceShape.CUBE -> drawCubeIcon(tint, strokeWidth)
            SurfaceShape.RECTANGULAR_PRISM -> drawRectangularPrismIcon(tint, strokeWidth)
            SurfaceShape.CYLINDER -> drawCylinderIcon(tint, strokeWidth)
            SurfaceShape.SPHERE -> drawSphereIcon(tint, strokeWidth)
            SurfaceShape.CONE -> drawConeIcon(tint, strokeWidth)
            SurfaceShape.SQUARE_PYRAMID -> drawSquarePyramidIcon(tint, strokeWidth)
            SurfaceShape.TRIANGULAR_PRISM -> drawTriangularPrismIcon(tint, strokeWidth)
            SurfaceShape.HEMISPHERE -> drawHemisphereIcon(tint, strokeWidth)
        }
    }
}

private fun DrawScope.drawCubeIcon(color: Color, strokeWidth: Float) {
    val w = size.width
    val h = size.height
    val p = Path()
    // Front face
    val x0 = w * 0.15f
    val y0 = h * 0.4f
    val sw = w * 0.5f
    val sh = h * 0.5f

    // Isometric / oblique cube
    val dx = w * 0.25f
    val dy = h * 0.25f

    // Back top lines
    p.moveTo(x0, y0)
    p.lineTo(x0 + dx, y0 - dy)
    p.lineTo(x0 + sw + dx, y0 - dy)
    p.lineTo(x0 + sw, y0)
    p.close()

    // Side face
    p.moveTo(x0 + sw, y0)
    p.lineTo(x0 + sw + dx, y0 - dy)
    p.lineTo(x0 + sw + dx, y0 + sh - dy)
    p.lineTo(x0 + sw, y0 + sh)
    p.close()

    // Front face
    p.moveTo(x0, y0)
    p.lineTo(x0 + sw, y0)
    p.lineTo(x0 + sw, y0 + sh)
    p.lineTo(x0, y0 + sh)
    p.close()

    drawPath(p, color = color, style = Stroke(width = strokeWidth))
}

private fun DrawScope.drawRectangularPrismIcon(color: Color, strokeWidth: Float) {
    val w = size.width
    val h = size.height
    val p = Path()
    val x0 = w * 0.1f
    val y0 = h * 0.45f
    val sw = w * 0.6f
    val sh = h * 0.45f
    val dx = w * 0.2f
    val dy = h * 0.25f

    // Top face
    p.moveTo(x0, y0)
    p.lineTo(x0 + dx, y0 - dy)
    p.lineTo(x0 + sw + dx, y0 - dy)
    p.lineTo(x0 + sw, y0)
    p.close()

    // Right face
    p.moveTo(x0 + sw, y0)
    p.lineTo(x0 + sw + dx, y0 - dy)
    p.lineTo(x0 + sw + dx, y0 + sh - dy)
    p.lineTo(x0 + sw, y0 + sh)
    p.close()

    // Front face
    p.moveTo(x0, y0)
    p.lineTo(x0 + sw, y0)
    p.lineTo(x0 + sw, y0 + sh)
    p.lineTo(x0, y0 + sh)
    p.close()

    drawPath(p, color = color, style = Stroke(width = strokeWidth))
}

private fun DrawScope.drawCylinderIcon(color: Color, strokeWidth: Float) {
    val w = size.width
    val h = size.height
    val rx = w * 0.35f
    val ry = h * 0.15f
    val cx = w * 0.5f

    // Top oval
    drawOval(
        color = color,
        topLeft = Offset(cx - rx, h * 0.15f),
        size = Size(rx * 2, ry * 2),
        style = Stroke(width = strokeWidth)
    )

    // Sides
    drawLine(color, Offset(cx - rx, h * 0.15f + ry), Offset(cx - rx, h * 0.65f + ry), strokeWidth)
    drawLine(color, Offset(cx + rx, h * 0.15f + ry), Offset(cx + rx, h * 0.65f + ry), strokeWidth)

    // Bottom arc
    drawArc(
        color = color,
        startAngle = 0f,
        sweepAngle = 180f,
        useCenter = false,
        topLeft = Offset(cx - rx, h * 0.65f),
        size = Size(rx * 2, ry * 2),
        style = Stroke(width = strokeWidth)
    )
}

private fun DrawScope.drawSphereIcon(color: Color, strokeWidth: Float) {
    val w = size.width
    val h = size.height
    val r = w.coerceAtMost(h) * 0.4f
    val cx = w * 0.5f
    val cy = h * 0.5f

    // Outer circle
    drawCircle(color, radius = r, center = Offset(cx, cy), style = Stroke(width = strokeWidth))

    // Equator ellipse
    drawOval(
        color = color.copy(alpha = 0.65f),
        topLeft = Offset(cx - r, cy - r * 0.35f),
        size = Size(r * 2, r * 0.7f),
        style = Stroke(width = strokeWidth * 0.8f)
    )
}

private fun DrawScope.drawConeIcon(color: Color, strokeWidth: Float) {
    val w = size.width
    val h = size.height
    val rx = w * 0.35f
    val ry = h * 0.14f
    val cx = w * 0.5f
    val apexY = h * 0.15f
    val baseY = h * 0.75f

    // Sides
    drawLine(color, Offset(cx, apexY), Offset(cx - rx, baseY + ry), strokeWidth)
    drawLine(color, Offset(cx, apexY), Offset(cx + rx, baseY + ry), strokeWidth)

    // Bottom base ellipse
    drawOval(
        color = color,
        topLeft = Offset(cx - rx, baseY),
        size = Size(rx * 2, ry * 2),
        style = Stroke(width = strokeWidth)
    )
}

private fun DrawScope.drawSquarePyramidIcon(color: Color, strokeWidth: Float) {
    val w = size.width
    val h = size.height
    val apex = Offset(w * 0.5f, h * 0.15f)
    val bLeft = Offset(w * 0.15f, h * 0.75f)
    val bCenter = Offset(w * 0.5f, h * 0.9f)
    val bRight = Offset(w * 0.85f, h * 0.75f)

    val p = Path()
    // Left face
    p.moveTo(apex.x, apex.y)
    p.lineTo(bLeft.x, bLeft.y)
    p.lineTo(bCenter.x, bCenter.y)
    p.close()

    // Right face
    p.moveTo(apex.x, apex.y)
    p.lineTo(bCenter.x, bCenter.y)
    p.lineTo(bRight.x, bRight.y)
    p.close()

    drawPath(p, color = color, style = Stroke(width = strokeWidth))
}

private fun DrawScope.drawTriangularPrismIcon(color: Color, strokeWidth: Float) {
    val w = size.width
    val h = size.height
    val p = Path()

    // Front triangle
    val tApex = Offset(w * 0.3f, h * 0.25f)
    val tLeft = Offset(w * 0.1f, h * 0.8f)
    val tRight = Offset(w * 0.5f, h * 0.8f)

    val dx = w * 0.4f
    val dy = -h * 0.15f

    // Top face
    p.moveTo(tApex.x, tApex.y)
    p.lineTo(tApex.x + dx, tApex.y + dy)
    p.lineTo(tRight.x + dx, tRight.y + dy)
    p.lineTo(tRight.x, tRight.y)
    p.close()

    // Front triangle
    p.moveTo(tApex.x, tApex.y)
    p.lineTo(tLeft.x, tLeft.y)
    p.lineTo(tRight.x, tRight.y)
    p.close()

    // Back side line
    p.moveTo(tLeft.x, tLeft.y)
    p.lineTo(tLeft.x + dx, tLeft.y + dy)
    p.lineTo(tRight.x + dx, tRight.y + dy)

    drawPath(p, color = color, style = Stroke(width = strokeWidth))
}

private fun DrawScope.drawHemisphereIcon(color: Color, strokeWidth: Float) {
    val w = size.width
    val h = size.height
    val cx = w * 0.5f
    val cy = h * 0.6f
    val r = w * 0.38f
    val ry = h * 0.15f

    // Dome arc
    drawArc(
        color = color,
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = false,
        topLeft = Offset(cx - r, cy - r),
        size = Size(r * 2, r * 2),
        style = Stroke(width = strokeWidth)
    )

    // Flat base oval
    drawOval(
        color = color,
        topLeft = Offset(cx - r, cy - ry),
        size = Size(r * 2, ry * 2),
        style = Stroke(width = strokeWidth)
    )
}
