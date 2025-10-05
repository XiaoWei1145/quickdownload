package com.multicloud.downloader.ui

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multicloud.downloader.core.ParserFactory
import com.multicloud.downloader.download.DownloadManager
import com.multicloud.downloader.download.DownloadTask
import com.multicloud.downloader.ui.createDirectoryPickerIntent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

class DownloadViewModel : ViewModel() {
    private val _linkInput = MutableStateFlow("")
    val linkInput: StateFlow<String> = _linkInput.asStateFlow()
    
    private val _downloadDirectory = MutableStateFlow(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
    )
    val downloadDirectory: StateFlow<String> = _downloadDirectory.asStateFlow()
    
    private val downloadManager = DownloadManager.getInstance()
    private val parserFactory = ParserFactory.getInstance()
    
    val downloadTasks = downloadManager.getTasksFlow()
    
    fun setLink(link: String) {
        _linkInput.value = link
    }
    
    fun parseAndDownload() {
        viewModelScope.launch {
            val link = _linkInput.value
            if (link.isEmpty()) return@launch
            
            val parser = parserFactory.getParser(link)
            if (parser != null) {
                try {
                    val result = parser.parse(link)
                    if (result != null) {
                        // 创建下载任务
                        val task = DownloadTask(
                            id = UUID.randomUUID().toString(),
                            fileName = result.fileName,
                            url = result.directLink,
                            fileSize = result.fileSize ?: 0L
                        )
                        
                        // 添加到下载管理器
                        downloadManager.addTask(task)
                        // 重置输入框
                        _linkInput.value = ""
                    }
                } catch (e: Exception) {
                    // 处理解析错误
                    e.printStackTrace()
                }
            }
        }
    }
    
    fun pauseTask(id: String) {
        downloadManager.pauseTask(id)
    }
    
    fun resumeTask(id: String) {
        downloadManager.resumeTask(id)
    }
    
    fun cancelTask(id: String) {
        downloadManager.cancelTask(id)
    }
    
    fun retryTask(id: String) {
        downloadManager.retryTask(id)
    }
    
    fun pickDownloadDirectory(context: Context) {
        // This would be called from the UI to launch the directory picker
        // Implementation would be in the Composable
    }
    
    fun setDownloadDirectory(path: String) {
        _downloadDirectory.value = path
    }
}