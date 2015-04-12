package com.github.rzabini.ssh.expect

import org.apache.sshd.SshServer
import org.apache.sshd.common.Factory
import org.apache.sshd.server.CommandFactory
import org.apache.sshd.server.PasswordAuthenticator
import org.codehaus.groovy.tools.Utilities
import org.hidetake.groovy.ssh.server.ServerIntegrationTest
import org.hidetake.groovy.ssh.server.SshServerMock
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static org.hidetake.groovy.ssh.server.SshServerMock.commandWithExit
import static org.hidetake.groovy.ssh.server.SshServerMock.commandWithExit
import static org.hidetake.groovy.ssh.server.SshServerMock.commandWithExit

@org.junit.experimental.categories.Category(ServerIntegrationTest)
class ExpectSpec extends Specification {

    SshServer server
    CommandExecutor commandExecutor=Mock(CommandExecutor)

    ServiceWithExpect ssh
    def PROMPT='$'

    @Rule
    TemporaryFolder temporaryFolder

    def setup() {
        startServer()

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

    private startServer() {
        server = SshServerMock.setUpLocalhostServer()
        server.commandFactory = Mock(CommandFactory)

        server.shellFactory = Mock(Factory){
            create() >> new StubShell(commandExecutor, PROMPT)
        }
        server.passwordAuthenticator = Mock(PasswordAuthenticator) {
            (0.._) * authenticate('someuser', 'somepassword', _) >> true
        }
        server.start()
    }

    def cleanup() {
        server.stop(true)
    }

    def "can expect for prompt"() {
        when:
        ssh.run {
            session(ssh.remotes.testServer) {
                shellExpect {
                    expectOrThrow 1, PROMPT
                }
            }
        }

        then:
        notThrown(Exception)

    }


    def "can send command and expect a result"() {
        when:

        ssh.run {
            session(ssh.remotes.testServer) {
                shellExpect {
                    send 'hello server'
                    expectOrThrow 1,'please enter password:'
                    send 'Welcome1'
                    expectOrThrow 1, 'password OK'
                }
            }
        }

        then:
        1 * commandExecutor.processCommand("hello server") >> 'please enter password:'
        commandExecutor.processCommand("Welcome1") >> 'password OK'
        commandExecutor.processCommand("Welcome2") >> 'wrong password'
        notThrown Exception
    }

    def "throws exception when result not found in specified time"() {
        when:

        ssh.run {
            session(ssh.remotes.testServer) {
                shellExpect {
                    send 'hello server'
                    expectOrThrow 1,'please enter password:'
                    send 'Welcome2'
                    expectOrThrow 1, 'password OK'
                }
            }
        }

        then:
        1 * commandExecutor.processCommand("hello server") >> 'please enter password:'
        commandExecutor.processCommand("Welcome1") >> 'password OK'
        commandExecutor.processCommand("Welcome2") >> 'wrong password'
        thrown Expect.TimeoutException
    }

    def "can specify a script as extension"() {
        def doLogin = { password ->
            shellExpect {
                send 'hello server'
                expectOrThrow 3,'please enter password:'
                send password
                expectOrThrow 3, "password OK"
            }
        }
        ssh.settings {
            extensions.add login: doLogin
        }
        when:
        ssh.run {
            session(ssh.remotes.testServer) {
                login('Welcome1')
            }
        }
        then:
        commandExecutor.processCommand("hello server") >> 'please enter password:'
        commandExecutor.processCommand("Welcome1") >> 'password OK'
        notThrown Exception
    }

}
