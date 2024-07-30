# Security Web Application for Client and Employee Management

### Overview
This project was developed as part of the Security in Electronic Business course, focusing on creating a secure web application to manage client and employee information for a marketing agency. The application handles security problems and uses strong defenses against potential threats and vulnerabilities.

### Technologies
* Back-end: Java with Spring Boot
* Front-end: Angular with TypeScript, HTML, and CSS
* Database: PostgreSQL

### Personal Contributions
In this project, my primary responsibilities included:

- **Data Encryption**: Applying encryption techniques to secure sensitive client and employee data
- **Certificate Management**: Utilizing Public Key Infrastructure (PKI) services for efficient handling and management of certificates.
- **JWT Authentication**: Implementing JSON Web Tokens (JWT)  with Spring Security for secure and efficient authentication of clients and employees.
- **Real-Time Logging and Alerts**: Integrating logging mechanisms and real-time alerts to monitor and respond to critical security events, including Grafana.
- **Penetration Testing**:Using OWASP ZAP to find and fix security issues. Successfully resolved high-level vulnerabilities found during testing.

### Project Specifications
This project includes various features and functionalities:
1. *User Types and Access Control*:
* Clients can update personal data, request advertisements, and change service packages.
* Employees can create advertisements, update personal data, and view ads created by others.
* Administrators have full access to the system, including managing user permissions.
2. *Security Features*:
* Registration and Authentication: Secure registration and login processes, including password hashing, two-factor authentication, and CAPTCHA.
* Access Control: Implementation of Role-Based Access Control (RBAC) to manage permissions and roles.
* Data Protection: Encryption of sensitive data in compliance with GDPR guidelines.
* Logging and Monitoring: Comprehensive logging and real-time alerts for monitoring system events and potential security threats.
* HTTPS Communication: Ensured secure communication between client and server through HTTPS.
* Single Sign-On (SSO): Integrated SSO for streamlined user authentication across the system.
* Penetration Testing and Security Analysis: Conducted thorough testing and analysis of the system's security, including third-party components.

### Application Setup
To set up the project locally:
* Clone the repository.
* Set up the backend using Java and Spring Boot.
* Set up the frontend using Angular.
* Configure the database with pgAdmin.
* Run the Angular application with SSL using the command: ng serve --ssl.

### Contributors
* Kristina Zelić
* Ana Radovanović
* Milica Petrović
