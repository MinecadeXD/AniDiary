package com.minecade.anidiary;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

        // AniDiary is designed as a full-screen app. Hide Android's grey status and
        // navigation bars, but allow them to appear temporarily with an edge swipe.
        enableImmersiveMode();

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
        enableImmersiveMode();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) enableImmersiveMode();
    }

    private void enableImmersiveMode() {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        if (controller != null) {
            controller.hide(WindowInsetsCompat.Type.statusBars() | WindowInsetsCompat.Type.navigationBars());
            controller.setSystemBarsBehavior(
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            );
        }

        // Avoid Android adding a contrast/scrim background when the navigation bar
        // temporarily appears.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            getWindow().setNavigationBarContrastEnforced(false);
            getWindow().setStatusBarContrastEnforced(false);
        }
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
