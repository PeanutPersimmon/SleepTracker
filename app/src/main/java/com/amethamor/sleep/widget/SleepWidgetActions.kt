package com.amethamor.sleep.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import kotlinx.coroutines.delay

class SleepCheckInAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val success = SleepWidgetRepository(context).sleepCheckIn()

        if (success) {
            showSuccessCheck(context, glanceId, parameters, SleepWidgetActionType.Sleep)
        } else {
            updateWidget(context, glanceId, parameters)
        }
    }
}

class WakeCheckInAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        SleepWidgetRepository(context).wakeCheckIn()
        showSuccessCheck(context, glanceId, parameters, SleepWidgetActionType.Wake)
    }
}

private suspend fun showSuccessCheck(
    context: Context,
    glanceId: GlanceId,
    parameters: ActionParameters,
    actionType: SleepWidgetActionType
) {
    updateSuccessAction(context, glanceId, actionType)
    updateWidget(context, glanceId, parameters)
    delay(800)
    clearSuccessAction(context, glanceId)
    updateWidget(context, glanceId, parameters)
}

private suspend fun updateSuccessAction(
    context: Context,
    glanceId: GlanceId,
    actionType: SleepWidgetActionType
) {
    updateAppWidgetState(context, glanceId) { prefs ->
        prefs[LastSuccessActionKey] = actionType.value
    }
}

private suspend fun clearSuccessAction(
    context: Context,
    glanceId: GlanceId
) {
    updateAppWidgetState(context, glanceId) { prefs ->
        prefs.remove(LastSuccessActionKey)
    }
}

private suspend fun updateWidget(
    context: Context,
    glanceId: GlanceId,
    parameters: ActionParameters
) {
    val layoutType = when (parameters[WidgetLayoutTypeKey]) {
        SleepWidgetLayoutType.Vertical.value -> SleepWidgetLayoutType.Vertical
        else -> SleepWidgetLayoutType.Horizontal
    }
    SleepCheckWidget(layoutType).update(context, glanceId)
}
