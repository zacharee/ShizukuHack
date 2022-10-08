package moe.shizuku.manager.starter

import android.content.Context
import android.os.Build
import android.os.UserManager
import android.system.ErrnoException
import android.system.Os
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import moe.shizuku.manager.R
import moe.shizuku.manager.application
import moe.shizuku.manager.ktx.createDeviceProtectedStorageContextCompat
import moe.shizuku.manager.ktx.logd
import moe.shizuku.manager.ktx.loge
import rikka.core.os.FileUtils
import java.io.*
import java.nio.file.Files
import java.util.zip.ZipFile

object Starter {

    private var commandInternal = arrayOfNulls<String>(2)

    val dataCommand get() = commandInternal[0]!!

    val sdcardCommand get() = commandInternal[1]!!

    val adbCommand: String
        get() = "adb shell $sdcardCommand"

    fun writeSdcardFilesAsync(context: Context) {
        GlobalScope.launch(Dispatchers.IO) { writeSdcardFiles(context.applicationContext) }
    }

    private fun writeSdcardFiles(context: Context) {
        if (commandInternal[1] != null) {
            logd("already written")
            return
        }

        val um = context.getSystemService(UserManager::class.java)!!
        val unlocked = Build.VERSION.SDK_INT < 24 || um.isUserUnlocked
        if (!unlocked) return

        try {
            val dir = context.getExternalFilesDir(null)?.parentFile ?: return
            val starter = copyStarter(context, "libshizuku.so", File(dir, "starter"))
            val sh = writeScript(context, File(dir, "start.sh"), starter)

            commandInternal[1] = "sh $sh"
            logd(commandInternal[1]!!)

            try {
                Os.chmod(dir.absolutePath, 777 /* 0711 */)
            } catch (e: ErrnoException) {
                e.printStackTrace()
            }

            try {
                Os.chmod(starter, 777 /* 0644 */)
            } catch (e: ErrnoException) {
                e.printStackTrace()
            }
            try {
                Os.chmod(sh, 777 /* 0644 */)
            } catch (e: ErrnoException) {
                e.printStackTrace()
            }
        } catch (e: IOException) {
            loge("write files", e)
        }
    }

    fun writeDataFiles(context: Context, permission: Boolean = false): File? {
        if (commandInternal[0] != null && !permission) {
            logd("already written")
            return null
        }

        val dir = File("/data/data/${application.packageName}") ?: return null

        dir.deleteRecursively()
        dir.mkdir()

        return try {
            val starter = copyStarter(context, "libshizuku.so", File(dir, "starter"))
            val sh = writeScript(context, File(dir, "start.sh"), starter)
            commandInternal[0] = "sh $sh --apk=${context.applicationInfo.sourceDir}"
            logd(commandInternal[0]!!)

            if (permission) {
                try {
                    Runtime.getRuntime().exec("chmod 777 $starter").waitFor()
                } catch (e: ErrnoException) {
                    e.printStackTrace()
                }
                try {
                    Runtime.getRuntime().exec("chmod 777 $sh").waitFor()
                } catch (e: ErrnoException) {
                    e.printStackTrace()
                }
            }

            Log.e("Shizuku", "${dir.listFiles().map { "${it.absolutePath} + ${Files.getPosixFilePermissions(it.toPath())}" }}")

            File(starter)
        } catch (e: IOException) {
            e.printStackTrace()
            loge("write files", e)
            null
        }
    }

    private fun copyStarter(context: Context, input: String, out: File): String {
        val so = "lib/${Build.SUPPORTED_ABIS[0]}/$input"
        val ai = context.applicationInfo

        out.delete()
        Log.e("Shizuku", "file $out")
        out.createNewFile()

        val fos = FileOutputStream(out)
        val apk = ZipFile(ai.sourceDir)
        val entries = apk.entries()
        while (entries.hasMoreElements()) {
            val entry = entries.nextElement() ?: break
            if (entry.name != so) continue

            val buf = ByteArray(entry.size.toInt())
            val dis = DataInputStream(apk.getInputStream(entry))
            dis.readFully(buf)
            FileUtils.copy(ByteArrayInputStream(buf), fos)
            break
        }
        return out.absolutePath
    }

    private fun writeScript(context: Context, out: File, starter: String): String {
        if (!out.exists()) {
            out.createNewFile()
        }
        val `is` = BufferedReader(InputStreamReader(context.resources.openRawResource(R.raw.start)))
        out.delete()
        val os = PrintWriter(FileWriter(out))
        var line: String?
        while (`is`.readLine().also { line = it } != null) {
            os.println(line!!.replace("%%%STARTER_PATH%%%", starter))
        }
        os.flush()
        os.close()
        return out.absolutePath
    }
}
