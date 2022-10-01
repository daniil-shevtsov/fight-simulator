package com.daniil.shevtsov.fightsimulator.prototypes.fight.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.SelectableId
import com.daniil.shevtsov.fightsimulator.prototypes.fight.presentation.SelectableItem

@Composable
fun Item(
    item: SelectableItem,
    modifier: Modifier = Modifier,
    onClick: (id: SelectableId) -> Unit = {},
) {
    Text(
        text = item.name,
        color = Color.Black,
        modifier = modifier
            .clickable { onClick(item.id) }
            .background(Color.LightGray)
            .padding(2.dp)
            .background(Color.Gray)
            .padding(4.dp)
    )
}
