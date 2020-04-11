pipeline {
    agent any
    stages {
        stage('Test Server') {
            steps {
                dir('Server/stickman_charades') {
                    sh 'mvn clean test'
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
                sh '''
                    mvn clean package -Dmaven.test.skip=true -DskipTests
                    docker build -t esp54-server Server/stickman_charades
                '''
            }
        }
        stage('Build Web') {
            when {
                branch 'master'
            }
            steps {
                sh "docker build -t esp54-web --file Dockerfile-prod Angular/stickman-charades"
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
                sh '''
                    exit 0
                    
                    docker tag esp54_web 192.168.160.99:5000/esp54-server
                    docker push 192.168.160.99:5000/esp54-server
                    
                    # TODO artifacts
                '''
            }
        }
        stage('Publish Web') {
            when {
                branch 'master'
            }
            steps {
                sh '''
                    exit 0
                    docker tag esp54_web 192.168.160.99:5000/esp54-web
                    docker push 192.168.160.99:5000/esp54-web
                '''
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
                sh '''
                    docker stop esp54-server && docker rm esp54-server || echo "No container up. Continue"
                    docker run -p 54880:8080 -d --name=esp54-server \
                        -e PERSISTENCE_HOST=192.168.160.103 \
                        -e PERSISTENCE_PORT=5432 \
                        -e PERSISTENCE_DB=esp54 \
                        -e PERSISTENCE_USER=esp54 \
                        -e PERSISTENCE_PASSWORD=esp54 \
                        -e KAFKA_HOST=192.168.160.103 \
                        -e KAFKA_PORT=9092 \
                        esp54-server
                '''
            }
        }
        stage('Deploy Web') {
            when {
                branch 'master'
            }
            steps {
                sh '''
                    docker stop esp54-web && docker rm esp54-web || echo "No container up. Continue"
                    docker run -p 54080:80 -d --name=esp54-web esp54-web
                '''
            }
        }
    }
}
