package com.example.quizos.view

import androidx.compose.runtime.Composable
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.RadioButton
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable

@Composable
fun QuestionCard(
    question: String,
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit
) {
    Card(
        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = androidx.compose.ui.Modifier.padding(16.dp)) {
            Text(text = question, fontSize = 20.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = androidx.compose.ui.Modifier.height(12.dp))
            options.forEach { option ->
                Row(
                    androidx.compose.ui.Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (option == selectedOption),
                            onClick = { onOptionSelected(option) }
                        )
                        .padding(vertical = 8.dp)
                ) {
                    RadioButton(
                        selected = (option == selectedOption),
                        onClick = { onOptionSelected(option) }
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(text = option)
                }
            }
        }
    }
}