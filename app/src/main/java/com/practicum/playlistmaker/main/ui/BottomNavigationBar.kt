package com.practicum.playlistmaker.main.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.practicum.playlistmaker.R

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val items = listOf(
        BottomNavItem(Routes.Search.name, stringResource(R.string.search), painterResource(R.drawable.ic_search)),
        BottomNavItem(Routes.MediaLibrary.name, stringResource(R.string.media_library), painterResource(R.drawable.ic_media_library)),
        BottomNavItem(Routes.Settings.name, stringResource(R.string.settings), painterResource(R.drawable.ic_settings)),
    )

    Column {
        HorizontalDivider(color = colorResource(R.color.greyLight), thickness = 1.dp)
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.background,
        ) {
            items.forEach { item ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            item.icon,
                            contentDescription = ""
                        )
                   },
                    label = {
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = Color.Unspecified
                            ),
                        )
                    },
                    selected = currentDestination?.route == item.route,
                    onClick = { navController.navigate(item.route) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = colorResource(R.color.blue),
                        unselectedIconColor = MaterialTheme.colorScheme.onBackground,
                        selectedTextColor = MaterialTheme.colorScheme.secondary,
                        unselectedTextColor = MaterialTheme.colorScheme.onBackground,
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    }
}