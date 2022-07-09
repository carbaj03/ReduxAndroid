package com.acv.redux

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.acv.redux.types.Action
import com.acv.redux.types.State
import com.acv.redux.types.Store
import com.acv.redux.ui.theme.ReduxTheme
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds


data class ScreenState(val counter: Int) : State
data class Screen2State(val counter: Int) : State
data class Screen3State(val counter: Int) : State


sealed interface AppAction : Action {
    object Load : AppAction
    object Delete : AppAction
}

sealed interface ScreenAction : Action {
    object Load : ScreenAction
    object Delete : ScreenAction
}

sealed interface Screen2Action : Action {
    object Load : Screen2Action
    object Delete : Screen2Action
}

val reducer = { s: ScreenState, a: AppAction ->
    when (a) {
        AppAction.Load -> s.copy(s.counter + 1)
        AppAction.Delete -> s.copy(0)
    }
}

val reducer2 = { s: ScreenState, a: ScreenAction ->
    when (a) {
        ScreenAction.Load -> s.copy(s.counter + 1)
        ScreenAction.Delete -> s.copy(0)
    }
}

val store = createStore(reducer, initialState = ScreenState(0))


val slice: Slice<ScreenState> = createSlice<ScreenState, AppAction>(ScreenState(0)) { s, a ->
    when (a) {
        AppAction.Load -> s.copy(s.counter + 1)
        AppAction.Delete -> s.copy(0)
    }
}


val slice2 = createSlice<Screen2State, ScreenAction>(Screen2State(0)) { s, a ->
    when (a) {
        ScreenAction.Load -> s.copy(s.counter + 1)
        ScreenAction.Delete -> s.copy(0)
    }
}

val slice3: Slice<Screen3State> = createSlice<Screen3State, Screen2Action>(Screen3State(0)) { s, a ->
    when (a) {
        Screen2Action.Load -> s.copy(s.counter + 1)
        Screen2Action.Delete -> s.copy(0)
    }
}

val s = configureStore(slice, slice2, slice3)


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ReduxTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    val state by s.state.collectAsState()

                    LaunchedEffect(key1 = s, block = {
                        delay(1.seconds)
                        s.dispatch(AppAction.Load)
                    })

                    Column {
                        Screen(state.select(slice).counter.toString(), { s.dispatch(AppAction.Load) }, { s.dispatch(AppAction.Delete) })
                        Screen(state.select(slice2).counter.toString(), { s.dispatch(ScreenAction.Load) }, { s.dispatch(ScreenAction.Delete) })
                        Screen(state.select(slice3).counter.toString(), { s.dispatch(Screen2Action.Load) }, { s.dispatch(Screen2Action.Delete) })
                    }
                }
            }
        }
    }
}

@Composable
fun Screen(title: String, load: () -> Unit, delete: () -> Unit) {
    Column {
        Row {
            Button(onClick = load) {
                Text(text = "Reducer1")
            }

            Button(onClick = delete) {
                Text(text = "Reducer2")
            }
        }
        Text(text = title)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ReduxTheme {
        Screen("Android", {}, {})
    }
}