# Wendys Family Tree - SEPM Individual phase 2022W

An individual project consisting of web applicaton both frontend and backend with CRUD functionality

## Usage

**Backend**
```
cd backend
mvn clean package
java -jar target/e12208176-0.0.1-SNAPSHOT.jar
```
To load with test data use
```
java -jar Dspring.profiles.active=datagen -jar target/e12208176-0.0.1-SNAPSHOT.jar
```
The server backend is running on `http://localhost:8080`

**Frontend**
```
cd frontend
npm install
ng serve
```
The frontend is running on `http://localhost:4200`
