package com.daniil.shevtsov.fightsimulator.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal fun Modifier.protrusive(
    lightColor: Color,
    darkColor: Color,
) = innerBorder(
    startTopColor = lightColor,
    bottomEndColor = darkColor,
)

internal fun Modifier.cavitary(
    lightColor: Color,
    darkColor: Color,
) = innerBorder(
    startTopColor = darkColor,
    bottomEndColor = lightColor,
)

internal fun Modifier.innerBorder(
    startTopColor: Color,
    bottomEndColor: Color,
    size: Dp = 1.dp,
) = background(startTopColor)
    .padding(top = size, start = size)
    .background(bottomEndColor)
    .padding(bottom = size, end = size)
