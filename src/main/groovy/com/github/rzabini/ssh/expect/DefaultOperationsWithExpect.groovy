package com.github.rzabini.ssh.expect

import com.jcraft.jsch.ChannelShell
import groovy.util.logging.Slf4j
import org.hidetake.groovy.ssh.connection.Connection
import org.hidetake.groovy.ssh.core.settings.OperationSettings
import org.hidetake.groovy.ssh.operation.DefaultOperations

@Slf4j
class DefaultOperationsWithExpect extends DefaultOperations implements OperationsWithExpect {

    private final Connection connection

    DefaultOperationsWithExpect(Connection connection1) {
        super(connection1)
        connection=connection1
    }

    void shellExpect(OperationSettings settings) {
        log.debug("Execute a shellExpect with $settings")

        ChannelShell channel = connection.createShellChannel(settings)
        Expect expectObj = new Expect(channel.getInputStream(), channel.getOutputStream())
        channel.connect()
        settings.interaction.delegate=expectObj
        try{
            settings.interaction.call()
        }
        finally{
            expectObj.close()
            channel.disconnect()
        }
    }

    @Override
    void shellExpect(Closure interaction){
        ChannelShell channel = connection.createShellChannel(null)
        Expect expectObj = new Expect(channel.getInputStream(), channel.getOutputStream())
        channel.connect()
        interaction.delegate=expectObj
        try{
            interaction.call()
        }
        finally{
            expectObj.close()
            channel.disconnect()
        }

    }
}
