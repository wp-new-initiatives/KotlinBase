pipeline {
    agent any
    options {
        ansiColor(colorMapName: 'XTerm')
        timestamps()
    }
    environment {
        REGISTRY = 'docker.util.pages/platform/onepages/kotlinproject'
        SHORT_COMMIT = "${GIT_COMMIT[0..6]}"
    }

    stages {
        stage('Build Test and Push') {
            when {
                expression { env.TAG_NAME == null }
            }
            steps {
                script {
                    docker_image = docker.build (registry + ":$SHORT_COMMIT")
                    docker_image.push()
                }
            }
        }
        stage('deploy to k8s - stage') {
            when {
                expression {
                    return "${BRANCH_NAME}" == 'staging'
                }
            }
            steps {
                build job: 'platform deployment', parameters: [
                    [$class: 'StringParameterValue', name: 'DEPLOYMENT', value: "kotlinproject"],
                    [$class: 'StringParameterValue', name: 'K8S_CONTEXT', value: "stage"],
                    [$class: 'StringParameterValue', name: 'TAG', value: "$SHORT_COMMIT"]
                ]
            }
        }
        stage('deploy to k8s - prod') {
            when {
                allOf {
                    expression { env.TAG_NAME != null }
                    expression {
                        def fromStaging = sh( returnStatus: true, script: "git branch --contains refs/tags/$TAG_NAME | grep staging")
                        return fromStaging
                    }
                }
            }
            steps {
                build job: 'platform deployment', parameters: [
                    [$class: 'StringParameterValue', name: 'DEPLOYMENT', value: "kotlinproject"],
                    [$class: 'StringParameterValue', name: 'K8S_CONTEXT', value: "prod"],
                    [$class: 'StringParameterValue', name: 'TAG', value: "$SHORT_COMMIT"]
                ]
            }
        }
    }
}
