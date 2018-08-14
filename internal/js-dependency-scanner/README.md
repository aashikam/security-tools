## Security tool to automating the scan related to vulnerabilities of Java Script Libraries used in products.

*This tool performs security scans for weekly release of every WSO2 products. The weekly release of products
downloaded from respective github repository. 

*This tool currently uses Retire.js as a JS Security Scan tool. Retire.js is an open source tool
which helps to detect JS libraries with known vulnerabilities.

*This tool generates a report for each product which includes retire.js result as json format.

*This tool delivers the generated reports in fallowing ways : 
        1. Upload reports to particular git repository.
        2. If the report contains any known vulnerability, Create JIRA ticket.
        3. Integrate with Vulnerability Management System (VMS).
        
* Before you begin : 

    1. Install Oracle Java SE Development Kit (JDK) version 1.7* or 1.8 and set the JAVA_HOME environment variable.

    2. Install Maven
    
    3. Install Retire.Js (Please find instruction to install Retire.js : https://www.npmjs.com/package/retire)

    4. Clone : https://github.com/wso2/security-tools
    
    5. Github credential details. (Username, password, github access token)
    
    6. JIRA credential details. (Username, password)
    
    
Note : Following configurations need to be done before executing this scan tool. 

   1. There are separate configurations for each product. Provide product details in respective configuration files.
   2. Provide github credential information in githubconfig.properties.
   3. Provide github accesstoken information in githubaccesstoken.properties.
   4. Provide JIRA credential information in issuecreatorconfig.properties.
   5. Provide jira ticket  parameter values in jiraticketinfo.properties.
   6. Provide supported product list.
   7. Replace the actual git repository url where the scan reports should be uploaded in SECURITY_ARTIFACT_REPO
    placeholder in JSScannerConstants.java file.
   8. Replace the JIRA URL in WSO2_JIRA_BASE_URL placeholder in IssueCreatorConstants.java
  
    
* How to use this tool?

    1. After providing above configuration details, Go to the root directory of this tool.
    2. Run this command.
`            mvn clean install
`    
    3. Run this command.
`             java -jar target/JsSecurityScanner-1.0-SNAPSHOT.jar 
`