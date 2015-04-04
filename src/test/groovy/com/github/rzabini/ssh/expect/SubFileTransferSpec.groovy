package com.github.rzabini.ssh.expect

import org.hidetake.groovy.ssh.Ssh
import org.hidetake.groovy.ssh.server.FileTransferSpec

class SubFileTransferSpec extends FileTransferSpec {

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
