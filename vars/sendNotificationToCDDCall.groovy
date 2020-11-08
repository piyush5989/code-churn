def call() {
 try {
  environmentSetUp()
  sendNotificationToCDDirector()
  processCDDReleases(evaluate("${currentBuild.description}"))
 } catch (ex) {
  echo "Exception occurred. Skipping notification to CDD. Error is [" + ex.toString() + "]"
 }
}

void environmentSetUp() {
 getAPIKeyFromCredentials()
 setGitEnvironmentVariables()
 env.CDD_SERVER_NAME = "cddirector.io"
 env.CDD_SERVER_PORT = "443"
 env.CDD_TENANT_ID = "17827b55-3a42-403f-b1f8-9655d7e625bb"
 env.CDD_USE_SSL = true
 env.CDD_APPLICATION_NAME = "${env.GIT_URL.replaceFirst(/^.*\/([^\/]+?).git$/, '$1')}"
 env.CDD_APPLICATION_VERSION = "$env.BRANCH_NAME"
 env.CDD_GIT_COMMIT_ID = "$env.GIT_COMMIT"
 env.CDD_PREVIOUS_GIT_COMMIT_ID = "$env.GIT_PREVIOUS_SUCCESSFUL_COMMIT"
}

void getAPIKeyFromCredentials() {
 def credentials = com.cloudbees.plugins.credentials.CredentialsProvider.findCredentialById('CDD_API_KEY',
  com.cloudbees.plugins.credentials.Credentials.class,
  currentBuild.rawBuild, null
 );
 env.CDD_API_KEY = credentials.secret
}

void setGitEnvironmentVariables() {
 if (!env.GIT_URL) env.GIT_URL = sh(returnStdout: true, script: 'git config remote.origin.url').trim()
 if (!env.GIT_COMMIT) env.GIT_COMMIT = sh(returnStdout: true, script: 'git rev-parse HEAD').trim()
 if (!env.GIT_PREVIOUS_SUCCESSFUL_COMMIT) env.GIT_PREVIOUS_SUCCESSFUL_COMMIT = getLastSuccessfulCommit()
}

String getLastSuccessfulCommit() {
 String lastSuccessfulHash = null
 def lastSuccessfulBuild = currentBuild.rawBuild.getPreviousSuccessfulBuild()
 println "Previous Successful Build: [$lastSuccessfulBuild]"
 if (lastSuccessfulBuild) {
  def scmAction = lastSuccessfulBuild?.actions.find {
   action -> action instanceof jenkins.scm.api.SCMRevisionAction
  }
  lastSuccessfulHash = scmAction?.revision?.hash
  println "Previous Successful Build Revision: [${scmAction?.revision}], Hash: [$lastSuccessfulHash], Source Id: [${scmAction?.sourceId}]"
 }
 return lastSuccessfulHash
}

void sendNotificationToCDDirector() {
 echo '----------Sending Build Notification to CDD--------------'
 echo "Environment variables: GIT_URL: [$env.GIT_URL], GIT_BRANCH: [$env.GIT_BRANCH], BRANCH_NAME: [$env.BRANCH_NAME], GIT_LOCAL_BRANCH: [$env.GIT_LOCAL_BRANCH], CDD_APPLICATION_NAME: [${CDD_APPLICATION_NAME}], CDD_APPLICATION_VERSION: [${CDD_APPLICATION_VERSION}], GIT_COMMIT: [${env.GIT_COMMIT}], GIT_PREVIOUS_SUCCESSFUL_COMMIT: [${env.GIT_PREVIOUS_SUCCESSFUL_COMMIT}]"
 sendNotificationToCDD useSourceCodeRepositoryNameAsApplicationName: true,
  appName: "${CDD_APPLICATION_NAME}",
  useSourceCodeRepositoryBranchNameAsApplicationVersionName: true,
  appVersion: "${CDD_APPLICATION_VERSION}",
  gitCommit: "${CDD_GIT_COMMIT_ID}",
  gitPrevSuccessfulCommit: "${CDD_PREVIOUS_GIT_COMMIT_ID}",
  overrideCDDConfig: [
   customApiKey: "${CDD_API_KEY}",
   customProxyPassword: '',
   customProxyUrl: '',
   customProxyUsername: '',
   customServerName: "${CDD_SERVER_NAME}",
   customServerPort: "${CDD_SERVER_PORT}",
   customTenantId: "${CDD_TENANT_ID}",
   customUseSSL: "${CDD_USE_SSL}"
  ],
  releaseTokens: '{}',
  ignoreNonexistentApplication: true
 echo '----------Jenkins Pipeline completed successfully--------------'
}

void processCDDReleases(Map cddReleaseMap) {
 if (cddReleaseMap) {
  echo '----------Process CDD Releases--------------'
  cddReleaseMap.eachWithIndex {
   entry,
   index ->
   println "[$entry.key] = [$entry.value]"
   env.
   "$entry.key" = "$entry.value";
  }
 }
}
