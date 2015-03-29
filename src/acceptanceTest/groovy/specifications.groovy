final sshKey = ['id_ecdsa', 'id_rsa'].collect { new File("${System.getProperty('user.home')}/.ssh/$it") }.find { it.exists() }
println "Using SSH key: $sshKey"

ssh.remotes {
    localhost {
        host = 'localhost'
        user = System.getProperty('user.name')
        identity = sshKey
    }
}

def x = randomInt()
def y = randomInt()
ssh.run {
    session(ssh.remotes.localhost) {
        execute "expr $x + $y"
    }
} as int == (x + y)

static randomInt(int max = 10000) {
    (Math.random() * max) as int
}
