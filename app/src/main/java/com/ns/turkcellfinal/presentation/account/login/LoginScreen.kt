package com.ns.turkcellfinal.presentation.account.login

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ns.turkcellfinal.R
import com.ns.turkcellfinal.core.base.BaseResponse
import com.ns.turkcellfinal.core.domain.ViewState
import com.ns.turkcellfinal.presentation.account.AuthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginClick: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    val loginState = viewModel.loginState.collectAsState().value
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.login_with_username),
            color = colorResource(id = R.color.textColor),
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.SansSerif,
            modifier = Modifier
                .padding(vertical = 16.dp)
                .align(Alignment.CenterHorizontally)
        )

        OutlinedTextField(
            value = username.value,
            onValueChange = { username.value = it },
            label = { Text(text = stringResource(id = R.string.username)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = colorResource(id = R.color.tl_border_color),
                unfocusedBorderColor = colorResource(id = R.color.tl_border_color),
                cursorColor = colorResource(id = R.color.textColor),
                focusedLabelColor = colorResource(id = R.color.textGray),
            ),
            shape = RoundedCornerShape(8.dp)
        )

        OutlinedTextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text(text = stringResource(id = R.string.password)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = colorResource(id = R.color.tl_border_color),
                unfocusedBorderColor = colorResource(id = R.color.tl_border_color),
                cursorColor = colorResource(id = R.color.textColor),
                focusedLabelColor = colorResource(id = R.color.textGray)
            ),
            shape = RoundedCornerShape(8.dp),
            visualTransformation = PasswordVisualTransformation()
        )

        Button(
            onClick = {
                coroutineScope.launch {
                    viewModel.login(username.value, password.value)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.blue_stroke),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = stringResource(id = R.string.login),
                fontSize = 16.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Medium
            )
        }

        loginState.let { response ->
            when (response) {
                is ViewState.Loading -> {
                    Log.d("Loading", "Loading")
                }

                is ViewState.Success -> {
                    val result = response.result as BaseResponse.Success

                    result.data.let {
                        Text("Welcome ${it.firstName} ${it.lastName}")
                        onLoginClick()
                    }
                }

                is ViewState.Error -> {
                    Text("Error: ${response.error}")
                }

            }
        }
    }
}
