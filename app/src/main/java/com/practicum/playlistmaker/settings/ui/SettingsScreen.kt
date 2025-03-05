package com.practicum.playlistmaker.settings.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Switch
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.SwitchDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import com.practicum.playlistmaker.sharing.domain.model.ActionType
import com.practicum.playlistmaker.sharing.domain.model.IntentAction

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {

    val context = LocalContext.current
    val isChecked = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isChecked.value = viewModel.getThemeSwitchState()
        viewModel.sharedFlow.collect { action ->
            startIntent(context, action)
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        SettingsTheme(viewModel, isChecked)
        CompoundButton(stringResource(id = R.string.share_app), R.drawable.ic_share_app) { viewModel.shareApp() }
        CompoundButton(stringResource(id = R.string.contact_support), R.drawable.ic_contact_support) { viewModel.contactSupport() }
        CompoundButton(stringResource(id = R.string.user_agreement), R.drawable.ic_user_agreement) { viewModel.openTerms() }
    }
}

@Composable
fun SettingsTheme(viewModel: SettingsViewModel, isChecked: MutableState<Boolean>) {
    Row(
        modifier = Modifier.height(dimensionResource(R.dimen.panel_height))
            .padding(horizontal = dimensionResource(R.dimen.padding_small_8x)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(stringResource(id = R.string.theme_dark))
        Spacer(modifier = Modifier.weight(1f))
        Switch(
            checked = isChecked.value,
            onCheckedChange = {
                isChecked.value = it
                viewModel.saveTheme(it)
                viewModel.switchTheme(it)
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = colorResource(R.color.blue),
                uncheckedThumbColor = colorResource(R.color.greyMedium),
                checkedTrackColor = colorResource(R.color.rippleColor),
                uncheckedTrackColor = colorResource(R.color.greyLight),
            )
        )
    }
}

@Composable
fun TextButton(text: String) {
    Text(
        style = MaterialTheme.typography.titleLarge.copy(
            color = MaterialTheme.colorScheme.onBackground
        ),
        text = text
    )
}

@Composable
fun CompoundButton(title: String, icon: Int? = null, onClick: () -> Unit) {
    Row(
        modifier = Modifier.height(dimensionResource(R.dimen.panel_height))
            .padding(horizontal = dimensionResource(R.dimen.padding_small_8x))
            .clickable(
                onClick = onClick
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(title)
        Spacer(modifier = Modifier.weight(1f))
        icon?.let {
            Image(
                alignment = Alignment.Center,
                painter = painterResource(id = it),
                contentDescription = null,
            )
        }
    }
}

private fun startIntent(context: Context, action: IntentAction) {
    when (action.actionType) {
        ActionType.SHARE_APP -> {
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, action.extra[IntentAction.CONTENT])
                context.startActivity(Intent.createChooser(this, null))
            }
        }
        ActionType.CONTACT_SUPPORT -> {
            Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(action.extra[IntentAction.EMAIL]))
                putExtra(Intent.EXTRA_SUBJECT, action.extra[IntentAction.SUBJECT])
                putExtra(Intent.EXTRA_TEXT, action.extra[IntentAction.CONTENT])
                context.startActivity(this)
            }
        }
        ActionType.OPEN_TERMS -> {
            Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(action.extra[IntentAction.CONTENT])
                context.startActivity(this)
            }
        }
    }
}