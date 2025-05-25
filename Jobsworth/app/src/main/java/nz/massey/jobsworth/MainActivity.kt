@file:Suppress("DEPRECATION")

package nz.massey.jobsworth

import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.app.JobIntentService
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.jvm.java


@Composable
fun Main(modifier: Modifier = Modifier, startIntent:()->Unit, startJobIntent:()->Unit,
         startJobService:()->Unit, playSound:()->Unit, startWorkManager:()->Unit,
         startFgService:()->Unit) {
    Column(modifier = modifier) {
        Button(onClick = startIntent ) {Text("Start Intent Service")}
        Button(onClick = startFgService ) {Text("Start Foreground Service")}
        Button(onClick = startJobIntent ) {Text("Start Job Intent Service")}
        Button(onClick = startJobService ) {Text("Start Job Service")}
        Button(onClick = startWorkManager ) {Text("Start Work Manager")}
        Button(onClick = playSound ) {Text("Play Sound")}
    }
}

class MainActivity : ComponentActivity() {
    lateinit var mMp: MediaPlayer
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            Main(startIntent = {
                    startService(
                        Intent(
                            this,
                            MyIntentService::class.java
                        )
                    )
                },
                startJobIntent = {
                    JobIntentService.enqueueWork(this,
                        MyJobIntentService::class.java,
                        159336,
                        Intent())
                },
                startJobService = {
                    MyJobService.scheduleJob(this)
                },
                playSound = {
                    CoroutineScope(Dispatchers.Main).launch {
                        if (mMp.isPlaying) mMp.pause() else mMp.start()
                    }
                },
                startWorkManager = {
                    val workRequest = OneTimeWorkRequestBuilder<MyWorker>().build()
                    WorkManager.getInstance(this).enqueue(workRequest)
                },
                startFgService = {
                    startForegroundService(Intent(this,MyFgService::class.java))
                }
            )
        }
        if(Build.VERSION.SDK_INT>=33) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf<String>(android.Manifest.permission.POST_NOTIFICATIONS), 0)
            }
        }

        mMp = MediaPlayer.create(this, R.raw.example)
        mMp.isLooping = true

    }

    protected override fun onPause() {
        super.onPause()
        mMp.pause()
    }
}