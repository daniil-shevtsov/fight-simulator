package com.daniil.shevtsov.fightsimulator.prototypes.fight.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.daniil.shevtsov.fightsimulator.prototypes.fight.presentation.SelectableItem

@Composable
fun SelectableItem(
    item: SelectableItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    when (item) {
        is SelectableItem.BodyPartItem -> BodyPart(
            bodyPartItem = item,
            modifier = modifier,
            onClick = { onClick() }
        )
        is SelectableItem.Item -> Item(
            item = item,
            modifier = modifier,
            onClick = { onClick() }
        )
    }

}
