package com.arysugiarto.jetheroes

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.arysugiarto.jetheroes.model.Hero
import com.arysugiarto.jetheroes.model.HeroesData
import com.arysugiarto.jetheroes.ui.theme.JetHeroesTheme
import kotlinx.coroutines.launch
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.ViewModel
import com.arysugiarto.jetheroes.viewmodel.JetHeroesViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arysugiarto.jetheroes.repository.HeroRepository
import com.arysugiarto.jetheroes.viewmodel.ViewModelFactory


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun JetHeroesApp(
    modifier: Modifier = Modifier,
    viewModel: JetHeroesViewModel = viewModel(factory = ViewModelFactory(HeroRepository()))
) {

    val groupedHeroes by viewModel.groupedHeroes.collectAsState()
    val query by viewModel.query


    Box(modifier = Modifier) {

        val scope = rememberCoroutineScope()
        val listState = rememberLazyListState()
        val showButton: Boolean by remember {
            derivedStateOf { listState.firstVisibleItemIndex > 0 }
        }

        LazyColumn(state = listState,
            contentPadding = PaddingValues(bottom = 80.dp)) {
            item {
                SearchBar(
                    query = query,
                    onQueryChange = viewModel::search,
                    modifier = Modifier.background(MaterialTheme.colors.primary)
                )
            }

            groupedHeroes.forEach { (initial, heroes) ->
                stickyHeader {
                    CharacterHeader(initial)
                }

                items(heroes, key = { it.id }) { hero ->
                    HeroesListItem(
                        name = hero.name,
                        photoUrl = hero.photoUrl,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = showButton,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically(),
            modifier = Modifier
                .padding(bottom = 30.dp)
                .align(Alignment.BottomCenter)
        ) {
            ScrollToTopButton(
                onClick = {
                    scope.launch {
                        listState.scrollToItem(index = 0)
                    }
                }
            )
        }
    }
}




@Composable
fun HeroesListItem(name: String, photoUrl: String, modifier: Modifier = Modifier) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { }) {
        AsyncImage(model = photoUrl, contentDescription = null, contentScale = ContentScale.Crop,
        modifier = Modifier
            .padding(8.dp)
            .size(60.dp)
            .clip(CircleShape)
        )
        
        Text(text = name, fontWeight = FontWeight.Medium,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(start = 16.dp))
    }
}

@Composable
fun ScrollToTopButton(
    onClick: ()-> Unit,
    modifier: Modifier = Modifier
){
    Button(
        onClick = onClick,
    modifier = Modifier
        .shadow(elevation = 10.dp, shape = CircleShape)
        .clip(shape = CircleShape)
        .size(56.dp),
    colors = ButtonDefaults.buttonColors(
        backgroundColor = Color.White,
        contentColor = Color.Black
    )) {
        Icon(
            imageVector = Icons.Filled.KeyboardArrowUp,
            contentDescription = null,
        )
    }
}

@Composable
fun CharacterHeader(
    char: Char,
    modifier: Modifier = Modifier
){
    Surface(
        color = MaterialTheme.colors.primary,
        modifier = modifier
    ) {
        Text(
            text = char.toString(),
        fontWeight = FontWeight.Black,
        color = Color.White,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp))
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null
            )
        },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = MaterialTheme.colors.surface,
            disabledIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        placeholder = {
            Text(stringResource(R.string.search_hero))
        },
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .clip(RoundedCornerShape(16.dp))
    )
}

@Preview(showBackground = true)
@Composable
fun JetHeroesAppPreview() {
    JetHeroesTheme {
        JetHeroesApp()
    }
}

@Preview(showBackground = true)
@Composable
fun HeroListItemPreview() {
    JetHeroesTheme {
        HeroesListItem(
            name = "H.O.S. Cokroaminoto",
            photoUrl = ""
        )
    }
}
