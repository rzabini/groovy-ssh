package com.github.rzabini.ssh.expect

import groovy.util.logging.Slf4j

//import deprecated.DefaultOperationsWithExpect

//import deprecated.SessionHandlerWithExpect
import org.hidetake.groovy.ssh.connection.Connection
import org.hidetake.groovy.ssh.connection.ConnectionManager
import org.hidetake.groovy.ssh.core.settings.CompositeSettings
import org.hidetake.groovy.ssh.operation.DefaultOperations
import org.hidetake.groovy.ssh.operation.DryRunOperations
import org.hidetake.groovy.ssh.session.Executor
import org.hidetake.groovy.ssh.session.Plan
import org.hidetake.groovy.ssh.session.SessionHandler

import static org.hidetake.groovy.ssh.util.Utility.callWithDelegate
import static org.hidetake.groovy.ssh.util.Utility.callWithDelegate

@Slf4j
class ExecutorWithExpect extends Executor{

    protected getOperations(Connection connection) {
        new DefaultOperationsWithExpect(connection)
    }

    protected getHandler(DefaultOperations operations, settings) {
        new SessionHandlerWithExpect(operations, settings)
        //SessionHandler.create(operations, settings) //as SessionHandlerExpectTrait

    }

    def <T> List<T> execute(CompositeSettings compositeSettings, List<Plan<T>> plans) {
        if (compositeSettings.dryRun) {
            dryRun(compositeSettings, plans)
        } else {
            wetRun(compositeSettings, plans)
        }
    }

    private <T> List<T> dryRun(CompositeSettings compositeSettings, List<Plan<T>> plans) {
        log.debug("Executing ${plans.size()} session(s) as dry-run")
        plans.collect { plan ->
            def operations = new DryRunOperations(plan.remote)
            callWithDelegate(plan.closure, SessionHandlerWithExpect.create(operations, compositeSettings.operationSettings))
        }
    }

    private <T> List<T> wetRun(CompositeSettings compositeSettings, List<Plan<T>> plans) {
        log.debug("Executing ${plans.size()} session(s)")
        def manager = new ConnectionManager(compositeSettings.connectionSettings)
        try {
            plans.collect { plan ->
                def connection = manager.connect(plan.remote)
                def operations = new DefaultOperationsWithExpect(connection)
                callWithDelegate(plan.closure, SessionHandlerWithExpect.create(operations, compositeSettings.operationSettings))
            }
        } finally {
            manager.waitAndClose()
        }
    }

}




