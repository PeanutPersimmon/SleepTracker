package com.amethamor.sleep.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.unit.ColorProvider
import com.amethamor.sleep.R

enum class SleepWidgetLayoutType(val value: String) {
    Horizontal("horizontal"),
    Vertical("vertical")
}

enum class SleepWidgetActionType(val value: String) {
    Wake("wake"),
    Sleep("sleep")
}

val WidgetLayoutTypeKey = ActionParameters.Key<String>("layout_type")
val LastSuccessActionKey = androidx.datastore.preferences.core.stringPreferencesKey("last_success_action")

private val WidgetGlassSurface = ColorProvider(Color(0x4AFFFFFF))
private val WidgetGlassHighlight = ColorProvider(Color(0x24FFFFFF))
private val WidgetIconColor = ColorProvider(Color(0xE642536A))

class SleepCheckWidget(
    private val layoutType: SleepWidgetLayoutType = SleepWidgetLayoutType.Horizontal
) : GlanceAppWidget() {
    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            SleepCheckWidgetContent(layoutType = layoutType)
        }
    }
}

@Composable
private fun SleepCheckWidgetContent(layoutType: SleepWidgetLayoutType) {
    val prefs = currentState<Preferences>()
    val lastSuccessAction = prefs[LastSuccessActionKey]

    Box(
        modifier = GlanceModifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (layoutType == SleepWidgetLayoutType.Horizontal) {
            Row(
                modifier = GlanceModifier
                    .size(120.dp, 52.dp)
                    .background(WidgetGlassSurface)
                    .cornerRadius(26.dp)
                    .padding(horizontal = 7.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
                verticalAlignment = Alignment.Vertical.CenterVertically
            ) {
                Box(
                    modifier = GlanceModifier
                        .defaultWeight()
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    WidgetIconButton(
                        actionType = SleepWidgetActionType.Sleep,
                        layoutType = layoutType,
                        showCheck = lastSuccessAction == SleepWidgetActionType.Sleep.value
                    )
                }
                Box(
                    modifier = GlanceModifier
                        .defaultWeight()
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    WidgetIconButton(
                        actionType = SleepWidgetActionType.Wake,
                        layoutType = layoutType,
                        showCheck = lastSuccessAction == SleepWidgetActionType.Wake.value
                    )
                }
            }
        } else {
            Column(
                modifier = GlanceModifier
                    .size(56.dp, 120.dp)
                    .background(WidgetGlassSurface)
                    .cornerRadius(28.dp)
                    .padding(horizontal = 6.dp, vertical = 7.dp),
                horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
                verticalAlignment = Alignment.Vertical.CenterVertically
            ) {
                Box(
                    modifier = GlanceModifier
                        .defaultWeight()
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    WidgetIconButton(
                        actionType = SleepWidgetActionType.Sleep,
                        layoutType = layoutType,
                        showCheck = lastSuccessAction == SleepWidgetActionType.Sleep.value
                    )
                }
                Box(
                    modifier = GlanceModifier
                        .defaultWeight()
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    WidgetIconButton(
                        actionType = SleepWidgetActionType.Wake,
                        layoutType = layoutType,
                        showCheck = lastSuccessAction == SleepWidgetActionType.Wake.value
                    )
                }
            }
        }
    }
}

@Composable
private fun WidgetIconButton(
    actionType: SleepWidgetActionType,
    layoutType: SleepWidgetLayoutType,
    showCheck: Boolean
) {
    val action = when (actionType) {
        SleepWidgetActionType.Wake -> actionRunCallback<WakeCheckInAction>(
            actionParametersOf(WidgetLayoutTypeKey to layoutType.value)
        )

        SleepWidgetActionType.Sleep -> actionRunCallback<SleepCheckInAction>(
            actionParametersOf(WidgetLayoutTypeKey to layoutType.value)
        )
    }
    val iconRes = if (showCheck) {
        R.drawable.ic_widget_check
    } else {
        when (actionType) {
            SleepWidgetActionType.Wake -> R.drawable.ic_widget_wake_sun
            SleepWidgetActionType.Sleep -> R.drawable.ic_widget_sleep_moon
        }
    }
    val contentDescription = when (actionType) {
        SleepWidgetActionType.Wake -> "Wake check in"
        SleepWidgetActionType.Sleep -> "Sleep check in"
    }

    Box(
        modifier = GlanceModifier
            .size(40.dp)
            .background(WidgetGlassHighlight)
            .cornerRadius(20.dp)
            .clickable(action),
        contentAlignment = Alignment.Center
    ) {
        Image(
            provider = ImageProvider(iconRes),
            contentDescription = contentDescription,
            modifier = GlanceModifier.size(25.dp),
            colorFilter = ColorFilter.tint(WidgetIconColor)
        )
    }
}
