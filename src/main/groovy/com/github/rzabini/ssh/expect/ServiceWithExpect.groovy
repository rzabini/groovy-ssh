package com.github.rzabini.ssh.expect

import org.hidetake.groovy.ssh.core.RunHandler
import org.hidetake.groovy.ssh.core.Service
import org.hidetake.groovy.ssh.core.settings.CompositeSettings
import org.hidetake.groovy.ssh.util.Utility

class ServiceWithExpect extends Service{

    final ExecutorWithExpect executorWithExpect=new ExecutorWithExpect()


    @Override
    /**
     * Run a closure.
     *
     * @param closure
     * @return returned value of the last session
     */
    def run(@DelegatesTo(RunHandler) Closure closure) {
        assert closure, 'closure must be given'
        def handler = new RunHandler()
        Utility.callWithDelegate(closure, handler)

        def results = executorWithExpect.execute(CompositeSettings.DEFAULT + settings + handler.settings, handler.sessions)
        results.empty ? null : results.last()
    }

}
