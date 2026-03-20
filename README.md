<div align="center">

# Currency Convertor Application Pipeline
Application repository for the Currency Converter portfolio project. Contains the Java Spring Boot application, Dockerfile, and pipeline buildspecs. The companion infrastructure repository is [currency-converter-infra](https://github.com/dheeraj3choudhary/aws-cicd-currencyconvertor-java-infra/tree/main).

<img width="4000" height="2250" alt="AWS_CICD_Series (2)" src="https://github.com/user-attachments/assets/0b198d95-3e1e-466a-bee9-0f99189d4202" />


[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)
[![HTML5](https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white)](https://developer.mozilla.org/en-US/docs/Web/HTML)
[![AWS CodePipeline](https://img.shields.io/badge/CodePipeline-232F3E?style=for-the-badge&logo=amazonaws&logoColor=white)](https://aws.amazon.com/codepipeline/)
[![AWS CodeBuild](https://img.shields.io/badge/CodeBuild-232F3E?style=for-the-badge&logo=amazonaws&logoColor=white)](https://aws.amazon.com/codebuild/)
[![AWS CodeCommit](https://img.shields.io/badge/CodeCommit-232F3E?style=for-the-badge&logo=amazonaws&logoColor=white)](https://aws.amazon.com/codecommit/)
[![AWS ECR](https://img.shields.io/badge/ECR-FF9900?style=for-the-badge&logo=amazonaws&logoColor=white)](https://aws.amazon.com/ecr/)
[![AWS Elastic Beanstalk](https://img.shields.io/badge/Elastic%20Beanstalk-FF9900?style=for-the-badge&logo=amazonaws&logoColor=white)](https://aws.amazon.com/elasticbeanstalk/)

<a href="https://www.buymeacoffee.com/Dheeraj3" target="_blank">
  <img src="https://cdn.buymeacoffee.com/buttons/v2/default-blue.png" alt="Buy Me A Coffee" height="50">
</a>

## [Subscribe](https://www.youtube.com/@dheeraj-choudhary?sub_confirmation=1) to learn more About Artificial-Intellegence, Machine-Learning, Cloud & DevOps.

<p align="center">
<a href="https://www.linkedin.com/in/dheeraj-choudhary/" target="_blank">
  <img height="100" alt="Dheeraj Choudhary | LinkedIN"  src="https://user-images.githubusercontent.com/60597290/152035581-a7c6c0c3-65c3-4160-89c0-e90ddc1e8d4e.png"/>
</a> 

<a href="https://www.youtube.com/@dheeraj-choudhary?sub_confirmation=1">
    <img height="100" src="https://user-images.githubusercontent.com/60597290/152035929-b7f75d38-e1c2-4325-a97e-7b934b8534e2.png" />
</a>    
</p>

</div>

---

## Repository Structure

```
currency-application/
├── Dockerfile                          # Multi-stage build (Maven → JRE Alpine)
├── pom.xml                             # Spring Boot 3.2.3, Java 17
├── pipeline/
│   ├── buildspec-build.yml             # Stage 2: mvn test + mvn package
│   └── buildspec-docker.yml            # Stage 3: docker build + ECR push + Dockerrun.aws.json
├── src/
│   ├── main/
│   │   ├── java/com/portfolio/currencyconverter/
│   │   │   ├── CurrencyConverterApplication.java
│   │   │   └── controller/
│   │   │       └── CurrencyController.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/
│   │           └── index.html          # Frontend — calls exchangerate-api.com directly
│   └── test/
│       └── java/com/portfolio/currencyconverter/
│           └── CurrencyConverterApplicationTests.java
├── .gitignore
└── README.md
```

---

## Application Overview

A single-page Currency Converter built with Java Spring Boot and a plain HTML/JS frontend.

**How it works:**
1. Spring Boot serves `index.html` as static content
2. On page load the frontend calls `/api/config` to fetch the API key from the backend
3. The frontend calls `exchangerate-api.com` directly with the key to get live exchange rates
4. The API key is never hardcoded — it is injected as `EXCHANGE_API_KEY` environment variable by Elastic Beanstalk, resolved from SSM Parameter Store at deploy time

**Endpoints:**

| Endpoint      | Description                                      |
|---------------|--------------------------------------------------|
| `GET /`       | Serves the currency converter frontend           |
| `GET /api/config` | Returns the API key to the frontend          |
| `GET /api/health` | Health check for Beanstalk health reporting  |

---

## Pipeline Stages

| Stage          | Provider    | What it does                                              |
|----------------|-------------|-----------------------------------------------------------|
| Source         | CodeCommit  | Pulls latest code from `master` branch                   |
| BuildAndTest   | CodeBuild   | Runs JUnit tests, packages jar with Maven                 |
| DockerAndPush  | CodeBuild   | Builds Docker image, pushes to ECR, generates Dockerrun  |
| Deploy         | Beanstalk   | Deploys Dockerrun.aws.json to the Docker environment      |

Pipeline fails at `BuildAndTest` if any test fails — Docker image is never built from broken code.

---

## Docker Image

Multi-stage build for a minimal production image:

- **Stage 1 (builder):** `maven:3.9.6-eclipse-temurin-17` — compiles the app
- **Stage 2 (runtime):** `eclipse-temurin:17-jre-alpine` — runs the jar
- Non-root user for security
- Image tagged with `CODEBUILD_BUILD_NUMBER` for full traceability

---

## Pre-requisites

Before pushing to this repo, the infra pipeline must have run successfully and provisioned:
- ECR repository (`currency-converter`)
- Elastic Beanstalk application (`currency-converter`) and environment (`currency-converter-env`)
- SSM Parameter Store SecureString (`/currency-converter/api-key`)

See [currency-converter-infra](https://git-codecommit.us-west-2.amazonaws.com/v1/repos/currency-converter-infra) for setup instructions.

---

## Deployment

Deployment is fully automated. Push to `master` triggers the pipeline:

```bash
git add .
git commit -m "your message"
git push origin master
```

To get the application URL after deployment:

```bash
aws elasticbeanstalk describe-environments \
  --environment-names currency-converter-env \
  --region us-west-2 \
  --query "Environments[0].CNAME" \
  --output text
```

Then open `http://<CNAME>` in your browser.

---

## Rules

- App has zero AWS SDK code — it reads config from environment variables only
- API key is never hardcoded — injected via SSM → Beanstalk → env var
- Docker image is tagged with build number — every deploy is traceable
- Pipeline stops on test failure — broken code never reaches ECR or Beanstalk
