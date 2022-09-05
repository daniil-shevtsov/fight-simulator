package com.daniil.shevtsov.fightsimulator.core.ui.widgets

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daniil.shevtsov.fightsimulator.core.ui.Pallete

@Preview(heightDp = 400)
@Composable
fun CollapsablePreview() {
    Collapsable(
        title = "Preview",
        isCollapsed = false,
        collapsedContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .background(Color.Red)
            )
        },
        expandedContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(8.dp)
                    .background(Color.White)
            )
        },
        onToggleCollapse = {},
    )
}

@Composable
fun <ElementType, ComposableType> CollapsableColumn(
    title: String,
    isCollapsed: Boolean,
    items: List<ElementType>,
    modifier: Modifier = Modifier,
    composable: @Composable (item: ElementType) -> ComposableType,
    onToggleCollapse: () -> Unit,
) {
    Collapsable(
        title = title,
        isCollapsed = isCollapsed,
        modifier = modifier,
        collapsedContent = {
            composable(items.first())
        },
        expandedContent = {
            Column {
                items.forEach { item ->
                    composable(item)
                }
            }
        },
        onToggleCollapse = onToggleCollapse,
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Collapsable(
    title: String,
    isCollapsed: Boolean,
    modifier: Modifier = Modifier,
    collapsedContent: @Composable () -> Unit,
    expandedContent: @Composable () -> Unit,
    onToggleCollapse: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
                .background(Pallete.Red)
                .padding(4.dp)

        ) {
            CollapseButton(
                isCollapsed = isCollapsed,
                modifier = modifier,
                onClick = { onToggleCollapse() }
            )
            Text(
                text = title,
                color = Color.White,
                fontSize = 24.sp,
                modifier = modifier
            )
        }

        AnimatedContent(
            targetState = isCollapsed,
            transitionSpec = {
                if (targetState) {
                    fadeIn(initialAlpha = 1f) with fadeOut(targetAlpha = 1f)
                } else {
                    fadeIn(initialAlpha = 1f) with fadeOut(targetAlpha = 1f)
                }

            }
        ) { targetCollapsed ->
            if (targetCollapsed) {
                collapsedContent()
            } else {
                expandedContent()
            }
        }
    }
}

@Composable
fun CollapseButton(
    isCollapsed: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick, modifier = modifier
            .size(32.dp)
    ) {
        Icon(
            if (isCollapsed) Icons.Filled.ArrowDropDown else Icons.Filled.ArrowDropUp,
            contentDescription = "Collapse / Expand",
            modifier = modifier.fillMaxSize(),
            tint = Color.White,
        )
    }
}
