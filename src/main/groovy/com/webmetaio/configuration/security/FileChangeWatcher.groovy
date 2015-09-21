package com.webmetaio.configuration.security

import groovy.util.logging.Slf4j

import java.nio.file.*

import static java.nio.file.StandardWatchEventKinds.*

@Slf4j
class FileChangeWatcher implements Closeable {
  final Path filePath
  final Closure eventHandler
  private long lastModified
  private WatchService watchService

  FileChangeWatcher(String filename, Closure eventHandler, WatchService watchService = null) {
    this(Paths.get(filename), eventHandler, watchService)
  }

  FileChangeWatcher(Path filePath, Closure eventHandler, WatchService watchService = null) {
    this.filePath = filePath
    this.watchService = watchService = watchService ?: filePath.fileSystem.newWatchService()
    this.eventHandler = eventHandler
    this.watchService = watchService

    log.info("FileWatcher for: ${filePath.parent} and ${filePath}")
    filePath.parent.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY, OVERFLOW)

    Thread.startDaemon("FileChangeWatcher(${filePath.fileName})") {
      try {
        WatchKey watchKey = watchService.take()
        while (watchKey != null) {
          watchKey.pollEvents().each {
            if (triggerUpdate(it)) {
              fireEvent(it)
            }
          }
          boolean valid = watchKey.reset()
          if (!valid) {
            log.info("FileChangeWatcher for ${filePath} closed")
            return
          }
          try {
            watchKey = watchService.take()
          } catch (InterruptedException ignored) {
            log.info("FileChangeWatcher for ${filePath} interrupted")
            return
          }
        }
      } catch (Throwable throwable){
        log.error("Unhandled throwable, closing FileChangeWatcher", throwable)
        try {
          close()
        } catch (Exception ignore) {
          // throw the original exception instead
        }
        throw throwable
      }
    }
  }

  private boolean triggerUpdate(WatchEvent watchEvent) {
    if (watchEvent.kind() == OVERFLOW) {
      return updateLastModified(filePath) != lastModified
    } else {
      return filePath.equals(filePath.resolveSibling(watchEvent.context() as Path))
    }
  }

  private int updateLastModified(Path filePath) {
    def file = filePath.toFile()
    lastModified = file.exists() ? file.lastModified() : 0
  }

  private void fireEvent(WatchEvent watchEvent) {
    try {
      if (eventHandler.maximumNumberOfParameters == 0) {
        eventHandler.call()
      } else {
        eventHandler.call(watchEvent)
      }
    } catch (Exception e) {
      log.warn("Exception firing event", e)
    }
  }

  @Override
  void close() throws IOException {
    watchService.close()
  }
}
