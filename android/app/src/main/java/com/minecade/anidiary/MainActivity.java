package com.minecade.anidiary;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.graphics.Color;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.getcapacitor.BridgeActivity;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class MainActivity extends BridgeActivity {

    private ActivityResultLauncher<Intent> createBackupDocumentLauncher;
    private String pendingBackupJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Keep Android's status/navigation bars visible, but blend them into
        // AniDiary's dark UI instead of showing the default grey bars.
        configureSystemBars();

        createBackupDocumentLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() != Activity.RESULT_OK || result.getData() == null) {
                        pendingBackupJson = null;
                        notifyWeb("Backup export cancelled.", false);
                        return;
                    }

                    Uri uri = result.getData().getData();
                    if (uri == null || pendingBackupJson == null) {
                        pendingBackupJson = null;
                        notifyWeb("Failed to export backup.", true);
                        return;
                    }

                    try (OutputStream output = getContentResolver().openOutputStream(uri, "w")) {
                        if (output == null) throw new IllegalStateException("Could not open selected file");
                        output.write(pendingBackupJson.getBytes(StandardCharsets.UTF_8));
                        output.flush();
                        notifyWeb("Backup saved successfully!", false);
                    } catch (Exception error) {
                        error.printStackTrace();
                        notifyWeb("Failed to export backup.", true);
                    } finally {
                        pendingBackupJson = null;
                    }
                }
        );

        WebView webView = getBridge().getWebView();
        webView.addJavascriptInterface(new AniDiaryAndroidBridge(), "AndroidAniDiary");

        // Capacitor's default back behavior can finish the Activity when the WebView has
        // no browser-history entry. AniDiary has its own screen model, so ask JavaScript
        // to handle Back first. Only leave the app when JavaScript reports that the
        // dashboard/root screen is already open.
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handleAniDiaryBack();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        configureSystemBars();
    }

    private void configureSystemBars() {
        // Let Android lay out the WebView between the system bars. This keeps the
        // status/navigation bars visible, like a normal Android app.
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);

        int darkBackground = Color.parseColor("#0d0e15");
        getWindow().setStatusBarColor(darkBackground);
        getWindow().setNavigationBarColor(darkBackground);

        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        if (controller != null) {
            // White Android icons/text on the dark bars.
            controller.setAppearanceLightStatusBars(false);
            controller.setAppearanceLightNavigationBars(false);
        }

        // Disable Android's automatic contrast scrims so the bars stay the same
        // dark colour as the AniDiary background.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            getWindow().setNavigationBarContrastEnforced(false);
            getWindow().setStatusBarContrastEnforced(false);
            getWindow().setNavigationBarDividerColor(darkBackground);
        }

        // Make sure the decor view is allowed to use the normal system-window
        // insets rather than immersive/full-screen mode.
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
    }

    private void handleAniDiaryBack() {
        WebView webView = getBridge().getWebView();
        if (webView == null) {
            finish();
            return;
        }

        String script = "(function(){try{" +
                "if(typeof app!=='undefined'&&app&&typeof app.handleNativeBack==='function'){" +
                "return app.handleNativeBack();}" +
                "return false;}catch(e){console.error(e);return false;}})();";

        webView.evaluateJavascript(script, result -> {
            if (!"true".equalsIgnoreCase(result)) {
                finish();
            }
        });
    }

    private void notifyWeb(String message, boolean error) {
        WebView webView = getBridge().getWebView();
        if (webView == null) return;
        String safe = message.replace("\\", "\\\\").replace("'", "\\'");
        String type = error ? "error" : "info";
        webView.post(() -> webView.evaluateJavascript(
                "if(typeof app!=='undefined'&&app){app.showToast('" + safe + "','" + type + "');}",
                null
        ));
    }

    public class AniDiaryAndroidBridge {
        @JavascriptInterface
        public void exportBackup(String json, String filename) {
            if (json == null || filename == null) return;

            runOnUiThread(() -> {
                pendingBackupJson = json;
                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/json");
                intent.putExtra(Intent.EXTRA_TITLE, filename);
                createBackupDocumentLauncher.launch(intent);
            });
        }
    }
}
