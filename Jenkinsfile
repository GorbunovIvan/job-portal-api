pipeline {
    agent any

    environment {
        APP_NAME = 'job-portal-api'  // Application name
        DOCKER_REGISTRY = 'ivangorbunovv'  // Docker registry
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'master', url: 'https://github.com/GorbunovIvan/job-portal-api'
            }
        }

        stage('Test') {
            steps {
                bat "gradle clean test"
            }
        }

        stage('Build') {
            steps {
                // Package the Spring Boot app
                bat "gradle bootJar"
            }
        }

        stage('Docker Build Image') {
            steps {
                script {
                    // Get the short Git commit hash
                    def commitHash = bat(script: "git rev-parse HEAD", returnStdout: true).trim().split("\n")[-1].trim().substring(0, 7).trim()

                    // Docker image name with the commit hash in the tag
                    def imageTag = "${DOCKER_REGISTRY}/${APP_NAME}:${commitHash}"

                    // Use Jenkins credentials for Docker login and then build and tag Docker image
                    withCredentials([usernamePassword(credentialsId: 'docker-hub-credentials', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                        bat "docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD"
                        bat "docker build -t ${imageTag} ."
                    }
                }
            }
        }

        stage('Docker Push to Docker Hub') {
            steps {
                script {
                    // Get the short Git commit hash
                    def commitHash = bat(script: "git rev-parse HEAD", returnStdout: true).trim().split("\n")[-1].trim().substring(0, 7).trim()

                    // Docker image name with the commit hash in the tag
                    def imageTag = "${DOCKER_REGISTRY}/${APP_NAME}:${commitHash}"

                    // Use Jenkins credentials for Docker login and then build and tag Docker image
                    withCredentials([usernamePassword(credentialsId: 'docker-hub-credentials', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                        bat "docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD"
                        bat "docker push ${imageTag}"
                    }
                }
            }
        }

        stage('Docker-compose Deploy') {
            steps {
                script {
                    // Deploy a docker image using docker-compose

                    def image = "${DOCKER_REGISTRY}/${APP_NAME}"
                    def commitHash = bat(script: "git rev-parse HEAD", returnStdout: true).trim().split("\n")[-1].trim().substring(0, 7).trim()
                    def imageTag = "${image}:${commitHash}"
                    def imageTagLatest = "${image}:latest"
                    
                    // Pull the repository from Docker Hub
                    bat "docker pull ${imageTag}"
                    
                    // Change the image tag, so that the "docker-compose.yml" file can find the image
                    bat "docker tag ${imageTag} ${imageTagLatest}"
                    
                    // Path to the local docker-compose.yml
                    def composeFilePath = "${pwd()}\\docker-compose.yml"
                
                    // Run docker-compose with the appropriate image tag
                    bat "docker-compose -f ${composeFilePath} build"
                    bat "docker-compose -f ${composeFilePath} up -d"
                }
            }
        }
    }

    post {
        always {
            // Clean workspace
            cleanWs()
        }
        success {
            // Notify success
            echo "Deployment of ${APP_NAME} succeeded!"
        }
        failure {
            // Notify failure
            echo "Deployment of ${APP_NAME} failed!"
        }
    }
}
