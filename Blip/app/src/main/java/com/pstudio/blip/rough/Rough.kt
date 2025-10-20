package com.pstudio.blip.rough

//Box(
//contentAlignment = Alignment.BottomEnd
//) {
//
//    when (message.messageType) {
//        "text" -> {
//            Text(
//                text = message.message,
//                color = if (isSystemInDarkTheme()) Color.White else Color.Black,
//                modifier = modifier.padding(horizontal = 10.dp, vertical = 7.dp),
//                fontFamily = FontFamily.Monospace
//            )
//        }
//
//        "image" -> {
//            val imageModifier = Modifier
//                .sizeIn(minWidth = 300.dp, maxWidth = 300.dp, minHeight = 300.dp, maxHeight = 300.dp)
//                .clip(RoundedCornerShape(8.dp))
//                .clickable {
//                    if (hasLocalUri) {
//                        val file = File(message.localUri ?: return@clickable) // Guard against null
//                        val uri = FileProvider.getUriForFile(
//                            context,
//                            "${context.packageName}.fileprovider",
//                            file
//                        )
//                        openFile(context, uri = Uri.parse(uri.toString()), mimeType = message.mimeType)
//                    } else if (downloadedFile.value != null) {
//                        openFile(context, file = downloadedFile.value!!, mimeType = message.mimeType)
//                    }
//                }
//                .border(4.dp, Color.Black, RoundedCornerShape(8.dp))
//
//            if (hasLocalUri) {
//                AsyncImage(
//                    model = Uri.parse(message.localUri),
//                    contentDescription = null,
//                    contentScale = ContentScale.Crop,
//                    modifier = imageModifier
//                )
//            } else if (downloadedFile.value != null) {
//                AsyncImage(
//                    model = downloadedFile.value,
//                    contentDescription = null,
//                    contentScale = ContentScale.Crop,
//                    modifier = imageModifier
//                )
//            } else {
//                Box(modifier = imageModifier.background(Color.DarkGray)) {
//                    if (downloadingState.value) {
//                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
//                    } else if (isReceiver) {
//                        IconButton(
//                            onClick = {
//                                coroutineScope.launch {
//                                    downloadingState.value = true
//                                    val result = downloader.downloadAndDecrypt(
//                                        message.message, message.mediaIv, message.mimeType, message.fileName
//                                    )
//                                    downloadingState.value = false
//                                    result.onSuccess { file ->
//                                        downloadedFile.value = file
//                                        val absPath = file.absolutePath
//                                        chatViewModel.setReceiverUri(message.senderId, message.receiverId, message.messageId, absPath)
//                                    }
//                                }
//                            },
//                            modifier = Modifier.align(Alignment.Center)
//                        ) {
//                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Download", tint = Color.White)
//                        }
//                    }
//                }
//            }
//        }
//
//        else -> {
//            Row(
//                modifier = Modifier
//                    .padding(10.dp)
//                    .background(Color.DarkGray, shape = RoundedCornerShape(8.dp))
//                    .clickable {
//                        if (hasLocalUri) {
//                            openFile(context, uri = Uri.parse(message.localUri), mimeType = message.mimeType)
//                        } else if (downloadedFile.value != null) {
//                            openFile(context, file = downloadedFile.value!!, mimeType = message.mimeType)
//                        }
//                    },
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
//                Text(
//                    text = message.fileName,
//                    color = Color.White,
//                    maxLines = 1,
//                    overflow = TextOverflow.Ellipsis,
//                    modifier = Modifier.padding(start = 8.dp)
//                        .weight(0.7f)
//                )
//
//                if (isReceiver && downloadedFile.value == null && !downloadingState.value) {
//                    IconButton(
//                        onClick = {
//                            coroutineScope.launch {
//                                downloadingState.value = true
//                                val result = downloader.downloadAndDecrypt(
//                                    message.message, message.mediaIv, message.mimeType, message.fileName
//                                )
//                                downloadingState.value = false
//                                result.onSuccess { downloadedFile.value = it }
//                            }
//                        },
//                        modifier = Modifier.padding(start = 8.dp)
//                            .weight(0.3f)
//                    ) {
//                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Download", tint = Color.White)
//                    }
//                }
//
//                if (downloadingState.value) {
//                    CircularProgressIndicator(
//                        modifier = Modifier
//                            .size(16.dp)
//                            .padding(start = 8.dp),
//                        strokeWidth = 2.dp
//                    )
//                }
//            }
//        }
//    }
//    if (isSenderMe) {
//        Box(
//            modifier = modifier
//                .padding(4.dp)
//                .size(7.dp)
//                .background(color, shape = CircleShape)
//        )
//    }
//}