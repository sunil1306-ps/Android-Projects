package com.study.prototype1


import android.content.Context
import android.net.Uri
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.prototype1.R
import com.study.prototype1.downloadmanagerguide.AndroidDownloader
import com.study.prototype1.ui.theme.Prototype1Theme
import com.study.prototype1.viewModels.MainViewModel
import java.util.Locale

class Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun yearScreen(
        signOutClicked: () -> Unit,
        profileImage: Uri,
        navController: NavHostController,
        profileName: String,
        modifier: Modifier = Modifier
    ) {

        Scaffold(
            topBar = {
                TopAppBar(navController)
            },
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(it),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {


                Row(
                    modifier = modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        modifier = Modifier
                            .size(50.dp)
                            .fillMaxHeight(0.4f)
                            .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape)
                            .padding(5.dp),
                        shape = RoundedCornerShape(125.dp),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 20.dp
                        )
                    ) {
                        AsyncImage(
                            model = profileImage,
                            contentDescription = "profileImage",
                            contentScale = ContentScale.FillBounds,
                            modifier = modifier
                                .align(Alignment.CenterHorizontally)
                                .size(150.dp)
                        )
                    }
                    Spacer(modifier = modifier.padding(10.dp))
                    Text(
                        text = "Welcome ${profileName.toUpperCase(Locale.ROOT)}!",
                        fontSize = 30.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                Spacer(modifier = modifier.weight(0.40f))
                ///Year selection cards section start

                Column(
                    modifier = modifier.fillMaxWidth()
                ) {

                    YearText(year = "First Year", navController = navController, path = "branch")
                    YearText(year = "Second Year", navController = navController, path = "branch")
                    YearText(year = "Third Year", navController = navController, path = "branch")
                    YearText(year = "Forth Year", navController = navController, path = "branch")


                }

                //Year selection cards section ends
                Spacer(modifier = modifier.weight(0.50f))
                OutlinedButton(
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 15.dp,
                        pressedElevation = 5.dp,
                        disabledElevation = 0.dp,
                        hoveredElevation = 10.dp,
                        focusedElevation = 5.dp
                    ),
                    colors = ButtonDefaults.buttonColors(
                        MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline),
                    onClick = { signOutClicked() },
                    modifier = modifier
                        .fillMaxWidth()
                        .height(45.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "SignOut",
                            color = MaterialTheme.colorScheme.outline,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = modifier.weight(0.10f))
            }
        }

    }

    @Composable
    fun YearText(
        year: String,
        navController: NavHostController,
        path: String,
        modifier: Modifier = Modifier,
    ) {
        ElevatedCard(
            elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = 10.dp
            ),
            modifier = modifier
                .fillMaxWidth()
                .padding(10.dp)
                .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                .height(100.dp)
                .clickable {
                    states.distinct_token[0] = branch.year_ids[year].toString()
                    navController.navigate(path)
                }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = modifier
                    .fillMaxSize()
            ) {
                Text(
                    text = year,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }

    @Composable
    fun BranchText(
        branchh: String,
        navController: NavHostController,
        path: String,
        modifier: Modifier = Modifier,
    ) {
        var expanded by remember { mutableStateOf(false) }
        ElevatedCard(
            elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = 10.dp
            ),
            modifier = modifier
                .fillMaxWidth()
                .padding(10.dp)
                .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
//                .height(100.dp)
                .clickable {
                    states.distinct_token[0] = states.distinct_token[0].substring(
                        0,
                        1
                    ) + branch.branch_ids[branchh].toString()
                    navController.navigate(path)
                }
        ) {

            Column(
                modifier = modifier
                    .animateContentSize(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = modifier
                        .fillMaxSize()
                        .height(100.dp)
                ) {
                    Spacer(modifier = modifier.weight(0.5f))
                    Text(
                        text = branchh,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = modifier.weight(0.4f))
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = if (expanded) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                            contentDescription = "choose semester",
                            tint = MaterialTheme.colorScheme.outline,
                        )
                    }
                    Spacer(modifier = modifier.weight(0.1f))

                }
            }


            if(expanded) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = modifier.height(50.dp)
                ) {
                    Button(
                        onClick = { navController.navigate(path) },
                        modifier = modifier
                            .fillMaxSize()
                            .weight(0.5f)
                    ) {
                        Text(
                            text = "SEM-1",
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                    Button(
                        onClick = { navController.navigate(path) },
                        modifier = modifier
                            .fillMaxHeight()
                            .weight(0.5f)
                    ) {
                        Text(
                            text = "SEM-2",
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun SubjectText(
        subject: String,
        navController: NavHostController,
        path: String,
        modifier: Modifier = Modifier,
    ) {
        ElevatedCard(
            elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = 10.dp
            ),
            modifier = modifier
                .fillMaxWidth()
                .padding(10.dp)
                .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                .height(100.dp)
                .clickable {
                    navController.navigate(path)
                }
        ) {
            Column(
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = modifier
                        .fillMaxSize()
                ) {
                    Text(
                        text = subject,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun branchScreen(navController: NavHostController, modifier: Modifier = Modifier) {
//        Column(
//            modifier = modifier.fillMaxSize(),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
//            for (i in branch.branch) {
//                Text(
//                    text = i,
//                    fontSize = 20.sp,
//                    modifier = modifier.clickable {
//                        states.distinct_token[0] = states.distinct_token[0].substring(0, 1) + branch.branch_ids[i].toString()
//                        navController.navigate("subject")
//                    }
//                )
//                Spacer(modifier = modifier.height(10.dp))
//            }
//        }

        Scaffold(
            topBar = {
                TopAppBar(navController)
            },
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(
                        it
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items( items = branch.branch) { branch ->
                    BranchText(branchh = branch, navController = navController, path = "subject")
                }
            }
            Spacer(modifier = modifier.height(10.dp))
        }

    }
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun subjectScreen(navController: NavHostController, modifier: Modifier = Modifier) {

        Scaffold(
            topBar = {
                TopAppBar(navController)
            },
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(
                        it
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when(states.distinct_token[0]) {

                    "11" -> {
                        items(items = branch.cse_1) { item ->
                            SubjectText(subject = item, navController = navController, path = "docs")
                        }
                    }
                    "21" -> {
                        items(items = branch.cse_2) { item ->
                            SubjectText(subject = item, navController = navController, path = "docs")
                        }
                    }
                    "31" -> {
                        items(items = branch.cse_3) { item ->
                            SubjectText(subject = item, navController = navController, path = "docs")
                        }
                    }
                    "12" -> {
                        items(items = branch.it_1) { item ->
                            SubjectText(subject = item, navController = navController, path = "docs")
                        }
                    }
                    "22" -> {
                        items(items = branch.it_2) { item ->
                            SubjectText(subject = item, navController = navController, path = "docs")
                        }
                    }
                    "32" -> {
                        items(items = branch.it_3) { item ->
                            SubjectText(subject = item, navController = navController, path = "docs")
                        }
                    }
                    "13" -> {
                        items(items = branch.civil_1) { item ->
                            SubjectText(subject = item, navController = navController, path = "docs")
                        }
                    }
                    "23" -> {
                        items(items = branch.civil_2) { item ->
                            SubjectText(subject = item, navController = navController, path = "docs")
                        }
                    }
                    "33" -> {
                        items(items = branch.civil_3) { item ->
                            SubjectText(subject = item, navController = navController, path = "docs")
                        }
                    }
                    "14" -> {
                        items(items = branch.mech_1) { item ->
                            SubjectText(subject = item, navController = navController, path = "docs")
                        }
                    }
                    "24" -> {
                        items(items = branch.mech_2) { item ->
                            SubjectText(subject = item, navController = navController, path = "docs")
                        }
                    }
                    "34" -> {
                        items(items = branch.mech_3) { item->
                            SubjectText(subject = item, navController = navController, path = "docs")
                        }
                    }
                    else -> {
                        item { 
                            Text(text = "Something's wrong!")
                        }
                    }
                }
            }
        }
    }
    @Composable
    fun docsScreen(viewModel: MainViewModel, downloader: AndroidDownloader, context: Context, navController: NavHostController) {
        SetData(viewModel = viewModel, downloader, context, navController)
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SignInScreen(
        signInClicked: () -> Unit,
        navController: NavHostController,
        modifier: Modifier = Modifier
    ) {

        Scaffold(
            topBar = {
                TopAppBar(navController)
            },
            modifier = modifier
                .fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Spacer(modifier = modifier.weight(0.20f))

                Image(
                    painter = painterResource(id = R.drawable.logo_white),
                    contentDescription = "studyHUB logo",
                    contentScale = ContentScale.Fit,
                    modifier = modifier
                        .height(550.dp)
                        .width(500.dp)
                        .padding(it)
                )

                Spacer(modifier.weight(0.80f))

                OutlinedButton(
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 15.dp,
                        pressedElevation = 5.dp,
                        disabledElevation = 0.dp,
                        hoveredElevation = 10.dp,
                        focusedElevation = 5.dp
                    ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline),
                    onClick = { signInClicked() },
                    modifier = modifier
                        .fillMaxWidth()
                        .height(45.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                        modifier = modifier.fillMaxWidth()
                    ) {
//                    Image(
//                        painter = painterResource(id = R.drawable.google_icon),
//                        contentScale = ContentScale.FillHeight,
//                        contentDescription = "Google Logo",
//                        modifier = modifier.fillMaxHeight()
//                    )
                        Icon(
                            painter = painterResource(id = R.drawable.google_logo_9824),
                            contentDescription = "google logo",
                            modifier = modifier
                                .fillMaxHeight()
                                .wrapContentSize(),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = modifier.weight(.40f))
                        Text(
                            text = "Continue with Google",
                            color = MaterialTheme.colorScheme.outline,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = modifier.weight(.60f))
                    }
                }
                Spacer(modifier = modifier.padding(10.dp))

            }
        }

    }

}

val var_screen = Screen()

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignInPreview() {
    Prototype1Theme {
        //var_screen.SignInScreen()
    }
}