package com.github.rzabini.ssh.expect

import org.hidetake.groovy.ssh.Ssh
import org.hidetake.groovy.ssh.core.Service

class SshWithExpect extends Ssh {
    static Service newService() {
        new ServiceWithExpect()
    }
}
