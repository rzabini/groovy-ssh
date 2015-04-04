package com.github.rzabini.ssh.expect

import org.hidetake.groovy.ssh.core.settings.OperationSettings
import org.hidetake.groovy.ssh.operation.Operations

interface OperationsWithExpect extends Operations {
    void shellExpect(OperationSettings operationSettings)

    void shellExpect(Closure interaction)
}