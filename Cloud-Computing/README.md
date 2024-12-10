# CLoud Computing Path
## Member
- C318B4KY0018 – Abdul Aziz
- C318B4KY3819 – Ridho Abdul Aziz

## Requirement
- node.js
- Flask
- postman
- Visual Studio Code
- Google Cloud Services

## Services
- Cloud Run
- App Engine
- Firestore
- Cloud Storage

## Implementation
- Implementing a REST API architecture to create endpoints
- Deploy Model Machine Learning on Cloud Run
- Deploy node.js app on App Engine
- Connecting App Engine to Firestore and Cloud Storage to retrieve and store data
- Connecting App Engine to Cloud Run to retrieve prediction results

## Install dependencies
1. node.js App
   - enter back-end folder
   ```bash
   cd back-end
   ```
   - install all dependencies
   ```bash
   npm install
   ```
   If you haven't installed Node.js yet, please install it by visiting the following link [node.js](https://nodejs.org/en)
2. Machine Learning Model
   - enter model folder
   ```bash
   cd model
   ```
   - install all dependencies
   ```bash
   pip install -r requirements.txt
   ```
   If you haven't installed Python yet, please install it by visiting the following link [Python](https://www.python.org/)

## Tes App
1. node.js 
- open terminal in your Visual Studio Code
- run app with npm command
  ```bash
  npm run start-dev
  ```
- copy link and test endpoint using postman
2. Flask
- open terminal in your Visual Studio Code
- run app with python command
  ```bash
  python app.py
  ```
- copy link and test endpoint using postman

## Next Step
Once all the applications are running smoothly, you can deploy the application to Google Cloud services. Good luck!
