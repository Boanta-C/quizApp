# QuizApp

**QuizApp** is a web-based application that allows users to manage and take quizzes. The app provides functionalities to add, modify, delete, and view questions.

## Features

- **View all questions**: View all added questions.
- **Create and manage questions**: Add, update, and delete questions with their corresponding options. Supports adding a single question or adding multiple questions from a CSV file
- **Random question retrieval**: Retrieve random quiz questions based on the following filters: Language, Domain, Question Type and Difficulty Level
- **Active/inactive question management**: Toggle question activation status in order to include the question in the quizzes or not.

## Tech Breakdown

- **Frontend**: Thymeleaf (for rendering HTML templates) and Bootstrap (for styling and layout)
- **Backend**: Spring Boot
- **Database**: MySQL

## Installation

### Prerequisites

- Java 17 or later
- Maven 3.8 or later
- MySQL (or any compatible database)

### Steps

1. **Clone the repository**:

    Run the following command to clone the repository:
    ```bash
    git clone https://github.com/Boanta-C/quizApp.git
    cd quizapp
    ```

2. **Configure the database**:

    - Ensure MySQL is installed and running.
    - Create a database (e.g., `quizapp`) by running:
    ```sql
    CREATE DATABASE quizapp;
    ```
    - Modify the `application.properties` file under `src/main/resources` to match your database credentials:
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/quizapp
    spring.datasource.username=root
    spring.datasource.password=yourDataBasePassword

    # Enable database initialization only on the first run
    spring.sql.init.mode=always
    ```
    - You can use the current `data.sql` script in `src/main/resources` to initialize the database schema, or you can add your own.

   > **Note**: After the first application startup, remember to change `spring.sql.init.mode` to `never` to avoid re-running the schema creation on every start.
   
3. **Build the application**:

    Run the following command to build the application:
    ```bash
    mvn clean install
    ```

4. **Run the application**:

    Start the application with:
    ```bash
    mvn spring-boot:run
    ```
   
    The application will be accessible at `http://localhost:8080`.

   > **Note**: If you're running the app from IntelliJ IDEA or another IDE, you can simply run the application directly without using these Maven commands.

## Usage

- **Access the QuizApp**: Open a browser and go to `http://localhost:8080`.


- **Add single question**: `http://localhost:8080/questions/add-single-question`
- **Add multiple questions from a CSV file**: `http://localhost:8080/questions/upload-questions`
- **Edit questions or activate/deactivate them**: Available from the all questions page. `http://localhost:8080/questions/all`
- **Create and answer quizzes**: `http://localhost:8080/quiz/setup`
- **View quiz results**: After completing a quiz the results will be automatically shown to you.

> **Note**: All the features can be accessed from the main page `http://localhost:8080`



## Question sample
A sample CSV file with 759 QA and Java-related questions is included that can be found under `src/main/resources`. You can upload this file to quickly populate your database with questions.

> **Note**: AI was used to generate some of the questions in the CSV file. While I’ve tried to check for accuracy, some questions might not have the correct syntax (particularly in Romanian) or could contain minor errors (especially on the Single Choice and Multiple Choice questions). Please feel free to review and adjust the questions as needed. 