package com.example.lunchtray.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.lunchtray.datasource.DataSource
import com.example.lunchtray.model.MenuItem
import com.example.lunchtray.model.MenuItem.EntreeItem

@Composable
fun EntreeMenuScreen(
    options: List<EntreeItem>,
    onCancelButtonClicked: () -> Unit,
    onNextButtonClicked: () -> Unit,
    onSelectionChanged: (EntreeItem) -> Unit,
    modifier: Modifier = Modifier
) {
    BaseMenuScreen(
        options = options,
        onCancelButtonClicked = onCancelButtonClicked,
        onNextButtonClicked = onNextButtonClicked,
        onSelectionChanged = onSelectionChanged as (MenuItem) -> Unit
    )
}

@Preview
@Composable
fun EntreeMenuPreview(){
    EntreeMenuScreen(
        options = DataSource.entreeMenuItems,
        onCancelButtonClicked = {},
        onNextButtonClicked = {},
        onSelectionChanged = {}
    )
}