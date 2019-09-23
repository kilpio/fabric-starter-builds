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

                stage('Tests') {
                    steps{
                            runShell("pwd && ls -la")
                        }
                    }
            }//end stages
}//end pipeline


//		echo sh(script: 'pgrep httpd', returnStdout: true).result
//		echo sh(script: 'ip addr show', returnStdout: true).result
//		echo sh(script: 'curl http://127.0.0.1/', returnStdout: true).result
