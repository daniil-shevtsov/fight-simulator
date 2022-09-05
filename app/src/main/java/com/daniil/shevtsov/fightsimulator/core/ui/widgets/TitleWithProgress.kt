package com.daniil.shevtsov.fightsimulator.core.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daniil.shevtsov.fightsimulator.core.ui.Pallete

@Preview
@Composable
fun MainPreview() {
    Column{
        TitleWithProgress(
            title = "Title",
            progress = 0.5f,
        )
        TitleWithProgress(
            title = "Suspicion",
            progress = 0.5f,
        )
    }
}

@Composable
fun TitleWithProgress(
    title: String,
    progress: Float,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Pallete.Red)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = modifier.weight(0.35f),
            text = title,
            fontSize = 24.sp,
            color = Color.White,
            textAlign = TextAlign.Start
        )
        MyProgressBar(
            progressPercentage = progress,
            modifier = modifier.weight(0.65f),
        )
    }
}
