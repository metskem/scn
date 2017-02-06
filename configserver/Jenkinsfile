#!/usr/bin/env groovy
def TEST_SPACE = "test-space"
def PROD_SPACE = "prod-space"

node {
    withCredentials([
            [$class: 'UsernamePasswordMultiBinding', credentialsId: 'cf-test-user', usernameVariable: 'CF_USER', passwordVariable: 'CF_PASSWORD']
    ]) {
        CF_API = 'https://api.local.pcfdev.io'
        CF_ORG = 'pcfdev-org'
        CF_USER = env.CF_USER
        CF_PASSWORD = env.CF_PASSWORD
        VERSION = "1.0.${env.BUILD_NUMBER}"
    }

    def mvnHome = tool 'default maven'

    stage('Checkout') {
        executeWithNotification 'Unable to checkout the project', {
            checkout scm
        }
    }

    stage('Build') {
        executeWithNotification 'Build failed. Unable to package the artifact', {
            sh "${mvnHome}/bin/mvn clean package"
        }
    }

    CF_HOME = sh(script: 'echo /tmp/CF_HOME.$$', returnStdout: true).trim()
    sh 'mkdir ' + CF_HOME
    sh 'echo "current BRANCH_NAME: "' + env.BRANCH_NAME + ", BUILD_NUMBER: " + env.BUILD_NUMBER + ", JOB_NAME: " + env.JOB_NAME

    withEnv(["CF_HOME=${CF_HOME}"]) {

        if (env.BRANCH_NAME == "develop") {
            stage('Prepare for test') {
                executeWithNotification 'Unable to prepare Test', {
                    login(TEST_SPACE)
                }
            }
            stage('Deploy to Test') {
                executeWithNotification 'Unable to deploy to Test', {
                    sh 'cf push'
                }
            }
        }
        if (env.BRANCH_NAME == "master") {
            stage('Prepare for production') {
                executeWithNotification 'Unable to prepare Prod', {
                    login(PROD_SPACE)
                }
            }
            stage('Deploy to production') {
                executeWithNotification 'Unable to deploy to Prod', {
                    sh 'cf push --hostname configserver'
                }
            }
        }
    }
    sh 'rm -r ' + CF_HOME
    notify('good', 'Build succeeded')
}

def login(space) {
    sh "set +x; cf login --skip-ssl-validation -a ${CF_API} -u ${CF_USER} -p ${CF_PASSWORD} -o ${CF_ORG} -s ${space}"
}

def createService(registryServiceName, plan, customServiceName) {
    if (!serviceExists(customServiceName)) {
        def serviceParams = sprintf('%1$s %2$s %3$s', [registryServiceName, plan, customServiceName])
        sh 'cf create-service ' + serviceParams
    } else {
        space = sh(script: 'cf target | sed -n -e 5p | sed "s/ //g"', returnStdout: true).trim()
        echo "Service with name '${customServiceName}' already exists in '${space}', skip creating"
    }
}

def serviceExists(serviceName) {
    try {
        def output = sh(script: "cf service " + serviceName, returnStdout: true).trim()
        echo output
        return !output.startsWith("FAILED")
    }catch (ex){
        return false
    }
}

def executeWithNotification(String errorMessage, Closure body) {
    try {
        body.call()
    } catch (err) {
        sh 'rm -r ' + CF_HOME
        notify('danger', errorMessage)
        throw err;
    }
}

def notify(color, message) {
    sh 'git rev-parse HEAD > commit'
    def gitCommit = readFile('commit').trim()
    sh 'rm commit'
    def String gitCommitLink = "<https://github.com/metskem/configserver/commit/${gitCommit}>"
    mail (to: 'harry.metske@gmail.com',
          subject: "Job '${env.JOB_NAME}' , build (${env.BUILD_NUMBER}) has ended",
          body: "Please check the git commit at ${gitCommitLink}");
}
