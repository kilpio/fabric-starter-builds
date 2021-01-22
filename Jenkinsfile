/* groovylint-disable LineLength */
CRED = '\033[1;31m'
CGREEN = '\033[1;32m'
CYELLOW = '\033[1;33m'
CBLUE = '\033[1;34m'
CMAGENTA = '\033[1;35m'
CCYAN = '\033[1;36m'
CWHITE = '\033[1;37m'
CFRAMED = '\033[51m'
CNOTFRAMED = '\033[54m'
CUNDERLINED = '\033[4m'
CNOTUNDERLINED = '\033[24m'
CNORMAL = '\033[0m'
// https://en.wikipedia.org/wiki/ANSI_escape_code

node {

    //? Delete images from previous build
    echo "Runnning build ${BUILD_NUMBER} of ${JOB_NAME}"
    int previousJobNo=BUILD_NUMBER as Integer
    previousJobNo-=1
    previousBuildNumber=previousJobNo.toString()
    echo CRED
    echo "Pruning results of build no. ${previousBuildNumber} of ${JOB_NAME}"
    sh "docker image prune --all --force --filter \"label=jenkins_job_name=${JOB_NAME}\" --filter \"label=jenkins_job_build=${previousBuildNumber}\""
    echo CNORMAL

    //? Cleaning workspace
    def isWorkspaceNotOK = !(WORKSPACE?.trim())
        if (isWorkspaceNotOK) {
        echo 'Failure: WORKSPACE variable is undefined!'
        currentBuild.result = 'FAILURE'
        return
        } else {
        echo "Cleaning workspace: ${WORKSPACE}"
        dir(WORKSPACE) {
            deleteDir()
            sh 'ls -ld $(find .)'
        }
        }

    //? ======================================== BUILDING FABRIC-TOOLS-EXTENDED IMAGES =========================
    stage('Fabric-Starter-snapshot') {
        ansiColor('xterm') {
            def newFabricStarterTag
            echo CRED
            stage('Fabric-Starter-git-checkout-pull-master') {
                echo 'Pull fabric-starter master and evaluate next snapshot'
                echo CBLUE
                checkoutFromGithubToSubfolder('fabric-starter', "${BUILD_BRANCH}")
                dir('fabric-starter') {
                    // sh "git checkout ${MASTER_BRANCH}"
                    // sh "git pull"
                    newFabricStarterTag = evaluateNextSnapshotGitTag('Fabric-starter')
                }
                echo CNORMAL
            }

            echo CRED
            stage ('Fabric-Starter-Merge-master-to-stable') {BUILD_NUMBER
                echo 'Merge current branch and update tags from latest to stable'
                echo 'Modify .env files according to new tag, modify current yamls -> stable'
                echo 'Commit modifications'
                echo ' '
                echo CMAGENTA
                echo "Master Branch is: ${MASTER_BRANCH}"
                dir('fabric-starter') {
                    sh "git config user.email ${GIT_REPO_OWNER}@jenkins"
                    sh "git config user.name  ${GIT_REPO_OWNER}"
                    updateAndCommitBranch(MASTER_BRANCH, 'latest', 'stable')
                }
                echo CNORMAL
            }

            echo CRED
            stage ('Fabric-Starter-Merge-stable-to-snapshot') {
                echo 'Merge current branch and update tags from latest to stable'
                echo 'Modify .env files according to new tag, modify current yamls -> current snapshot'
                echo 'Commit modifications'
                //sh "git config --global --list"
                sh 'git config --list'
                echo '______________________________________________________'
                echo CCYAN
                dir('fabric-starter') {
                    updateAndCommitBranch('stable', 'stable', newFabricStarterTag)
                }
                echo CNORMAL
            }

            echo CRED
            stage('Fabric-Tools-Extended-build-extended-images') {
                echo 'Build fabric-tools-extended images for latest, stable and snapshot'
                echo CYELLOW
                dir('fabric-starter') {
                    //buildDockerImage('fabric-tools-extended', newFabricStarterTag, newFabricStarterTag, "--no-cache --build-arg FABRIC_VERSION=${FABRIC_VERSION} -f fabric-tools-extended/Dockerfile .")
                    //buildDockerImage('fabric-tools-extended', 'stable', 'stable', "--no-cache --build-arg FABRIC_VERSION=${FABRIC_VERSION} -f fabric-tools-extended/Dockerfile .")
                    buildDockerImage(
                                        'fabric-tools-extended', 
                                        'latest',
                                        MASTER_BRANCH,
                                        "--no-cache --build-arg FABRIC_VERSION=${FABRIC_VERSION} -f fabric-tools-extended/Dockerfile ."
                                    )

                    tagDockerImage('fabric-tools-extended','latest','stable')
                    tagDockerImage('fabric-tools-extended','latest',newFabricStarterTag)
                }
                echo CNORMAL
            }
            //? ======================================== BUILDING FABRIC-STARTER-REST IMAGES =========================
            echo CRED
            stage('Farbric-starter-REST-checkout') {
                echo 'Pull fabric-starter-rest and checkout to the master branch'
                echo CCYAN
                checkoutFromGithubToSubfolder('fabric-starter-rest')
                // dir('fabric-starter-rest') {
                //     sh "git checkout ${MASTER_BRANCH}"
                //     sh "git pull"
                // }
                echo CNORMAL
            }

            echo CRED
            echo 'Take updates from master to stable, commit stable (create stable branch if does not exist)'
            stage('Farbric-starter-REST-merge-master-to-stable') {
                echo CBLUE
                    dir('fabric-starter-rest') {
                        sh "git config user.email ${GIT_REPO_OWNER}@jenkins"
                        sh "git config user.name  ${GIT_REPO_OWNER}"
                        updateAndCommitRESTBranch(MASTER_BRANCH, 'latest', 'stable')
                    }
                echo CNORMAL
            }

            echo CRED
            echo 'Take updates from master (pretend as from stable) to snapshot, commit snapshot (create snapshot branch if does not exist)'
            stage('Farbric-starter-REST-copy-stable-to-snapshot') {
                echo CYELLOW
                dir('fabric-starter-rest') {
                        updateAndCommitRESTBranch('stable', 'stable', newFabricStarterTag)
                }
                echo CNORMAL
            }

            echo CRED
            stage('Fabic-Starter-REST-build-docker-images') {
                echo 'Build snapshot, stable and latest fabric-starter-rest images'
                echo CGREEN

                dir('fabric-starter-rest') {
                    buildDockerImage(
                                        'fabric-starter-rest',
                                        'latest',
                                        MASTER_BRANCH,
                                        "--build-arg FABRIC_STARTER_REPOSITORY=${FABRIC_STARTER_REPOSITORY}  --no-cache -f Dockerfile ."
                                     )

                    tagDockerImage('fabric-starter-rest','latest','stable')
                    tagDockerImage('fabric-starter-rest','latest',newFabricStarterTag)
                    //buildDockerImage('fabric-starter-rest', newFabricStarterTag, newFabricStarterTag, "--build-arg FABRIC_STARTER_REPOSITORY=${FABRIC_STARTER_REPOSITORY}  --no-cache -f Dockerfile .")
                    //buildDockerImage('fabric-starter-rest', 'stable', 'stable', "--build-arg FABRIC_STARTER_REPOSITORY=${FABRIC_STARTER_REPOSITORY}  --no-cache -f Dockerfile .")

                }
                echo CNORMAL
                }
//? ==========================================RUN TESTS BEFORE PUSHING IMAGES ==========================
            echo CRED
            stage('Test-Stable-Branch') {
            echo CNORMAL
            echo CFRAMED

            echo "Start stable branch test in workspace ${WORKSPACE}/fabric-starter"
            dir("$WORKSPACE/fabric-starter/test"){

                sh    "docker container stop ubuntu_dockerized || true"
                sh    "docker container rm \$(docker volume rm test_volume 2>&1 | awk -F'[][]' '{print \$2}' | sed -e 's/,//g') || docker volume rm test_volume || true"
                sh '''              docker container create --name dummy -v test_volume:/root hello-world
                        docker cp $WORKSPACE/fabric-starter/ dummy:/root
                        docker rm dummy
                        docker run -v test_volume:/root alpine ls /root/fabric-starter/
                        docker image ls
                        docker pull kilpio/ubuntu_dockerized:latest
                        docker run -d --rm --name ubuntu_dockerized -v test_volume:/root/tests -v /var/run/docker.sock:/var/run/docker.sock kilpio/ubuntu_dockerized:latest tail -f /dev/null
                '''

                sh "docker container exec ubuntu_dockerized ps" 
                sh "docker container exec ubuntu_dockerized ls"
                sh "docker container exec ubuntu_dockerized pwd"
                sh "docker container exec ubuntu_dockerized id"
                sh "docker container exec ubuntu_dockerized export TEST='test'"
                sh "docker container exec ubuntu_dockerized echo "TEST: /${TEST}"

                sh    "docker container stop ubuntu_dockerized || true"
                sh    "docker container rm \$(docker volume rm test_volume 2>&1 | awk -F'[][]' '{print \$2}' | sed -e 's/,//g') || docker volume rm test_volume || true"


                //sh "pwd; source \$(pwd)/local-test-env.sh example.com; ./scenarios/01-fabric-starter-acceptance-test/create-test-network.sh org1 org2; ./scenarios/01-fabric-starter-acceptance-test/run-scenario.sh cli org1 org2; DEBUG=true ./scenarios/02-basic-functionality-test/run-scenario.sh api org1 org2"

            }
            echo CNOTFRAMED
            echo CNORMAL
            }

//? ========================================== DOCKER PUSH==============================================

            echo CRED
            stage('Fabic-Starter-REST-push-docker-images') {
                echo CBLUE

                pushDockerImage('fabric-starter-rest', newFabricStarterTag)
                pushDockerImage('fabric-starter-rest', 'stable')
                pushDockerImage('fabric-starter-rest', 'latest')
                echo CNORMAL
            }

            echo CRED
            stage('Fabic-Starter-push-docker-images') {
                echo CCYAN
                pushDockerImage('fabric-tools-extended', newFabricStarterTag)
                pushDockerImage('fabric-tools-extended', 'stable')
                pushDockerImage('fabric-tools-extended', 'latest')

                echo CNORMAL
            }

            //? ==================================== GIT PUSH==========================================
            echo CRED
            stage('Fabic-Starter-REST-git-push-snapshot') {
                echo CBLUE
                dir('fabric-starter-rest') {
                        gitPushToBranch(newFabricStarterTag, 'fabric-starter-rest')
                }
                echo CNORMAL
            }

            echo CRED
            stage('Fabic-Starter-REST-git-push-stable') {
                echo CGREEN
                dir('fabric-starter-rest') {
                    gitPushToBranch('stable', 'fabric-starter-rest')
                }
                echo CNORMAL
            }

            echo CRED
            stage ('Fabic-Starter-git-push-snapshot') {
                echo CMAGENTA
                dir('fabric-starter') {
                    gitPushToBranch(newFabricStarterTag, 'fabric-starter')
                }
                echo CNORMAL
            }

            echo CRED
            stage ('Fabic-Starter-git-push-stable') {
                echo CBLUE
                dir('fabric-starter') {
                    gitPushToBranch('stable', 'fabric-starter')
                }
                echo CNORMAL
            }
        } //AnsiColor
    } //Fabric-Starter-Packages-snapshot
}//node

//! ======================================== FUNCTIONS ===========================================

def checkoutFromGithubToSubfolder(repositoryName, def branch = 'master') {
    echo 'If login fails here with right credentials, please add github.com to known hosts for jenkins user (ssh-keyscan -H github.com >> .ssh/known_hosts)'
    sshagent(credentials: ['${GITHUB_SSH_CREDENTIALS_ID}']) {
            sh 'pwd'
            sh 'ls -la'
            sh "git clone git@github.com:${GIT_REPO_OWNER}/${repositoryName}.git"
            dir(repositoryName) {
                    sh "git checkout ${MASTER_BRANCH}"
                    sh 'git pull'
            }
    }
}

def evaluateNextSnapshotGitTag(repositoryTitle) {
    echo "Evaluate next snapshot name for ${repositoryTitle}"
    def lastSnapshot = sh(returnStdout: true, script: "git branch -r --list 'origin/snapshot-*' --sort=-committerdate | sort --version-sort --reverse | head -1").trim()
    echo "Current latest snapshot: ${lastSnapshot}"
    def (branchPrefix, version, fabricVersion) = lastSnapshot.tokenize('-')
    def (majorVer, minorVer) = version.tokenize('.')
    int minorVersion = (minorVer as int)
    def newGitTag = "${branchPrefix}-${majorVer}.${minorVersion + 1}-${FABRIC_VERSION}"

    newTag = newGitTag.split('/')[1]
    echo "New Tag for ${repositoryTitle}: ${newTag}"
    newTag
}

void buildDockerImage(imageName, tag, branchToBuildImageFrom, def args = '') {
    CUNDERLINED = '\033[4m'
    CNOTUNDERLINED = '\033[24m'
    CRED = '\033[1;31m'

    echo CUNDERLINED
    echo imageName
    echo tag
    echo branchToBuildImageFrom
    sh 'id'
    sh 'docker image ls'
    echo CNOTUNDERLINED

    sh "git checkout ${branchToBuildImageFrom}"
    if (!args?.trim()) {
        args = '-f Dockerfile .'
    }

    args = "-t ${DOCKER_REPO}/${imageName}:${tag} --label \"jenkins_job_build=${BUILD_NUMBER}\" --label \"jenkins_job_name=${JOB_NAME}\" ${args}"
    echo "docker build args: $args"

    sh "docker image build ${args}"

    echo CUNDERLINED
    sh 'docker image ls'
    echo CRED
    echo imageName
    echo tag
    echo branchToBuildImageFrom

    echo CNOTUNDERLINED
}

void tagDockerImage(imageName, tag, newTag) {
    sh "docker tag ${DOCKER_REPO}/${imageName}:${tag} ${DOCKER_REPO}/${imageName}:${newTag}"
}

void pushDockerImage(imageName, tag) {
    withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: '${DOCKER_CREDENTIALS_ID}',
usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
        sh "docker logout"
        sh "docker login -u $USERNAME -p $PASSWORD ${DOCKER_REGISTRY}"
        sh "docker push ${USERNAME}/${imageName}:${tag}"
        sh "docker logout"
    }
}

void commitBranch(branchName) {
    sh "git checkout ${branchName}"
    sh "git commit -m '${branchName}' || true"
}

void gitPushToBranch(branchName, repoName) {
    echo GITHUB_SSH_CREDENTIALS_ID
    sshagent(credentials: ['${GITHUB_SSH_CREDENTIALS_ID}']) {
        sh "git config user.name ${GIT_REPO_OWNER}"
        sh "git remote set-url origin git@github.com:${GIT_REPO_OWNER}/${repoName}.git"
        sh "git checkout ${branchName}"
        sh("git push -u origin ${branchName}")
    }
}
                        // master:      latest   ->   stable
                        // stable:      stable   ->   snapshot
void updateAndCommitBranch(fromBranchName, replaceTag, toBranchName) {
    echo "Now merging from ${fromBranchName}"
    checkoutAndThenPullIfRemoteExists(toBranchName)
    if (MERGE_FROM_MASTER == 'true') {
        sh "git merge --strategy-option=theirs ${fromBranchName} -m \"merge ${fromBranchName} into ${toBranchName}\""
        sh "git checkout ${fromBranchName} -- ."
        sh "git checkout ${MASTER_BRANCH} -- .env"
        envAppendVersionVars(toBranchName, FABRIC_VERSION)
        envAppendRepoVar(FABRIC_STARTER_REPOSITORY)
    }

    updateEnvFileWithVersions(FABRIC_VERSION, replaceTag, toBranchName)
    updateComposeFilesWithVersions(FABRIC_VERSION, replaceTag, toBranchName)

    commitBranch(toBranchName)
}

void updateAndCommitRESTBranch(fromBranchName, replaceTag, toBranchName) {
    checkoutAndThenPullIfRemoteExists(toBranchName)
    if (MERGE_FROM_MASTER == 'true') {
        sh "git merge --strategy-option=theirs ${fromBranchName} -m \"merge ${fromBranchName} into ${toBranchName}\""
        sh "git checkout ${fromBranchName} -- ."
    }
    updateImagesReferencesVersion('Dockerfile', '', replaceTag, toBranchName)
    commitBranch(toBranchName)
}

void checkoutAndThenPullIfRemoteExists(toBranchName) {
    if (remoteBranchExists(toBranchName)) {
        sshagent(credentials: ['${GITHUB_SSH_CREDENTIALS_ID}']) {
            sh "git checkout ${toBranchName}"
            sh 'git pull'
        }
    } else {
        sh "git checkout -B ${toBranchName}"
    }
}

void envAppendVersionVars(currentBranchName, fabricVersion) {
    def fileContent = readFile '.env'
    writeFile file: '.env', text: "${fileContent}\nFABRIC_STARTER_VERSION=${currentBranchName}\nFABRIC_VERSION=${fabricVersion}"
    sh 'git add .env'
}

void envAppendRepoVar(currentRepoName) {
    def fileContent = readFile '.env'
    writeFile file: '.env', text: "${fileContent}\nFABRIC_STARTER_REPOSITORY=${currentRepoName}"
    sh 'git add .env'
}

// master:      latest   ->   stable
// stable:      stable   ->   snapshot
void updateEnvFileWithVersions(fabricVersion, replaceTag, currentBranch) {
    updateImagesReferencesVersion('.env', fabricVersion, replaceTag, currentBranch)
}

def updateComposeFilesWithVersions(fabricVersion, replaceTag, currentBranch) { //mergeBranch -> currenBranch
    updateImagesReferencesVersion('docker-compose.yaml', fabricVersion, replaceTag, currentBranch)
    updateImagesReferencesVersion('docker-compose-clean.yaml', fabricVersion, replaceTag, currentBranch)
    updateImagesReferencesVersion('docker-compose-orderer.yaml', fabricVersion, replaceTag, currentBranch)
    updateImagesReferencesVersion('docker-compose-listener.yaml', fabricVersion, replaceTag, currentBranch)
    updateImagesReferencesVersion('docker-compose-deploy.yaml', fabricVersion, replaceTag, currentBranch)
    updateImagesReferencesVersion('network-create-base.sh', fabricVersion, replaceTag, currentBranch)
    updateImagesReferencesVersion('peer-start.sh', fabricVersion, replaceTag, currentBranch)
    updateImagesReferencesVersion('raft-start-docker-machine.sh', fabricVersion, replaceTag, currentBranch)
    updateImagesReferencesVersion('https/docker-compose-generate-tls-certs.yaml', fabricVersion, replaceTag, currentBranch)
    updateImagesReferencesVersion('extra/docker-registry-local/start-docker-registry-local.sh', fabricVersion, replaceTag, currentBranch)
    updateImagesReferencesVersion('deploy.sh', fabricVersion, replaceTag, currentBranch)
}

def updateImagesReferencesVersion(fileToProcess, fabricVersion, replaceTag, currentBranch) {
    fileContent = readFile fileToProcess

    fileContent = fileContent.replace("\${FABRIC_VERSION:-" + "${replaceTag}" + '}', "\${FABRIC_VERSION:-" + "${fabricVersion}" + '}')
    fileContent = fileContent.replace("\${FABRIC_STARTER_VERSION:-" + "${replaceTag}" + '}', "\${FABRIC_STARTER_VERSION:-" + "${currentBranch}" + '}')

    fileContent = fileContent.replace('FABRIC_VERSION=' + "${replaceTag}", 'FABRIC_VERSION=' + "${fabricVersion}")
    fileContent = fileContent.replace('FABRIC_STARTER_VERSION=' + "${replaceTag}", 'FABRIC_STARTER_VERSION=' + "${currentBranch}")
    
    fileContent = fileContent.replaceAll("\\{FABRIC_STARTER_REPOSITORY:\\-[a-z]*}", "{FABRIC_STARTER_REPOSITORY:-$FABRIC_STARTER_REPOSITORY}")

    writeFile file: fileToProcess, text: fileContent

    echo "Content for ${fileToProcess}: ${fileContent}"

    sh "git add ${fileToProcess}"
}

def remoteBranchExists(branchName) {
    def checkRemoteBranch = sh(script: "git branch --list -r origin/${branchName} | wc -l", returnStdout: true).toString().trim()
    def whetherExists = checkRemoteBranch.isInteger() ? checkRemoteBranch.toInteger() : 0
    return (whetherExists > 0)
}
