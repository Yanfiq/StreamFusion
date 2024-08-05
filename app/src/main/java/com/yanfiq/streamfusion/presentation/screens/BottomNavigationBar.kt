package com.yanfiq.streamfusion.presentation.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yanfiq.streamfusion.domain.model.Track
import com.yanfiq.streamfusion.presentation.screens.home.HomeScreen
import com.yanfiq.streamfusion.presentation.screens.search.SearchScreen
import com.yanfiq.streamfusion.presentation.screens.search.SearchScreenData
import com.yanfiq.streamfusion.presentation.screens.search.StreamingPlatform
import com.yanfiq.streamfusion.presentation.screens.settings.SettingsScreen

@Composable
fun BottomNavigationBar(searchScreenData: SearchScreenData, onSearchClick: (searchQuery: String) -> Unit, onPlayClick: (Track, StreamingPlatform) -> Unit) {
    var navigationSelectedItem by remember {
        mutableStateOf(0)
    }
    /**
     * by using the rememberNavController()
     * we can get the instance of the navController
     */
    val navController = rememberNavController()

    //scaffold to hold our bottom navigation Bar
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                //getting the list of bottom navigation items for our data class
                BottomNavigationItem().bottomNavigationItems().forEachIndexed { index, navigationItem ->

                    //iterating all items with their respective indexes
                    NavigationBarItem(
                        /*If our current index of the list of items
                         *is equal to navigationSelectedItem then simply
                         *The selected item is active in overView this
                         *is used to know the selected item
                         */
                        selected = index == navigationSelectedItem,

                        //Label is used to bottom navigation labels like Home, Search
                        label = {
                            Text(navigationItem.label)
                        },

                        // Icon is used to display the icons of the bottom Navigation Bar
                        icon = {
                            Icon(
                                navigationItem.icon,
                                contentDescription = navigationItem.label
                            )
                        },
                        // used to handle click events of navigation items
                        onClick = {
                            navigationSelectedItem = index
                            navController.navigate(navigationItem.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) {paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screens.Home.route,
            modifier = Modifier.padding(paddingValues = paddingValues)
        ) {
            composable(Screens.Home.route) {
                HomeScreen(
                    navController = navController
                )
            }
            composable(Screens.Search.route) {
                SearchScreen(
                    audiusSearchData = searchScreenData.audiusSearchData,
                    soundcloudsearchData = searchScreenData.soundcloudSearchData,
                    spotifySearchData = searchScreenData.spotifySearchData,
                    youtubeSearchData = searchScreenData.youtubeSearchData,
                    navController = navController,
                    onSearchClick = {searchQuery -> onSearchClick(searchQuery) },
                    onPlayClick = {track, platform -> onPlayClick(track, platform) }
                )
            }
            composable(Screens.Settings.route) {
                SettingsScreen()
            }
        }
    }
}