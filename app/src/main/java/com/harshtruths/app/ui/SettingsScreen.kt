package com.harshtruths.app.ui

import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshtruths.app.R
import com.harshtruths.app.data.NotificationScheduler
import com.harshtruths.app.data.QuoteRepository
import com.harshtruths.app.data.SettingsDataStore
import com.harshtruths.app.data.UpdateManager
import com.harshtruths.app.data.UpdateState
import com.harshtruths.app.ui.theme.ThemeMode
import com.harshtruths.app.ui.theme.Typography
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    quoteRepository: QuoteRepository,
    settingsDataStore: SettingsDataStore,
    appVersion: String = "1.0.0",
    onNavigateBack: () -> Unit
) {
    val timePair by settingsDataStore.notificationTime.collectAsState(initial = 9 to 0)
    val (hour, minute) = timePair
    val enabled by settingsDataStore.notificationsEnabled.collectAsState(initial = true)
    val themeMode by settingsDataStore.themeMode.collectAsState(initial = ThemeMode.SYSTEM)
    val userName by settingsDataStore.userName.collectAsState(initial = null)
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val topAppBarColors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.background,
        titleContentColor = MaterialTheme.colorScheme.onBackground
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Settings", fontWeight = FontWeight.Medium) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = "Back"
                        )
                    }
                },
                colors = topAppBarColors
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = padding.calculateTopPadding() + 16.dp,
                bottom = padding.calculateBottomPadding() + 16.dp,
                start = 16.dp,
                end = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item(key = "profile") {
                ProfileCard(
                    userName = userName,
                    onNameSaved = { newName ->
                        coroutineScope.launch {
                            settingsDataStore.setUserName(newName)
                        }
                    }
                )
            }

            item(key = "appearance") {
                AppearanceCard(
                    themeMode = themeMode,
                    onThemeSelected = { mode ->
                        coroutineScope.launch {
                            settingsDataStore.setThemeMode(mode)
                        }
                    }
                )
            }

            item(key = "notification") {
                NotificationCard(
                    enabled = enabled,
                    hour = hour,
                    minute = minute,
                    onEnabledChanged = { newValue ->
                        coroutineScope.launch {
                            settingsDataStore.setNotificationsEnabled(newValue)
                            if (newValue) {
                                NotificationScheduler.scheduleDailyNotification(context, hour, minute)
                            } else {
                                NotificationScheduler.cancelDailyNotification(context)
                            }
                        }
                    },
                    onTimeChanged = { selectedHour, selectedMinute ->
                        coroutineScope.launch {
                            settingsDataStore.setNotificationTime(selectedHour, selectedMinute)
                            NotificationScheduler.scheduleDailyNotification(context, selectedHour, selectedMinute)
                        }
                    }
                )
            }

            item(key = "updates") {
                AppUpdatesCard(currentVersion = appVersion)
            }

            item(key = "reset") {
                ResetProgressCard(
                    onReset = {
                        quoteRepository.resetStartDate()
                    }
                )
            }

            item(key = "about") {
                AboutCard(version = appVersion)
            }

            item(key = "cryptic_hint") {
                CrypticHintSection()
            }
        }
    }
}

@Composable
private fun ProfileCard(
    userName: String?,
    onNameSaved: (String) -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }

    if (showEditDialog) {
        EditNameDialog(
            currentName = userName ?: "",
            onDismiss = { showEditDialog = false },
            onNameSaved = onNameSaved
        )
    }

    OutlinedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Your Name",
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (userName.isNullOrBlank()) "Not set" else userName,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Button(onClick = { showEditDialog = true }) {
                    Text("Edit")
                }
            }
        }
    }
}

@Composable
private fun AppearanceCard(
    themeMode: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Appearance",
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            val themeOptions = remember {
                listOf(
                    ThemeMode.SYSTEM to "System default",
                    ThemeMode.LIGHT to "Light",
                    ThemeMode.DARK to "Dark"
                )
            }

            Column(modifier = Modifier.selectableGroup()) {
                themeOptions.forEach { (mode, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .selectable(
                                selected = (themeMode == mode),
                                onClick = { onThemeSelected(mode) },
                                role = Role.RadioButton
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (themeMode == mode),
                            onClick = null
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = label,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationCard(
    enabled: Boolean,
    hour: Int,
    minute: Int,
    onEnabledChanged: (Boolean) -> Unit,
    onTimeChanged: (Int, Int) -> Unit
) {
    val context = LocalContext.current
    val formattedTime = remember(hour, minute) {
        String.format("%02d:%02d", hour, minute)
    }

    OutlinedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Daily Notification",
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Receive today's quote at your chosen time",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = enabled,
                    onCheckedChange = onEnabledChanged
                )
            }

            if (enabled) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Time: $formattedTime",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Button(onClick = {
                        TimePickerDialog(
                            context,
                            { _, selectedHour, selectedMinute ->
                                onTimeChanged(selectedHour, selectedMinute)
                            },
                            hour,
                            minute,
                            true
                        ).show()
                    }) {
                        Text("Change")
                    }
                }
            }
        }
    }
}

@Composable
private fun AppUpdatesCard(
    currentVersion: String
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var updateState by remember { mutableStateOf<UpdateState>(UpdateState.Idle) }

    OutlinedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "App Updates",
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Installed Version: v$currentVersion",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (updateState is UpdateState.Checking) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                updateState = UpdateState.Checking
                                updateState = UpdateManager.checkForUpdates(context)
                            }
                        }
                    ) {
                        Text("Check")
                    }
                }
            }

            when (val state = updateState) {
                is UpdateState.UpToDate -> {
                    Text(
                        text = "You are on the latest version.",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                is UpdateState.Error -> {
                    Text(
                        text = state.message,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                is UpdateState.UpdateAvailable -> {
                    val info = state.info
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Version ${info.versionName} Available!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = info.releaseNotes,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    updateState = UpdateState.Downloading(0)
                                    val apkFile = UpdateManager.downloadUpdate(context, info) { percent ->
                                        updateState = UpdateState.Downloading(percent)
                                    }
                                    if (apkFile != null) {
                                        updateState = UpdateState.ReadyToInstall(apkFile)
                                        UpdateManager.promptInstall(context, apkFile)
                                    } else {
                                        updateState = UpdateState.Error("Failed to download update.")
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Download & Install Update")
                        }
                    }
                }
                is UpdateState.Downloading -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Downloading update... ${state.progressPercent}%",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        LinearProgressIndicator(
                            progress = { state.progressPercent / 100f },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                is UpdateState.ReadyToInstall -> {
                    Button(
                        onClick = {
                            UpdateManager.promptInstall(context, state.apkFile)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Install Update")
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun ResetProgressCard(
    onReset: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Reset Progress",
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Start over from day 1. This will reset your reading progress.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(
                onClick = onReset,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Text("Reset to Day 1")
            }
        }
    }
}

@Composable
private fun AboutCard(
    version: String
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "100 Harsh Truths",
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Version $version",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "99 timeless life truths for daily reflection",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun CrypticHintSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = "One last thing",
            style = Typography.titleSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
            textAlign = TextAlign.Center
        )
        Text(
            text = "You might notice something missing. Some things are better discovered than explained.",
            style = Typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
}
