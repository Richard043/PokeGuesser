package com.example.pokeguess

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.pokeguess.ui.theme.PokeGuessTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PokeGuessTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    GameScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun GameScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var pokemonName by remember { mutableStateOf("") }
    var guess by remember { mutableStateOf(TextFieldValue("")) }
    var result by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }

    // Background image URL
    val backgroundImageUrl = "https://i.imgur.com/hpubTfI.jpeg"

    fun fetchPokemon() {
        val randomPokeId = Random.nextInt(1, 1026)
        val client = OkHttpClient()
        val request = Request.Builder().url("https://pokeapi.co/api/v2/pokemon/$randomPokeId").build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                result = "Request failed: ${e.message}"
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                val json = response.body?.string()
                val jsonObject = JSONObject(json)
                pokemonName = jsonObject.getString("name")
                imageUrl = "https://play.pokemonshowdown.com/sprites/ani/$pokemonName.gif"
            }
        })
    }

    LaunchedEffect(Unit) {
        fetchPokemon()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = rememberImagePainter(backgroundImageUrl),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Who's that Pokemon?!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (imageUrl.isNotEmpty()) {
                Image(
                    painter = rememberImagePainter(data = imageUrl),
                    contentDescription = null,
                    modifier = Modifier.size(200.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            BasicTextField(
                value = guess,
                onValueChange = { guess = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.Black)
                    .background(Color.White)
                    .padding(8.dp),
                singleLine = true,
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        if (guess.text.isEmpty()) {
                            Text("Enter your guess", color = Color.Black)
                        }
                        innerTextField()
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                if (guess.text.lowercase() == pokemonName) {
                    result = "You are correct! :)"
                } else {
                    result = "You are incorrect :v\nThe answer is $pokemonName"
                }
                fetchPokemon()
            }) {
                Text("Submit")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = result,
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                color = Color.White
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    PokeGuessTheme {
        GameScreen()
    }
}
