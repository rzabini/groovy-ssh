package com.github.rzabini.ssh.expect

import org.hidetake.groovy.ssh.server.CommandExecutionSpec
import org.hidetake.groovy.ssh.server.FileTransferSpec

class SubCommandExecutionSpec extends CommandExecutionSpec {

    def setup() {
        ssh = SshWithExpect.newService()
        ssh.settings {
            knownHosts = allowAnyHosts
        }
        ssh.remotes {
            testServer {
                host = server.host
                port = server.port
                user = 'someuser'
                password = 'somepassword'
            }
        }
    }

}
