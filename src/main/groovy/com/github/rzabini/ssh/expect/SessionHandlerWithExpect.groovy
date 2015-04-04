package com.github.rzabini.ssh.expect

import groovy.util.logging.Slf4j
import org.hidetake.groovy.ssh.core.Remote
import org.hidetake.groovy.ssh.core.settings.OperationSettings
import org.hidetake.groovy.ssh.extension.CoreExtensions
import org.hidetake.groovy.ssh.operation.Operations
import org.hidetake.groovy.ssh.operation.SftpOperations
import org.hidetake.groovy.ssh.session.SessionHandler

@Slf4j
class SessionHandlerWithExpect implements CoreExtensions {

    OperationsWithExpect operations
    private final OperationSettings operationSettings

    SessionHandlerWithExpect(OperationsWithExpect operations, OperationSettings operationSettings) {
        this.operations=operations
        this.operationSettings=operationSettings
    }

    void shellExpect(HashMap settings) {
        assert settings != null, 'settings must not be null'
        log.info("Execute a shell with settings ($settings)")
        operations.shellExpect(operationSettings + new OperationSettings(settings))
    }

    void shellExpect(Closure interaction){
        operations.shellExpect(interaction)

    }

    static def create(Operations operations, OperationSettings operationSettings) {
        def handler = new SessionHandlerWithExpect(operations, operationSettings)
        operationSettings.extensions.inject(handler) { applied, extension ->
            if (extension instanceof Class) {
                log.debug("Applying extension: $extension")
                applied.withTraits(extension)
            } else if (extension instanceof Map<String, Closure>) {
                extension.each { String name, Closure implementation ->
                    log.debug("Applying extension method: $name")
                    applied.metaClass[name] = implementation
                }
                applied
            } else {
                log.error("Ignored unknown extension: $extension")
                applied
            }
        }
    }

    @Override
    Operations getOperations() {
        operations
    }

    @Override
    OperationSettings getOperationSettings() {
        operationSettings
    }

    @Override
    Remote getRemote() {
        operations.remote
    }

    @Override
    def sftp(@DelegatesTo(SftpOperations) Closure closure) {
        assert closure, 'closure must be given'
        operations.sftp(closure)
    }
}
