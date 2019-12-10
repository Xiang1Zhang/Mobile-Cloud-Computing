# task-management-application
A task management Android application that enables planning of both personal and collaborative work. The topic is from CS-E4100 Mobile Cloud Computing 


### Backend
The backend implemented by using the [Google App Engine Flexible Environment](https://cloud.google.com/appengine/docs/flexible/). The application use the [Firebase Cloud Firestore](https://firebase.google.com/docs/firestore) to sync information across different devices and [Cloud Storage](https://firebase.google.com/docs/storage/) to store media. In addition, it use [Cloud Endpoints](https://cloud.google.com/endpoints/) to deploy the API described in the previous section. 

The backend provides an API for project management as follows:

+ Create a user, which returns a unique project ID and sets user attributes after successful user creation.
+ Update user information.
+ Query users by username, which gives an array of required users.
+ Create a project, which returns a unique project ID after successful project creation.
+ Delete a project, which deletes all the content of a project given the project ID. This action is only available to the project administrator.
+ Add members to a project, given the project ID and a list of members (it can contain one or more users).
+ Query projects by user ID, which returns a list of projects that has that user in it.
+ Create a task, given the project ID and the task attributes. It returns the task ID after creation.
+ Update a task, which updates the task status given a task ID.
+ Assign a task to a user(s), given the project ID and the task ID.
+ Query tasks by project ID
+ Add files Url to the database and fetch url from database

### Run backend locally
<pre><code>npm install</code></pre>
<pre><code>npm start</code></pre>

### Backend Deployment
<p>After setting up a Google Cloud Platform (GCP) SDK in local environment</p>
<p>Deploy the Endpoints configuration to create an Endpoints service</p>
<pre><code>gcloud endpoints services deploy openapi-appengine.yaml</code></pre>
<p>Deploy the API to App Engine</p>
<pre><code>gcloud app deploy</code></pre>


### Frontend
The Fronend is coding with Kotlin. It provides required user interface.

+ User authentication: A login screen prompting for user's email and password. In addition a new user could sign up an account and then login. The sign up screen consists three fields of name, email and password
+ Profile settings: User profile and password update
+ Create a project: On the top left, there is a 'Create Project'. The user could create personal and group projects.The user could add name, description, keywords and deadline and save. The button of selecting collaborators would be hided if the user choose to create a personal project.The person who create the project would be the administrator and this role could not be transferred to other users. The administrator could also select a badge from album.
+ Add members to a project: After project creation, users can add members through a search based on the display name
+ Add members to a project: The administrator could select collaborators by searching for the name.
+ Add tasks to a project: Once click on the button of the project item from the list,we changes into another lists.Tasks could be added to the projects. Task creation includes a description, a status and a deadline 
+ Convert image to task: The user is able to write the task description by recognizting the text of an image
+ Add attachments to project: text, documents(.pdf),images(.jpg) audio(.mp3) and video(.mp4) could be added to a project and a list of them could be showed. Documents(.pdf) could be downloaded through browser.
+ Image upload: The user can upload images via gallery or camera.
+ Delete a Project
+ Show Project content: For each project, there is a view to display all its contents. There is a checkbox of each task.
+ Show List of created projects: There are three fields of Project List about Favorite Projects, All of the projects and Projects List according to their deadline. Each item of projects list shows the name, deadline and last modified time. 
+ Update task status: The user is able to click the check box to mark the task as completed.
+ Delete a task: The user can delete a task by clicking the cross button.
+ Display image list
+ Display file list
+ Search for a project: The user could both search for projects according to the name and keywords.


### Description of the project files
The repository consists of the needed files for this task management app project and is depicted as follows:

+ Server folder includes all the relavant files for the backend.
+ Client folder is composed with all the frontend files.
+ The deply.sh is to build all needed software components and to deploy and start the backend in Google Cloud Platform.
+ This README.md describes the project files and elaborates on how to make it run.


