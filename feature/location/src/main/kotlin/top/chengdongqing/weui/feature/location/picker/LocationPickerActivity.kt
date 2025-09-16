package top.chengdongqing.weui.feature.location.picker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import top.chengdongqing.weui.core.ui.theme.WeUITheme
import top.chengdongqing.weui.feature.location.data.model.LocationItem

class LocationPickerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WeUITheme {
                WeLocationPicker(onCancel = { finish() }) { location ->
                    val intent = Intent().apply {
                        putExtra("location", location)
                    }
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
        }
    }

    companion object {
        fun newIntent(context: Context) = Intent(context, LocationPickerActivity::class.java)
    }
}

@Composable
fun rememberPickLocationLauncher(onChange: (LocationItem) -> Unit): () -> Unit {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    getParcelableExtra("location", LocationItem::class.java)?.let(onChange)
                } else {
                    @Suppress("DEPRECATION")
                    (getParcelableExtra("location") as? LocationItem)?.let(onChange)
                }
            }
        }
    }

    return {
        launcher.launch(LocationPickerActivity.newIntent(context))
    }
}