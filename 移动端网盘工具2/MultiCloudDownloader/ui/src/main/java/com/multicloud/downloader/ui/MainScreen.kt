package com.multicloud.downloader.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.multicloud.downloader.download.DownloadTask

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: DownloadViewModel = viewModel()) {
    val context = LocalContext.current
    val linkInput by viewModel.linkInput.collectAsState()
    val downloadTasks by viewModel.downloadTasks.collectAsState()
    val downloadDirectory by viewModel.downloadDirectory.collectAsState()
    
    var showSettings by remember { mutableStateOf(false) }
    
    if (showSettings) {
        SettingsDialog(
            currentDirectory = downloadDirectory,
            onDirectoryPicker = { viewModel.pickDownloadDirectory(context) },
            onDismiss = { showSettings = false }
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("多云下载器") },
                actions = {
                    IconButton(onClick = { showSettings = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "设置")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = linkInput,
                onValueChange = { viewModel.setLink(it) },
                label = { Text("粘贴网盘链接") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
            
            Button(
                onClick = {
                    if (linkInput.isNotEmpty()) {
                        viewModel.parseAndDownload()
                    } else {
                        Toast.makeText(context, "请输入链接", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text("解析并下载")
            }
            
            Text(
                text = "下载列表",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            DownloadList(downloadTasks, viewModel)
        }
    }
}

@Composable
fun SettingsDialog(
    currentDirectory: String,
    onDirectoryPicker: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("设置") },
        text = {
            Column {
                Text(
                    text = "下载目录: $currentDirectory",
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Button(onClick = onDirectoryPicker) {
                    Text("选择下载目录")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("确定")
            }
        }
    )
}

@Composable
fun DownloadList(tasks: List<DownloadTask>, viewModel: DownloadViewModel) {
    LazyColumn {
        items(tasks) { task ->
            DownloadItem(task, viewModel)
            Divider()
        }
    }
}

@Composable
fun DownloadItem(task: DownloadTask, viewModel: DownloadViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = task.fileName,
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = task.progress / 100f,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${task.progress}%",
                    style = MaterialTheme.typography.bodySmall
                )
                
                Row {
                    when (task.status) {
                        0 -> { // 等待
                            IconButton(onClick = { viewModel.resumeTask(task.id) }) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "开始")
                            }
                        }
                        1 -> { // 下载中
                            IconButton(onClick = { viewModel.pauseTask(task.id) }) {
                                Icon(Icons.Default.Pause, contentDescription = "暂停")
                            }
                        }
                        2 -> { // 暂停
                            IconButton(onClick = { viewModel.resumeTask(task.id) }) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "继续")
                            }
                        }
                        3 -> { // 完成
                            IconButton(onClick = { /* TODO: 打开文件 */ }) {
                                Icon(Icons.Default.Check, contentDescription = "完成")
                            }
                        }
                        4 -> { // 错误
                            IconButton(onClick = { viewModel.retryTask(task.id) }) {
                                Icon(Icons.Default.Refresh, contentDescription = "重试")
                            }
                        }
                    }
                    
                    IconButton(onClick = { viewModel.cancelTask(task.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "删除")
                    }
                }
            }
        }
    }
}