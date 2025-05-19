package com.example.superawesometodolistcatgaggingappgagging.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.superawesometodolistcatgaggingappgagging.R
import com.example.superawesometodolistcatgaggingappgagging.ui.theme.AppTheme
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// TODO: Adapt for landscape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel(factory = LoginViewModelProvider.Factory),
    onSignIn: () -> Unit = {}
) {
    val painter = painterResource(R.drawable.logo)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    var username by remember { mutableStateOf("") }
    var passwordState = rememberTextFieldState()
    var passwordVisible by remember { mutableStateOf(false) }
    var loginState = viewModel.loginStateFlow.collectAsState()

    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        bottomBar = {
            Row (
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 40.dp,
                        end = 40.dp,
                        bottom = 60.dp
                    )
            ) {
                TextButton(
                    onClick = {
                        scope.launch {
                            viewModel.register(
                                context = context,
                                username = username,
                                password = passwordState.text.toString(),
                            )
                        }
                    }
                ) {
                    Text(stringResource(R.string.create_an_account))
                }
                Button(
                    onClick = {
                        scope.launch {
                            viewModel.login(
                                context = context,
                                username = username,
                                password = passwordState.text.toString(),
                            )
                        }
                    }
                ) {
                    Text(stringResource(R.string.sign_in))
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement =  Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painter,
                contentDescription = stringResource(R.string.application_logo),
                modifier = Modifier.padding(20.dp).size(200.dp)
            )
            Text(
                text = stringResource(R.string.welcome_back),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.displayMedium
            )
            Text(
                text = stringResource(R.string.sign_in_with_your_satdlcgag_account),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Column(
                modifier = Modifier.padding(top = 30.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text(stringResource(R.string.username)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                )
                OutlinedSecureTextField(
                    state = passwordState,
                    label = { Text(stringResource(R.string.password)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    textObfuscationMode = if (passwordVisible) TextObfuscationMode.Visible else TextObfuscationMode.RevealLastTyped,
                    trailingIcon = {
                        IconButton(
                            onClick = { passwordVisible = !passwordVisible }
                        ) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = if (passwordVisible) stringResource(R.string.hide_password) else stringResource(
                                    R.string.show_password
                                )
                            )
                        }
                    }
                )
                Text(buildAnnotatedString {
                    withLink(
                        LinkAnnotation.Clickable(
                            tag = "passwordForgotten",
                            linkInteractionListener = {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        "That is unfortunate"
                                    )
                                }
                            },
                            styles = TextLinkStyles(
                                SpanStyle(
                                    color = MaterialTheme.colorScheme.primary,
                                    textDecoration = TextDecoration.Underline
                                )
                            )
                        )
                    ) {
                        append("I forgot my password")
                    }
                })
            }
        }

        when (loginState.value) {
            LoginResult.NONE -> {}
            LoginResult.LOADING -> {
                BasicAlertDialog(
                    onDismissRequest = {
                        scope.launch {
                            viewModel.loginStateFlow.update {
                                LoginResult.NONE
                            }
                        }
                    }
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
            LoginResult.INCORRECT -> {
                LaunchedEffect(Unit) {
                    snackbarHostState.showSnackbar(
                        "Incorrect"
                    )
                }
            }
            LoginResult.FAILED -> {
                LaunchedEffect(Unit) {
                    snackbarHostState.showSnackbar(
                        "Failed"
                    )
                }
            }
            LoginResult.CORRECT -> onSignIn()
        }
    }
}

@Composable
@Preview
fun LoginScreenPreview() {
    AppTheme {
        LoginScreen()
    }
}