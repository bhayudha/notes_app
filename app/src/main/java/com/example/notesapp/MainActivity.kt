package com.example.notesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notesapp.data.Note
import com.example.notesapp.ui.NotesViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    NotesScreen()
                }
            }
        }
    }
}

@Composable
fun NotesScreen(vm: NotesViewModel = viewModel()) {
    val notes by vm.notes.collectAsState()
    var input by rememberSaveable { mutableStateOf("") }
    
    var editingId by rememberSaveable { mutableStateOf<Long?>(null) }
    var editingText by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f),
                label = { Text("Catatan baru") }
            )
            Button(onClick = { vm.addNote(input); input = "" }) {
                Text("Tambah")
            }
        }

        Spacer(Modifier.height(12.dp))
        
        LazyColumn {
            items(notes, key = { it.id }) { note ->
                val isEditing = editingId == note.id
                if (isEditing) {
                    NoteRowEditing(
                        text = editingText,
                        onTextChange = { editingText = it },
                        onSave = {
                            val newText = editingText.trim()
                            if (newText.isNotEmpty()) vm.updateNote(note.copy(title = newText))
                            editingId = null
                        },
                        onCancel = { editingId = null }
                    )
                } else {
                    NoteRowEditable(
                        note = note,
                        onEdit = {
                            editingId = note.id
                            editingText = note.title
                        },
                        onDelete = { vm.deleteNote(note) }
                    )
                }
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun NoteRowEditable(note: Note, onEdit: () -> Unit, onDelete: () -> Unit) {
    ListItem(
        headlineContent = { Text(note.title) },
        trailingContent = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = "Hapus")
                }
            }
        }
    )
}

@Composable
fun NoteRowEditing(
    text: String,
    onTextChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier.weight(1f),
            label = { Text("Edit catatan") }
        )
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Button(onClick = onSave) {
                Text("Simpan")
            }
            OutlinedButton(onClick = onCancel) {
                Text("Batal")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NoteRowEditablePreview() {
    MaterialTheme {
        NoteRowEditable(
            note = Note(id = 1, title = "Contoh catatan untuk preview"),
            onEdit = {},
            onDelete = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NoteRowEditingPreview() {
    MaterialTheme {
        NoteRowEditing(
            text = "Edit catatan ini",
            onTextChange = {},
            onSave = {},
            onCancel = {}
        )
    }
}
