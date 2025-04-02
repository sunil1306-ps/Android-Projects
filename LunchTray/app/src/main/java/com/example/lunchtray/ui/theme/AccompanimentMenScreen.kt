package com.example.lunchtray.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.lunchtray.datasource.DataSource
import com.example.lunchtray.model.MenuItem
import com.example.lunchtray.model.MenuItem.AccompanimentItem
import com.example.lunchtray.ui.theme.BaseMenuScreen

@Composable
fun AccompanimentMenuScreen(
    options: List<AccompanimentItem>,
    onCancelButtonClicked: () -> Unit,
    onNextButtonClicked: () -> Unit,
    onSelectionChanged: (AccompanimentItem) -> Unit,
    modifier: Modifier = Modifier
) {
    BaseMenuScreen(
        options = options,
        onCancelButtonClicked = onCancelButtonClicked,
        onNextButtonClicked = onNextButtonClicked,
        onSelectionChanged = onSelectionChanged as (MenuItem) -> Unit
    )
}

@Preview(showBackground = true)
@Composable
fun AccompanimentMenuPreview(){
    AccompanimentMenuScreen(
        options = DataSource.accompanimentMenuItems,
        onNextButtonClicked = {},
        onCancelButtonClicked = {},
        onSelectionChanged = {}
    )
}