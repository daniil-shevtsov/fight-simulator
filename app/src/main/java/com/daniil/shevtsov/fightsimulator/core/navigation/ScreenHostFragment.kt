package com.daniil.shevtsov.fightsimulator.core.navigation

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import by.kirich1409.viewbindingdelegate.viewBinding
import com.daniil.shevtsov.fightsimulator.R
import com.daniil.shevtsov.fightsimulator.application.FightSimulatorApplication
import com.daniil.shevtsov.fightsimulator.databinding.FragmentMainBinding
import com.daniil.shevtsov.fightsimulator.prototypes.fight.ui.*
import com.google.accompanist.insets.ProvideWindowInsets
import javax.inject.Inject

class ScreenHostFragment : Fragment(R.layout.fragment_main) {

    private val binding by viewBinding(FragmentMainBinding::bind)

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: ScreenHostViewModel by viewModels { viewModelFactory }
    private val fightImperativeShell: FightImperativeShell by viewModels { viewModelFactory }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (context.applicationContext as FightSimulatorApplication)
            .appComponent
            .inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            composeView.setContent {
                ProvideWindowInsets {
                    val prototypeBody = listOf(
                        BodyPart(id = 0L, name = "Head", parentId = 1L, type = BodyPartType.Head),
                        BodyPart(id = 1L, name = "Body", childId = 0L, type = BodyPartType.Body),
                        BodyPart(id = 2L, name = "Left Arm", parentId = 1L, type = BodyPartType.Arm),
                        BodyPart(id = 3L, name = "Right Arm", parentId = 1L, type = BodyPartType.Arm),
                    )
                    Row {
                        CustomBodyLayout(prototypeBody) {
                            prototypeBody.forEach {
                                PrototypeSimpleBodyPart(
                                    part = it,
                                    modifier = Modifier
                                        .height(50.dp)
                                        .layoutId(it)
                                )
                            }
                        }
                    }
//                    ScreenHostComposable(viewModel = viewModel)
//                    FightScreenComposable(imperativeShell = fightImperativeShell)
                }
            }
        }
    }
}
