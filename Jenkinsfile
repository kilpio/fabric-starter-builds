#!/usr/bin/env groovy
def runShell(String command){
    def responseCode = sh returnStatus: true, script: "${command} &> tmp.txt" 
    def output =  readFile(file: "tmp.txt")

    if (responseCode != 0){
      println "[ERROR] ${output}"
      throw new Exception("${output}")
    }else{
      return "${output}"
    }
}

pipeline {
    agent any
    stages {
                stage('Clone fabric-starter sources') {
                    steps{
                        git url: 'https://github.com/olegabu/fabric-starter.git'
                        }
                    }
                stage('Clone fabric-starter build artifacts') {
                steps{
                      dir('fabric-starter-builds') {git credentialsId: '3fd7c1f8-c6bd-46d2-b1e0-014750954bfe', url: 'https://github.com/kilpio/fabric-starter-builds.git'}
                    }
                    }    
                
                stage('Apply patches') {
                    steps {
                        runShell('/usr/bin/env  patch < ./fabric-starter-builds/clean.sh.patch')
                    }
                }

                stage('Clean') {
                    environment {
                    ORG = 'org1'
                    DOMAIN = 'example.com'
                    CHANNEL = 'common'
                    CHAINCODE_INSTALL_ARGS = 'reference'
                    CHAINCODE_INSTANTIATE_ARGS = 'common reference'
                    DOCKER_COMPOSE_ARGS = '-f docker-compose.yaml -f docker-compose-couchdb.yaml -f docker-compose-dev.yaml '
                        }        
                    steps{
                            echo sh(script: './clean.sh', returnStdout: true)
                          }
                    }

                stage('Create ordrer') {
                    environment {
                    DOMAIN = 'example.com'
                    CHANNEL = 'common'
                    CHAINCODE_INSTALL_ARGS = 'reference'
                    CHAINCODE_INSTANTIATE_ARGS = 'common reference'
                    DOCKER_COMPOSE_ARGS = '-f docker-compose.yaml -f docker-compose-couchdb.yaml -f docker-compose-dev.yaml '
                        }        
                    steps{
                            echo sh(script: 'docker-compose -f docker-compose-orderer.yaml -f docker-compose-orderer-ports.yaml up -d', returnStdout: true)
                         }
                    }    

                stage('Create ordrer') {
                    environment {
                    ORG = ''
                    COMPOSE_PROJECT_NAME = ''    
                    DOMAIN = 'example.com'
                    CHANNEL = 'common'
                    CHAINCODE_INSTALL_ARGS = 'reference'
                    CHAINCODE_INSTANTIATE_ARGS = 'common reference'
                    DOCKER_COMPOSE_ARGS = '-f docker-compose.yaml -f docker-compose-couchdb.yaml -f docker-compose-dev.yaml '
                        }        
                    steps{
                            echo sh(script: '$ORG', returnStdout: true)
                            echo sh(script: 'docker-compose -f docker-compose-orderer.yaml -f docker-compose-orderer-ports.yaml up -d', returnStdout: true)
                         }
                    }        


            }//end stages
}//end pipeline

