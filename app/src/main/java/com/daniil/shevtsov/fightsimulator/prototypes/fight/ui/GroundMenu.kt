package com.daniil.shevtsov.fightsimulator.prototypes.fight.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.FightAction
import com.daniil.shevtsov.fightsimulator.prototypes.fight.domain.groundId
import com.daniil.shevtsov.fightsimulator.prototypes.fight.presentation.GroundMenu
import com.daniil.shevtsov.fightsimulator.prototypes.fight.presentation.bodyPartItem
import com.daniil.shevtsov.fightsimulator.prototypes.fight.presentation.selectableItem

private fun groundMenuPreviewData() = GroundMenu(
    id = groundId(1L),
    selectables = listOf(
        selectableItem(id = 1L, name = "Knife"),
        selectableItem(id = 2L, name = "Spear"),
        bodyPartItem(
            id = 3L,
            name = "Head",
            contained = setOf(bodyPartItem(id = 4L, name = "Skull")),
            lodgedIn = listOf(selectableItem(id = 5L, name = "Arrow"))
        )
    ),
    isSelected = true,
)

@Preview(widthDp = 412, heightDp = 732)
@Composable
fun GroundResizePreview() {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Text("LOL",modifier = Modifier.weight(1f))
        repeat(5) {
            GroundMenu(
                ground = groundMenuPreviewData(),
                modifier = Modifier,
                onAction = {},
            )
        }
        Text("Kek",modifier = Modifier)
    }
}

@Preview
@Composable
fun GroundMenuPreview() {
    GroundMenu(
        ground = groundMenuPreviewData(),
        onAction = {},
    )
}

@Composable
fun GroundMenu(
    ground: GroundMenu,
    modifier: Modifier = Modifier,
    onAction: (FightAction) -> Unit,
) {
    Box(
        modifier = modifier,
        propagateMinConstraints = true,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .background(Color.LightGray)
                .let { modifier ->
                    when (ground.isSelected) {
                        true -> modifier
                            .padding(2.dp)
                            .background(Color.Black)
                            .padding(2.dp)
                            .background(Color.LightGray)
                        false -> modifier
                    }
                }
                .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                .background(Color.DarkGray)
        ) {
            Text(
                text = "Ground", modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray)
            )
            Row(
                modifier = Modifier
                    .height(IntrinsicSize.Max)
                    .padding(4.dp),
                horizontalArrangement = spacedBy(4.dp),
            ) {
                ground.selectables.forEach { item ->
                    SelectableItem(
                        item = item,
                        modifier = Modifier.weight(1f),
                        onClick = { onAction(FightAction.SelectSomething(ground.id, item.id)) }
                    )
                }
            }
        }

    }
}
