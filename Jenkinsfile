pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "spring-to-do"
        DOCKER_TAG = "latest"
        DOCKER_CREDENTIALS = credentials('dockerhub-creds')
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    if (env.CHANGE_ID) {
                        checkout([
                            $class: 'GitSCM',
                            branches: [[name: 'refs/pull/${CHANGE_ID}/head']],
                            extensions: [[$class: 'CleanCheckout']],
                            userRemoteConfigs: [[url: 'https://github.com/SattarovAA/SpringToDo.git']]
                        ])
                    } else {
                        checkout scm
                    }
                }
            }
        }

        stage('Build & Test') {
            steps {
                bat 'mvn clean install'
            }
        }

        stage('Build Docker Image') {
            when {
                allOf {
                    branch 'main'
                    not { changeRequest() }
                }
            }
            steps {
                script {
                    docker.build("${DOCKER_IMAGE}:${DOCKER_TAG}")
                }
            }
        }

        stage('Push to Docker Hub') {
            when {
                allOf {
                    branch 'main'
                    not { changeRequest() }
                }
            }
            steps {
                script {
                    docker.withRegistry('https://registry.hub.docker.com', 'dockerhub-creds') {
                        docker.image("${DOCKER_IMAGE}:${DOCKER_TAG}").push()
                    }
                }
            }
        }
    }

    post {
        always {
            echo "Pipeline завершен: ${currentBuild.result ?: 'SUCCESS'}"
        }
        success {
            script {
                if (env.CHANGE_ID) {
                    githubNotify status: 'SUCCESS', context: 'CI/PR Build'
                }
            }
        }
        failure {
            script {
                if (env.CHANGE_ID) {
                    githubNotify status: 'FAILURE', context: 'CI/PR Build'
                }
            }
        }
    }
}