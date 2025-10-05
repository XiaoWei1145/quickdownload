package com.multicloud.downloader.download

import android.os.Environment
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.io.RandomAccessFile
import java.util.concurrent.TimeUnit

class DownloadManager private constructor() {
    private val _tasks = MutableStateFlow<List<DownloadTask>>(emptyList())
    private val tasks: StateFlow<List<DownloadTask>> = _tasks.asStateFlow()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    
    // 默认下载目录
    private var downloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    
    // OkHttp client for downloading
    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()
    
    companion object {
        private var INSTANCE: DownloadManager? = null
        
        fun getInstance(): DownloadManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: DownloadManager().also { INSTANCE = it }
            }
        }
    }
    
    fun getTasksFlow(): StateFlow<List<DownloadTask>> = tasks
    
    fun setDownloadDirectory(directory: File) {
        downloadDirectory = directory
    }
    
    fun addTask(task: DownloadTask) {
        coroutineScope.launch {
            val currentTasks = _tasks.value.toMutableList()
            currentTasks.add(task)
            _tasks.emit(currentTasks)
            
            // Start downloading
            startDownload(task)
        }
    }
    
    private fun startDownload(task: DownloadTask) {
        coroutineScope.launch {
            try {
                task.status = 1 // 下载中
                
                val request = Request.Builder()
                    .url(task.url)
                    .build()
                
                httpClient.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        task.status = 4 // 错误
                        return@launch
                    }
                    
                    val body = response.body
                    if (body == null) {
                        task.status = 4 // 错误
                        return@launch
                    }
                    
                    // Update file size if not set
                    if (task.fileSize == 0L) {
                        task.fileSize = body.contentLength()
                    }
                    
                    // Create file
                    val file = File(downloadDirectory, task.fileName)
                    
                    // Write to file
                    val buffer = ByteArray(8192)
                    var totalBytesRead = 0L
                    
                    body.byteStream().use { inputStream ->
                        RandomAccessFile(file, "rw").use { outputFile ->
                            var bytesRead: Int
                            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                                outputFile.write(buffer, 0, bytesRead)
                                totalBytesRead += bytesRead
                                task.downloadedSize = totalBytesRead
                                
                                // Update progress
                                val currentTasks = _tasks.value.toMutableList()
                                val index = currentTasks.indexOfFirst { it.id == task.id }
                                if (index != -1) {
                                    currentTasks[index] = task.copy()
                                    _tasks.emit(currentTasks)
                                }
                            }
                        }
                    }
                    
                    task.status = 3 // 完成
                    // Update final state
                    val currentTasks = _tasks.value.toMutableList()
                    val index = currentTasks.indexOfFirst { it.id == task.id }
                    if (index != -1) {
                        currentTasks[index] = task.copy()
                        _tasks.emit(currentTasks)
                    }
                }
            } catch (e: Exception) {
                Log.e("DownloadManager", "Download failed", e)
                task.status = 4 // 错误
                
                // Update error state
                val currentTasks = _tasks.value.toMutableList()
                val index = currentTasks.indexOfFirst { it.id == task.id }
                if (index != -1) {
                    currentTasks[index] = task.copy()
                    coroutineScope.launch {
                        _tasks.emit(currentTasks)
                    }
                }
            }
        }
    }
    
    fun pauseTask(id: String) {
        coroutineScope.launch {
            val currentTasks = _tasks.value.toMutableList()
            val task = currentTasks.find { it.id == id }
            task?.let {
                it.status = 2 // 暂停
                val index = currentTasks.indexOfFirst { t -> t.id == id }
                if (index != -1) {
                    currentTasks[index] = it.copy()
                    _tasks.emit(currentTasks)
                }
            }
        }
    }
    
    fun resumeTask(id: String) {
        coroutineScope.launch {
            val currentTasks = _tasks.value.toMutableList()
            val task = currentTasks.find { it.id == id }
            task?.let {
                // In a real implementation, we would resume from where it left off
                // For now, we'll just restart the download
                it.status = 1 // 下载中
                val index = currentTasks.indexOfFirst { t -> t.id == id }
                if (index != -1) {
                    currentTasks[index] = it.copy()
                    _tasks.emit(currentTasks)
                    startDownload(it)
                }
            }
        }
    }
    
    fun cancelTask(id: String) {
        coroutineScope.launch {
            val currentTasks = _tasks.value.toMutableList()
            val task = currentTasks.find { it.id == id }
            task?.let {
                // Delete the partially downloaded file if exists
                val file = File(downloadDirectory, it.fileName)
                if (file.exists()) {
                    file.delete()
                }
            }
            
            val updatedTasks = currentTasks.filter { it.id != id }
            _tasks.emit(updatedTasks)
        }
    }
    
    fun retryTask(id: String) {
        resumeTask(id)
    }
}