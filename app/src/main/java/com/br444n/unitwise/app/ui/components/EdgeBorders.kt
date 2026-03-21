package com.br444n.unitwise.app.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp

fun Modifier.topAppBarBorder(color: Color, width: Dp, cornerRadius: Dp): Modifier = this.drawWithContent {
    drawContent() // Draw the component first
    val strokeWidth = width.toPx()
    val halfStroke = strokeWidth / 2f
    val radius = cornerRadius.toPx()
    
    val path = Path().apply {
        moveTo(halfStroke, 0f)
        lineTo(halfStroke, size.height - radius)
        arcTo(
            rect = Rect(halfStroke, size.height - 2 * radius + halfStroke, 2 * radius - halfStroke, size.height - halfStroke),
            startAngleDegrees = 180f,
            sweepAngleDegrees = -90f,
            forceMoveTo = false
        )
        lineTo(size.width - radius, size.height - halfStroke)
        arcTo(
            rect = Rect(size.width - 2 * radius + halfStroke, size.height - 2 * radius + halfStroke, size.width - halfStroke, size.height - halfStroke),
            startAngleDegrees = 90f,
            sweepAngleDegrees = -90f,
            forceMoveTo = false
        )
        lineTo(size.width - halfStroke, 0f)
    }
    
    drawPath(
        path = path,
        color = color,
        style = Stroke(width = strokeWidth)
    )
}

fun Modifier.bottomNavBarBorder(color: Color, width: Dp, cornerRadius: Dp): Modifier = this.drawWithContent {
    drawContent() // Draw the component first
    val strokeWidth = width.toPx()
    val halfStroke = strokeWidth / 2f
    val radius = cornerRadius.toPx()
    
    val path = Path().apply {
        moveTo(halfStroke, size.height)
        lineTo(halfStroke, radius)
        arcTo(
            rect = Rect(halfStroke, halfStroke, 2 * radius - halfStroke, 2 * radius - halfStroke),
            startAngleDegrees = 180f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )
        lineTo(size.width - radius, halfStroke)
        arcTo(
            rect = Rect(size.width - 2 * radius + halfStroke, halfStroke, size.width - halfStroke, 2 * radius - halfStroke),
            startAngleDegrees = 270f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )
        lineTo(size.width - halfStroke, size.height)
    }
    
    drawPath(
        path = path,
        color = color,
        style = Stroke(width = strokeWidth)
    )
}
