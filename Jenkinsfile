pipeline {
    agent any
    stages {
        stage('Test Server') {
            steps {
                dir('Server/stickman_charades') {
                    sh 'mvn clean verify'
                }
            }
        }
        stage('Test Web') {
            steps {
                dir ('Angular/stickman-charades') {
                    sh 'echo Skipping...'
                }
            }
        }
        stage('Test Desktop') {
            steps {
                dir('Unity/StickmanCharades') {
                    sh 'echo Skipping...'
                }
            }
        }
        stage('Build Server') {
            when {
                branch 'master'
            }
            steps {
                dir('Server/stickman_charades') {
                    sh '''
                        mvn clean package -Dmaven.test.skip=true

                        docker build -t esp54-server .
                    '''
                }
            }
        }
        stage('Build Web') {
            when {
                branch 'master'
            }
            steps {
                dir('Angular/stickman-charades') {
                    sh 'docker build -t esp54-web --file Dockerfile-prod .'
                }
            }
        }
        stage('Build Desktop') {
            when {
                branch 'master'
            }
            steps {
                sh 'echo Skipping...'
            }
        }
        stage('Publish Server') {
            when {
                branch 'master'
            }
            steps {
                dir('Server/stickman_charades') {
                    sh '''
                        mvn clean deploy -Dmaven.test.skip=true

                        docker tag esp54-server 192.168.160.99:5000/esp54-server
                        docker push 192.168.160.99:5000/esp54-server
                    '''
                }
            }
        }
        stage('Publish Web') {
            when {
                branch 'master'
            }
            steps {
                dir('Angular/stickman-charades') {
                    sh '''
                        docker tag esp54-web 192.168.160.99:5000/esp54-web
                        docker push 192.168.160.99:5000/esp54-web
                    '''
                }
            }
        }
        stage('Publish Desktop') {
            when {
                branch 'master'
            }
            steps {
                sh 'echo Skipping...'
            }
        }
        stage('Deploy Server') {
            when {
                branch 'master'
            }
            steps {
                sshagent(credentials: ['esp54-ssh-runtime-vm']) {
                    sh '''
                        docker stop esp54-server && docker rm esp54-server || echo "No container up. Continue"
                        docker pull 192.168.160.99:5000/esp54-server
                        docker run -p 54880:8080 -d --name=esp54-server \
                            -e PERSISTENCE_HOST=db \
                            -e PERSISTENCE_PORT=5432 \
                            -e PERSISTENCE_DB=esp54 \
                            -e PERSISTENCE_USER=esp54 \
                            -e PERSISTENCE_PASSWORD=esp54 \
                            -e KAFKA_HOST=192.168.160.103 \
                            -e KAFKA_PORT=9092 \
                            --network shared_postgres
                            192.168.160.99:5000/esp54-server
                    '''
                }
            }
        }
        stage('Deploy Web') {
            when {
                branch 'master'
            }
            steps {
                sshagent(credentials: ['esp54-ssh-runtime-vm']) {
                    sh '''
                        docker stop esp54-web && docker rm esp54-web || echo "No container up. Continue"
                        docker pull 192.168.160.99:5000/esp54-web
                        docker run -p 54080:80 -d --name=esp54-web 192.168.160.99:5000/esp54-web
                    '''
                }
            }
        }
    }
}
