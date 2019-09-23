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
                        ORG = ''
                        COMPOSE_PROJECT_NAME = ''    
                        DOMAIN = 'example.com'
                        CHANNEL = 'common'
                        CHAINCODE_INSTALL_ARGS = 'reference'
                        CHAINCODE_INSTANTIATE_ARGS = 'common reference'
                        DOCKER_COMPOSE_ARGS = '-f docker-compose.yaml -f docker-compose-couchdb.yaml -f docker-compose-dev.yaml '
                            }        
                        steps{
                                //echo sh(script: 'echo $DOMAIN; echo $ORG; echo $COMPOSE_PROJECT_NAME; echo $CHANNEL', returnStdout: true)
                                echo sh(script: 'docker-compose -f docker-compose-orderer.yaml -f docker-compose-orderer-ports.yaml up -d', returnStdout: true)
                            }
                    }        
                                       
                    stage('Create organization org1') {
                        environment {
                            ORG = 'org1'
                            API_PORT = '4000'
                            WWW_PORT = '81'
                            CA_PORT = '7054'
                            PEER0_PORT = '7051'
                            LDAP_PORT_HTTP = '6080'
                            LDAP_PORT_HTTPS = '6443'
                            COMPOSE_PROJECT_NAME = "${ORG}"
                            DOCKER_COMPOSE_ARGS = '-f docker-compose.yaml -f docker-compose-couchdb.yaml -f docker-compose-dev.yaml'
                        }

                        steps{
                                echo sh(script: 'echo "Creating member organization $ORG with api $API_PORT"', returnStdout: true)
                                ansiColor('xterm') {echo sh(script: "docker-compose ${DOCKER_COMPOSE_ARGS} up -d", returnStdout: true)}
                            }
                    }


                    stage('Adding org1 to consortium') {

                        //when {
                          //      expression { params.ADD_TO_COSORTIUM == 'enabled' }
                            //  }

                        environment {
                            ORG = 'org1'
                            DOMAIN = 'example.com'
                            CHANNEL = 'common'
                        }

                        steps{
                                echo sh(script: 'echo "Adding $ORG organization to the consortium"', returnStdout: true)
                                ansiColor('xterm'){echo sh(script: "./consortium-add-org.sh \${ORG} || true", returnStdout: true)}
                            }
                    }

                    stage('Creating and joining the channel with org1') {

                        
                        environment {
                            ORG = 'org1'
                            DOMAIN = 'example.com'
                            CHANNEL = 'common'
                            COMPOSE_PROJECT_NAME = "${ORG}"
                        }

                        steps{
                                echo sh(script: 'echo "Creating channel $CHANNEL by $ORG"', returnStdout: true)
                                ansiColor('xterm'){echo sh(script: "./channel-create.sh $CHANNEL || true", returnStdout: true)}
                                ansiColor('xterm'){echo sh(script: "./channel-join.sh   $CHANNEL || true", returnStdout: true)}
                            }
                    }

                    stage('Integration test') {

                        when {expression {5 == 6}}
                        environment {
                            ORG = 'org1'
                            DOMAIN = 'example.com'
                            CHANNEL = 'common'
                            COMPOSE_PROJECT_NAME = "${ORG}"
                        }

                        steps{
                                //echo sh(script: 'echo "Install and instantiate nodejs chaincode reference on channel common.""', returnStdout: true) 
                                echo ansiColor('xterm') {echo sh(script: './chaincode-install.sh reference || true', returnStdout: true)}
                                echo ansiColor('xterm') {echo sh(script: './chaincode-instantiate.sh common reference || true', returnStdout: true)}
                                
                            }
                    }
                    


            }//end stages
}//end pipeline
