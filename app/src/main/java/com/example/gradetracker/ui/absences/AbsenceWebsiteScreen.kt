package com.example.gradetracker.ui.absences

import android.graphics.Bitmap
import android.os.Build
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AbsenceWebsiteScreen (
    absenceId: Int,
    userToken: String,
    userJson: String,
    isAdmin: Boolean,
    onClose: () -> Unit
) {
    val targetUrl =
        "$ABSENCE_WEBSITE_ORIGIN/student/excuse-absence/$absenceId"

    var webView by remember {
        mutableStateOf<WebView?>(null)
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    BackHandler {
        val currentWebView = webView

        if (currentWebView?.canGoBack() == true) {
            currentWebView.goBack()
        } else {
            onClose()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(0, 0, 0, 0),
                title = {
                    Text("Absenz bearbeiten")
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onClose()
                        }
                    ) {
                        Icon(
                            imageVector =
                                Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Zurück"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    var sessionWasInjected = false

                    WebView(context).apply {
                        webView = this

                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true

                        settings.allowFileAccess = false
                        settings.allowContentAccess = false
                        settings.setSupportMultipleWindows(false)

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            settings.mixedContentMode =
                                WebSettings.MIXED_CONTENT_NEVER_ALLOW
                        }

                        webViewClient = object : WebViewClient() {
                            override fun shouldOverrideUrlLoading(
                                view: WebView,
                                request: WebResourceRequest
                            ): Boolean {
                                val url = request.url

                                val isAllowed =
                                    url.scheme == "https" &&
                                            url.host == ABSENCE_WEBSITE_HOST

                                // true bedeutet: Navigation abbrechen.
                                return !isAllowed
                            }

                            override fun onPageStarted(
                                view: WebView,
                                url: String,
                                favicon: Bitmap?
                            ) {
                                super.onPageStarted(view, url, favicon)
                                isLoading = true
                            }

                            override fun onPageFinished(
                                view: WebView,
                                url: String
                            ) {
                                super.onPageFinished(view, url)

                                val uri = url.toUri()
                                val isCorrectOrigin =
                                    uri.scheme == "https" &&
                                            uri.host == ABSENCE_WEBSITE_HOST

                                if (
                                    isCorrectOrigin &&
                                    !sessionWasInjected
                                ) {
                                    sessionWasInjected = true

                                    val safeToken =
                                        JSONObject.quote(userToken)

                                    val safeUser =
                                        JSONObject.quote(userJson)

                                    val safeIsAdmin =
                                        JSONObject.quote(
                                            isAdmin.toString()
                                        )

                                    view.evaluateJavascript(
                                        """
                                        localStorage.setItem(
                                            "token",
                                            $safeToken
                                        );
                                        localStorage.setItem(
                                            "user",
                                            $safeUser
                                        );
                                        localStorage.setItem(
                                            "isAdmin",
                                            $safeIsAdmin
                                        );
                                        """.trimIndent()
                                    ) {
                                        view.loadUrl(targetUrl)
                                    }
                                } else {
                                    isLoading = false
                                }
                            }
                        }

                        loadUrl(ABSENCE_WEBSITE_ORIGIN)
                    }
                },
                onRelease = { releasedWebView ->
                    webView = null
                    releasedWebView.stopLoading()
                    releasedWebView.destroy()
                }
            )

            if (isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                )
            }
        }
    }
}
