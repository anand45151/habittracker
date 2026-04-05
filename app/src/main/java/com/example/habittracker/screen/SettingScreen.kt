package com.example.habittracker.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habittracker.ui.theme.mainGradient

@Composable
fun SettingScreenUi(
    username: String = "Anand",
    email: String = "anand@email.com",
    onChangePasswordClick: () -> Unit = {}
) {


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(mainGradient)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "Settings",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(30.dp))


            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Username", fontWeight = FontWeight.Bold)
                    Text(username)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Email Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Email", fontWeight = FontWeight.Bold)
                    Text(email)
                }
            }

            Spacer(modifier = Modifier.height(30.dp))


            Button(
                onClick = { onChangePasswordClick() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00C853)
                )
            ) {
                Text(
                    text = "Change Password",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
    }
}
@Composable
@Preview(showBackground = true, showSystemUi = true)
fun SettingScreenUiPreview() {
    SettingScreenUi()
}
