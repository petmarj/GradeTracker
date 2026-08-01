package com.example.gradetracker.ui.timetables

import android.content.Context
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.pdf.ExperimentalPdfApi
import androidx.pdf.PdfDocument
import androidx.pdf.SandboxedPdfLoader
import androidx.pdf.compose.PdfViewer
import androidx.pdf.compose.PdfViewerState
import androidx.pdf.view.PdfView
import com.example.gradetracker.model.TimetableLink
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException

@Composable
fun TimetablesScreen(
    viewModel: TimetablesViewModel
) {
    val state by viewModel.uiState.collectAsState()

    var selectedTimetable by remember {
        mutableStateOf<TimetableLink?>(null)
    }

    var displayedTimetable by remember {
        mutableStateOf<TimetableLink?>(null)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        when {
            state.isLoading -> {
                CircularProgressIndicator()
            }

            state.errorMessage != null -> {
                Text(
                    text = state.errorMessage!!,
                    color = MaterialTheme.colorScheme.error
                )
            }

            else -> {
                TimetableControls(
                    timetables = state.timetableLinks,
                    selectedTimetable = selectedTimetable,
                    onTimetableSelected = {
                        selectedTimetable = it
                    },
                    onShow = {
                        displayedTimetable = selectedTimetable
                    }
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    val timetable = displayedTimetable

                    if (timetable == null) {
                        Text(
                            text = "Wähle einen Stundenplan aus.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        RemotePdfViewer(
                            pdfUrl = timetable.url,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}
@Composable
private fun TimetableControls(
    timetables: List<TimetableLink>,
    selectedTimetable: TimetableLink?,
    onTimetableSelected: (TimetableLink) -> Unit,
    onShow: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TimetableDropdown(
                timetables = timetables.sortedBy { it.title },
                selectedTimetable = selectedTimetable,
                onTimetableSelected = onTimetableSelected,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = onShow,
                enabled = selectedTimetable != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Anzeigen")
            }


        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableDropdown(
    modifier: Modifier = Modifier,
    timetables: List<TimetableLink>,
    selectedTimetable: TimetableLink?,
    onTimetableSelected: (TimetableLink) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedTimetable?.title.orEmpty(),
            onValueChange = {},
            readOnly = true,
            label = {
                Text("Klasse")
            },
            placeholder = {
                Text("Klasse auswählen")
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            modifier = Modifier
                .menuAnchor(
                    type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                    enabled = true
                )
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            timetables.forEach { timetable ->
                val isSelected =
                    timetable.url == selectedTimetable?.url

                DropdownMenuItem(
                    text = {
                        Text(timetable.title)
                    },
                    trailingIcon = if (isSelected) {
                        {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Ausgewählt"
                            )
                        }
                    } else {
                        null
                    },
                    onClick = {
                        onTimetableSelected(timetable)
                        expanded = false
                    },
                    modifier = Modifier.then(
                        if (isSelected) {
                            Modifier.background(
                                color = MaterialTheme.colorScheme
                                    .secondaryContainer,
                                shape = RoundedCornerShape(12.dp)
                            )
                        } else {
                            Modifier
                        }
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalPdfApi::class)
@Composable
fun RemotePdfViewer(
    pdfUrl: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var document by remember {
        mutableStateOf<PdfDocument?>(null)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    val loader = remember {
        SandboxedPdfLoader(context.applicationContext)
    }

    val viewerState = remember {
        PdfViewerState()
    }

    LaunchedEffect(pdfUrl) {
        document = null
        errorMessage = null

        try {
            val file = downloadPdf(context, pdfUrl)

            val descriptor = ParcelFileDescriptor.open(
                file,
                ParcelFileDescriptor.MODE_READ_ONLY
            )

            document = try {
                loader.openDocument(
                    uri = Uri.fromFile(file),
                    fileDescriptor = descriptor
                )
            } catch (exception: Exception) {
                descriptor.close()
                throw exception
            }
        } catch (exception: Exception) {
            errorMessage =
                exception.message ?: "PDF konnte nicht geladen werden."
        }
    }

    DisposableEffect(document) {
        val currentDocument = document

        onDispose {
            currentDocument?.close()
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when {
            errorMessage != null -> {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error
                )
            }

            document == null -> {
                CircularProgressIndicator()
            }

            else -> {
                PdfViewer(
                    pdfDocument = document,
                    state = viewerState,
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = PdfView.VERTICAL_ALIGNMENT_TOP,
                    contentPadding = PaddingValues(top = 16.dp)

                )
            }
        }
    }
}
private suspend fun downloadPdf(
    context: Context,
    url: String
): File = withContext(Dispatchers.IO) {
    val file = File(
        context.cacheDir,
        "timetable-${url.hashCode()}.pdf"
    )

    if (!file.exists()) {
        val request = Request.Builder()
            .url(url)
            .build()

        OkHttpClient()
            .newCall(request)
            .execute()
            .use { response ->
                if (!response.isSuccessful) {
                    throw IOException(
                        "PDF-Download fehlgeschlagen: ${response.code}"
                    )
                }

                val body = response.body
                    ?: throw IOException("Die PDF-Antwort ist leer.")

                file.outputStream().use { output ->
                    body.byteStream().use { input ->
                        input.copyTo(output)
                    }
                }
            }
    }

    file
}